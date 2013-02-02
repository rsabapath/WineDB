package com.selesse.android.winedb.model;

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
}
