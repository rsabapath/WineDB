package com.selesse.android.winedb.winescraper;

import java.util.List;

import com.google.common.collect.Lists;
import com.selesse.android.winedb.database.Wine;
import com.selesse.android.winedb.winescraper.impl.GoogleShopperWineScraper;
import com.selesse.android.winedb.winescraper.impl.UPCDatabaseOrgWineScraper;
import com.selesse.android.winedb.winescraper.impl.UPCDatabaseWineScraper;

/**
 * Collection of wine scrapers that uses 1 or more wine scrapers to return results.
 *
 * @author Alex Selesse
 *
 */
public class WineScrapers {

  private List<WineScraper> wineScrapers;

  public WineScrapers(String barcode) {
    WineScraper googleWineScraper = new GoogleShopperWineScraper(barcode);
    UPCDatabaseWineScraper upcScraper = new UPCDatabaseWineScraper(barcode);
    UPCDatabaseOrgWineScraper upcOrgScraper = new UPCDatabaseOrgWineScraper(barcode);

    this.wineScrapers = Lists.newArrayList(googleWineScraper, upcScraper, upcOrgScraper);
  }

  public List<Wine> scrape() {
    List<Wine> scrapedWines = Lists.newArrayList();

    for (WineScraper scraper : wineScrapers) {
      scrapedWines.addAll(scraper.scrape());
    }

    return scrapedWines;
  }
}
