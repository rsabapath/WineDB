package com.selesse.android.winedb;

import java.util.ArrayList;
import java.util.List;

import com.selesse.android.winedb.model.Wine;

/**
 * The FileManager for the WineScanner application. This class essentially keeps
 * a {@link java.util.List} of Wine objects, loads it and saves it to a file.
 * 
 * @author Selesse
 */
public class FileManager {
  // FIXME add some sort of preference and smarter way of saving - assume that
  // we are in debug mode for now
  public final static String WINEDB_LOCATION = "/sdcard/winescanner/";
  public final static String WINEDB_FILE = "wineDB";
  public static List<Wine> wineList;

  /**
   * Loads the ArrayList of Wines given the WINEDB_LOCATION and WINEDB_FILE.
   * 
   * @return The loaded ArrayList of Wine objects, if such a list exists,
   *         otherwise an empty List.
   */
  public static ArrayList<Wine> loadWine() {
    return new ArrayList<Wine>();
  }

  /**
   * Find the wine at a particular position, take note of its barcode. Go
   * through the list of wines, one by one, writing to a temporary ArrayList. If
   * you come across the old barcode, skip it. This is kind of a silly way of
   * doing it. TODO refactor this.
   * 
   * @param position
   *          The position of the deleted wine in the ArrayList.
   */
  public static void deleteWine(int position) {

  }

  /**
   * Adds a wine to the master list and saves to the file.
   * 
   * @param newWine
   *          The new Wine object to add to the static wine list.
   */
  public static void addWine(Wine newWine) {
    wineList.add(newWine);
    saveFile();
  }

  /**
   * Saves the static ArrayList of wines to the location.
   */
  public static void saveFile() {
    
  }

}
