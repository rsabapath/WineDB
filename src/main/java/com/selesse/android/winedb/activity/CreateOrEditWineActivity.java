package com.selesse.android.winedb.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.selesse.android.winedb.R;
import com.selesse.android.winedb.contentprovider.WineContentProvider;
import com.selesse.android.winedb.model.Wine;
import com.selesse.android.winedb.model.Wine.WineColor;

public class CreateOrEditWineActivity extends Activity {

  Wine wine = null;
  EditText barcodeText, nameText, countryText, yearText, descText, ratingText, priceText,
      commentText, imageText;
  Spinner spinner;
  boolean editMode = false;

  private Uri wineUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Bundle bundle = this.getIntent().getExtras();

    if (savedInstanceState != null) {
      wineUri = (Uri) savedInstanceState.getParcelable(WineContentProvider.CONTENT_ITEM_TYPE);
      wine = WineContentProvider.getWineFromUri(wineUri, getContentResolver());
    }
    if (bundle != null) {
      editMode = true;
      wine = (Wine) bundle.getSerializable("wine");
      wineUri = (Uri) bundle.getParcelable(WineContentProvider.CONTENT_ITEM_TYPE);

      if (wineUri == null) {
        wineUri = Uri.parse(WineContentProvider.CONTENT_URI + "/" + wine.getId());
      }
      if (wine == null) {
        wine = WineContentProvider.getWineFromUri(wineUri, getContentResolver());
      }
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

    spinner = (Spinner) findViewById(R.id.wineColorSpinner);
    ArrayAdapter<WineColor> spinnerArrayAdapter = new ArrayAdapter<WineColor>(this,
        android.R.layout.simple_spinner_item, Wine.WineColor.values());
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(spinnerArrayAdapter);

    // if we're in edit mode, go through all the fields - if they're valid, display them
    if (editMode) {
      if (!wine.getBarcode().equals("")) {
        barcodeText.setText(wine.getBarcode());
      }
      if (!wine.getName().equals("")) {
        nameText.setText(wine.getName());
      }
      if (!wine.getCountry().equals("")) {
        countryText.setText(wine.getCountry());
      }
      if (wine.getYear() > 0 && wine.getYear() < 2500) {
        yearText.setText(String.valueOf(wine.getYear()));
      }
      if (!wine.getDescription().equals("")) {
        descText.setText(wine.getDescription());
      }
      if (wine.getRating() > 0 && wine.getRating() < 11) {
        ratingText.setText(String.valueOf(wine.getRating()));
      }
      if (!wine.getPrice().equals("")) {
        priceText.setText(wine.getPrice());
      }
      if (!wine.getComment().equals("")) {
        commentText.setText(wine.getComment());
      }
      if (!wine.getImageURL().equals("")) {
        imageText.setText(wine.getImageURL());
      }
      if (wine.getColor() != WineColor.UNKNOWN) {
        spinner.setSelection(wine.getColor().ordinal());
      }
    }

    Button save = (Button) findViewById(R.id.saveWine);
    save.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        // make sure that if you're pressing save, you save a name
        if (nameText.getText().toString().trim().length() == 0) {
          Toast.makeText(getApplicationContext(), R.string.empty_name, Toast.LENGTH_SHORT).show();
          nameText.setFocusableInTouchMode(true);
          nameText.requestFocus();

          ScrollView scrollView = (ScrollView) findViewById(R.id.edit_wine_scrollview);
          scrollView.scrollTo(0, 0);
          return;
        }

        if (barcodeText.getText().toString().length() > 0) {
          wine.setBarcode(barcodeText.getText().toString());
        }

        if (nameText.getText().toString().length() > 0) {
          wine.setName(nameText.getText().toString());
        }

        if (countryText.getText().toString().length() > 0) {
          wine.setCountry(countryText.getText().toString());
        }

        if (yearText.getText().toString().length() > 0) {
          wine.setYear(Integer.parseInt(yearText.getText().toString()));
        }

        if (descText.getText().toString().length() > 0) {
          wine.setDescription(descText.getText().toString());
        }

        if (ratingText.getText().toString().length() > 0) {
          wine.setRating(Integer.parseInt(ratingText.getText().toString()));
        }

        if (priceText.getText().toString().length() > 0) {
          wine.setPrice(priceText.getText().toString());
        }

        if (commentText.getText().toString().length() > 0) {
          wine.setComment(commentText.getText().toString());
        }

        if (imageText.getText().toString().length() > 0) {
          wine.setImageURL(imageText.getText().toString());
        }

        if (spinner.getSelectedItemPosition() > 0) {
          wine.setColor(WineColor.values()[spinner.getSelectedItemPosition()]);
        }

        Intent data = new Intent();
        data.putExtra("wine", wine);
        setResult(RESULT_OK, data);
        finish();
      }
    });

  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putParcelable(WineContentProvider.CONTENT_ITEM_TYPE, wineUri);
  }

}
