package com.selesse.android.winedb.model;

public enum SortOrder {
  ASCENDING("ASC"),
  DESCENDING("DESC");

  private String order;

  private SortOrder(String order) {
    this.order = order;
  }

  @Override
  public String toString() {
    return order;
  }
}
