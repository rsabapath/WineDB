package com.selesse.android.winedb.database.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WineDatabaseHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "wines.db";
  private static final int DATABASE_VERSION = 1;

  public WineDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    WineTable.onCreate(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    WineTable.onUpgrade(db, oldVersion, newVersion);
  }

}
