package com.selesse.android.winedb;

import java.util.List;

import android.test.AndroidTestCase;

import com.selesse.android.winedb.database.Wine;
import com.selesse.android.winedb.winescraper.impl.UPCDatabaseWineScraper;

public class UPCDatabaseWineScraperTest extends AndroidTestCase {

  public void test_query_url() {
    String barcode = "012044011409";

    UPCDatabaseWineScraper scraper = new UPCDatabaseWineScraper(barcode);

    String expectedUrl = String.format("http://www.upcdatabase.com/item/%s", barcode);

    assertEquals(expectedUrl, scraper.getQueryUrl());
  }

  public void test_old_spice_barcode() {
    String barcode = "012044011409";

    UPCDatabaseWineScraper scraper = new UPCDatabaseWineScraper(barcode);

    List<Wine> wines = scraper.scrape();

    assertEquals("There should have been a result!", 1, wines.size());
  }

  public void test_bad_barcode() {
    String barcode = "012345678";

    UPCDatabaseWineScraper scraper = new UPCDatabaseWineScraper(barcode);

    List<Wine> wines = scraper.scrape();

    assertEquals(0, wines.size());
  }
}
