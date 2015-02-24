package com.timehop.stickyheadersrecyclerview.rendering;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.timehop.stickyheadersrecyclerview.calculation.DimensionCalculator;

/**
 * Responsible for drawing headers to the canvas provided by the item decoration
 */
public class HeaderRenderer {

  private final DimensionCalculator mDimensionCalculator;

  public HeaderRenderer() {
    this(new DimensionCalculator());
  }

  private HeaderRenderer(DimensionCalculator dimensionCalculator) {
    mDimensionCalculator = dimensionCalculator;
  }

  /**
   * Draws a header to a canvas, offsetting by some x and y amount
   * @param recyclerView the parent recycler view for drawing the header into
   * @param canvas the canvas on which to draw the header
   * @param header the view to draw as the header
   * @param offset a Rect used to define the x/y offset of the header. Specify x/y offset by setting
   *               the {@link Rect#left} and {@link Rect#top} properties, respectively.
   */
  public void drawHeader(RecyclerView recyclerView, Canvas canvas, View header, Rect offset) {
    canvas.save();

    Rect recyclerMargins = mDimensionCalculator.getMargins(recyclerView);
    // Clip drawing of headers to the padding of the RecyclerView. Avoids drawing in the padding
    if (recyclerView.getLayoutManager().getClipToPadding()) {
      Rect clipRect = new Rect(
          recyclerView.getLeft() - recyclerMargins.left + recyclerView.getPaddingLeft(),
          recyclerView.getTop() - recyclerMargins.top + recyclerView.getPaddingTop(),
          recyclerView.getRight() - recyclerMargins.right - recyclerView.getPaddingRight(),
          recyclerView.getBottom() - recyclerMargins.bottom - recyclerView.getPaddingBottom());
      canvas.clipRect(clipRect);
    }

    canvas.translate(offset.left, offset.top);

    header.draw(canvas);
    canvas.restore();
  }

}
