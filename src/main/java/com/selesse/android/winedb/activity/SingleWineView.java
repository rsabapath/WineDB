package com.selesse.android.winedb.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.selesse.android.winedb.R;
import com.selesse.android.winedb.database.Wine;
import com.selesse.android.winedb.database.WineDatabaseHandler;
import com.selesse.android.winedb.model.RequestCode;
import com.selesse.android.winedb.model.WineColor;

public class SingleWineView extends SherlockActivity {
  Wine wine = new Wine();
  TextView nameText = null;
  TextView countryText = null;
  TextView yearText = null;
  TextView colorText = null;
  TextView descText = null;
  TextView ratingText = null;
  TextView priceText = null;
  TextView commentText = null;
  ImageView image = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Bundle extras = this.getIntent().getExtras();

    if (savedInstanceState != null) {
      wine.setId((Long) savedInstanceState.get("id"));
    }
    if (extras != null) {
      wine.setId(extras.getLong("id"));
    }
    if (wine.getId() >= 0) {
      wine = WineDatabaseHandler.getInstance(this).getWine(wine.getId());
    }

    super.onCreate(savedInstanceState);
    setContentView(R.layout.single_wine);

    nameText = (TextView) findViewById(R.id.wineNameText);
    countryText = (TextView) findViewById(R.id.countryText);
    yearText = (TextView) findViewById(R.id.yearText);
    colorText = (TextView) findViewById(R.id.colorText);
    descText = (TextView) findViewById(R.id.descText);
    ratingText = (TextView) findViewById(R.id.ratingText);
    priceText = (TextView) findViewById(R.id.priceText);
    commentText = (TextView) findViewById(R.id.commentText);
    image = (ImageView) findViewById(R.id.image);

    updateView(wine);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.single_wine, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    switch (item.getItemId()) {
      case R.id.edit_wine_button:
        Intent intent = new Intent(getBaseContext(), CreateOrEditWineActivity.class);
        intent.putExtra("id", wine.getId());
        startActivityForResult(intent, RequestCode.EDIT_WINE.ordinal());
        return true;
      case R.id.delete_wine_button:
        confirmDeleteDialog();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void confirmDeleteDialog() {
    new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.confirm_delete).setMessage(R.string.confirm_delete_message)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            WineDatabaseHandler.getInstance(getApplicationContext()).removeWine(wine);
            finish();
          }
        }).setNegativeButton(R.string.no, null).show();
  }

  private void updateView(Wine wine) {

    if (!wine.getName().equals("")) {
      nameText.setText(wine.getName());
    }
    if (wine.getCountry() != null && !wine.getCountry().equals("")) {
      countryText.setText(wine.getCountry());
    }
    if (wine.getYear() != -1) {
      yearText.setText("" + wine.getYear());
    }
    if (wine.getColor() != WineColor.UNKNOWN) {
      colorText.setText(wine.getColor().toString());
    }
    if (wine.getDescription() != null && !wine.getDescription().equals("")) {
      descText.setText(wine.getDescription());
    }
    if (wine.getRating() != -1) {
      ratingText.setText("" + wine.getRating());
    }
    else {
      ratingText.setText("Unrated");
    }
    if (!wine.getPrice().equals("")) {
      priceText.setText(wine.getPrice());
    }
    if (wine.getComment() != null && !wine.getComment().equals("")) {
      commentText.setText(wine.getComment());
    }
    else {
      commentText.setText("No comments yet.");
    }

    if (wine.getImageURL() != null && wine.getImageURL().startsWith("http")) {
      BitmapFactory.Options bmOptions;
      bmOptions = new BitmapFactory.Options();
      bmOptions.inSampleSize = 1;
      Bitmap bm = loadImage(wine.getImageURL(), bmOptions);
      if (bm != null) {
        image.setImageBitmap(bm);
        // TODO make this run in a separate thread
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RequestCode.EDIT_WINE.ordinal() && resultCode == Activity.RESULT_OK) {
      wine = WineDatabaseHandler.getInstance(this).getWine(wine.getId());
      updateView(wine);
    }
  }

  private Bitmap loadImage(String imageUrl, Options options) {
    Bitmap bitmap = null;
    InputStream in = null;
    try {
      in = openHttpConnection(imageUrl);
      if (in == null) {
        return bitmap;
      }
      bitmap = BitmapFactory.decodeStream(in, null, options);
      in.close();
    }
    catch (IOException e1) {

    }
    return bitmap;
  }

  private InputStream openHttpConnection(String streamUrl) {
    InputStream inputStream = null;
    try {
      URL url = new URL(streamUrl);
      URLConnection conn = url.openConnection();

      HttpURLConnection httpConn = (HttpURLConnection) conn;
      httpConn.setRequestMethod("GET");
      httpConn.connect();

      if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        inputStream = httpConn.getInputStream();
      }

    }
    catch (IOException e) {

    }

    return inputStream;
  }

}
