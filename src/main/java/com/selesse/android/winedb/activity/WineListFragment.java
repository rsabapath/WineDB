package com.selesse.android.winedb.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

import com.selesse.android.winedb.R;
import com.selesse.android.winedb.contentprovider.WineContentProvider;
import com.selesse.android.winedb.database.Wine;

public class WineListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

  private Cursor cursor;
  private SimpleCursorAdapter adapter;

  private static final String[] PROJECTION = new String[] {
      Wine.COLUMN_ID,
      Wine.COLUMN_NAME,
      Wine.COLUMN_COLOR };
  private static final int LOADER_ID = 0;
  private LoaderManager.LoaderCallbacks<Cursor> callBacks;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    String[] from = { Wine.COLUMN_NAME, Wine.COLUMN_COLOR };
    int[] to = { R.id.name, R.id.wine_color };

    adapter = new SimpleCursorAdapter(getActivity(), R.layout.rows, cursor, from, to, 0);
    callBacks = this;
    
    LoaderManager lm = getActivity().getSupportLoaderManager();
    lm.initLoader(LOADER_ID, null, callBacks);

    setListAdapter(adapter);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);

    Intent intent = new Intent(getActivity(), SingleWineView.class);
    intent.putExtra("id", id);
    startActivity(intent);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(), WineContentProvider.CONTENT_URI, PROJECTION, null, null,
        null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    switch (loader.getId()) {
      case LOADER_ID:
        adapter.swapCursor(cursor);
        break;
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> arg0) {
    adapter.swapCursor(null);
  }

}
