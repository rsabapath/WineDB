package com.selesse.android.winedb.winescraper.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;
import com.selesse.android.winedb.model.Wine;
import com.selesse.android.winedb.winescraper.WineScraper;

public class UPCDatabaseWineScraper implements WineScraper {

  private String url;
  private List<Exception> errors;
  private List<Wine> wines;

  public UPCDatabaseWineScraper(String barcode) {
    url = "http://www.upcdatabase.com/item/" + barcode;
  }

  @Override
  public List<Wine> scrape() {
    List<Wine> scrapedWines = Lists.newArrayList();
    try {
      URL url = new URL(getQueryUrl());
      URLConnection connection = url.openConnection();
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String rawHtml = "";

      String buffer;
      while ((buffer = in.readLine()) != null) {
        rawHtml += buffer;
      }

      Document document = Jsoup.parse(rawHtml);
      Elements table = document.select("table.data");
      Elements rows = table.select("tr");
      
      // need the Description
      
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
