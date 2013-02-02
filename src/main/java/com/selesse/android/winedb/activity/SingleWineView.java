package com.selesse.android.winedb.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.selesse.android.winedb.R;
import com.selesse.android.winedb.database.WineDatabaseHandler;
import com.selesse.android.winedb.database.WineTable;
import com.selesse.android.winedb.model.RequestCode;
import com.selesse.android.winedb.model.WineColor;

public class SingleWineView extends Activity {
  WineTable wine = new WineTable();
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
      wine.id = (Long) savedInstanceState.get("id");
    }
    if (extras != null) {
      wine.id = extras.getLong("id");
    }
    if (wine.id > 0) {
      wine = WineDatabaseHandler.getInstance(this).getWine(wine.id);
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
    Button editButton = (Button) findViewById(R.id.editWine);

    editButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getBaseContext(), CreateOrEditWineActivity.class);
        intent.putExtra("id", wine.id);
        startActivityForResult(intent, RequestCode.EDIT_WINE.ordinal());
      }
    });

    updateView(wine);
  }

  private void updateView(WineTable wine) {

    if (!wine.getName().equals("")) {
      nameText.setText(wine.getName());
    }
    if (wine.getCountry() != null && !wine.getCountry().equals("")) {
      countryText.setText(wine.getCountry());
    }
    if (wine.getYear() > 1500) {
      yearText.setText(String.valueOf(wine.getYear()));
    }
    if (wine.getColor() != WineColor.UNKNOWN) {
      colorText.setText(wine.getColor().toString());
    }
    if (wine.getDescription() != null && !wine.getDescription().equals("")) {
      descText.setText(wine.getDescription());
    }
    if (wine.getRating() > 0) {
      ratingText.setText(String.valueOf(wine.getRating()));
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
      // FIXME
      
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
