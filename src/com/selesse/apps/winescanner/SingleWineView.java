package com.selesse.apps.winescanner;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SingleWineView extends Activity {
	Wine w = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		

		Bundle b = this.getIntent().getExtras();
		if (b != null)
			w = (Wine) b.getSerializable("wine");
 
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
				i.putExtra("wine", w);
				startActivity(i);
				// TODO fix this stupid crap
			}
		});

		if (w.getName() != null && !w.getName().equals("null"))
			nameText.setText(w.getName());
		if (w.getCountry() != null && !w.getCountry().equals("null"))
			countryText.setText(w.getCountry());
		if (!w.getYear().equals("null"))
			yearText.setText(String.valueOf(w.getYear()));
		if (w.getDescription() != null && !w.getDescription().equals("null"))
			descText.setText(w.getDescription());
		if (!w.getRating().equals("null"))
			ratingText.setText(String.valueOf(w.getRating()));
		else
			ratingText.setText("Unrated");
		if (!w.getPrice().equals("null"))
			priceText.setText(w.getPrice());
		if (w.getComment() != null && !w.getComment().equals("null"))
			commentText.setText(w.getComment());
		else
			commentText.setText("No comments yet.");

		if (w.getImageURL() != null && w.getImageURL().startsWith("http")) {
			BitmapFactory.Options bmOptions;
			bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 1;
			Bitmap bm = LoadImage(w.getImageURL(), bmOptions);
			image.setImageBitmap(bm);
		}
	}

	private Bitmap LoadImage(String URL, BitmapFactory.Options options) {
		Bitmap bitmap = null;
		InputStream in = null;
		try {
			in = OpenHttpConnection(URL);
			bitmap = BitmapFactory.decodeStream(in, null, options);
			in.close();
		} catch (IOException e1) {
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
		} catch (Exception ex) {
		}
		return inputStream;
	}
	
	
}
