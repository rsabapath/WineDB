package com.selesse.android.winedb.util;

import java.util.List;

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
   * Return the list of loaded wines from the database. The list will be empty if there are no
   * wines.
   *
   * @return A list of the user's wines.
   */
  List<Wine> getAllWines();

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
}
