package com.timehop.stickyheadersrecyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.timehop.stickyheadersrecyclerview.caching.HeaderProvider;
import com.timehop.stickyheadersrecyclerview.caching.HeaderViewCache;
import com.timehop.stickyheadersrecyclerview.rendering.HeaderRenderer;
import com.timehop.stickyheadersrecyclerview.util.LinearLayoutOrientationProvider;
import com.timehop.stickyheadersrecyclerview.util.OrientationProvider;

public class StickyRecyclerHeadersDecoration extends RecyclerView.ItemDecoration {

  private final StickyRecyclerHeadersAdapter mAdapter;
  private final SparseArray<Rect> mHeaderRects = new SparseArray<>();
  private final HeaderProvider mHeaderProvider;
  private final OrientationProvider mOrientationProvider;
  private final HeaderPositionCalculator mHeaderPositionCalculator;
  private final HeaderRenderer mRenderer;

  public StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter) {
    this(adapter, new LinearLayoutOrientationProvider());
  }

  private StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter,
                                          OrientationProvider orientationProvider) {
    this(adapter, orientationProvider, new HeaderViewCache(adapter, orientationProvider));
  }

  private StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter,
                                          OrientationProvider orientationProvider,
                                          HeaderProvider headerProvider) {
    mAdapter = adapter;
    mHeaderProvider = headerProvider;
    mOrientationProvider = orientationProvider;
    mRenderer = new HeaderRenderer();
    mHeaderPositionCalculator = new HeaderPositionCalculator(adapter, headerProvider, orientationProvider);
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    int itemPosition = parent.getChildPosition(view);
    if (mHeaderPositionCalculator.hasNewHeader(itemPosition)) {
      View header = getHeaderView(parent, itemPosition);
      setItemOffsetsForHeader(outRect, header, mOrientationProvider.getOrientation(parent));
    }
  }

  /**
   * Sets the offsets for the first item in a section to make room for the header view
   * @param itemOffsets rectangle to define offsets for the item
   * @param header view used to calculate offset for the item
   * @param orientation used to calculate offset for the item
   */
  private void setItemOffsetsForHeader(Rect itemOffsets, View header, int orientation) {
    if (orientation == LinearLayoutManager.VERTICAL) {
      itemOffsets.top = header.getHeight();
    } else {
      itemOffsets.left = header.getWidth();
    }
  }

  @Override
  public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    super.onDrawOver(canvas, parent, state);
    int orientation = mOrientationProvider.getOrientation(parent);
    mHeaderRects.clear();

    if (parent.getChildCount() <= 0 || mAdapter.getItemCount() <= 0) {
      return;
    }

    View firstView = parent.getChildAt(0);
    int firstPosition = parent.getChildPosition(firstView);
    // If first position should have a header
    if (mAdapter.getHeaderId(firstPosition) >= 0) {
      View header = mHeaderProvider.getHeader(parent, firstPosition);
      Rect stickyHeaderOffsets =
          mHeaderPositionCalculator.getStickyHeaderBounds(parent, header, firstView);
      mRenderer.drawStickyHeader(canvas, header, stickyHeaderOffsets);
      mHeaderRects.put(firstPosition, stickyHeaderOffsets);
    }

    for (int i = 1; i < parent.getChildCount(); i++) {
      int position = parent.getChildPosition(parent.getChildAt(i));
      if (mHeaderPositionCalculator.hasNewHeader(position)) {
        // this header is different than the previous, it must be drawn in the correct place
        int translationX = 0;
        int translationY = 0;
        View header = getHeaderView(parent, position);
        if (orientation == LinearLayoutManager.VERTICAL) {
          translationY = parent.getChildAt(i).getTop() - header.getHeight();
        } else {
          translationX = parent.getChildAt(i).getLeft() - header.getWidth();
        }
        canvas.save();
        canvas.translate(translationX, translationY);
        header.draw(canvas);
        canvas.restore();
        mHeaderRects.put(position, new Rect(translationX, translationY,
            translationX + header.getWidth(), translationY + header.getHeight()));
      }
    }
  }

  /**
   * Gets the position of the header under the specified (x, y) coordinates.
   * @param x x-coordinate
   * @param y y-coordinate
   * @return position of header, or -1 if not found
   */
  public int findHeaderPositionUnder(int x, int y) {
    for (int i = 0; i < mHeaderRects.size(); i++) {
      Rect rect = mHeaderRects.get(mHeaderRects.keyAt(i));
      if (rect.contains(x, y)) {
        return mHeaderRects.keyAt(i);
      }
    }
    return -1;
  }

  /**
   * Gets the header view for the associated position.  If it doesn't exist yet, it will be
   * created, measured, and laid out.
   * @param parent
   * @param position
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
  }
}
