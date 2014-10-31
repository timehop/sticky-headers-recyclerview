package com.timehop.stickyheadersrecyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class StickyRecyclerHeadersDecoration extends RecyclerView.ItemDecoration {
  private final StickyRecyclerHeadersAdapter mAdapter;
  private final LongSparseArray<View> mHeaderViews = new LongSparseArray<>();
  private final SparseArray<Rect> mHeaderRects = new SparseArray<>();

  public StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter) {
    mAdapter = adapter;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    int orientation = getOrientation(parent);
    int itemPosition = parent.getChildPosition(view);
    if (hasNewHeader(itemPosition)) {
      View header = getHeaderView(parent, itemPosition);
      if (orientation == LinearLayoutManager.VERTICAL) {
        outRect.top = header.getHeight();
      } else {
        outRect.left = header.getWidth();
      }
    }
  }

  @Override
  public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    super.onDrawOver(canvas, parent, state);
    int orientation = getOrientation(parent);
    mHeaderRects.clear();

    if (parent.getChildCount() > 0 && mAdapter.getItemCount() > 0) {
      View firstView = parent.getChildAt(0);
      // draw the first visible child's header at the top of the view
      int firstPosition = parent.getChildPosition(firstView);
      View firstHeader = getHeaderView(parent, firstPosition);
      View nextView = getNextView(parent);
      int translationX = 0;
      int translationY = 0;
      int nextPosition = parent.getChildPosition(nextView);
      if (nextPosition != -1 && hasNewHeader(nextPosition)) {
        View secondHeader = getHeaderView(parent, nextPosition);
        //Translate the topmost header so the next header takes its place, if applicable
        if (orientation == LinearLayoutManager.VERTICAL &&
            nextView.getTop() - secondHeader.getHeight() - firstHeader.getHeight() < 0) {
          translationY = nextView.getTop() - secondHeader.getHeight() - firstHeader.getHeight();
        } else if (orientation == LinearLayoutManager.HORIZONTAL &&
            nextView.getLeft() - secondHeader.getWidth() - firstHeader.getWidth() < 0) {
          translationX = nextView.getLeft() - secondHeader.getWidth() - firstHeader.getWidth();
        }
      }
      canvas.save();
      canvas.translate(translationX, translationY);
      firstHeader.draw(canvas);
      canvas.restore();
      mHeaderRects.put(firstPosition, new Rect(translationX, translationY,
          translationX + firstHeader.getWidth(), translationY + firstHeader.getHeight()));

      if (parent.getChildCount() > 1)
        for (int i = 1; i < parent.getChildCount(); i++) {
          int position = parent.getChildPosition(parent.getChildAt(i));
          if (hasNewHeader(position)) {
            // this header is different than the previous, it must be drawn in the correct place
            translationX = 0;
            translationY = 0;
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
  }

  /**
   * Returns the first item currently in the recyclerview that's not obscured by a header.
   * @param parent
   * @return
   */
  private View getNextView(RecyclerView parent) {
    View firstView = parent.getChildAt(0);
    // draw the first visible child's header at the top of the view
    int firstPosition = parent.getChildPosition(firstView);
    View firstHeader = getHeaderView(parent, firstPosition);
    for (int i = 0; i < parent.getChildCount(); i++) {
      if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
        if (parent.getChildAt(i).getTop() > firstHeader.getHeight()) {
          return parent.getChildAt(i);
        }
      } else {
        if (parent.getChildAt(i).getLeft() > firstHeader.getWidth()) {
          return parent.getChildAt(i);
        }
      }
    }
    return null;
  }

  private int getOrientation(RecyclerView parent) {
    if (parent.getLayoutManager() instanceof LinearLayoutManager) {
      LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
      return layoutManager.getOrientation();
    } else {
      throw new IllegalStateException("StickyListHeadersDecoration can only be used with a " +
          "LinearLayoutManager.");
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
    long headerId = mAdapter.getHeaderId(position);

    View header = mHeaderViews.get(headerId);
    if (header == null) {
      //TODO - recycle views
      RecyclerView.ViewHolder viewHolder = mAdapter.onCreateHeaderViewHolder(parent);
      mAdapter.onBindHeaderViewHolder(viewHolder, position);
      header = viewHolder.itemView;
      if (header.getLayoutParams() == null) {
        header.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      }

      int widthSpec;
      int heightSpec;

      if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
        widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);
      } else {
        widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.UNSPECIFIED);
        heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.EXACTLY);
      }

      int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
          parent.getPaddingLeft() + parent.getPaddingRight(), header.getLayoutParams().width);
      int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
          parent.getPaddingTop() + parent.getPaddingBottom(), header.getLayoutParams().height);
      header.measure(childWidth, childHeight);
      header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
      mHeaderViews.put(headerId, header);
    }
    return header;
  }

  private boolean hasNewHeader(int position) {
    if (position >= 0 && position < mAdapter.getItemCount()) {
      return position == 0 || mAdapter.getHeaderId(position) != mAdapter.getHeaderId(position - 1);
    } else {
      return false;
    }
  }
}
