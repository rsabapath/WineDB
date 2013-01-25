package com.selesse.android.winedb.model;

import java.io.Serializable;

/**
 * Model class for a wine object.
 *
 * @author Alex Selesse
 *
 */
public class Wine implements Serializable {

  private static final long serialVersionUID = 231532486466908646L;
  private long id;
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

  public enum Attribute {
    BARCODE("Barcode"),
    NAME("Name"),
    RATING("Rating"),
    COMMENT("Comment"),
    COUNTRY("Country"),
    DESCRIPTION("Description"),
    IMAGE_URL("Image URL"),
    PRICE("Price"),
    YEAR("Year"),
    WINE_COLOR("Wine color");

    private String name;

    private Attribute(String name) {
      this.name = name;
    }

    public String getAttributeName() {
      return name;
    }
  }

  public String getValueFromAttribute(Attribute attr) {
    switch (attr) {
      case BARCODE:
        return getBarcode();
      case COMMENT:
        return getComment();
      case COUNTRY:
        return getCountry();
      case DESCRIPTION:
        return getDescription();
      case IMAGE_URL:
        return getImageURL();
      case NAME:
        return getName();
      case PRICE:
        return getPrice();
      case RATING:
        return "" + getRating();
      case WINE_COLOR:
        return getColor().toString();
      case YEAR:
        return "" + getYear();
      default:
        return "programmer error";
    }
  }

  public void putValueFromAttribute(Attribute attr, String value) {
    switch (attr) {
      case BARCODE:
        setBarcode(value);
        break;
      case COMMENT:
        setComment(value);
        break;
      case COUNTRY:
        setCountry(value);
        break;
      case DESCRIPTION:
        setDescription(value);
        break;
      case IMAGE_URL:
        setImageURL(value);
        break;
      case NAME:
        setName(value);
        break;
      case PRICE:
        setPrice(value);
        break;
      case RATING:
        setRating(Integer.parseInt(value));
        break;
      case WINE_COLOR:
        setColor(WineColor.valueOf(value));
        break;
      case YEAR:
        setYear(Integer.parseInt(value));
        break;
    }
  }

  public enum WineColor {
    UNKNOWN(""),
    RED("Red"),
    WHITE("White"),
    ROSE("Rose");

    String name;

    private WineColor(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImageURL() {
    return imageURL;
  }

  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public WineColor getColor() {
    return color;
  }

  public void setColor(WineColor color) {
    this.color = color;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

}
