package com.selesse.android.winedb.database;

import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.selesse.android.winedb.model.WineColor;

public class WinesDataSource {
  private SQLiteDatabase db;
  private WineDatabaseHandler dbHelper;
  private String[] allColumns = {
      Wine.COLUMN_ID,
      Wine.COLUMN_BARCODE,
      Wine.COLUMN_NAME,
      Wine.COLUMN_RATING,
      Wine.COLUMN_COMMENT,
      Wine.COLUMN_COUNTRY,
      Wine.COLUMN_DESCRIPTION,
      Wine.COLUMN_IMAGE_URL,
      Wine.COLUMN_PRICE,
      Wine.COLUMN_YEAR,
      Wine.COLUMN_COLOR };

  public WinesDataSource(Context context) {
    dbHelper = new WineDatabaseHandler(context);
  }

  public void open() {
    try {
      db = dbHelper.getWritableDatabase();
    }
    catch (SQLException e) {
      Log.w(WinesDataSource.class.getName(), "Could not get writable database");
    }
  }

  public void close() {
    dbHelper.close();
  }

  public Wine createWine(Wine wine) {
    ContentValues values = new ContentValues();

    values.put(Wine.COLUMN_BARCODE, wine.getBarcode());
    values.put(Wine.COLUMN_NAME, wine.getName());
    values.put(Wine.COLUMN_RATING, wine.getRating());
    values.put(Wine.COLUMN_COMMENT, wine.getComment());
    values.put(Wine.COLUMN_COUNTRY, wine.getCountry());
    values.put(Wine.COLUMN_DESCRIPTION, wine.getDescription());
    values.put(Wine.COLUMN_IMAGE_URL, wine.getImageURL());
    values.put(Wine.COLUMN_PRICE, wine.getPrice());
    values.put(Wine.COLUMN_YEAR, wine.getYear());
    values.put(Wine.COLUMN_COLOR, wine.getColor().toString());

    long insertId = db.insert(Wine.TABLE_WINES, null, values);

    Cursor cursor = db.query(Wine.TABLE_WINES, allColumns, Wine.COLUMN_ID + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    Wine newWine = cursorToWine(cursor);
    cursor.close();

    return newWine;
  }

  public void deleteWine(Wine wine) {
    long id = wine.getId();
    db.delete(Wine.TABLE_WINES, Wine.COLUMN_ID + " = " + id, null);
  }

  public Wine cursorToWine(Cursor cursor) {
    Wine wine = new Wine();

    wine.setId(cursor.getLong(0));
    wine.setBarcode(cursor.getString(1));
    wine.setName(cursor.getString(2));
    wine.setRating(cursor.getInt(3));
    wine.setComment(cursor.getString(4));
    wine.setCountry(cursor.getString(5));
    wine.setDescription(cursor.getString(6));
    wine.setImageURL(cursor.getString(7));
    wine.setPrice(cursor.getString(8));
    wine.setYear(cursor.getInt(9));
    try {
      wine.setColor(WineColor.valueOf(cursor.getString(10).toUpperCase(Locale.getDefault())));
    }
    catch (IllegalArgumentException e) {
      wine.setColor(WineColor.UNKNOWN);
    }

    return wine;
  }

  public void updateWine(Wine wine) {
    ContentValues values = new ContentValues();

    values.put(Wine.COLUMN_BARCODE, wine.getBarcode());
    values.put(Wine.COLUMN_NAME, wine.getName());
    values.put(Wine.COLUMN_RATING, wine.getRating());
    values.put(Wine.COLUMN_COMMENT, wine.getComment());
    values.put(Wine.COLUMN_COUNTRY, wine.getCountry());
    values.put(Wine.COLUMN_DESCRIPTION, wine.getDescription());
    values.put(Wine.COLUMN_IMAGE_URL, wine.getImageURL());
    values.put(Wine.COLUMN_PRICE, wine.getPrice());
    values.put(Wine.COLUMN_YEAR, wine.getYear());
    values.put(Wine.COLUMN_COLOR, wine.getColor().toString());

    db.update(Wine.TABLE_WINES, values, Wine.COLUMN_ID + " = " + wine.getId(), null);

  }

  public Cursor getAllWines() {
    Cursor cursor = db.query(Wine.TABLE_WINES, allColumns, null, null, null, null, null);

    return cursor;
  }

  /**
   * Tells you whether or not the value of the column is numeric.
   *
   * @param index
   *          The index of the cursor's column.
   * @return True if the Cursor's column index is numeric.
   */
  public static boolean isNumericColumn(int index) {
    return index == 1 || index == 3 || index == 9;
  }

}
