package com.timehop.stickyheadersrecyclerview.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * OrientationProvider for ReyclerViews who use a LinearLayoutManager
 */
public class LinearLayoutOrientationProvider implements OrientationProvider {

  @Override
  public int getOrientation(RecyclerView recyclerView) {
    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
    throwIfNotLinearLayoutManager(layoutManager);
    return ((LinearLayoutManager) layoutManager).getOrientation();
  }

  @Override
  public boolean isReverseLayout(RecyclerView recyclerView) {
    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
    throwIfNotLinearLayoutManager(layoutManager);
    return ((LinearLayoutManager) layoutManager).getReverseLayout();
  }

  private void throwIfNotLinearLayoutManager(RecyclerView.LayoutManager layoutManager){
    if (!(layoutManager instanceof LinearLayoutManager)) {
      throw new IllegalStateException("StickyListHeadersDecoration can only be used with a " +
          "LinearLayoutManager.");
    }
  }
}
