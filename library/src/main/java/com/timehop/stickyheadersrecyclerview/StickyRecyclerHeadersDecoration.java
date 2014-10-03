package com.timehop.stickyheadersrecyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

class StickyRecyclerHeadersDecoration extends RecyclerView.ItemDecoration {
  private final StickyRecyclerHeadersAdapter mAdapter;
  private final LongSparseArray<View> mHeaderViews = new LongSparseArray<>();

  public StickyRecyclerHeadersDecoration(StickyRecyclerHeadersAdapter adapter) {
    mAdapter = adapter;
  }

  @Override
  public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
    int orientation = getOrientation(parent);
    if (hasNewHeader(itemPosition)) {
      View header = getHeader(parent, itemPosition);
      if (orientation == LinearLayoutManager.VERTICAL) {
        outRect.top = header.getHeight();
      } else {
        outRect.left = header.getWidth();
      }
    }
  }

  @Override
  public void onDrawOver(Canvas canvas, RecyclerView parent) {
    super.onDrawOver(canvas, parent);
    int orientation = getOrientation(parent);

    if (parent.getChildCount() > 0) {
      View firstView = parent.getChildAt(0);
      // draw the first visible child's header at the top of the view
      int firstPosition = parent.getChildPosition(firstView);
      View firstHeader = getHeader(parent, firstPosition);
      canvas.save();
      View nextView = getNextView(parent);
      int nextPosition = parent.getChildPosition(nextView);
      if (nextPosition != -1 && hasNewHeader(nextPosition)) {
        View secondHeader = getHeader(parent, nextPosition);
        //Translate the topmost header so the next header takes its place, if applicable
        if (orientation == LinearLayoutManager.VERTICAL &&
            nextView.getTop() - secondHeader.getHeight() - firstHeader.getHeight() < 0) {
          canvas.translate(0, nextView.getTop() - secondHeader.getHeight() - firstHeader.getHeight());
        } else if (orientation == LinearLayoutManager.HORIZONTAL &&
            nextView.getLeft() - secondHeader.getWidth() - firstHeader.getWidth() < 0) {
          canvas.translate(nextView.getLeft() - secondHeader.getWidth() - firstHeader.getWidth(), 0);
        }
      }
      firstHeader.draw(canvas);
      canvas.restore();

      if (parent.getChildCount() > 1)
        for (int i = 1; i < parent.getChildCount(); i++) {
          int position = parent.getChildPosition(parent.getChildAt(i));
          if (hasNewHeader(position)) {
            // this header is different than the previous, it must be drawn in the correct place
            View header = getHeader(parent, position);
            canvas.save();
            if (orientation == LinearLayoutManager.VERTICAL) {
              canvas.translate(0, parent.getChildAt(i).getTop() - header.getHeight());
            } else {
              canvas.translate(parent.getChildAt(i).getLeft() - header.getWidth(), 0);
            }
            header.draw(canvas);
            canvas.restore();
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
    View firstHeader = getHeader(parent, firstPosition);
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

  private View getHeader(RecyclerView parent, int position) {
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
    return position == 0 || mAdapter.getHeaderId(position) != mAdapter.getHeaderId(position - 1);
  }
}
