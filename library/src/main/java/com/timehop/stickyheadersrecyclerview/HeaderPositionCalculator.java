package com.timehop.stickyheadersrecyclerview;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.timehop.stickyheadersrecyclerview.caching.HeaderProvider;
import com.timehop.stickyheadersrecyclerview.util.OrientationProvider;

import static android.view.ViewGroup.MarginLayoutParams;

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
   *
   * @param position of the list item in questions
   * @return true if this item has a different header than the previous item in the list
   * @see {@link StickyRecyclerHeadersAdapter#getHeaderId(int)}
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

  public Rect getHeaderBounds(RecyclerView recyclerView, View header, View firstView, boolean firstHeader) {
    int orientation = mOrientationProvider.getOrientation(recyclerView);
    Rect bounds = getDefaultHeaderOffset(recyclerView, header, firstView, orientation);

    if (firstHeader && isStickyHeaderBeingPushedOffscreen(recyclerView, header)) {
      View viewAfterNextHeader = getFirstViewUnobscuredByHeader(recyclerView, header);
      int firstViewUnderHeaderPosition = recyclerView.getChildPosition(viewAfterNextHeader);
      View secondHeader = mHeaderProvider.getHeader(recyclerView, firstViewUnderHeaderPosition);
      translateHeaderWithNextHeader(recyclerView, mOrientationProvider.getOrientation(recyclerView), bounds,
          header, viewAfterNextHeader, secondHeader);
    }

    return bounds;
  }

  private Rect getDefaultHeaderOffset(RecyclerView recyclerView, View header, View firstView, int orientation) {
    int translationX, translationY;
    if (orientation == LinearLayoutManager.VERTICAL) {
      translationX = firstView.getLeft();
      translationY = Math.max(firstView.getTop() - header.getHeight(), getListTop(recyclerView));
    } else {
      translationY = firstView.getTop();
      translationX = Math.max(firstView.getLeft() - header.getWidth(), getListLeft(recyclerView));
    }

    return new Rect(translationX, translationY, translationX + header.getWidth(),
        translationY + header.getHeight());
  }

  private boolean isStickyHeaderBeingPushedOffscreen(RecyclerView recyclerView, View stickyHeader) {
    View viewAfterHeader = getFirstViewUnobscuredByHeader(recyclerView, stickyHeader);
    int firstViewUnderHeaderPosition = recyclerView.getChildPosition(viewAfterHeader);

    if (firstViewUnderHeaderPosition > 0 && hasNewHeader(firstViewUnderHeaderPosition)) {
      View nextHeader = mHeaderProvider.getHeader(recyclerView, firstViewUnderHeaderPosition);
      if (mOrientationProvider.getOrientation(recyclerView) == LinearLayoutManager.VERTICAL) {
        if (viewAfterHeader.getTop() - nextHeader.getHeight() < getListTop(recyclerView) + stickyHeader.getHeight()) {
          return true;
        }
      } else {
        if (viewAfterHeader.getLeft() - nextHeader.getWidth() < getListLeft(recyclerView) + stickyHeader.getWidth()) {
          return true;
        }
      }
    }

    return false;
  }

  private void translateHeaderWithNextHeader(RecyclerView recyclerView, int orientation, Rect translation, View currentHeader, View viewAfterNextHeader, View nextHeader) {
    //Translate the topmost header so the next header takes its place, if applicable
    if (orientation == LinearLayoutManager.VERTICAL) {
      int topOfList = getListTop(recyclerView);
      int shiftFromNextHeader = viewAfterNextHeader.getTop() - nextHeader.getHeight() - currentHeader.getHeight() - topOfList;
      if (shiftFromNextHeader < topOfList) {
        translation.top += shiftFromNextHeader;
      }
    } else {
      int leftOfList = getListLeft(recyclerView);
      int shiftFromNextHeader = viewAfterNextHeader.getLeft() - nextHeader.getWidth() - currentHeader.getWidth() - leftOfList;
      if (shiftFromNextHeader < leftOfList) {
        translation.left += shiftFromNextHeader;
      }
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
      if (item.getTop() - layoutParams.topMargin > header.getBottom()) {
        return false;
      }
    } else {
      if (item.getLeft() - layoutParams.leftMargin > header.getRight()) {
        return false;
      }
    }

    return true;
  }

  private int getListTop(RecyclerView view) {
    MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
    int top = view.getTop() - layoutParams.topMargin;

    if (view.getLayoutManager().getClipToPadding()) {
      top += view.getPaddingTop();
    }

    return top;
  }

  private int getListLeft(RecyclerView view) {
    MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
    int left = view.getLeft() - layoutParams.leftMargin;

    if (view.getLayoutManager().getClipToPadding()) {
      left += view.getPaddingLeft();
    }

    return left;
  }

}
