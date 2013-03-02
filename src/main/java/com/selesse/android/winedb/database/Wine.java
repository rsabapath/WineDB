package com.selesse.android.winedb.database;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.selesse.android.winedb.R;
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

  /**
   * Constructor used as a cloner. Will create a new {@link Wine} based entirely on the wine
   * parameter passed to the constructor.
   *
   * @param wine
   *          The model Wine that we're essentially cloning.
   */
  public Wine(Wine wine) {
    this.id = wine.getId();
    this.barcode = wine.getBarcode();
    this.name = wine.getName();
    this.rating = wine.getRating();
    this.comment = wine.getComment();
    this.country = wine.getCountry();
    this.description = wine.getDescription();
    this.imageURL = wine.getImageURL();
    this.price = wine.getPrice();
    this.year = wine.getYear();
    this.color = wine.getColor();
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((barcode == null) ? 0 : barcode.hashCode());
    result = prime * result + ((color == null) ? 0 : color.hashCode());
    result = prime * result + ((comment == null) ? 0 : comment.hashCode());
    result = prime * result + ((country == null) ? 0 : country.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((imageURL == null) ? 0 : imageURL.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((price == null) ? 0 : price.hashCode());
    result = prime * result + rating;
    result = prime * result + year;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Wine other = (Wine) obj;
    if (barcode == null) {
      if (other.barcode != null) {
        return false;
      }
    }
    else if (!barcode.equals(other.barcode)) {
      return false;
    }
    if (color != other.color) {
      return false;
    }
    if (comment == null) {
      if (other.comment != null) {
        return false;
      }
    }
    else if (!comment.equals(other.comment)) {
      return false;
    }
    if (country == null) {
      if (other.country != null) {
        return false;
      }
    }
    else if (!country.equals(other.country)) {
      return false;
    }
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    }
    else if (!description.equals(other.description)) {
      return false;
    }
    if (id != other.id) {
      return false;
    }
    if (imageURL == null) {
      if (other.imageURL != null) {
        return false;
      }
    }
    else if (!imageURL.equals(other.imageURL)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
      return false;
    }
    if (price == null) {
      if (other.price != null) {
        return false;
      }
    }
    else if (!price.equals(other.price)) {
      return false;
    }
    if (rating != other.rating) {
      return false;
    }
    if (year != other.year) {
      return false;
    }
    return true;
  }

  /**
   * Tells you whether or not the value of columnIndex is numeric in nature.
   *
   * @param columnIndex
   *          The DB column index of the wine.
   * @return Whether or not columnIndex is numeric.
   */
  public static boolean isNumericColumn(int columnIndex) {
    return columnIndex == 1 || columnIndex == 3 || columnIndex == 9;
  }

  public static boolean isColor(int columnIndex) {
    return columnIndex == 10;
  }

  public static List<String> getLocalizedSortStrings(Resources resources) {
    String name = resources.getString(R.string.wine_name);
    String rating = resources.getString(R.string.wine_rating);
    String year = resources.getString(R.string.wine_year);
    String country = resources.getString(R.string.wine_country);
    String color = resources.getString(R.string.wine_color);
    String price = resources.getString(R.string.wine_price);
    return Arrays.asList(name, rating, year, country, color, price);
  }

}
