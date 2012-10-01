package com.selesse.android.winedb.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
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
import com.selesse.android.winedb.FileManager;
import com.selesse.android.winedb.R;
import com.selesse.android.winedb.WineAdapter;
import com.selesse.android.winedb.impl.FileManagerImpl;
import com.selesse.android.winedb.model.RequestCode;
import com.selesse.android.winedb.model.Wine;
import com.selesse.android.winedb.model.WineContextMenu;

public class WineDB extends ListActivity {

  public static ArrayList<Wine> wineList = new ArrayList<Wine>();
  private WineAdapter wineAdapter;
  public Wine tempWine;
  private FileManager fileManager;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    final Activity activity = this;

    fileManager = new FileManagerImpl();

    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    wineAdapter = new WineAdapter(this, R.layout.rows, wineList);
    setListAdapter(wineAdapter);

    ListView lv = getListView();
    lv.setTextFilterEnabled(true);

    lv.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // get the wine at that position and pass it to the view single
        // wine activity
        Wine displayedWine = wineList.get(position);

        Intent intent = new Intent(activity, SingleWineView.class);
        intent.putExtra("wine", displayedWine);
        startActivity(intent);
      }
    });

    lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

      @Override
      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        int id = (int) info.id;

        menu.setHeaderTitle(wineList.get(id).getName().substring(0, Math.min(wineList.get(id).getName().length(), 22)));

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

    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

    int wineID = (int) info.id;

    WineContextMenu selectedItem = WineContextMenu.values()[item.getOrder()];

    switch (selectedItem) {
      case DELETE:
        Wine delete_wine = wineList.get(wineID);
        fileManager.deleteWine(delete_wine);
        wineAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Deleted " + delete_wine.getName().substring(0, Math.min(40, delete_wine.getName().length())),
            Toast.LENGTH_SHORT).show();
        break;
      case EDIT:
        Intent i = new Intent(this, EditWineView.class);
        i.putExtra("wine", wineList.get(wineID));
        tempWine = wineList.remove(wineID);
        startActivityForResult(i, RequestCode.DELETE_THEN_EDIT.ordinal());
        break;
    }

    return true;
  }

  @Override
  public void onActivityResult(int requestCodeNumber, int resultCode, Intent intent) {
    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCodeNumber, resultCode, intent);

    if (requestCodeNumber == IntentIntegrator.REQUEST_CODE && scanResult.getContents() == null)
      return;
    else if (requestCodeNumber == IntentIntegrator.REQUEST_CODE) {
      getWineFromResults(scanResult.getContents());
      return;
    }

    RequestCode requestCode = RequestCode.values()[requestCodeNumber];

    if (requestCode == RequestCode.EDIT_WINE || requestCode == RequestCode.DELETE_THEN_EDIT) {
      // this is a returned wine
      if (resultCode == RESULT_OK) {
        Bundle bundle = intent.getExtras();
        Wine w = (Wine) bundle.get("Wine");
        fileManager.addWine(w);
        wineAdapter.notifyDataSetChanged();
        return;
      }
      else {
        if (requestCode == RequestCode.DELETE_THEN_EDIT) {
          fileManager.addWine(tempWine);
          wineAdapter.notifyDataSetChanged();
          return;
        }
        return;
      }
    }
  }

  private void getWineFromResults(String barcode) {

    if (Pattern.matches("[0-9]{1,13}", barcode)) {

      // response is a UPC code, fetch product meta data
      // using Google Products API, Best Buy Remix, etc.
      try {
        // go to this link!
        String shoppingLink = "http://www.selesse.com/winescanner/scan.php?code=" + barcode;
        URL url = new URL(shoppingLink);

        // connect to the link, read the contents
        URLConnection connection = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        // use array list to get contents
        ArrayList<String> contents = new ArrayList<String>();

        String inputLine;
        while ((inputLine = in.readLine()) != null)
          contents.add(inputLine);

        if (contents.get(0).startsWith("404")) {
          Log.v("WineScanner", "Wine not found through Google API");
          Wine newWine = new Wine(barcode, "null", "null", "null");
          Intent i = new Intent(this, EditWineView.class);
          i.putExtra("wine", newWine);
          startActivityForResult(i, RequestCode.EDIT_WINE.ordinal());
        }
        else if (contents.get(0).startsWith("UPCDB")) {
          Wine newWine = new Wine(barcode, contents.get(1), "null", "null");
          Intent i = new Intent(this, EditWineView.class);
          i.putExtra("wine", newWine);
          startActivityForResult(i, RequestCode.EDIT_WINE.ordinal());
        }
        else {
          String wineName = contents.get(0);
          String desc = contents.get(1);
          String price = contents.get(2);
          String image = "null";
          String country = "null";
          int year = 0;

          Wine newWine = new Wine(barcode, wineName, price, desc);

          for (String line : contents) {
            if (line.startsWith("http") && image.equals("null")) {
              newWine.setImageURL(line);
            }

            if (year == 0) {
              Pattern p = Pattern.compile("\\b\\d{4}\\b");
              Matcher m = p.matcher(line);

              if (m.find()) {
                newWine.setYear(Integer.parseInt(m.group(0)));
              }
            }

            if (country.equals("null")) {
              if (line.toLowerCase().contains("italy"))
                newWine.setCountry("Italy");
              if (line.toLowerCase().contains("france"))
                newWine.setCountry("France");
              if (line.toLowerCase().contains("spain") || line.toLowerCase().contains("spanien"))
                newWine.setCountry("Spain");
              if (line.toLowerCase().contains("germany"))
                newWine.setCountry("Germany");
              if (line.toLowerCase().contains("canada"))
                newWine.setCountry("Canada");
            }
          }

          Log.v("WineScanner", newWine.toString());

          Intent i = new Intent(this, EditWineView.class);
          i.putExtra("wine", newWine);
          startActivityForResult(i, RequestCode.EDIT_WINE.ordinal());
        }
      }
      catch (MalformedURLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (ConnectException e) {
        Toast.makeText(this, "Internet doesn't work!", Toast.LENGTH_LONG).show();
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
    else {
      Log.v("WineScanner", "Failed to find UPC Code");
    }

    Log.v("WineScanner", barcode.toString());
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
      Collections.sort(wineList, new Comparator<Wine>() {
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