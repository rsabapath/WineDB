package com.selesse.apps.winescanner;

import java.io.Serializable;

public class Wine implements Serializable, Comparable<Wine> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 231532486466908646L;
	public String barcode;
	public String name;
	public String rating;
	public String comment;
	public String country;
	public String description;
	public String imageURL;
	public String price;
	public String year;

	public Wine() 
	{
		
	}
	public Wine(String barcode, String name, String price, String description)
	{
		this.barcode = barcode;
		this.name = name;
		this.price = price;
		this.description = description;
		
		this.rating = "null";
		this.comment = "null";
		this.country = "null";
		this.year = "null";
		this.imageURL = "null";
		
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

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
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

	@Override
	public String toString() {
		return "Wine [barcode=" + barcode + ", name=" + name + ", rating="
				+ rating + ", comment=" + comment + ", country=" + country
				+ ", description=" + description + ", imageURL=" + imageURL
				+ ", price=" + price + ", year=" + year + "]";
	}
	@Override
	public int compareTo(Wine another) {
		return this.getName().compareTo(another.getName());
	}


}
