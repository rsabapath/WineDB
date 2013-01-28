package com.selesse.android.winedb.database;

import android.database.Cursor;

import com.selesse.android.winedb.model.Wine;

/**
 * The wine database interface responsible for saving the wine information for a user.
 *
 * @author Alex Selesse
 */
public interface WineDatabase {

  /**
   * Remove a particular {@link com.selesse.android.winedb.model.Wine} from the database.
   *
   * @param wine
   *          The Wine object to be deleted.
   */
  void deleteWine(Wine wine);

  /**
   * Create a {@link com.selesse.android.winedb.model.Wine} object with as much or as little
   * information as you want to provide about it.
   *
   * @param wine
   *          The Wine object you're adding to the database.
   * @return The Wine object you want to create (will populate its "id" field).
   */
  Wine createWine(Wine wine);

  /**
   * Open the connection to the database.
   */
  void open();

  /**
   * Close the connection to the database.
   */
  void close();

  /**
   * Update the Wine (assuming that they have a valid id) to have new attributes.
   *
   * @param Wine
   *          The wine you'd like to update with the new attributes.
   */
  void updateWine(Wine wine);
  
  Cursor getAllWines();
  
  Wine cursorToWine(Cursor cursor);
}
