package com.selesse.android.winedb.database;

import java.io.Serializable;
import java.util.Locale;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.selesse.android.winedb.model.WineColor;

public class Wine implements Serializable {
  private static final long serialVersionUID = 6500980482273835304L;
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

  public static final String[] FIELDS = {
      COLUMN_ID,
      COLUMN_BARCODE,
      COLUMN_NAME,
      COLUMN_RATING,
      COLUMN_COMMENT,
      COLUMN_COUNTRY,
      COLUMN_DESCRIPTION,
      COLUMN_IMAGE_URL,
      COLUMN_PRICE,
      COLUMN_YEAR,
      COLUMN_COLOR };

  private static final String DATABASE_CREATE = "create table " + TABLE_WINES + "(" + COLUMN_ID
      + " integer primary key autoincrement, " + COLUMN_BARCODE + " text, " + COLUMN_NAME
      + " text, " + COLUMN_RATING + " integer, " + COLUMN_COMMENT + " text, " + COLUMN_COUNTRY
      + " text, " + COLUMN_DESCRIPTION + " text, " + COLUMN_IMAGE_URL + " text," + COLUMN_PRICE
      + " text," + COLUMN_YEAR + " integer, " + COLUMN_COLOR + " text);";

  private long id = -1;
  private String barcode = "";
  private String name = "";
  private int rating = -1;
  private String comment = "";
  private String country = "";
  private String description = "";
  private String imageURL = "";
  private String price = "";
  private int year = -1;
  private WineColor color = WineColor.UNKNOWN;

  /*
   * No need to do anything, fields already have their default values.
   */
  public Wine() {

  }

  public Wine(final Cursor cursor) {
    this.id = cursor.getLong(0);
    this.barcode = cursor.getString(1);
    this.name = cursor.getString(2);
    this.rating = cursor.getInt(3);
    this.comment = cursor.getString(4);
    this.country = cursor.getString(5);
    this.description = cursor.getString(6);
    this.imageURL = cursor.getString(7);
    this.price = cursor.getString(8);
    this.year = cursor.getInt(9);

    try {
      this.color = WineColor.valueOf(cursor.getString(10).toUpperCase(Locale.getDefault()));
    }
    catch (IllegalArgumentException e) {
      this.color = WineColor.UNKNOWN;
    }
  }

  public ContentValues getContent() {
    final ContentValues values = new ContentValues();
    values.put(Wine.COLUMN_BARCODE, barcode);
    values.put(Wine.COLUMN_NAME, name);
    values.put(Wine.COLUMN_RATING, rating);
    values.put(Wine.COLUMN_COMMENT, comment);
    values.put(Wine.COLUMN_COUNTRY, country);
    values.put(Wine.COLUMN_DESCRIPTION, description);
    values.put(Wine.COLUMN_IMAGE_URL, imageURL);
    values.put(Wine.COLUMN_PRICE, price);
    values.put(Wine.COLUMN_YEAR, year);
    values.put(Wine.COLUMN_COLOR, color.toString());
    return values;
  }

  public static void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    // FIXME -> I doubt we want to kill the previous database
    database.execSQL("DROP TABLE IF EXISTS " + TABLE_WINES);
    onCreate(database);
  }

  public long getId() {
    return id;
  }

  public String getBarcode() {
    return barcode;
  }

  public String getName() {
    return name;
  }

  public int getRating() {
    return rating;
  }

  public String getComment() {
    return comment;
  }

  public String getCountry() {
    return country;
  }

  public String getDescription() {
    return description;
  }

  public String getImageURL() {
    return imageURL;
  }

  public String getPrice() {
    return price;
  }

  public int getYear() {
    return year;
  }

  public WineColor getColor() {
    return color;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public void setColor(WineColor color) {
    this.color = color;
  }

}
