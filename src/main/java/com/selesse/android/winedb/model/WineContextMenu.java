package com.selesse.android.winedb.model;

public enum WineContextMenu {
  EDIT("Edit"), DELETE("Delete");
  
  private String itemName;
  
  private WineContextMenu(String itemName) {
    this.itemName = itemName;
  }
  
  @Override
  public String toString() {
    return itemName;
  }
}
