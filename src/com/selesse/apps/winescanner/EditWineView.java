package com.selesse.apps.winescanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditWineView extends Activity {

	Wine w = null;
	EditText barcodeText, nameText, countryText, yearText, descText,
			ratingText, priceText, commentText, imageText;
	boolean editMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			w = (Wine) b.getSerializable("wine");
			editMode = true;
		} else {
			editMode = false;
			w = new Wine();
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_wine);

		barcodeText = (EditText) findViewById(R.id.barcodeEditText);
		nameText = (EditText) findViewById(R.id.nameEditText);
		countryText = (EditText) findViewById(R.id.countryEditText);
		yearText = (EditText) findViewById(R.id.yearEditText);
		descText = (EditText) findViewById(R.id.descEditText);
		ratingText = (EditText) findViewById(R.id.ratingEditText);
		priceText = (EditText) findViewById(R.id.priceEditText);
		commentText = (EditText) findViewById(R.id.commentEditText);
		imageText = (EditText) findViewById(R.id.imageEditText);

		if (editMode) {
			if (!w.getBarcode().equals("null"))
				barcodeText.setText(w.getBarcode());
			if (!w.getName().equals("null"))
				nameText.setText(w.getName());
			if (!w.getCountry().equals("null"))
				countryText.setText(w.getCountry());
			if (!w.getYear().equals("null"))
				yearText.setText(String.valueOf(w.getYear()));
			if (!w.getDescription().equals("null"))
				descText.setText(w.getDescription());
			if (!w.getRating().equals("null"))
				ratingText.setText(String.valueOf(w.getRating()));
			if (!w.getPrice().equals("null"))
				priceText.setText(w.getPrice());
			if (!w.getComment().equals("null"))
				commentText.setText(w.getComment());
			if (!w.getImageURL().equals("null"))
				imageText.setText(w.getImageURL());
		}

		Button save = (Button) findViewById(R.id.saveWine);
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (barcodeText.getText().toString().length() > 0)
					w.setBarcode(barcodeText.getText().toString());
				else
					w.setBarcode("null");

				if (nameText.getText().toString().length() > 0)
					w.setName(nameText.getText().toString());
				else
					w.setName("null");

				if (countryText.getText().toString().length() > 0)
					w.setCountry(countryText.getText().toString());
				else
					w.setCountry("null");

				if (yearText.getText().toString().length() > 0)
					w.setYear(yearText.getText().toString());
				else
					w.setYear("null");

				if (descText.getText().toString().length() > 0)
					w.setDescription(descText.getText().toString());
				else
					w.setDescription("null");

				if (ratingText.getText().toString().length() > 0)
					w.setRating(ratingText.getText().toString());
				else
					w.setRating("null");

				if (priceText.getText().toString().length() > 0)
					w.setPrice(priceText.getText().toString());
				else
					w.setPrice("null");

				if (commentText.getText().toString().length() > 0)
					w.setComment(commentText.getText().toString());
				else
					w.setComment("null");

				if (imageText.getText().toString().length() > 0)
					w.setImageURL(imageText.getText().toString());
				else
					w.setImageURL("null");

				Intent data = new Intent();
				data.putExtra("Wine", w);
				setResult(RESULT_OK, data);
				finish();
			}
		});

	}

}
