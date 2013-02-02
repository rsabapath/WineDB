package com.selesse.android.winedb.activity;

import java.util.List;
import java.util.regex.Pattern;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.selesse.android.winedb.R;
import com.selesse.android.winedb.contentprovider.WineContentProvider;
import com.selesse.android.winedb.database.WineTable;
import com.selesse.android.winedb.model.RequestCode;
import com.selesse.android.winedb.model.Wine;
import com.selesse.android.winedb.model.WineContextMenu;
import com.selesse.android.winedb.winescraper.WineScrapers;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WineDB extends SherlockListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

  private Cursor cursor;
  private SimpleCursorAdapter adapter;
  private static final String[] PROJECTION = new String[] {
      WineTable.COLUMN_ID,
      WineTable.COLUMN_NAME,
      WineTable.COLUMN_COLOR };
  private static final int LOADER_ID = 0;
  private LoaderManager.LoaderCallbacks<Cursor> callBacks;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    final Activity activity = this;

    String[] from = { WineTable.COLUMN_NAME, WineTable.COLUMN_COLOR };
    int[] to = { R.id.name, R.id.wine_color };

    adapter = new SimpleCursorAdapter(this, R.layout.rows, cursor, from, to, 0);

    setListAdapter(adapter);

    callBacks = this;

    LoaderManager lm = getLoaderManager();
    lm.initLoader(LOADER_ID, null, callBacks);

    registerForContextMenu(getListView());

    // part responsible for launching zxing intent
    final Button scan = (Button) findViewById(R.id.scan);
    scan.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.initiateScan();
      }
    });

  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);

    Intent intent = new Intent(this, SingleWineView.class);
    intent.putExtra("id", id);
    startActivity(intent);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);

    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

    for (WineContextMenu contextMenuItem : WineContextMenu.values()) {
      menu.add(0, info.position, contextMenuItem.ordinal(), contextMenuItem.toString());
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.main_activity, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    switch (item.getItemId()) {
      case R.id.add_wine:
        startCreateNewWineIntent(new Wine());
        return true;
      case R.id.sortBy:
        if (cursor.getCount() == 0) {
          Toast.makeText(getApplicationContext(), R.string.no_wines, Toast.LENGTH_SHORT).show();
        }
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }

  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    super.onContextItemSelected(item);

    WineContextMenu selectedItem = WineContextMenu.values()[item.getOrder()];
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

    switch (selectedItem) {
      case DELETE:
        Uri uri = Uri.parse(WineContentProvider.CONTENT_URI + "/" + info.id);
        getContentResolver().delete(uri, null, null);
        break;
      case EDIT:
        Intent intent = new Intent(getBaseContext(), CreateOrEditWineActivity.class);
        uri = Uri.parse(WineContentProvider.CONTENT_URI + "/" + info.id);
        intent.putExtra(WineContentProvider.CONTENT_ITEM_TYPE, uri);
        startActivityForResult(intent, RequestCode.EDIT_WINE.ordinal());
        break;
    }

    return true;
  }

  @Override
  public void onActivityResult(int requestCodeNumber, int resultCode, Intent intent) {
    if (requestCodeNumber == Activity.RESULT_CANCELED) {
      return;
    }

    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCodeNumber, resultCode,
        intent);

    // do nothing when the user doesn't scan anything
    if (requestCodeNumber == IntentIntegrator.REQUEST_CODE && scanResult.getContents() == null) {
      return;
    }
    // case where zxing was called and successfully scanned something
    else if (requestCodeNumber == IntentIntegrator.REQUEST_CODE) {
      // extract the wine object from barcode (presumably by going through a bunch of sources)
      String barcode = scanResult.getContents();

      // we'll only use the scrapers if it looks like it'll match approximately match UPC
      if (Pattern.matches("[0-9]{1,13}", barcode)) {
        scrapeWinesAndEditWine(barcode);
      }
      else {
        Wine wine = new Wine();
        wine.setBarcode(barcode);
        startCreateNewWineIntent(wine);
      }

      return;
    }

    // this is a request code made for the winedb application, figure out which one
    RequestCode requestCode = RequestCode.values()[requestCodeNumber];

    if (requestCode == RequestCode.EDIT_WINE) {
      Wine wine = null;

      // this is a returned wine
      if (resultCode == RESULT_OK) {
        // FIXME
      }
    }
  }

  public void startCreateNewWineIntent(Wine wine) {
    Intent editIntent = new Intent(this, CreateOrEditWineActivity.class);
    editIntent.putExtra("wine", wine);
    startActivityForResult(editIntent, RequestCode.EDIT_WINE.ordinal());
  }

  /**
   * Create an AsyncTask to go scrape wines for us. The real magic happens in
   * {@link WineScraperThread}.
   *
   * @param barcode
   *          The barcode of the wine we'll be scraping.
   */
  private void scrapeWinesAndEditWine(String barcode) {
    AsyncTask<String, Void, List<Wine>> task = new WineScraperThread();
    task.execute(barcode);
  }

  private class WineScraperThread extends AsyncTask<String, Void, List<Wine>> {
    private ProgressDialog progress;
    private String barcode;

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progress = new ProgressDialog(WineDB.this);
      progress.setMessage(getString(R.string.scraping));
      progress.setIndeterminate(true);
      progress.show();
    }

    @Override
    protected List<Wine> doInBackground(String... params) {
      barcode = params[0];
      WineScrapers scrapers = new WineScrapers(barcode);
      List<Wine> wines = scrapers.scrape();

      return wines;
    }

    @Override
    protected void onPostExecute(List<Wine> result) {
      super.onPostExecute(result);
      progress.dismiss();

      Wine wine = null;
      if (result.size() == 0) {
        wine = new Wine();
        wine.setBarcode(barcode);
      }
      // for now we'll just grab the first result, we can do something more sophisticated later
      else {
        wine = result.get(0);
      }

      startCreateNewWineIntent(wine);
    }

  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(WineDB.this, WineContentProvider.CONTENT_URI, PROJECTION, null, null,
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