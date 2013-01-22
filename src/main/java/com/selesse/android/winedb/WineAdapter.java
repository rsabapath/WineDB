package com.selesse.android.winedb;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.selesse.android.winedb.model.Wine;

public class WineAdapter extends ArrayAdapter<Wine> {

  private ArrayList<Wine> wines;

  public WineAdapter(Context context, int textViewResourceId, ArrayList<Wine> wine) {
    super(context, textViewResourceId, wine);
    this.wines = wine;
  }

  public void changeData(ArrayList<Wine> wine) {
    this.wines = wine;
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return this.wines.size();
  }

  @Override
  public Wine getItem(int position) {
    return this.wines.get(position);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = convertView;
    if (view == null) {
      LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = vi.inflate(R.layout.rows, null);
    }

    Wine wine = wines.get(position);

    if (wine != null) {
      TextView nameTextView = (TextView) view.findViewById(R.id.name);
      TextView countryTextView = (TextView) view.findViewById(R.id.country);
      TextView wineColorTextView = (TextView) view.findViewById(R.id.wine_color);
      TextView yearTextView = (TextView) view.findViewById(R.id.year);
      TextView ratingTextView = (TextView) view.findViewById(R.id.rating);

      nameTextView.setText(wine.getName());
      countryTextView.setText(wine.getCountry());
      wineColorTextView.setText(wine.getColor().toString());
      if (wine.getYear() > 0) {
        yearTextView.setText(wine.getYear() + "");
      }
      if (wine.getRating() > 0) {
        ratingTextView.setText(wine.getRating() + "");
      }
    }

    return view;
  }
}
