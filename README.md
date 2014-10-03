sticky-headers-recyclerview
===========================

This decorator allows you to easily create section headers for RecyclerViews using a
LinearLayoutManager in either vertical or horizontal orientation.

Credit to [Emil Sjölander](https://github.com/emilsjolander) for creating StickyListHeaders,
a library that many of us relied on for sticky headers in our listviews.

Here is a quick video of it in action (click to see the full video):

[![animated gif demo](http://i.imgur.com/EdClOFB.gif)](https://www.youtube.com/watch?v=zluBwbf3aew)

Usage
-----

There are two main classes, `StickyRecyclerHeadersAdapter` and `StickyRecyclerHeadersAdapter`.

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

Known Issues
------------

The header views aren't recycled at this time.  Contributions are most welcome.

Version History
---------------

0.1 (10/2/2014) - Initial Release