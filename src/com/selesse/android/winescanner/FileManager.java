package com.selesse.android.winescanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import android.util.Log;

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
    ArrayList<Wine> wines = new ArrayList<Wine>();

    File file = new File(WINEDB_LOCATION + WINEDB_FILE);

    if (file.exists()) {
      try {
        Scanner in = new Scanner(file);
        in.useDelimiter("\\||\n");

        while (in.hasNext()) {
          Wine wine = new Wine();
          wine.setBarcode(in.next());
          wine.setName(in.next());
          wine.setCountry(in.next());
          wine.setYear(in.next());
          wine.setDescription(in.next());
          wine.setRating(in.next());
          wine.setPrice(in.next());
          wine.setImageURL(in.next());
          String comment = in.nextLine();
          if (comment.startsWith("|null"))
            wine.setComment("null");
          else
            wine.setComment(comment.substring(1, comment.length()));

          Log.v("WineScanner", "Checking in " + wine.toString());

          wines.add(wine);
        }

      }
      catch (FileNotFoundException e) {
        Log.v("WineScanner", "File not found (shouldn't ever get here though)");
        e.printStackTrace();
      }

    }

    // otherwise, file doesn't exist - we return an empty array list
    Collections.sort(wines);
    wineList = wines;
    return wines;

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

    Wine wine = wineList.remove(position);
    String deleteCode = wine.getBarcode();

    try {
      File file = new File(WINEDB_LOCATION + WINEDB_FILE);
      Scanner in = new Scanner(file);

      ArrayList<String> fileContents = new ArrayList<String>();
      while (in.hasNext()) {
        String buffer = in.nextLine();
        String barcode = buffer.substring(0, buffer.indexOf('|'));
        if (barcode.equals(deleteCode))
          continue;
        fileContents.add(buffer);
      }

      PrintWriter out = new PrintWriter(file);

      for (String line : fileContents)
        out.println(line);

      out.close();

    }
    catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

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
    File file = new File(WINEDB_LOCATION);
    if (!file.exists())
      file.mkdir();

    file = new File(WINEDB_LOCATION + WINEDB_FILE);

    try {
      PrintWriter out = new PrintWriter(file);

      for (Wine w : wineList) {
        out.print(w.getBarcode() + "|");
        out.print(w.getName() + "|");
        out.print(w.getCountry() + "|");
        out.print(w.getYear() + "|");
        out.print(w.getDescription() + "|");
        out.print(w.getRating() + "|");
        out.print(w.getPrice() + "|");
        out.print(w.getImageURL() + "|");
        out.println(w.getComment());
        out.flush();
      }

      out.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

}
