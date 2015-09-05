package com.timehop.stickyheadersrecyclerview.sample;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import java.security.SecureRandom;

public class MainActivity extends Activity {

  public static final String ANIMALS_BELOW = "Animals below!";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    Button button = (Button) findViewById(R.id.button_update);
    Button buttonAdd = (Button) findViewById(R.id.button_add);
    final ToggleButton isReverseButton = (ToggleButton) findViewById(R.id.button_is_reverse);

    // Set adapter populated with example dummy data
    final SampleArrayHeadersAdapter mAdapter = new SampleArrayHeadersAdapter();
    mAdapter.add("Animals below!");
    mAdapter.addAll(getDummyDataSet());
    recyclerView.setAdapter(mAdapter);

    // Set button to update all views one after another (Test for the "Dance")
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Handler handler = new Handler(Looper.getMainLooper());
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
          final int index = i;
          handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              mAdapter.notifyItemChanged(index);
            }
          }, 50);
        }
      }
    });

    // Add/remove item to the top of list (Test animations)
      buttonAdd.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (ANIMALS_BELOW.equals(mAdapter.getItem(0))) {
                  mAdapter.remove(ANIMALS_BELOW);
              } else {
                  mAdapter.add(0, ANIMALS_BELOW);
              }
          }
      });

    // Set layout manager
    int orientation = getLayoutManagerOrientation(getResources().getConfiguration().orientation);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this, orientation, isReverseButton.isChecked());
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
      @Override
      public void onChanged() {
        headersDecor.invalidateHeaders();
      }
    });

    isReverseButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        boolean isChecked = isReverseButton.isChecked();
        isReverseButton.setChecked(isChecked);
        layoutManager.setReverseLayout(isChecked);
        mAdapter.notifyDataSetChanged();
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

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
      TextView textView = (TextView) holder.itemView;
      textView.setText(String.valueOf(getItem(position).charAt(0)));
      holder.itemView.setBackgroundColor(getRandomColor());
    }

    private int getRandomColor() {
      SecureRandom rgen = new SecureRandom();
      return Color.HSVToColor(150, new float[]{
          rgen.nextInt(359), 1, 1
      });
    }

  }
}
