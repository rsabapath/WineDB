package com.selesse.android.winescanner;

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

    if (wine.getName() != null && !wine.getName().equals("null"))
      nameText.setText(wine.getName());
    if (wine.getCountry() != null && !wine.getCountry().equals("null"))
      countryText.setText(wine.getCountry());
    if (!wine.getYear().equals("null"))
      yearText.setText(String.valueOf(wine.getYear()));
    if (wine.getDescription() != null && !wine.getDescription().equals("null"))
      descText.setText(wine.getDescription());
    if (!wine.getRating().equals("null"))
      ratingText.setText(String.valueOf(wine.getRating()));
    else
      ratingText.setText("Unrated");
    if (!wine.getPrice().equals("null"))
      priceText.setText(wine.getPrice());
    if (wine.getComment() != null && !wine.getComment().equals("null"))
      commentText.setText(wine.getComment());
    else
      commentText.setText("No comments yet.");

    if (wine.getImageURL() != null && wine.getImageURL().startsWith("http")) {
      BitmapFactory.Options bmOptions;
      bmOptions = new BitmapFactory.Options();
      bmOptions.inSampleSize = 1;
      Bitmap bm = LoadImage(wine.getImageURL(), bmOptions);
      image.setImageBitmap(bm);
    }
  }

  private Bitmap LoadImage(String URL, Options options) {
    Bitmap bitmap = null;
    InputStream in = null;
    try {
      in = OpenHttpConnection(URL);
      bitmap = BitmapFactory.decodeStream(in, null, options);
      in.close();
    }
    catch (IOException e1) {
    }
    return bitmap;
  }

  private InputStream OpenHttpConnection(String strURL) throws IOException {
    InputStream inputStream = null;
    URL url = new URL(strURL);
    URLConnection conn = url.openConnection();

    try {
      HttpURLConnection httpConn = (HttpURLConnection) conn;
      httpConn.setRequestMethod("GET");
      httpConn.connect();

      if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        inputStream = httpConn.getInputStream();
      }
    }
    catch (Exception ex) {
    }
    return inputStream;
  }

}
