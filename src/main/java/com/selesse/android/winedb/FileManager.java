package com.selesse.android.winedb;

import java.util.ArrayList;

import com.selesse.android.winedb.model.Wine;


/**
 * The file manager interface for the WineScanner application.
 * 
 * @author Alex Selesse
 */
public interface FileManager {

  /**
   * Remove a particular {@link com.selesse.android.winedb.model.Wine} from the
   * list of wines.
   * 
   * @param delete_wine
   *          The Wine object to be deleted.
   */
  public void deleteWine(Wine delete_wine);

  /**
   * Adds a wine to the master list.
   * 
   * @param newWine
   *          The new Wine object to add to the master wine list.
   */
  public void addWine(Wine newWine);

  /**
   * Saves the wines.
   */
  public void saveWines();

  public ArrayList<Wine> getWineList();
}
