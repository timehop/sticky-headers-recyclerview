package com.timehop.stickyheadersrecyclerview.sample;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import java.util.Collection;

public class MainActivity extends AppCompatActivity {

  private static final int NUM_COLUMNS = 3;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    Button button = (Button) findViewById(R.id.button_update);
    final ToggleButton isReverseButton = (ToggleButton) findViewById(R.id.button_is_reverse);

    // Set adapter populated with example dummy data
    final AnimalsHeadersAdapter adapter = new AnimalsHeadersAdapter();
    adapter.setNumColumns(NUM_COLUMNS);
//    adapter.add("Animals below!");
    adapter.addAll(getDummyDataSet());
    recyclerView.setAdapter(adapter);

    // Set button to update all views one after another (Test for the "Dance")
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Handler handler = new Handler(Looper.getMainLooper());
        for (int i = 0; i < adapter.getItemCount(); i++) {
          final int index = i;
          handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              adapter.notifyItemChanged(index);
            }
          }, 50);
        }
      }
    });

    // Set layout manager
    int orientation = getLayoutManagerOrientation(getResources().getConfiguration().orientation);
//    final LinearLayoutManager layoutManager = new LinearLayoutManager(this, orientation, isReverseButton.isChecked());

    final GridLayoutManager layoutManager = new GridLayoutManager(this, NUM_COLUMNS);
    recyclerView.setLayoutManager(layoutManager);

    // Add the sticky headers decoration
    final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
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
        adapter.remove(adapter.getItem(position));
      }
    }));
    adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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
        adapter.notifyDataSetChanged();
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

  private class AnimalsHeadersAdapter extends AnimalsAdapter<RecyclerView.ViewHolder>
      implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
      private static final String EMPTY_NAME = " ";
      private int numColumns = 1;

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
        int numColumnOfItem = position % numColumns;
        if (numColumnOfItem == 0) {
            return getFirstChar(getItem(position));
        } else {
            return getHeaderId(position - numColumnOfItem);
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

    @Override
    public int getNumColumns() {
      return numColumns;
    }

    public void setNumColumns(int numColumns) {
      this.numColumns = numColumns;
    }

      @Override
      public void add(int index, String object) {
          super.add(index, object);
          reorderItems();
      }

      @Override
      public void add(String object) {
          super.add(object);
          reorderItems();
      }

      @Override
      public void addAll(Collection<? extends String> collection) {
          super.addAll(collection);
          reorderItems();
      }

      @Override
      public void addAll(String... items) {
          super.addAll(items);
          reorderItems();
      }

      @Override
      public void remove(String object) {
          super.remove(object);
          reorderItems();
      }

      private void reorderItems() {
        long firstCharOnLastItem = -1;

        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i);
            if (getFirstChar(item) != firstCharOnLastItem) { // new header found for item
                int numColumnOfItem = i % numColumns;
                if (numColumnOfItem > 0 && !EMPTY_NAME.equals(item)) { // fill row with empty items
                    int emptyVideos = numColumns - numColumnOfItem;
                    for (int j = 0; j < emptyVideos; j++) {
                        items.add(i, EMPTY_NAME);
                        if (j != emptyVideos - 1) {
                            i++;
                        }
                    }
                    continue;
                } else if (numColumnOfItem == 0 && EMPTY_NAME.equals(item)){
                    // remove empty items to avoid empty rows when removing items
                    while (items.get(i).equals(EMPTY_NAME)) {
                        items.remove(i);
                    }
                    i--;
                }
                if (!EMPTY_NAME.equals(item)) {
                    firstCharOnLastItem = getFirstChar(item);
                }
            }
        }
      }

      private int getFirstChar(String name) {
          if (TextUtils.isEmpty(name)) {
              return 0;
          } else {
              return name.charAt(0);
          }
      }
  }
}
