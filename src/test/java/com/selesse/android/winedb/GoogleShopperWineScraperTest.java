package com.selesse.android.winedb;

import android.test.AndroidTestCase;

import com.selesse.android.winedb.priv.GoogleShopperKey;
import com.selesse.android.winedb.winescraper.impl.GoogleShopperWineScraper;

public class GoogleShopperWineScraperTest extends AndroidTestCase {

  /**
   * Make sure that getQueryUrl corresponds to the correct URL.
   */
  public void test_correct_url() {
    String barcode = "8410113003027";
    GoogleShopperWineScraper scraper = new GoogleShopperWineScraper(barcode);

    String expectedUrl = String
        .format(
            "https://www.googleapis.com/shopping/search/v1/public/products?country=US&language=en&q=%s&key=%s",
            barcode, GoogleShopperKey.getKey());

    assertEquals(expectedUrl, scraper.getQueryUrl());
  }
}
