package com.timehop.stickyheadersrecyclerview;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.sample.R;


public class MainActivity extends Activity {

  private RecyclerView mRecyclerView;
  private DividerDecoration mDividersDecor;
  private StickyRecyclerHeadersDecoration mHeadersDecor;
  private SampleArrayHeadersAdapter mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    mAdapter = new SampleArrayHeadersAdapter();
    String[] animals = getResources().getStringArray(R.array.animals);
    mAdapter.addAll(animals);
    mRecyclerView.setAdapter(mAdapter);
    setUpRecycler();
  }

  private void setUpRecycler() {
    int orientation;
    if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
      orientation = LinearLayoutManager.VERTICAL;
    } else {
      orientation = LinearLayoutManager.HORIZONTAL;
    }
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this, orientation, false));
    if (mDividersDecor != null) {
      mRecyclerView.removeItemDecoration(mDividersDecor);
    }
    if (mHeadersDecor != null) {
      mRecyclerView.removeItemDecoration(mHeadersDecor);
    }
    mDividersDecor = new DividerDecoration(this);
    mHeadersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
    mRecyclerView.addItemDecoration(mDividersDecor);
    mRecyclerView.addItemDecoration(mHeadersDecor);
  }

  private class SampleArrayHeadersAdapter extends RecyclerArrayAdapter<String, RecyclerView.ViewHolder>
      implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_item, parent, false);
      return new RecyclerView.ViewHolder(view) { };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      TextView textView = (TextView) holder.itemView;
      textView.setText(getItem(position));
    }

    @Override
    public long getHeaderId(int position) {
      return getItem(position).charAt(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_header, parent, false);
      return new RecyclerView.ViewHolder(view) { };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
      TextView textView = (TextView) holder.itemView;
      textView.setText(String.valueOf(getItem(position).charAt(0)));
    }
  }

}
