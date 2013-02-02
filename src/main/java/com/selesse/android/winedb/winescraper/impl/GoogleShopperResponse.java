package com.selesse.android.winedb.winescraper.impl;

import java.util.List;

import com.google.common.collect.Lists;
import com.selesse.android.winedb.database.Wine;
import com.selesse.android.winedb.winescraper.AbstractWineResponse;
import com.selesse.android.winedb.winescraper.WineResponse;

public class GoogleShopperResponse extends AbstractWineResponse implements WineResponse {
  public int totalItems;
  public List<GoogleShopperItem> items;
  public Error error;

  public class GoogleShopperItem {
    public String kind;
    public String id;
    public GoogleShopperProduct product;
  }

  public class GoogleShopperProduct {
    public String country;
    public String title;
    public String description;
    public String link;
    public String brand;
  }

  public class Error {
    public List<Errors> errors;
    String code;

    public class Errors {
      String reason;
      String message;
    }
  }

  @Override
  public int getResultsSize() {
    return totalItems;
  }

  @Override
  protected List<Wine> convertResponsesToWineList() {
    List<Wine> wines = Lists.newArrayList();
    for (GoogleShopperItem item : items) {
      GoogleShopperProduct product = item.product;
      Wine wine = new Wine();
      wine.setName(product.title);
      wine.setCountry(product.country);
      wine.setDescription(product.description);
      wines.add(wine);
    }
    return wines;
  }

}
