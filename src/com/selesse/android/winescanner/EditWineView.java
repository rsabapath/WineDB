package com.selesse.android.winescanner;

import com.selesse.android.winescanner.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditWineView extends Activity {

  Wine wine = null;
  EditText barcodeText, nameText, countryText, yearText, descText, ratingText, priceText, commentText, imageText;
  boolean editMode;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    Bundle bundle = this.getIntent().getExtras();
    if (bundle != null) {
      wine = (Wine) bundle.getSerializable("wine");
      editMode = true;
    }
    else {
      editMode = false;
      wine = new Wine();
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
      if (!wine.getBarcode().equals("null"))
        barcodeText.setText(wine.getBarcode());
      if (!wine.getName().equals("null"))
        nameText.setText(wine.getName());
      if (!wine.getCountry().equals("null"))
        countryText.setText(wine.getCountry());
      if (!wine.getYear().equals("null"))
        yearText.setText(String.valueOf(wine.getYear()));
      if (!wine.getDescription().equals("null"))
        descText.setText(wine.getDescription());
      if (!wine.getRating().equals("null"))
        ratingText.setText(String.valueOf(wine.getRating()));
      if (!wine.getPrice().equals("null"))
        priceText.setText(wine.getPrice());
      if (!wine.getComment().equals("null"))
        commentText.setText(wine.getComment());
      if (!wine.getImageURL().equals("null"))
        imageText.setText(wine.getImageURL());
    }

    Button save = (Button) findViewById(R.id.saveWine);
    save.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        if (barcodeText.getText().toString().length() > 0)
          wine.setBarcode(barcodeText.getText().toString());
        else
          wine.setBarcode("null");

        if (nameText.getText().toString().length() > 0)
          wine.setName(nameText.getText().toString());
        else
          wine.setName("null");

        if (countryText.getText().toString().length() > 0)
          wine.setCountry(countryText.getText().toString());
        else
          wine.setCountry("null");

        if (yearText.getText().toString().length() > 0)
          wine.setYear(yearText.getText().toString());
        else
          wine.setYear("null");

        if (descText.getText().toString().length() > 0)
          wine.setDescription(descText.getText().toString());
        else
          wine.setDescription("null");

        if (ratingText.getText().toString().length() > 0)
          wine.setRating(ratingText.getText().toString());
        else
          wine.setRating("null");

        if (priceText.getText().toString().length() > 0)
          wine.setPrice(priceText.getText().toString());
        else
          wine.setPrice("null");

        if (commentText.getText().toString().length() > 0)
          wine.setComment(commentText.getText().toString());
        else
          wine.setComment("null");

        if (imageText.getText().toString().length() > 0)
          wine.setImageURL(imageText.getText().toString());
        else
          wine.setImageURL("null");

        Intent data = new Intent();
        data.putExtra("Wine", wine);
        setResult(RESULT_OK, data);
        finish();
      }
    });

  }

}
