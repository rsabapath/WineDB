package com.selesse.android.winedb.activity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.selesse.android.winedb.R;
import com.selesse.android.winedb.WineAdapter;
import com.selesse.android.winedb.model.RequestCode;
import com.selesse.android.winedb.model.Wine;
import com.selesse.android.winedb.model.WineContextMenu;
import com.selesse.android.winedb.util.FileManager;
import com.selesse.android.winedb.util.impl.sqlite.WinesDataSource;
import com.selesse.android.winedb.winescraper.WineScrapers;

public class WineDB extends ListActivity {
  
  private FileManager dataSource;
  private List<Wine> wines;
  private WineAdapter wineAdapter;
  private Wine tempWine;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    final Activity activity = this;
    
    dataSource = new WinesDataSource(this);
    dataSource.open();
    
    wines = dataSource.getAllWines();

    wineAdapter = new WineAdapter(this, R.layout.rows, wines);
    setListAdapter(wineAdapter);

    ListView listView = getListView();
    listView.setTextFilterEnabled(true);

    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // get the wine at that position and pass it to the view single wine activity
        Wine displayedWine = wines.get(position);

        Intent intent = new Intent(activity, SingleWineView.class);
        intent.putExtra("wine", displayedWine);
        startActivity(intent);
      }
    });

    listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

      @Override
      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        // go through all the WineContextMenu enum, make them clickable items
        
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        int id = (int) info.id;

        menu.setHeaderTitle(wines.get(id).getName()
            .substring(0, Math.min(wines.get(id).getName().length(), 22)));

        for (WineContextMenu contextMenuItem : WineContextMenu.values()) {
          menu.add(0, id, contextMenuItem.ordinal(), contextMenuItem.toString());
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

    int wineID = (int) info.id;

    WineContextMenu selectedItem = WineContextMenu.values()[item.getOrder()];

    switch (selectedItem) {
      case DELETE:
        Wine delete_wine = wines.get(wineID);
        dataSource.deleteWine(delete_wine);
        wineAdapter.notifyDataSetChanged();
        Toast.makeText(
            this,
            "Deleted "
                + delete_wine.getName().substring(0, Math.min(40, delete_wine.getName().length())),
            Toast.LENGTH_SHORT).show();
        break;
      case EDIT:
        Intent i = new Intent(this, EditWineView.class);
        i.putExtra("wine", wines.get(wineID));
        tempWine = wines.remove(wineID);
        startActivityForResult(i, RequestCode.DELETE_THEN_EDIT.ordinal());
        break;
    }

    return true;
  }

  @Override
  public void onActivityResult(int requestCodeNumber, int resultCode, Intent intent) {
    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCodeNumber, resultCode,
        intent);

    if (requestCodeNumber == IntentIntegrator.REQUEST_CODE && scanResult.getContents() == null)
      return;
    else if (requestCodeNumber == IntentIntegrator.REQUEST_CODE) {
      Wine wine = getWineFromResults(scanResult.getContents());
      // TODO edit, add, etc
      Intent i = new Intent(this, EditWineView.class);
      i.putExtra("wine", wine);
      startActivityForResult(i, RequestCode.EDIT_WINE.ordinal());
      return;
    }

    RequestCode requestCode = RequestCode.values()[requestCodeNumber];

    if (requestCode == RequestCode.EDIT_WINE || requestCode == RequestCode.DELETE_THEN_EDIT) {
      // this is a returned wine
      if (resultCode == RESULT_OK) {
        Bundle bundle = intent.getExtras();
        Wine w = (Wine) bundle.get("Wine");
        dataSource.createWine(w);
        wineAdapter.notifyDataSetChanged();
        return;
      }
      else {
        if (requestCode == RequestCode.DELETE_THEN_EDIT) {
          dataSource.createWine(tempWine);
          wineAdapter.notifyDataSetChanged();
          return;
        }
        return;
      }
    }
  }

  private Wine getWineFromResults(String barcode) {

    if (Pattern.matches("[0-9]{1,13}", barcode)) {
      WineScrapers scrapers = new WineScrapers(barcode);
      List<Wine> wines = scrapers.scrape();
      if (wines.size() > 0) {
        return wines.get(0);
      }
    }
    else {
      Log.v("WineScanner", "Failed to find UPC Code");
    }

    Log.v("WineScanner", barcode.toString());
    return null;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuItem addItem = menu.add("Add wine");
    addItem.setIcon(android.R.drawable.ic_menu_add);

    MenuItem sortItem = menu.add("Sort");
    sortItem.setIcon(android.R.drawable.ic_menu_sort_alphabetically);

    MenuItem helpItem = menu.add("Help");
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
      // TODO sort by name works for now, let's do rest later
      Collections.sort(wines, new Comparator<Wine>() {
        @Override
        public int compare(Wine wine1, Wine wine2) {
          return wine1.getName().compareTo(wine2.getName());
        }
      });
      wineAdapter.notifyDataSetChanged();
    }
    else if (item.getTitle().equals("Add wine")) {
      Intent i = new Intent(this, EditWineView.class);
      startActivityForResult(i, RequestCode.EDIT_WINE.ordinal());
    }

    return true;
  }
}