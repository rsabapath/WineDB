package com.selesse.android.winedb.util.impl.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WineSQLiteHelper extends SQLiteOpenHelper {

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

  private static final String DATABASE_NAME = "wines.db";
  private static final int DATABASE_VERSION = 1;

  private static final String DATABASE_CREATE = "create table " + TABLE_WINES + "(" + COLUMN_ID
      + " integer primary key autoincrement, " + COLUMN_BARCODE + " text not null, " + COLUMN_NAME
      + " text, " + COLUMN_RATING + " integer, " + COLUMN_COMMENT + " text, " + COLUMN_COUNTRY
      + " text, " + COLUMN_DESCRIPTION + " text, " + COLUMN_IMAGE_URL + " text," + COLUMN_PRICE
      + " text," + COLUMN_YEAR + " integer, " + COLUMN_COLOR + " text);";

  public WineSQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(DATABASE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(WineSQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion
        + " to " + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_WINES);
    onCreate(db);
  }

}
