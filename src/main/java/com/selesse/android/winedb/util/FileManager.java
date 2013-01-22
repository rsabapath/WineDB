package com.selesse.android.winedb.util;

import java.util.List;

import com.selesse.android.winedb.model.Wine;

/**
 * The file manager interface for the WineScanner application.
 * 
 * @author Alex Selesse
 */
public interface FileManager {

  /**
   * Remove a particular {@link com.selesse.android.winedb.model.Wine} from the list of wines.
   * 
   * @param wine
   *          The Wine object to be deleted.
   */
  public void deleteWine(Wine wine);

  public Wine createWine(Wine wine);

  public List<Wine> getAllWines();

  void open();

  void close();
}
