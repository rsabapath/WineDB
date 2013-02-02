package com.selesse.android.winedb.database;

import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.selesse.android.winedb.model.Wine;
import com.selesse.android.winedb.model.WineColor;

public class WinesDataSource {
  private SQLiteDatabase db;
  private WineDatabaseHandler dbHelper;
  private String[] allColumns = {
      WineTable.COLUMN_ID,
      WineTable.COLUMN_BARCODE,
      WineTable.COLUMN_NAME,
      WineTable.COLUMN_RATING,
      WineTable.COLUMN_COMMENT,
      WineTable.COLUMN_COUNTRY,
      WineTable.COLUMN_DESCRIPTION,
      WineTable.COLUMN_IMAGE_URL,
      WineTable.COLUMN_PRICE,
      WineTable.COLUMN_YEAR,
      WineTable.COLUMN_COLOR };

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

    values.put(WineTable.COLUMN_BARCODE, wine.getBarcode());
    values.put(WineTable.COLUMN_NAME, wine.getName());
    values.put(WineTable.COLUMN_RATING, wine.getRating());
    values.put(WineTable.COLUMN_COMMENT, wine.getComment());
    values.put(WineTable.COLUMN_COUNTRY, wine.getCountry());
    values.put(WineTable.COLUMN_DESCRIPTION, wine.getDescription());
    values.put(WineTable.COLUMN_IMAGE_URL, wine.getImageURL());
    values.put(WineTable.COLUMN_PRICE, wine.getPrice());
    values.put(WineTable.COLUMN_YEAR, wine.getYear());
    values.put(WineTable.COLUMN_COLOR, wine.getColor().toString());

    long insertId = db.insert(WineTable.TABLE_WINES, null, values);

    Cursor cursor = db.query(WineTable.TABLE_WINES, allColumns, WineTable.COLUMN_ID + " = "
        + insertId, null, null, null, null);
    cursor.moveToFirst();
    Wine newWine = cursorToWine(cursor);
    cursor.close();

    return newWine;
  }

  public void deleteWine(Wine wine) {
    long id = wine.getId();
    db.delete(WineTable.TABLE_WINES, WineTable.COLUMN_ID + " = " + id, null);
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

    values.put(WineTable.COLUMN_BARCODE, wine.getBarcode());
    values.put(WineTable.COLUMN_NAME, wine.getName());
    values.put(WineTable.COLUMN_RATING, wine.getRating());
    values.put(WineTable.COLUMN_COMMENT, wine.getComment());
    values.put(WineTable.COLUMN_COUNTRY, wine.getCountry());
    values.put(WineTable.COLUMN_DESCRIPTION, wine.getDescription());
    values.put(WineTable.COLUMN_IMAGE_URL, wine.getImageURL());
    values.put(WineTable.COLUMN_PRICE, wine.getPrice());
    values.put(WineTable.COLUMN_YEAR, wine.getYear());
    values.put(WineTable.COLUMN_COLOR, wine.getColor().toString());

    db.update(WineTable.TABLE_WINES, values, WineTable.COLUMN_ID + " = " + wine.getId(), null);

  }

  public Cursor getAllWines() {
    Cursor cursor = db.query(WineTable.TABLE_WINES, allColumns, null, null, null, null, null);

    return cursor;
  }

}
