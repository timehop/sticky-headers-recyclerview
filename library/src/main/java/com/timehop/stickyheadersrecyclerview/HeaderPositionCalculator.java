package com.timehop.stickyheadersrecyclerview;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.timehop.stickyheadersrecyclerview.caching.HeaderProvider;
import com.timehop.stickyheadersrecyclerview.util.OrientationProvider;

/**
 * Calculates the position and location of header views
 */
public class HeaderPositionCalculator {

  private final StickyRecyclerHeadersAdapter mAdapter;
  private final OrientationProvider mOrientationProvider;
  private final HeaderProvider mHeaderProvider;

  public HeaderPositionCalculator(StickyRecyclerHeadersAdapter adapter,
                                  HeaderProvider headerProvider,
                                  OrientationProvider orientationProvider) {
    mAdapter = adapter;
    mHeaderProvider = headerProvider;
    mOrientationProvider = orientationProvider;
  }

  /**
   * Determines if an item in the list should have a header that is different than the item in the
   * list that immediately precedes it. Items with no headers will always return false.
   * @see {@link StickyRecyclerHeadersAdapter#getHeaderId(int)}
   * @param position of the list item in questions
   * @return true if this item has a different header than the previous item in the list
   */
  public boolean hasNewHeader(int position) {
    if (getFirstHeaderPosition() == position) {
      return true;
    }

    if (mAdapter.getHeaderId(position) < 0 || indexOutOfBounds(position)) {
      return false;
    }

    return mAdapter.getHeaderId(position) != mAdapter.getHeaderId(position - 1);
  }

  private boolean indexOutOfBounds(int position) {
    return position < 0 || position >= mAdapter.getItemCount();
  }

  private int getFirstHeaderPosition() {
    for (int i = 0; i < mAdapter.getItemCount(); i++) {
      if (mAdapter.getHeaderId(i) >= 0) {
        return i;
      }
    }
    return -1;
  }

  public Rect getStickyHeaderBounds(RecyclerView recyclerView, View header, View firstView) {

    Rect bounds =
        getTopHeaderOffset(header, firstView, mOrientationProvider.getOrientation(recyclerView));

    if (!isStickyHeaderFullyVisible(recyclerView, header)) {
      View viewAfterHeader = getFirstViewUnobscuredByHeader(recyclerView, header);
      int firstViewUnderHeaderPosition = recyclerView.getChildPosition(viewAfterHeader);
      View secondHeader = mHeaderProvider.getHeader(recyclerView, firstViewUnderHeaderPosition);
      translateHeaderWithNextHeader(mOrientationProvider.getOrientation(recyclerView), bounds,
          header, viewAfterHeader, secondHeader);
    }

    return bounds;
  }

  private Rect getTopHeaderOffset(View header, View firstView, int orientation) {
    // Math.max is used here because if the left or top of a child is offscreen, we don't want the
    // sticky header to be drawn offscreen. The left and top are important for taking into account
    // layout parameters of the recyclerView (i.e. padding)
    int translationX, translationY;
    if (orientation == LinearLayoutManager.VERTICAL) {
      translationX = firstView.getLeft();
      translationY = Math.max(firstView.getTop() - header.getHeight(), 0);
    } else {
      translationY = firstView.getTop();
      translationX = Math.max(firstView.getLeft() - header.getWidth(), 0);
    }

    return new Rect(translationX, translationY, translationX + header.getWidth(),
        translationY + header.getHeight());
  }

  private boolean isStickyHeaderFullyVisible(RecyclerView recyclerView, View stickyHeader) {
    View viewAfterHeader = getFirstViewUnobscuredByHeader(recyclerView, stickyHeader);
    int firstViewUnderHeaderPosition = recyclerView.getChildPosition(viewAfterHeader);

    if (firstViewUnderHeaderPosition > 0 && hasNewHeader(firstViewUnderHeaderPosition)) {
      return false;
    }

    return true;
  }

  private void translateHeaderWithNextHeader(int orientation, Rect translation, View currentHeader, View viewAfterHeader, View nextHeader) {
    //Translate the topmost header so the next header takes its place, if applicable
    if (orientation == LinearLayoutManager.VERTICAL &&
        viewAfterHeader.getTop() - nextHeader.getHeight() - currentHeader.getHeight() < 0) {
      translation.top += viewAfterHeader.getTop() - nextHeader.getHeight() - currentHeader.getHeight();
    } else if (orientation == LinearLayoutManager.HORIZONTAL &&
        viewAfterHeader.getLeft() - nextHeader.getWidth() - currentHeader.getWidth() < 0) {
      translation.left += viewAfterHeader.getLeft() - nextHeader.getWidth() - currentHeader.getWidth();
    }
  }

  /**
   * Returns the first item currently in the RecyclerView that is not obscured by a header.
   *
   * @param parent Recyclerview containing all the list items
   * @return first item that is fully beneath a header
   */
  private View getFirstViewUnobscuredByHeader(RecyclerView parent, View firstHeader) {
    for (int i = 0; i < parent.getChildCount(); i++) {
      View child = parent.getChildAt(i);
      if (!itemIsObscuredByHeader(child, firstHeader, mOrientationProvider.getOrientation(parent))) {
        return child;
      }
    }
    return null;
  }

  /**
   * Determines if an item is obscured by a header
   *
   * @param item        to determine if obscured by header
   * @param header      that might be obscuring the item
   * @param orientation of the {@link RecyclerView}
   * @return true if the item view is obscured by the header view
   */
  private boolean itemIsObscuredByHeader(View item, View header, int orientation) {
    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
    if (orientation == LinearLayoutManager.VERTICAL) {
      if (item.getTop() - layoutParams.topMargin > header.getHeight()) {
        return false;
      }
    } else {
      if (item.getLeft() - layoutParams.leftMargin > header.getWidth()) {
        return false;
      }
    }

    return true;
  }

}
