package com.selesse.android.winedb.model;

import java.util.Arrays;
import java.util.List;

import android.content.res.Resources;

import com.selesse.android.winedb.R;

public enum WineColor {
  UNKNOWN(""),
  RED("Red"),
  WHITE("White"),
  ROSE("Rose"),
  PORT("Port"),
  SPARKLING("Sparkling");

  String name;

  private WineColor(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  public static List<String> getLocalizedStrings(Resources res) {
    String red = res.getString(R.string.wine_color_red);
    String white = res.getString(R.string.wine_color_white);
    String rose = res.getString(R.string.wine_color_rose);
    String port = res.getString(R.string.wine_color_port);
    String sparkling = res.getString(R.string.wine_color_sparkling);
    
    return Arrays.asList("", red, white, rose, port, sparkling);
  }

  public static String getLocalizedString(Resources res, String string) {
    if (string.equals("")) {
      return "";
    }
    else if (string.equalsIgnoreCase("red")) {
      return res.getString(R.string.wine_color_red);
    }
    else if (string.equalsIgnoreCase("white")) {
      return res.getString(R.string.wine_color_white);
    }
    else if (string.equalsIgnoreCase("rose")) {
      return res.getString(R.string.wine_color_rose);
    }
    else if (string.equalsIgnoreCase("port")) {
      return res.getString(R.string.wine_color_port);
    }
    else if (string.equalsIgnoreCase("sparkling")) {
      return res.getString(R.string.wine_color_sparkling);
    }
    return "";
  }

}