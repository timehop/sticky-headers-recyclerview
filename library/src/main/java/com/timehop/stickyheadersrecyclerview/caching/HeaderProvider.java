package com.timehop.stickyheadersrecyclerview.caching;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Implemented by objects that provide header views for decoration
 */
public interface HeaderProvider {

  /**
   * Will provide a header view for a given position in the RecyclerView
   *
   * @param recyclerView that will display the header
   * @param position     that will be headed by the header
   * @return a header view for the given position and list
   */
  public View getHeader(RecyclerView recyclerView, int position);

  /**
   * TODO: describe this functionality and its necessity
   */
  void invalidate();
}
