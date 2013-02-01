package com.selesse.android.winedb.contentprovider;

import java.util.Locale;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.selesse.android.winedb.database.sqlite.WineDatabaseHelper;
import com.selesse.android.winedb.database.sqlite.WineTable;
import com.selesse.android.winedb.model.Wine;
import com.selesse.android.winedb.model.Wine.WineColor;

public class WineContentProvider extends ContentProvider {

  private WineDatabaseHelper db;
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
        rowsDeleted = sqlDB.delete(WineTable.TABLE_WINES, selection, selectionArgs);
        break;
      case WINE_ID:
        String id = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection)) {
          rowsDeleted = sqlDB.delete(WineTable.TABLE_WINES, WineTable.COLUMN_ID + "=" + id, null);
        }
        else {
          rowsDeleted = sqlDB.delete(WineTable.TABLE_WINES, WineTable.COLUMN_ID + "=" + id
              + " and " + selection, selectionArgs);
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
        id = sqlDB.insert(WineTable.TABLE_WINES, null, values);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return Uri.parse(BASE_PATH + "/" + id);
  }

  @Override
  public boolean onCreate() {
    db = new WineDatabaseHelper(getContext());
    return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sortOrder) {
    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    queryBuilder.setTables(WineTable.TABLE_WINES);
    int uriType = sURIMatcher.match(uri);
    switch (uriType) {
      case WINE_ID:
        queryBuilder.appendWhere(WineTable.COLUMN_ID + "=" + uri.getLastPathSegment());
        break;
      case WINES:
        // no filter
        break;
      default:
        throw new IllegalArgumentException("Unknown URI");
    }
    Cursor cursor = queryBuilder.query(db.getReadableDatabase(), projection, selection,
        selectionArgs, null, null, sortOrder);
    cursor.setNotificationUri(getContext().getContentResolver(), uri);
    return cursor;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase sqlDB = db.getWritableDatabase();
    int rowsUpdated = 0;
    switch (uriType) {
      case WINES:
        rowsUpdated = sqlDB.update(WineTable.TABLE_WINES, values, selection, selectionArgs);
        break;
      case WINE_ID:
        String id = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection)) {
          rowsUpdated = sqlDB.update(WineTable.TABLE_WINES, values, WineTable.COLUMN_ID + "=" + id,
              null);
        }
        else {
          rowsUpdated = sqlDB.update(WineTable.TABLE_WINES, values, WineTable.COLUMN_ID + "=" + id
              + " and " + selection, selectionArgs);
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return rowsUpdated;
  }

  public static Wine getWineFromUri(Uri wineUri, ContentResolver resolver) {
    String[] projection = {
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
    Wine wine = new Wine();

    Cursor cursor = resolver.query(wineUri, projection, null, null, null);
    if (cursor != null) {
      cursor.moveToFirst();
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

      // Always close the cursor
      cursor.close();
    }

    return wine;
  }

  public static ContentValues getContentValuesFromWine(Wine wine) {
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

    return values;
  }

}
