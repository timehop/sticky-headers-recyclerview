package com.timehop.stickyheadersrecyclerview.rendering;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.timehop.stickyheadersrecyclerview.HeaderPositionCalculator;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.caching.HeaderProvider;
import com.timehop.stickyheadersrecyclerview.util.OrientationProvider;

/**
 * {@link Please describe HeaderRenderer!}
 */
public class HeaderRenderer {

  public void drawStickyHeader(Canvas canvas, View header, Rect offset) {
    canvas.save();
    canvas.translate(offset.left, offset.top);
    header.draw(canvas);
    canvas.restore();
  }


}
