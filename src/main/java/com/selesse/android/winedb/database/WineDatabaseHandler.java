package com.selesse.android.winedb.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.selesse.android.winedb.contentprovider.WineContentProvider;

public class WineDatabaseHandler extends SQLiteOpenHelper {

  private static WineDatabaseHandler singleton;

  public static WineDatabaseHandler getInstance(final Context context) {
    if (singleton == null) {
      singleton = new WineDatabaseHandler(context);
    }
    return singleton;
  }

  private static final String DATABASE_NAME = "wines.db";
  private static final int DATABASE_VERSION = 1;
  private Context context;

  public WineDatabaseHandler(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    this.context = context;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    WineTable.onCreate(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    WineTable.onUpgrade(db, oldVersion, newVersion);
  }

  public synchronized WineTable getWine(final long id) {
    final SQLiteDatabase db = this.getReadableDatabase();
    final Cursor cursor = db.query(WineTable.TABLE_WINES, WineTable.FIELDS, WineTable.COLUMN_ID
        + " IS ?", new String[] { String.valueOf(id) }, null, null, null, null);
    if (cursor == null || cursor.isAfterLast()) {
      return null;
    }

    WineTable item = null;
    if (cursor.moveToFirst()) {
      item = new WineTable(cursor);
    }
    cursor.close();

    return item;
  }

  public synchronized boolean putWineTable(final WineTable wine) {
    boolean success = false;
    int result = 0;
    final SQLiteDatabase db = this.getWritableDatabase();

    if (wine.id > -1) {
      result += db.update(WineTable.TABLE_WINES, wine.getContent(), WineTable.COLUMN_ID + " IS ?",
          new String[] { String.valueOf(wine.id) });
    }

    if (result > 0) {
      success = true;
    }
    else {
      // Update failed or wasn't possible, insert instead
      final long id = db.insert(WineTable.TABLE_WINES, null, wine.getContent());

      if (id > -1) {
        wine.id = id;
        success = true;
      }
    }

    if (success) {
      notifyProviderOnPersonChange();
    }

    return success;
  }

  public synchronized int removeWine(final WineTable wine) {
    final SQLiteDatabase db = this.getWritableDatabase();
    final int result = db.delete(WineTable.TABLE_WINES, WineTable.COLUMN_ID + " IS ?",
        new String[] { Long.toString(wine.id) });

    if (result > 0) {
      notifyProviderOnPersonChange();
    }
    return result;
  }

  private void notifyProviderOnPersonChange() {
    context.getContentResolver().notifyChange(WineContentProvider.CONTENT_URI, null, false);
  }

}
