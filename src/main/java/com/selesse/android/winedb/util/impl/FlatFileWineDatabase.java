package com.selesse.android.winedb.util.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import android.os.Environment;

import com.google.common.collect.Lists;
import com.selesse.android.winedb.model.Wine;
import com.selesse.android.winedb.model.Wine.Attribute;
import com.selesse.android.winedb.util.WineDatabase;

/**
 * Flat file implementation of the wine database. This is one way to store the wines, but it's not
 * as clean as using SQLite.
 *
 * <p>
 * Its advantage is its portability, readability (i.e. just send me your wineDB file) but
 * disadvantage is in the implementation. It's harder to do things like search, sort, filter and
 * anything special (especially when compared to SQLite).
 * </p>
 *
 * @author Alex Selesse
 *
 */
public class FlatFileWineDatabase implements WineDatabase {
  public final static String WINEDB_LOCATION = Environment.getExternalStorageDirectory().getPath();
  public final static String WINEDB_FILE = WINEDB_LOCATION + "/winescanner/wineDB";

  private List<Wine> wines;

  @Override
  public void open() {
    wines = loadWines();
  }

  @Override
  public void close() {
    saveWines();
  }

  private List<Wine> loadWines() {
    File file = new File(WINEDB_FILE);

    if (file.exists()) {
      wines = loadWines(file);
    }

    return wines;
  }

  private List<Wine> loadWines(File file) {
    List<Wine> readWines = Lists.newArrayList();
    try {
      BufferedReader input = new BufferedReader(new FileReader(file));
      String line;
      int attributes = 0;
      Wine wine = new Wine();
      while ((line = input.readLine()) != null) {
        String key = line.substring(0, line.indexOf(":"));
        String value = line.substring(line.indexOf(":") + 1);

        wine.putValueFromAttribute(Wine.Attribute.valueOf(key), value);

        if (attributes < Wine.Attribute.values().length) {
          attributes++;
        }
        else {
          attributes = 0;
          readWines.add(wine);
          wine = new Wine();
        }
      }

      input.close();
    }
    catch (FileNotFoundException e) {

    }
    catch (IOException e) {

    }

    return readWines;
  }

  private void saveWines() {
    File file = new File(WINEDB_FILE);
    try {
      PrintStream output = new PrintStream(file);
      for (Wine wine : wines) {
        for (Attribute attr : Wine.Attribute.values()) {
          String key = attr.getAttributeName();
          String value = wine.getValueFromAttribute(attr);

          output.println(key + ":" + value);
        }
      }

      output.close();
    }
    catch (FileNotFoundException e) {

    }
  }

  @Override
  public void deleteWine(Wine wine) {
    wines.remove(wine);
    saveWines();
  }

  @Override
  public Wine createWine(Wine wine) {
    wines.add(wine);
    saveWines();
    return wine;
  }

  @Override
  public List<Wine> getAllWines() {
    return this.wines;
  }

  @Override
  public void updateWine(Wine wine) {
    deleteWine(wine);
    createWine(wine);
  }

}
