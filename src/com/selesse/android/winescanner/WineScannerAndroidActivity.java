package com.selesse.android.winescanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
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

public class WineScannerAndroidActivity extends ListActivity {

  public ArrayList<Wine> wineList;
  private WineAdapter wineAdapter;
  public Wine tempWine;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    final Activity activity = this;

    wineList = FileManager.loadWine();

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

        Intent i = new Intent(activity, SingleWineView.class);
        i.putExtra("wine", displayedWine);
        startActivity(i);

      }
    });

    lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

      @Override
      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        int id = (int) info.id;

        menu.setHeaderTitle(wineList.get(id).getName().substring(0, Math.min(wineList.get(id).getName().length(), 22)));
        menu.add(0, id, 0, "Edit");
        menu.add(0, id, 1, "Delete");

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
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

    int wineID = (int) info.id;

    // action is 0 for edit, 1 for delete... there is definitely an OO
    // procedure I'm not following
    String action = item.getOrder() == 0 ? "Edit" : "Delete";

    if (action.equals("Delete")) {
      String name = wineList.get(wineID).getName();
      FileManager.deleteWine(wineID);
      wineAdapter.notifyDataSetChanged();
      Toast.makeText(this, "Deleted " + name.substring(0, Math.min(40, name.length())), Toast.LENGTH_SHORT).show();
    }
    else if (action.equals("Edit")) {
      Intent i = new Intent(this, EditWineView.class);
      i.putExtra("wine", wineList.get(wineID));
      tempWine = wineList.remove(wineID);
      startActivityForResult(i, 3);
    }

    return true;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

    if (requestCode == 2 || requestCode == 3) {
      // this is a returned wine
      if (resultCode == RESULT_OK) {
        Bundle bundle = intent.getExtras();
        Wine w = (Wine) bundle.get("Wine");
        FileManager.addWine(w);
        wineAdapter.notifyDataSetChanged();
        return;
      }
      else {
        if (requestCode == 3) {
          FileManager.addWine(tempWine);
          wineAdapter.notifyDataSetChanged();
          return;
        }
        // Toast.makeText(this, "Result was bad!",
        // Toast.LENGTH_LONG).show();
        return;
      }
    }
    // handle empty scans
    if (scanResult.getContents() == null)
      return;

    if (scanResult != null) {
      String barcodeNumber = scanResult.getContents();
      if (Pattern.matches("[0-9]{1,13}", barcodeNumber)) {

        // response is a UPC code, fetch product meta data
        // using Google Products API, Best Buy Remix, etc.
        try {
          // go to this link!
          String shoppingLink = "http://www.selesse.com/winescanner/scan.php?code=" + barcodeNumber;
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
            Wine newWine = new Wine(barcodeNumber, "null", "null", "null");
            Intent i = new Intent(this, EditWineView.class);
            i.putExtra("wine", newWine);
            startActivityForResult(i, 2);
          }
          else if (contents.get(0).startsWith("UPCDB")) {
            Wine newWine = new Wine(barcodeNumber, contents.get(1), "null", "null");
            Intent i = new Intent(this, EditWineView.class);
            i.putExtra("wine", newWine);
            startActivityForResult(i, 2);
          }
          else {
            String wineName = contents.get(0);
            String desc = contents.get(1);
            String price = contents.get(2);
            String image = "null";
            String country = "null";
            int year = 0;

            Wine newWine = new Wine(barcodeNumber, wineName, price, desc);

            for (String line : contents) {
              if (line.startsWith("http") && image.equals("null")) {
                newWine.setImageURL(line);
              }

              if (year == 0) {
                Pattern p = Pattern.compile("\\b\\d{4}\\b");
                Matcher m = p.matcher(line);

                if (m.find()) {
                  newWine.setYear(m.group(0));
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
            startActivityForResult(i, 2);
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

      Log.v("WineScanner", scanResult.toString());
    }

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
      Collections.sort(wineList);
      wineAdapter.notifyDataSetChanged();
    }
    else if (item.getTitle().equals("Add wine")) {
      Intent i = new Intent(this, EditWineView.class);
      startActivityForResult(i, 2);
    }

    return true;
  }

  public void onSetData(View v) {
    this.wineAdapter.changeData(wineList);
  }

  public void onSetEmpty(View v) {
    this.wineAdapter.changeData(wineList);
  }
}