package com.selesse.android.winedb.util.impl.sqlite;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.collect.Lists;
import com.selesse.android.winedb.model.Wine;
import com.selesse.android.winedb.model.Wine.WineColor;
import com.selesse.android.winedb.util.WineDatabase;

public class WinesDataSource implements WineDatabase {
  private SQLiteDatabase db;
  private WineSQLiteHelper dbHelper;
  private String[] allColumns = {
      WineSQLiteHelper.COLUMN_ID,
      WineSQLiteHelper.COLUMN_BARCODE,
      WineSQLiteHelper.COLUMN_NAME,
      WineSQLiteHelper.COLUMN_RATING,
      WineSQLiteHelper.COLUMN_COMMENT,
      WineSQLiteHelper.COLUMN_COUNTRY,
      WineSQLiteHelper.COLUMN_DESCRIPTION,
      WineSQLiteHelper.COLUMN_IMAGE_URL,
      WineSQLiteHelper.COLUMN_PRICE,
      WineSQLiteHelper.COLUMN_YEAR,
      WineSQLiteHelper.COLUMN_COLOR };

  public WinesDataSource(Context context) {
    dbHelper = new WineSQLiteHelper(context);
  }

  @Override
  public void open() {
    try {
      db = dbHelper.getWritableDatabase();
    }
    catch (SQLException e) {
      Log.w(WinesDataSource.class.getName(), "Could not get writable database");
    }
  }

  @Override
  public void close() {
    dbHelper.close();
  }

  @Override
  public Wine createWine(Wine wine) {
    ContentValues values = new ContentValues();

    values.put(WineSQLiteHelper.COLUMN_BARCODE, wine.getBarcode());
    values.put(WineSQLiteHelper.COLUMN_NAME, wine.getName());
    values.put(WineSQLiteHelper.COLUMN_RATING, wine.getRating());
    values.put(WineSQLiteHelper.COLUMN_COMMENT, wine.getComment());
    values.put(WineSQLiteHelper.COLUMN_COUNTRY, wine.getCountry());
    values.put(WineSQLiteHelper.COLUMN_DESCRIPTION, wine.getDescription());
    values.put(WineSQLiteHelper.COLUMN_IMAGE_URL, wine.getImageURL());
    values.put(WineSQLiteHelper.COLUMN_PRICE, wine.getPrice());
    values.put(WineSQLiteHelper.COLUMN_YEAR, wine.getYear());
    values.put(WineSQLiteHelper.COLUMN_COLOR, wine.getColor().toString());

    long insertId = db.insert(WineSQLiteHelper.TABLE_WINES, null, values);

    Cursor cursor = db.query(WineSQLiteHelper.TABLE_WINES, allColumns, WineSQLiteHelper.COLUMN_ID
        + " = " + insertId, null, null, null, null);
    cursor.moveToFirst();
    Wine newWine = cursorToWine(cursor);
    cursor.close();

    return newWine;
  }

  @Override
  public void deleteWine(Wine wine) {
    long id = wine.getId();
    db.delete(WineSQLiteHelper.TABLE_WINES, WineSQLiteHelper.COLUMN_ID + " = " + id, null);
  }

  @Override
  public List<Wine> getAllWines() {
    List<Wine> wines = Lists.newArrayList();

    Cursor cursor = db
        .query(WineSQLiteHelper.TABLE_WINES, allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Wine wine = cursorToWine(cursor);
      wines.add(wine);
      cursor.moveToNext();
    }
    cursor.close();

    return wines;
  }

  private Wine cursorToWine(Cursor cursor) {
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
      wine.setColor(WineColor.valueOf(cursor.getString(10)));
    }
    catch (IllegalArgumentException e) {
      wine.setColor(WineColor.UNKNOWN);
    }

    return wine;
  }

}
