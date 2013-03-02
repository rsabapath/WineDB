package com.selesse.android.winedb.activity;

import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.selesse.android.winedb.R;
import com.selesse.android.winedb.contentprovider.WineContentProvider;
import com.selesse.android.winedb.database.Wine;
import com.selesse.android.winedb.model.RequestCode;
import com.selesse.android.winedb.model.WineContextMenu;
import com.selesse.android.winedb.winescraper.WineScrapers;

public class WineDB extends SherlockFragmentActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    if (findViewById(R.id.fragment_container) != null) {

      if (savedInstanceState != null) {
        return;
      }

      WineListFragment firstFragment = new WineListFragment();

      firstFragment.setArguments(getIntent().getExtras());

      // Add the fragment to the 'fragment_container' FrameLayout
      getSupportFragmentManager().beginTransaction()
          .add(R.id.fragment_container, firstFragment, WineListFragment.TAG).commit();
    }
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
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    switch (item.getItemId()) {
      case R.id.scan:
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
        return true;
      case R.id.add_wine:
        startCreateNewWineIntent(new Wine());
        return true;
      case R.id.sortBy:
        queryUserForSortOption();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }

  }

  private void queryUserForSortOption() {
    // final WineListFragment fragment = (WineListFragment) getSupportFragmentManager()
    // .findFragmentByTag(WineListFragment.TAG);
    // SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sort_by,
    // android.R.layout.simple_spinner_dropdown_item);
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
        intent.putExtra("id", info.id);
        startActivity(intent);
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
    }
  }

  public void startCreateNewWineIntent(Wine wine) {
    Intent editIntent = new Intent(this, CreateOrEditWineActivity.class);
    Bundle extras = new Bundle();
    extras.putLong("id", wine.getId());
    extras.putSerializable("wine", wine);
    editIntent.putExtras(extras);
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

}