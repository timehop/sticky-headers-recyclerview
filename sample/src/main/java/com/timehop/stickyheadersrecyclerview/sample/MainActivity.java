package com.timehop.stickyheadersrecyclerview.sample;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import java.security.SecureRandom;
import java.util.HashMap;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

    // Set adapter populated with example dummy data
    final SampleArrayHeadersAdapter mAdapter = new SampleArrayHeadersAdapter();
    mAdapter.add("Animals below!");
    mAdapter.addAll(getDummyDataSet());
    recyclerView.setAdapter(mAdapter);

    // Set layout manager
    int orientation = getLayoutManagerOrientation(getResources().getConfiguration().orientation);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this, orientation, false);
    recyclerView.setLayoutManager(layoutManager);

    // Add the sticky headers decoration
    final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
    recyclerView.addItemDecoration(headersDecor);

    // Add decoration for dividers between list items
    recyclerView.addItemDecoration(new DividerDecoration(this));

    // Add touch listeners
    StickyRecyclerHeadersTouchListener touchListener =
        new StickyRecyclerHeadersTouchListener(recyclerView, headersDecor);
    touchListener.setOnHeaderClickListener(
        new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
          @Override
          public void onHeaderClick(View header, int position, long headerId) {
            Toast.makeText(MainActivity.this, "Header position: " + position + ", id: " + headerId,
                Toast.LENGTH_SHORT).show();
          }
        });
    recyclerView.addOnItemTouchListener(touchListener);
    recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
      @Override
      public void onItemClick(View view, int position) {
        mAdapter.remove(mAdapter.getItem(position));
      }
    }));
    mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override public void onChanged() {
        headersDecor.invalidateHeaders();
      }
    });
  }

  private String[] getDummyDataSet() {
    return getResources().getStringArray(R.array.animals);
  }

  private int getLayoutManagerOrientation(int activityOrientation) {
    if (activityOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
      return LinearLayoutManager.VERTICAL;
    } else {
      return LinearLayoutManager.HORIZONTAL;
    }
  }

  private class SampleArrayHeadersAdapter extends RecyclerArrayAdapter<String, RecyclerView.ViewHolder>
      implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    HashMap<Long, Integer> mBackgroundColorMap = new HashMap<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_item, parent, false);
      return new RecyclerView.ViewHolder(view) {
      };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      TextView textView = (TextView) holder.itemView;
      textView.setText(getItem(position));
    }

    @Override
    public long getHeaderId(int position) {
      // Don't count first row when drawing headers; it's set to "Animals below!"
      if (position == 0) {
        return -1;
      } else {
        return getItem(position).charAt(0);
      }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_header, parent, false);
      return new RecyclerView.ViewHolder(view) {
      };
    }

    // TODO: consider changing onBindHeaderViewHolder interface to take headerId instead of position to match the consistency with onBindViewHolder
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
      TextView textView = (TextView) holder.itemView;
      textView.setText(getItem(position));

      // Get the color mapped to the header Id
      long headerId = getHeaderId(position);
      Integer backgroundColor = mBackgroundColorMap.get(headerId);

      if (backgroundColor == null) {
        backgroundColor = getRandomColor();
        mBackgroundColorMap.put(headerId, backgroundColor);
      }

      textView.setBackgroundColor(backgroundColor);
    }

    private int getRandomColor() {
      SecureRandom rgen = new SecureRandom();
      return Color.HSVToColor(150, new float[]{
          rgen.nextInt(359), 1, 1
      });
    }

  }
}
