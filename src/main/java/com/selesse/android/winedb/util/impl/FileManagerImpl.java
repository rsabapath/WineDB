package com.selesse.android.winedb.util.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import android.os.Environment;

import com.selesse.android.winedb.activity.WineDB;
import com.selesse.android.winedb.model.Wine;
import com.selesse.android.winedb.model.Wine.Attribute;
import com.selesse.android.winedb.util.FileManager;

public class FileManagerImpl implements FileManager {
  public final static String WINEDB_LOCATION = Environment.getExternalStorageDirectory().getPath();
  public final static String WINEDB_FILE = WINEDB_LOCATION + "/winescanner/wineDB";
  
  ArrayList<Wine> wineList;
  
  public FileManagerImpl() {
    this.wineList = WineDB.wineList;
    wineList = loadWineList();
  }

  private ArrayList<Wine> loadWineList() {
    File file = new File(WINEDB_FILE);

    if (file.exists()) {
      wineList = loadWines(file);
    }
    
    return wineList;
  }

  private ArrayList<Wine> loadWines(File file) {
    ArrayList<Wine> readWines = new ArrayList<Wine>();
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

  @Override
  public void saveWines() {
    File file = new File(WINEDB_FILE);
    try {
      PrintStream output = new PrintStream(file);
      for (Wine wine : wineList) {
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
    wineList.remove(wine);
    saveWines();
  }

  @Override
  public void addWine(Wine wine) {
    wineList.add(wine);
    saveWines();
  }

  @Override
  public ArrayList<Wine> getWineList() {
    return this.wineList;
  }

}
