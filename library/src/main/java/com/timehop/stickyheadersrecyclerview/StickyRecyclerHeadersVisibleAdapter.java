package com.timehop.stickyheadersrecyclerview;

/**
 * Created by gray on 2/12/16.
 */
public interface StickyRecyclerHeadersVisibleAdapter {

    /**
     *
     * Return true the specified adapter position is visible, false otherwise
     *
     * @param position the adapter position
     */
    boolean isPositionVisible(final int position);
}
