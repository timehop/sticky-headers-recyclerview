package com.timehop.stickyheadersrecyclerview.caching;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.util.OrientationProvider;

/**
 * An implementation of {@link HeaderProvider} that creates one header view managed by a
 * {@link android.support.v7.widget.RecyclerView.ViewHolder}. Each call to
 * {@link HeaderViewCache#getHeader(RecyclerView, int)}  updates the header to the specified
 * position with
 * {@link StickyRecyclerHeadersAdapter#onBindHeaderViewHolder(RecyclerView.ViewHolder, int)}.
 *
 * If a ViewHolder/View does not exist, it will be created, measured, and inflated.
 */
public class HeaderViewCache implements HeaderProvider {

  private final StickyRecyclerHeadersAdapter mAdapter;
  private RecyclerView.ViewHolder mViewHolder;
  private final OrientationProvider mOrientationProvider;

  public HeaderViewCache(StickyRecyclerHeadersAdapter adapter,
      OrientationProvider orientationProvider) {
    mAdapter = adapter;
    mOrientationProvider = orientationProvider;
  }

  @Override
  public View getHeader(RecyclerView parent, int position) {
    if (mViewHolder == null) {
      // Create the ViewHolder if it doesn't exist and bind values to it
      mViewHolder = mAdapter.onCreateHeaderViewHolder(parent);
      mAdapter.onBindHeaderViewHolder(mViewHolder, position);
      View header = mViewHolder.itemView;

      if (header.getLayoutParams() == null) {
        header.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      }

      int widthSpec;
      int heightSpec;

      if (mOrientationProvider.getOrientation(parent) == LinearLayoutManager.VERTICAL) {
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
    } else {
        mAdapter.onBindHeaderViewHolder(mViewHolder, position);
    }

    return mViewHolder.itemView;
  }

  @Override
  public void invalidate() {
  }
}
