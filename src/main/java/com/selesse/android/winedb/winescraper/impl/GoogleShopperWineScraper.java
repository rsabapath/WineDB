package com.selesse.android.winedb.winescraper.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.selesse.android.winedb.model.Wine;
import com.selesse.android.winedb.priv.GoogleShopperKey;
import com.selesse.android.winedb.winescraper.WineScraper;

public class GoogleShopperWineScraper implements WineScraper {
  private String url;
  private List<Exception> errors;

  public GoogleShopperWineScraper(String barcode) {
    List<String> options = Lists.newArrayList("country=US", "language=en", "q=" + barcode, "key=" + GoogleShopperKey.getKey());

    url = "https://www.googleapis.com/shopping/search/v1/public/products?";
    url += Joiner.on("&").join(options);
  }

  @Override
  public List<Wine> scrape() {
    List<Wine> scrapedWines = Lists.newArrayList();
    try {
      URL url = new URL(getQueryUrl());
      URLConnection connection = url.openConnection();
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String rawJson = "";
      
      String buffer;
      while ((buffer = in.readLine()) != null) {
        rawJson += buffer;
      }
      
      Gson gson = new Gson();
      GoogleShopperResponse results = gson.fromJson(rawJson, GoogleShopperResponse.class);
      if (results.getResultsSize() > 0) {
        return results.convertResponsesToWineList();
      }
    }
    catch (MalformedURLException e) {
      errors.add(e);
    }
    catch (IOException e) {
      errors.add(e);
    }
    
    return scrapedWines;
  }

  @Override
  public String getQueryUrl() {
    return url;
  }
  
  @Override
  public List<Exception> getErrors() {
    return errors;
  }

}
