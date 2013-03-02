package com.selesse.android.winedb.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.selesse.android.winedb.R;
import com.selesse.android.winedb.contentprovider.WineContentProvider;
import com.selesse.android.winedb.database.Wine;
import com.selesse.android.winedb.database.WineDatabaseHandler;
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
        startEditWineIntent(new Wine());
        return true;
      case R.id.export_database:
        startExportDatabase();
        return true;
      case R.id.import_database:
        startImportDatabase();
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
        // first, check to see if we already have this barcode in our database.
        // if we do, we edit that wine; otherwise we scrape and create

        Wine wine = WineDatabaseHandler.getInstance(this).getWineByBarcode(barcode);
        if (wine == null) {
          scrapeWinesAndEditWine(barcode);
        }
        else {
          startEditWineIntent(wine);
        }
      }
      else {
        Wine wine = new Wine();
        wine.setBarcode(barcode);
        startEditWineIntent(wine);
      }
    }
  }

  public void startEditWineIntent(Wine wine) {
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

      startEditWineIntent(wine);
    }
  }

  private void startImportDatabase() {
    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    alert.setTitle(R.string.import_dialog_title);
    alert.setMessage(R.string.import_dialog_message);

    // Set an EditText view to get user input
    final EditText input = new EditText(this);
    input.setText(Environment.getExternalStorageDirectory().getPath() + "/winedb.bak");
    input.setInputType(~(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT));
    alert.setView(input);

    alert.setPositiveButton("Import", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int whichButton) {
        String value = input.getText().toString();
        final File importLocation = new File(value);
        if (importLocation.exists()) {
          importWineDatabase(importLocation);
        }
        else {
          showExportError(getString(R.string.import_file_not_found));
        }
      }
    }).setNegativeButton("Cancel", null);

    alert.show();
  }

  private void importWineDatabase(File importLocation) {
    try {
      WineDatabaseHandler handler = WineDatabaseHandler.getInstance(this);
      handler.importDatabase(importLocation.getPath());
      handler.refresh();
    }
    catch (FileNotFoundException e) {
      showExportError(getString(R.string.import_error));
    }
    catch (IOException e) {
      showExportError(getString(R.string.import_error));
    }
  }

  private void startExportDatabase() {
    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    alert.setTitle(R.string.export_dialog_title);
    alert.setMessage(R.string.export_dialog_message);

    // Set an EditText view to get user input
    final EditText input = new EditText(this);
    input.setText(Environment.getExternalStorageDirectory().getPath() + "/winedb.bak");
    input.setInputType(~(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT));
    alert.setView(input);

    if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      showExportError(getString(R.string.sd_card_not_mounted));
    }

    if (!Environment.getExternalStorageDirectory().canWrite()) {
      showExportError(getString(R.string.export_dialog_no_write));
      return;
    }

    alert.setPositiveButton("Export", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int whichButton) {
        String value = input.getText().toString();
        final File exportLocation = new File(value);
        if (exportLocation.exists()) {
          createConfirmExportDialog(exportLocation);
        }
        else {
          exportWineDatabase(exportLocation);
        }
      }
    });

    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int whichButton) {
        // do nothing
      }
    });

    alert.show();
  }

  private void createConfirmExportDialog(final File exportLocation) {
    AlertDialog.Builder confirm = new AlertDialog.Builder(this);
    DialogInterface.OnClickListener dialogListener = new OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
          exportWineDatabase(exportLocation);
        }
      }
    };
    confirm.setTitle(R.string.confirm);
    confirm.setMessage(R.string.export_overwrite_file);

    confirm.setPositiveButton("Yes", dialogListener).setNegativeButton("No", dialogListener).show();
  }

  private void exportWineDatabase(File exportLocation) {
    try {
      WineDatabaseHandler handler = WineDatabaseHandler.getInstance(this);
      handler.exportDatabase(exportLocation.getPath());
      Toast.makeText(this, R.string.export_success, Toast.LENGTH_SHORT).show();
    }
    catch (FileNotFoundException e) {
      showExportError(getString(R.string.export_dialog_no_database));
    }
    catch (IOException e) {
      showExportError(getString(R.string.export_error));
    }
  }

  private void showExportError(String errorString) {
    Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
  }

}