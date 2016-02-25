package com.timehop.stickyheadersrecyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.timehop.stickyheadersrecyclerview.caching.HeaderProvider;
import com.timehop.stickyheadersrecyclerview.caching.HeaderViewCache;
import com.timehop.stickyheadersrecyclerview.calculation.DimensionCalculator;
import com.timehop.stickyheadersrecyclerview.rendering.HeaderRenderer;
import com.timehop.stickyheadersrecyclerview.util.LinearLayoutOrientationProvider;
import com.timehop.stickyheadersrecyclerview.util.OrientationProvider;

public class StickyRecyclerHeadersDecoration extends RecyclerView.ItemDecoration {

  private final StickyRecyclerHeadersAdapter mAdapter;
  private final ItemVisibilityAdapter mVisibilityAdapter;
  private final SparseArray<Rect> mHeaderRects = new SparseArray<>();
  private final HeaderProvider mHeaderProvider;
  private final OrientationProvider mOrientationProvider;
  private final HeaderPositionCalculator mHeaderPositionCalculator;
  private final HeaderRenderer mRenderer;
  private final DimensionCalculator mDimensionCalculator;

  /**
   * The following field is used as a buffer for internal calculations. Its sole purpose is to avoid
   * allocating new Rect every time we need one.
   */
  private final Rect mTempRect = new Rect();

  // TODO: Consider passing in orientation to simplify orientation accounting within calculation
  public StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter) {
    this(adapter, new LinearLayoutOrientationProvider(), new DimensionCalculator(), null);
  }

  public StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter, ItemVisibilityAdapter visibilityAdapter) {
    this(adapter, new LinearLayoutOrientationProvider(), new DimensionCalculator(), visibilityAdapter);
  }

  private StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter, OrientationProvider orientationProvider,
      DimensionCalculator dimensionCalculator, ItemVisibilityAdapter visibilityAdapter) {
    this(adapter, orientationProvider, dimensionCalculator, new HeaderRenderer(orientationProvider),
        new HeaderViewCache(adapter, orientationProvider), visibilityAdapter);
  }

  private StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter, OrientationProvider orientationProvider,
      DimensionCalculator dimensionCalculator, HeaderRenderer headerRenderer, HeaderProvider headerProvider, ItemVisibilityAdapter visibilityAdapter) {
    this(adapter, headerRenderer, orientationProvider, dimensionCalculator, headerProvider,
        new HeaderPositionCalculator(adapter, headerProvider, orientationProvider,
            dimensionCalculator), visibilityAdapter);
  }

  private StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter, HeaderRenderer headerRenderer,
      OrientationProvider orientationProvider, DimensionCalculator dimensionCalculator, HeaderProvider headerProvider,
      HeaderPositionCalculator headerPositionCalculator, ItemVisibilityAdapter visibilityAdapter) {
    mAdapter = adapter;
    mHeaderProvider = headerProvider;
    mOrientationProvider = orientationProvider;
    mRenderer = headerRenderer;
    mDimensionCalculator = dimensionCalculator;
    mHeaderPositionCalculator = headerPositionCalculator;
    mVisibilityAdapter = visibilityAdapter;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    int itemPosition = parent.getChildAdapterPosition(view);
    if (itemPosition == RecyclerView.NO_POSITION) {
        return;
    }
    if (mHeaderPositionCalculator.hasNewHeader(itemPosition, mOrientationProvider.isReverseLayout(parent))) {
      View header = getHeaderView(parent, itemPosition);
      setItemOffsetsForHeader(outRect, header, mOrientationProvider.getOrientation(parent));
    }
  }

  /**
   * Sets the offsets for the first item in a section to make room for the header view
   *
   * @param itemOffsets rectangle to define offsets for the item
   * @param header      view used to calculate offset for the item
   * @param orientation used to calculate offset for the item
   */
  private void setItemOffsetsForHeader(Rect itemOffsets, View header, int orientation) {
    mDimensionCalculator.initMargins(mTempRect, header);
    if (orientation == LinearLayoutManager.VERTICAL) {
      itemOffsets.top = header.getHeight() + mTempRect.top + mTempRect.bottom;
    } else {
      itemOffsets.left = header.getWidth() + mTempRect.left + mTempRect.right;
    }
  }

  @Override
  public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    super.onDrawOver(canvas, parent, state);

    final int childCount = parent.getChildCount();
    if (childCount <= 0 || mAdapter.getItemCount() <= 0) {
      return;
    }

    for (int i = 0; i < childCount; i++) {
      View itemView = parent.getChildAt(i);
      int position = parent.getChildAdapterPosition(itemView);
      if (position == RecyclerView.NO_POSITION) {
          continue;
      }

      boolean hasStickyHeader = mHeaderPositionCalculator.hasStickyHeader(itemView, mOrientationProvider.getOrientation(parent), position);
      if (hasStickyHeader || mHeaderPositionCalculator.hasNewHeader(position, mOrientationProvider.isReverseLayout(parent))) {
        View header = mHeaderProvider.getHeader(parent, position);
        //re-use existing Rect, if any.
        Rect headerOffset = mHeaderRects.get(position);
        if (headerOffset == null) {
          headerOffset = new Rect();
          mHeaderRects.put(position, headerOffset);
        }
        mHeaderPositionCalculator.initHeaderBounds(headerOffset, parent, header, itemView, hasStickyHeader);
        mRenderer.drawHeader(parent, canvas, header, headerOffset);
      }
    }
  }

  /**
   * Gets the position of the header under the specified (x, y) coordinates.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   * @return position of header, or -1 if not found
   */
  public int findHeaderPositionUnder(int x, int y) {
    for (int i = 0; i < mHeaderRects.size(); i++) {
      Rect rect = mHeaderRects.get(mHeaderRects.keyAt(i));
      if (rect.contains(x, y)) {
        int position = mHeaderRects.keyAt(i);
        if (mVisibilityAdapter == null || mVisibilityAdapter.isPositionVisible(position)) {
          return position;
        }
      }
    }
    return -1;
  }

  /**
   * Gets the header view for the associated position.  If it doesn't exist yet, it will be
   * created, measured, and laid out.
   *
   * @param parent the recyclerview
   * @param position the position to get the header view for
   * @return Header view
   */
  public View getHeaderView(RecyclerView parent, int position) {
    return mHeaderProvider.getHeader(parent, position);
  }

  /**
   * Invalidates cached headers.  This does not invalidate the recyclerview, you should do that manually after
   * calling this method.
   */
  public void invalidateHeaders() {
    mHeaderProvider.invalidate();
    mHeaderRects.clear();
  }
}
