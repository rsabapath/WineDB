package com.selesse.android.winedb.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.selesse.android.winedb.database.Wine;
import com.selesse.android.winedb.database.WineDatabaseHandler;

public class WineContentProvider extends ContentProvider {

  private WineDatabaseHandler db;
  private static final String AUTHORITY = "com.selesse.android.winedb.contentprovider.WineContentProvider";
  public static final int WINES = 100;
  public static final int WINE_ID = 110;
  private static final String BASE_PATH = "wines";
  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
  public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/wines";
  public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/wines";

  private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  static {
    sURIMatcher.addURI(AUTHORITY, BASE_PATH, WINES);
    sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", WINE_ID);
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase sqlDB = db.getWritableDatabase();
    int rowsDeleted = 0;
    switch (uriType) {
      case WINES:
        rowsDeleted = sqlDB.delete(Wine.TABLE_WINES, selection, selectionArgs);
        break;
      case WINE_ID:
        String id = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection)) {
          rowsDeleted = sqlDB.delete(Wine.TABLE_WINES, Wine.COLUMN_ID + "=" + id, null);
        }
        else {
          rowsDeleted = sqlDB.delete(Wine.TABLE_WINES, Wine.COLUMN_ID + "=" + id + " and "
              + selection, selectionArgs);
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return rowsDeleted;
  }

  @Override
  public String getType(Uri uri) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase sqlDB = db.getWritableDatabase();
    long id = 0;
    switch (uriType) {
      case WINES:
        id = sqlDB.insert(Wine.TABLE_WINES, null, values);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return Uri.parse(BASE_PATH + "/" + id);
  }

  @Override
  public boolean onCreate() {
    db = new WineDatabaseHandler(getContext());
    return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sortOrder) {
    Cursor result = null;
    if (CONTENT_URI.equals(uri)) {
      result = WineDatabaseHandler.getInstance(getContext()).getReadableDatabase()
          .query(Wine.TABLE_WINES, Wine.FIELDS, null, null, null, null, null, null);
      result.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
    }
    else if (uri.toString().startsWith(CONTENT_ITEM_TYPE)) {
      final long id = Long.parseLong(uri.getLastPathSegment());
      result = WineDatabaseHandler
          .getInstance(getContext())
          .getReadableDatabase()
          .query(Wine.TABLE_WINES, Wine.FIELDS, Wine.COLUMN_ID + " IS ?",
              new String[] { String.valueOf(id) }, null, null, null, null);
      result.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
    }
    else {
      throw new UnsupportedOperationException("Not yet implemented");
    }

    return result;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase sqlDB = db.getWritableDatabase();
    int rowsUpdated = 0;
    switch (uriType) {
      case WINES:
        rowsUpdated = sqlDB.update(Wine.TABLE_WINES, values, selection, selectionArgs);
        break;
      case WINE_ID:
        String id = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection)) {
          rowsUpdated = sqlDB.update(Wine.TABLE_WINES, values, Wine.COLUMN_ID + "=" + id, null);
        }
        else {
          rowsUpdated = sqlDB.update(Wine.TABLE_WINES, values, Wine.COLUMN_ID + "=" + id + " and "
              + selection, selectionArgs);
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return rowsUpdated;
  }

}
