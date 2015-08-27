package com.timehop.stickyheadersrecyclerview.rendering;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.timehop.stickyheadersrecyclerview.calculation.DimensionCalculator;
import com.timehop.stickyheadersrecyclerview.util.OrientationProvider;

/**
 * Responsible for drawing headers to the canvas provided by the item decoration
 */
public class HeaderRenderer {

  private final DimensionCalculator mDimensionCalculator;
  private final OrientationProvider mOrientationProvider;

  public HeaderRenderer(OrientationProvider orientationProvider) {
    this(orientationProvider, new DimensionCalculator());
  }

  private HeaderRenderer(OrientationProvider orientationProvider,
      DimensionCalculator dimensionCalculator) {
    mOrientationProvider = orientationProvider;
    mDimensionCalculator = dimensionCalculator;
  }

  /**
   * Draws a header to a canvas, offsetting by some x and y amount
   *
   * @param recyclerView the parent recycler view for drawing the header into
   * @param canvas       the canvas on which to draw the header
   * @param header       the view to draw as the header
   * @param offset       a Rect used to define the x/y offset of the header. Specify x/y offset by setting
   *                     the {@link Rect#left} and {@link Rect#top} properties, respectively.
   */
  public void drawHeader(RecyclerView recyclerView, Canvas canvas, View header, Rect offset) {
    canvas.save();

    // For sdk < 14 need to clip regardless because of full screen
    // header strangeness
    if (Build.VERSION.SDK_INT < 14 || recyclerView.getLayoutManager().getClipToPadding()) {
      // Clip drawing of headers to the padding of the RecyclerView. Avoids drawing in the padding
      Rect clipRect = getClipRectForHeader(recyclerView, header, offset);
      canvas.clipRect(clipRect);
    }

    canvas.translate(offset.left, offset.top);

    header.draw(canvas);
    canvas.restore();
  }

   /**
   * Gets a clipping rect for the header based on the margins of the header and the padding of the
   * recycler.
   * FIXME: Currently right margin in VERTICAL orientation and bottom margin in HORIZONTAL
   * orientation are clipped so they look accurate, but the headers are not being drawn at the
   * correctly smaller width and height respectively.
   *
   * @param recyclerView for which to provide a header
   * @param header       for clipping
   * @param offset       for clipping specific view offset rather than entire recyclerview
   * @return a {@link Rect} for clipping a provided header to the padding of a recycler view
   */
  private Rect getClipRectForHeader(RecyclerView recyclerView, View header, Rect offset) {
    Rect headerMargins = mDimensionCalculator.getMargins(header);
    
    // If sdk > 14 clip to recyclerview (What it was doing previously)
    if (Build.VERSION.SDK_INT > 14) {
      if (mOrientationProvider.getOrientation(recyclerView) == LinearLayout.VERTICAL) {
        return new Rect(
              recyclerView.getPaddingLeft(),
              recyclerView.getPaddingTop(),
              recyclerView.getWidth() - recyclerView.getPaddingRight() - headerMargins.right,
              recyclerView.getHeight() - recyclerView.getPaddingBottom());
      } else {
        return new Rect(
              recyclerView.getPaddingLeft(),
              recyclerView.getPaddingTop(),
              recyclerView.getWidth() - recyclerView.getPaddingRight(),
              recyclerView.getHeight() - recyclerView.getPaddingBottom() - headerMargins.bottom);
      }
    // If sdk < 14, do clipping which clips based on exact header size
    //and offset, instead of full size of recyclerview
    } else {
      if (mOrientationProvider.getOrientation(recyclerView) == LinearLayout.VERTICAL) {
  
        int offsetBottom = offset.top + header.getHeight();
        int recyclerBottom = recyclerView.getHeight() - recyclerView.getPaddingBottom();
  
        // If offsets are above or below no need for visibility
        if (offset.top <= recyclerView.getPaddingTop() && offsetBottom <= recyclerView.getPaddingTop()
                || offsetBottom >= recyclerBottom && offset.top >= recyclerBottom) {
            return new Rect(0, 0, 0, 0);
  
        // If topOffset within top padding and bottomOffset NOT within top padding
        } else if (offset.top < recyclerView.getPaddingTop() && offsetBottom > recyclerView.getPaddingTop()) {
                return new Rect(
                        recyclerView.getPaddingLeft(),
                        recyclerView.getPaddingTop(),
                        recyclerView.getWidth() - recyclerView.getPaddingRight() - headerMargins.right,
                        offsetBottom);
  
        // If offsetBottom within bottom padding and offsetTop NOT within bottom padding
        } else if (offsetBottom > recyclerBottom && offset.top < recyclerBottom) {
            return new Rect(
                    recyclerView.getPaddingLeft(),
                    offset.top,
                    recyclerView.getWidth() - recyclerView.getPaddingRight() - headerMargins.right,
                    recyclerBottom);
  
        // Else use offset for all clips
        } else {
            return new Rect(
                    recyclerView.getPaddingLeft(),
                    offset.top,
                    recyclerView.getWidth() - recyclerView.getPaddingRight() - headerMargins.right,
                    offset.top + header.getHeight());
        }
        
      } else {
        
        int offsetRight = offset.left + header.getWidth();
        int recyclerRight = recyclerView.getWidth() - recyclerView.getPaddingRight();
        
         // If offsets are both to right or left, no need for visibility
        if (offset.left <= recyclerView.getPaddingLeft() && offsetRight <= recyclerView.getPaddingLeft()
                || offsetRight >= recyclerRight && offset.left >= recyclerRight) {
            return new Rect(0, 0, 0, 0);
  
        // If offsetLeft within paddingLeft and offsetRight NOT within paddingLeft
        } else if (offset.left < recyclerView.getPaddingLeft() && offsetRight > recyclerView.getPaddingLeft()) {
            return new Rect(
                    recyclerView.getPaddingLeft(),
                    recyclerView.getPaddingTop(),
                    offsetRight,
                    recyclerView.getHeight() - recyclerView.getPaddingBottom() - headerMargins.bottom);
  
        // If offsetRight within paddingRight and offsetLeft NOT within paddingRight
        } else if (offsetRight > recyclerRight && offset.left < recyclerRight) {
            return new Rect(
                    offset.left,
                    recyclerView.getPaddingTop(),
                    recyclerView.getWidth() - recyclerView.getPaddingRight(),
                    recyclerView.getHeight() - recyclerView.getPaddingBottom()- headerMargins.bottom);
  
        // Else use offset for all clips
        } else {
            return new Rect(
                    offset.left,
                    recyclerView.getPaddingTop(),
                    offsetRight,
                    recyclerView.getHeight() - recyclerView.getPaddingBottom() - headerMargins.bottom);
        }
      }
    }
  }
}
