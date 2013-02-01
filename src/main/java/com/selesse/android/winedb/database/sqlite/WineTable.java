package com.selesse.android.winedb.database.sqlite;

import android.database.sqlite.SQLiteDatabase;

public class WineTable {
  public static final String TABLE_WINES = "wines";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_BARCODE = "barcode";
  public static final String COLUMN_NAME = "name";
  public static final String COLUMN_RATING = "rating";
  public static final String COLUMN_COMMENT = "comment";
  public static final String COLUMN_COUNTRY = "country";
  public static final String COLUMN_DESCRIPTION = "description";
  public static final String COLUMN_IMAGE_URL = "imageUrl";
  public static final String COLUMN_PRICE = "price";
  public static final String COLUMN_YEAR = "year";
  public static final String COLUMN_COLOR = "color";

  private static final String DATABASE_CREATE = "create table " + TABLE_WINES + "(" + COLUMN_ID
      + " integer primary key autoincrement, " + COLUMN_BARCODE + " text, " + COLUMN_NAME
      + " text, " + COLUMN_RATING + " integer, " + COLUMN_COMMENT + " text, " + COLUMN_COUNTRY
      + " text, " + COLUMN_DESCRIPTION + " text, " + COLUMN_IMAGE_URL + " text," + COLUMN_PRICE
      + " text," + COLUMN_YEAR + " integer, " + COLUMN_COLOR + " text);";

  public static void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    database.execSQL("DROP TABLE IF EXISTS " + TABLE_WINES);
    onCreate(database);
  }
}
