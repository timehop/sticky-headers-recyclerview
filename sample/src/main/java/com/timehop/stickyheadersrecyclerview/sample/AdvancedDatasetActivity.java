package com.timehop.stickyheadersrecyclerview.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.sample.AnimalsAdapter;
import com.timehop.stickyheadersrecyclerview.sample.DividerDecoration;
import com.timehop.stickyheadersrecyclerview.sample.R;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class AdvancedDatasetActivity extends Activity {
    final Map<String, String> mAnimalColors = new HashMap<>();
    final AnimalsColorHeadersAdapter mAdapter = new AnimalsColorHeadersAdapter();

    final String COLORNAME_BLACK = "Black";
    final String COLORNAME_BLUE = "Blue";
    final String COLORNAME_BROWN = "Brown";
    final String COLORNAME_OTHER = "Other";

    Comparator<String> mColorComparator = new Comparator<String>() {
        @Override
        public int compare(String lhsName, String rhsName) {
            // order by (color, name)
            String lhsColor = getColorForAnimalName(lhsName);
            String rhsColor = getColorForAnimalName(rhsName);

            if (lhsColor.compareTo(rhsColor) < 0) {
                // lhsColor < rhsColor
                return -1;
            } else if (lhsColor.compareTo(rhsColor) > 0) {
                // lhsColor > rhsColor
                return 1;
            } else {
                // color is equal
                return lhsName.compareTo(rhsName);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        // Add initial data to color map
        mAnimalColors.put("Aye Aye", COLORNAME_BROWN);
        mAnimalColors.put("Black Bear", COLORNAME_BLACK);
        mAnimalColors.put("Black Rhinoceros", COLORNAME_BLACK);
        mAnimalColors.put("Black Russian Terrier", COLORNAME_BLACK);
        mAnimalColors.put("Black Widow Spider", COLORNAME_BLACK);
        mAnimalColors.put("Blue Lacy Dog", COLORNAME_BLUE);
        mAnimalColors.put("Blue Whale", COLORNAME_BLUE);
        mAnimalColors.put("Highland Cattle", COLORNAME_BROWN);

        // Populate adapter with example dummy data
        mAdapter.addAll(getDummyDataSet());
        mAdapter.sort(mColorComparator);
        recyclerView.setAdapter(mAdapter);

        // Set layout manager
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        recyclerView.addItemDecoration(headersDecor);

        // Add decoration for dividers between list items
        recyclerView.addItemDecoration(new DividerDecoration(this));

        /*
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                //headersDecor.invalidateHeaders();
            }
        });
        */
    }

    private String[] getDummyDataSet() {
        return getResources().getStringArray(R.array.animals);
    }

    private String getColorForAnimalName(String animalName) {
        if (mAnimalColors.containsKey(animalName)) {
            return mAnimalColors.get(animalName);
        } else {
            return COLORNAME_OTHER;
        }
    }

    public void toggleAyeAyeColorClicked(View v) {
        if (mAnimalColors.get("Aye Aye").equals(COLORNAME_BROWN)) {
            mAnimalColors.put("Aye Aye", COLORNAME_BLACK);
        } else {
            mAnimalColors.put("Aye Aye", COLORNAME_BROWN);
        }
        mAdapter.sort(mColorComparator);
    }

    private class AnimalsColorHeadersAdapter extends AnimalsAdapter<RecyclerView.ViewHolder>
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
            String colorName = getColorForAnimalName(getItem(position));
            return colorName.hashCode();
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
            String colorName = getColorForAnimalName(getItem(position));
            TextView textView = (TextView) holder.itemView;
            textView.setText(colorName);
        }
    }
}
