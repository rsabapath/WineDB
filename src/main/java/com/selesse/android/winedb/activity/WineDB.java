package com.selesse.android.winedb.activity;

import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.selesse.android.winedb.R;
import com.selesse.android.winedb.database.WineDatabase;
import com.selesse.android.winedb.database.sqlite.WineTable;
import com.selesse.android.winedb.database.sqlite.WinesDataSource;
import com.selesse.android.winedb.model.RequestCode;
import com.selesse.android.winedb.model.Wine;
import com.selesse.android.winedb.model.WineContextMenu;
import com.selesse.android.winedb.winescraper.WineScrapers;

public class WineDB extends ListActivity {

  private WineDatabase wineDatabase;
  private Cursor cursor;
  private SimpleCursorAdapter adapter;

  // deprecated because we're using a deprecated SimpleCursorAdapter constructor for 2.3+
  // compatibility
  @SuppressWarnings("deprecation")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    final Activity activity = this;

    // initialize the database - this particular implementation is an SQLite DB
    wineDatabase = new WinesDataSource(this);
    wineDatabase.open();

    cursor = wineDatabase.getAllWines();
    startManagingCursor(cursor);

    String[] from = { WineTable.COLUMN_NAME, WineTable.COLUMN_COLOR };
    int[] to = { R.id.name, R.id.wine_color };

    adapter = new SimpleCursorAdapter(this, R.layout.rows, cursor, from, to);

    setListAdapter(adapter);

    ListView listView = getListView();

    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // get the wine at that position and pass it to the view single wine activity
        Wine wine = getWine(position);

        Intent intent = new Intent(activity, SingleWineView.class);
        intent.putExtra("wine", wine);
        startActivity(intent);
      }
    });

    listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

      @Override
      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        // go through all the WineContextMenu enum, make them clickable items

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        Wine wine = getWine(info.position);

        menu.setHeaderTitle(wine.getName().substring(0, Math.min(wine.getName().length(), 22)));

        for (WineContextMenu contextMenuItem : WineContextMenu.values()) {
          menu.add(0, info.position, contextMenuItem.ordinal(), contextMenuItem.toString());
        }
      }
    });

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
  public boolean onContextItemSelected(MenuItem item) {
    super.onContextItemSelected(item);

    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
        .getMenuInfo();

    int winePositionIndex = info.position;

    WineContextMenu selectedItem = WineContextMenu.values()[item.getOrder()];

    switch (selectedItem) {
      case DELETE:
        Wine delete_wine = getWine(winePositionIndex);
        Toast.makeText(
            this,
            "Deleted "
                + delete_wine.getName().substring(0, Math.min(40, delete_wine.getName().length())),
            Toast.LENGTH_SHORT).show();
        wineDatabase.deleteWine(delete_wine);
        refreshWines();
        break;
      case EDIT:
        Intent i = new Intent(this, CreateOrEditWineActivity.class);
        i.putExtra("wine", getWine(winePositionIndex));
        startActivityForResult(i, RequestCode.EDIT_WINE.ordinal());
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
        Bundle bundle = intent.getExtras();
        wine = (Wine) bundle.get("wine");

        // if it doesn't yet have an id, give it one
        if (wine.getId() == 0) {
          wine = wineDatabase.createWine(wine);
        }
        // otherwise just update the already-existing wine
        else {
          wineDatabase.updateWine(wine);
        }
      }
      refreshWines();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuItem addItem = menu.add(R.string.add_wine);
    addItem.setIcon(android.R.drawable.ic_menu_add);
  
    MenuItem sortItem = menu.add(R.string.sort_wines);
    sortItem.setIcon(android.R.drawable.ic_menu_sort_alphabetically);
  
    MenuItem helpItem = menu.add(R.string.help);
    helpItem.setIcon(android.R.drawable.ic_menu_help);
  
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getTitle().equals("Help")) {
      // TODO help screen
      Toast.makeText(this, "Only losers need help", Toast.LENGTH_LONG).show();
    }
    else if (item.getTitle().equals("Sort")) {
      // TODO FIXME
    }
    else if (item.getTitle().equals("Add wine")) {
      Intent intent = new Intent(this, CreateOrEditWineActivity.class);
      startActivityForResult(intent, RequestCode.EDIT_WINE.ordinal());
    }
  
    return true;
  }

  public void startCreateNewWineIntent(Wine wine) {
    Intent editIntent = new Intent(this, CreateOrEditWineActivity.class);
    editIntent.putExtra("wine", wine);
    startActivityForResult(editIntent, RequestCode.EDIT_WINE.ordinal());
  }

  @SuppressWarnings("deprecation")
  private void refreshWines() {
    cursor.requery();
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

  private Wine getWine(int position) {
    Cursor cursor = (Cursor) adapter.getItem(position);
    return wineDatabase.cursorToWine(cursor);
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
}