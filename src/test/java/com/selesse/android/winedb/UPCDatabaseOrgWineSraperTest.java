package com.selesse.android.winedb;

import java.util.List;

import android.test.AndroidTestCase;

import com.selesse.android.winedb.database.Wine;
import com.selesse.android.winedb.priv.UPCDatabaseOrgKey;
import com.selesse.android.winedb.winescraper.impl.UPCDatabaseOrgWineScraper;

public class UPCDatabaseOrgWineSraperTest extends AndroidTestCase {
  public void test_query_url() {
    String barcode = "0012044011409";

    UPCDatabaseOrgWineScraper scraper = new UPCDatabaseOrgWineScraper(barcode);

    String expectedUrl = String.format("http://upcdatabase.org/api/json/%s/%s",
        UPCDatabaseOrgKey.getKey(), barcode);

    assertEquals(expectedUrl, scraper.getQueryUrl());
  }

  public void test_old_spice_barcode() {
    String barcode = "0012044011409";

    UPCDatabaseOrgWineScraper scraper = new UPCDatabaseOrgWineScraper(barcode);

    List<Wine> wines = scraper.scrape();

    assertEquals("There should have been a result!", 1, wines.size());
  }

  public void test_bad_barcode() {
    String barcode = "123213123123123123123123";

    UPCDatabaseOrgWineScraper scraper = new UPCDatabaseOrgWineScraper(barcode);

    List<Wine> wines = scraper.scrape();

    assertEquals(0, wines.size());
  }
}
