package com.selesse.android.winedb.winescraper;

import java.util.List;

import com.selesse.android.winedb.model.Wine;

/**
 * Interface for a single Wine Scraper. Every class implementing this interface should fetch wine
 * information.
 *
 * @author Alex Selesse
 *
 */
public interface WineScraper {

  List<Wine> scrape();

  String getQueryUrl();

  List<Exception> getErrors();
}
