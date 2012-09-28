package com.selesse.android.winedb.impl;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;

import com.selesse.android.winedb.FileManager;
import com.selesse.android.winedb.model.Wine;

public class FileManagerImpl implements FileManager {
  public final static String WINEDB_LOCATION = Environment.getExternalStorageDirectory().getPath();
  public final static String WINEDB_FILE = WINEDB_LOCATION + "/winescanner/wineDB";

  @Override
  public ArrayList<Wine> loadWineList() {
    ArrayList<Wine> wineList = new ArrayList<Wine>();
    
    File file = new File(WINEDB_FILE);

    if (file.exists()) {
      wineList = loadWines(file);
    }
    
    return wineList;
  }

  private ArrayList<Wine> loadWines(File file) {
    // TODO
    return new ArrayList<Wine>();
  }

  @Override
  public void deleteWine(Wine delete_wine) {

  }

  @Override
  public void addWine(Wine newWine) {
    saveWines();
  }

  @Override
  public void saveWines() {

  }
}
