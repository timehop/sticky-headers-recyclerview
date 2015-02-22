package com.timehop.stickyheadersrecyclerview.caching;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * {@link Please describe HeaderProvider!}
 */
public interface HeaderProvider {

  /**
   *
   * @return
   */
  public View getHeader(RecyclerView recyclerView, int position);

  void invalidate();
}
