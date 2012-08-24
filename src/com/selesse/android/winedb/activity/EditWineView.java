package com.selesse.android.winedb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.selesse.android.winedb.R;
import com.selesse.android.winedb.model.Wine;

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
      if (!wine.getBarcode().equals(""))
        barcodeText.setText(wine.getBarcode());
      if (!wine.getName().equals(""))
        nameText.setText(wine.getName());
      if (!wine.getCountry().equals(""))
        countryText.setText(wine.getCountry());
      if (wine.getYear() > 0 && wine.getYear() < 2500)
        yearText.setText(String.valueOf(wine.getYear()));
      if (!wine.getDescription().equals(""))
        descText.setText(wine.getDescription());
      if (wine.getRating() > 0 && wine.getRating() < 11)
        ratingText.setText(String.valueOf(wine.getRating()));
      if (!wine.getPrice().equals(""))
        priceText.setText(wine.getPrice());
      if (!wine.getComment().equals(""))
        commentText.setText(wine.getComment());
      if (!wine.getImageURL().equals(""))
        imageText.setText(wine.getImageURL());
    }

    Button save = (Button) findViewById(R.id.saveWine);
    save.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        if (barcodeText.getText().toString().length() > 0)
          wine.setBarcode(barcodeText.getText().toString());

        if (nameText.getText().toString().length() > 0)
          wine.setName(nameText.getText().toString());

        if (countryText.getText().toString().length() > 0)
          wine.setCountry(countryText.getText().toString());

        if (yearText.getText().toString().length() > 0)
          wine.setYear(Integer.parseInt(yearText.getText().toString()));

        if (descText.getText().toString().length() > 0)
          wine.setDescription(descText.getText().toString());

        if (ratingText.getText().toString().length() > 0)
          wine.setRating(Integer.parseInt(ratingText.getText().toString()));

        if (priceText.getText().toString().length() > 0)
          wine.setPrice(priceText.getText().toString());

        if (commentText.getText().toString().length() > 0)
          wine.setComment(commentText.getText().toString());

        if (imageText.getText().toString().length() > 0)
          wine.setImageURL(imageText.getText().toString());

        Intent data = new Intent();
        data.putExtra("Wine", wine);
        setResult(RESULT_OK, data);
        finish();
      }
    });

  }

}
