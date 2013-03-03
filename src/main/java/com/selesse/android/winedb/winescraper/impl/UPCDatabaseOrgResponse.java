package com.selesse.android.winedb.winescraper.impl;

import java.util.List;

import com.google.common.collect.Lists;
import com.selesse.android.winedb.database.Wine;
import com.selesse.android.winedb.winescraper.AbstractWineResponse;
import com.selesse.android.winedb.winescraper.WineResponse;

public class UPCDatabaseOrgResponse extends AbstractWineResponse implements WineResponse {
  public String valid;
  public String reason;
  public String itemname;
  public String description;
  public String price;

  @Override
  public int getResultsSize() {
    if (valid.equals("false")) {
      return 0;
    }
    return 1;
  }

  @Override
  public List<Wine> convertResponsesToWineList() {
    List<Wine> wines = Lists.newArrayList();
    Wine wine = new Wine();
    wine.setName(itemname);
    wine.setDescription(description);
    if (!price.equals("0.00")) {
      wine.setPrice(price);
    }
    wines.add(wine);
    return wines;
  }
}
