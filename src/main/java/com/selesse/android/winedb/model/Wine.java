package com.selesse.android.winedb.model;

import java.io.Serializable;

public class Wine implements Serializable {

  private static final long serialVersionUID = 231532486466908646L;
  private String barcode;
  private String name;
  private int rating;
  private String comment;
  private String country;
  private String description;
  private String imageURL;
  private String price;
  private int year;
  private WineColor color;

  public enum WineColor {
    RED("Red"), WHITE("White"), ROSE("Rose");

    String name;

    private WineColor(String name) {
      this.name = name;
    }

    public String toString() {
      return name;
    }
  }

  public Wine() {
    
  }

  public Wine(String barcode, String name, String price, String description) {
    this.barcode = barcode;
    this.name = name;
    this.price = price;
    this.description = description;
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

}
