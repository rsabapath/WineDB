package com.selesse.android.winedb.winescraper;

import java.util.List;

import com.selesse.android.winedb.database.Wine;

public abstract class AbstractWineResponse {
  protected abstract List<Wine> convertResponsesToWineList();
}
