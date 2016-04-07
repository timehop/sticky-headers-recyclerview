This project is no longer being maintained
==========================================

sticky-headers-recyclerview
===========================

This decorator allows you to easily create section headers for RecyclerViews using a
LinearLayoutManager in either vertical or horizontal orientation.

Credit to [Emil Sjölander](https://github.com/emilsjolander) for creating StickyListHeaders,
a library that many of us relied on for sticky headers in our listviews.

Here is a quick video of it in action (click to see the full video):

[![animated gif demo](http://i.imgur.com/I0ztoPw.gif)](https://www.youtube.com/watch?v=zluBwbf3aew)

[![animated gif demo](http://i.imgur.com/b5pJjtL.gif)](https://www.youtube.com/watch?v=zluBwbf3aew)

Download
--------

Current version: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.timehop.stickyheadersrecyclerview/library/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.timehop.stickyheadersrecyclerview/library)

    compile 'com.timehop.stickyheadersrecyclerview:library:[latest.version.number]@aar'


Usage
-----

There are three main classes, `StickyRecyclerHeadersAdapter`, `StickyRecyclerHeadersDecoration`,
and `StickyRecyclerHeadersTouchListener`.

`StickyRecyclerHeadersAdapter` has a very similar interface to the `RecyclerView.Adapter`, and it
is recommended that you make your `RecyclerView.Adapter` implement `StickyRecyclerHeadersAdapter`.

There interface looks like this:

```java
public interface StickyRecyclerHeadersAdapter<VH extends RecyclerView.ViewHolder> {
  public long getHeaderId(int position);

  public VH onCreateHeaderViewHolder(ViewGroup parent);

  public void onBindHeaderViewHolder(VH holder, int position);

  public int getItemCount();
}
```

The second class, `StickyRecyclerHeadersDecoration`, is where most of the magic happens, and does
not require any configuration on your end.  Here's an example from `onCreate()` in an activity:

```java
mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
mAdapter = new MyStickyRecyclerHeadersAdapter();
mRecyclerView.setAdapter(mAdapter);
mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
mRecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(mAdapter));
```

`StickyRecyclerHeadersTouchListener` allows you to listen for clicks on header views.
Simply create an instance of `StickyRecyclerHeadersTouchListener`, set the `OnHeaderClickListener`,
and add the `StickyRecyclerHeadersTouchListener` as a touch listener to your `RecyclerView`.

```java
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
mRecyclerView.addOnItemTouchListener(touchListener);
```

The StickyHeaders aren't aware of your adapter so if you must notify them when your data set changes.

```java
    mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override public void onChanged() {
        headersDecor.invalidateHeaders();
      }
    });
```

If the Recyclerview's layout manager implements getExtraLayoutSpace (to preload more content then is
visible for performance reasons), you must implement ItemVisibilityAdapter and pass an instance as a
second argument to StickyRecyclerHeadersDecoration's constructor.
```java
    @Override
    public boolean isPositionVisible(final int position) {
        return layoutManager.findFirstVisibleItemPosition() <= position
            && layoutManager.findLastVisibleItemPosition() >= position;
    }
```


Item animators don't play nicely with RecyclerView decorations, so your mileage with that may vary.

Compatibility
-------------

API 11+

Known Issues
------------

* The header views aren't recycled at this time.  Contributions are most welcome.

* I haven't tested this with ItemAnimators yet.

* The header views are drawn to a canvas, and are not actually a part of the view hierarchy. As such, they can't have touch states, and you may run into issues if you try to load images into them asynchronously.

Version History
---------------
0.4.3 (12/24/2015) - Change minSDK to 11, fix issue with header bounds caching

0.4.2 (8/21/2015) - Add support for reverse `ReverseLayout` in `LinearLayoutManager` by [AntonPukhonin](https://github.com/AntonPukhonin)

0.4.1 (6/24/2015) - Fix "dancing headers" by DarkJaguar91

0.4.0 (4/16/2015) - Code reorganization by danoz73, fixes for different sized headers, performance improvements

0.3.6 (1/30/2015) - Prevent header clicks from passing on the touch event

0.3.5 (12/12/2014) - Add StickyRecyclerHeadersDecoration.invalidateHeaders() method

0.3.4 (12/3/2014) - Fix issues with rendering of header views with header ID = 0

0.3.3 (11/13/2014) - Fixes for padding, support views without headers

0.3.2 (11/1/2014) - Bug fixes for list items with margins and deleting items

0.2 (10/3/2014) - Add StickyRecyclerHeadersTouchListener

0.1 (10/2/2014) - Initial Release
