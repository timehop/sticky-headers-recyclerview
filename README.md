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

    compile 'com.timehop.stickyheadersrecyclerview:library:0.3.4@aar'

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

Finally, `StickyRecyclerHeadersTouchListener` allows you to listen for clicks on header views.
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

Compatibility
-------------

This should work everywhere that RecyclerView does (API 7+).

Known Issues
------------

* The header views aren't recycled at this time.  Contributions are most welcome.

* I haven't tested this with ItemAnimators yet.

Version History
---------------
0.3.4 (12/3/2014) - Fix issues with rendering of header views with header ID = 0

0.3.3 (11/13/2014) - Fixes for padding, support views without headers

0.3.2 (11/1/2014) - Bug fixes for list items with margins and deleting items

0.2 (10/3/2014) - Add StickyRecyclerHeadersTouchListener

0.1 (10/2/2014) - Initial Release