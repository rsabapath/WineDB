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
import com.selesse.android.winedb.model.Wine;

public class SingleWineView extends Activity {
  Wine wine = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    Bundle bundle = this.getIntent().getExtras();
    if (bundle != null)
      wine = (Wine) bundle.getSerializable("wine");

    super.onCreate(savedInstanceState);
    setContentView(R.layout.single_wine);

    TextView nameText = (TextView) findViewById(R.id.wineNameText);
    TextView countryText = (TextView) findViewById(R.id.countryText);
    TextView yearText = (TextView) findViewById(R.id.yearText);
    TextView descText = (TextView) findViewById(R.id.descText);
    TextView ratingText = (TextView) findViewById(R.id.ratingText);
    TextView priceText = (TextView) findViewById(R.id.priceText);
    TextView commentText = (TextView) findViewById(R.id.commentText);
    ImageView image = (ImageView) findViewById(R.id.image);
    Button editButton = (Button) findViewById(R.id.editWine);

    editButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent i = new Intent(getBaseContext(), EditWineView.class);
        i.putExtra("wine", wine);
        startActivity(i);
      }
    });

    if (wine.getName() != null && !wine.getName().equals(""))
      nameText.setText(wine.getName());
    if (wine.getCountry() != null && !wine.getCountry().equals(""))
      countryText.setText(wine.getCountry());
    if (wine.getYear() > 1500)
      yearText.setText(String.valueOf(wine.getYear()));
    if (wine.getDescription() != null && !wine.getDescription().equals(""))
      descText.setText(wine.getDescription());
    if (wine.getRating() > 0)
      ratingText.setText(String.valueOf(wine.getRating()));
    else
      ratingText.setText("Unrated");
    if (!wine.getPrice().equals(""))
      priceText.setText(wine.getPrice());
    if (wine.getComment() != null && !wine.getComment().equals(""))
      commentText.setText(wine.getComment());
    else
      commentText.setText("No comments yet.");

    if (wine.getImageURL() != null && wine.getImageURL().startsWith("http")) {
      BitmapFactory.Options bmOptions;
      bmOptions = new BitmapFactory.Options();
      bmOptions.inSampleSize = 1;
      Bitmap bm = LoadImage(wine.getImageURL(), bmOptions);
      if (bm != null)
        image.setImageBitmap(bm);
      // TODO make this run in a separate thread
    }
  }

  private Bitmap LoadImage(String URL, Options options) {
    Bitmap bitmap = null;
    InputStream in = null;
    try {
      in = OpenHttpConnection(URL);
      if (in == null)
        return bitmap;
      bitmap = BitmapFactory.decodeStream(in, null, options);
      in.close();
    }
    catch (IOException e1) {

    }
    return bitmap;
  }

  private InputStream OpenHttpConnection(String strURL) {
    InputStream inputStream = null;
    try {
      URL url = new URL(strURL);
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
