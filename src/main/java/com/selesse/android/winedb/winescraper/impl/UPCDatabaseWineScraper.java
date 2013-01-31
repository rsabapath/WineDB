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
  private String barcode;

  public UPCDatabaseWineScraper(String barcode) {
    this.barcode = barcode;
    url = "http://www.upcdatabase.com/item/" + barcode;
    errors = Lists.newArrayList();
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
      // if we don't have any table data, we didn't find the wine in the UPC database
      if (table.size() == 0) {
        errors.add(new Exception("Wine was not found!"));
        return scrapedWines;
      }

      Wine wine = new Wine();
      wine.setBarcode(barcode);

      Elements rows = table.select("tr");

      // the rows in the result have 3 <td>s: 1st is key, 3rd is value
      for (Element tr : rows) {
        Elements tds = tr.select("td");
        for (int i = 0; i < tds.size(); i++) {
          Element td = tds.get(i);
          // set the name to the description, it is usually short enough to fit in name
          if (td.text().contains("Description")) {
            wine.setName(tds.get(i + 2).text());
          }
          else if (td.text().contains("Country")) {
            wine.setCountry(tds.get(i + 2).text());
          }
        }
      }

      scrapedWines.add(wine);
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
