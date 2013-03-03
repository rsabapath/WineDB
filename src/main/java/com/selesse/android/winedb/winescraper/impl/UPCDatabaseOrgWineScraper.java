package com.selesse.android.winedb.winescraper.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.selesse.android.winedb.database.Wine;
import com.selesse.android.winedb.priv.UPCDatabaseOrgKey;
import com.selesse.android.winedb.winescraper.WineScraper;

public class UPCDatabaseOrgWineScraper implements WineScraper {
  private String url;
  private List<Exception> errors;

  public UPCDatabaseOrgWineScraper(String barcode) {
    url = "http://upcdatabase.org/api/json/" + UPCDatabaseOrgKey.getKey() + "/" + barcode;
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
      UPCDatabaseOrgResponse results = gson.fromJson(rawJson, UPCDatabaseOrgResponse.class);
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
