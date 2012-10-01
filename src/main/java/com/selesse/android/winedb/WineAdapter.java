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

  private ArrayList<Wine> wine;

  public WineAdapter(Context context, int textViewResourceId, ArrayList<Wine> wine) {
    super(context, textViewResourceId, wine);
    this.wine = wine;
  }

  public void changeData(ArrayList<Wine> wine) {
    this.wine = wine;
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return this.wine.size();
  }

  @Override
  public Wine getItem(int position) {
    return this.wine.get(position);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View v = convertView;
    if (v == null) {
      LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = vi.inflate(R.layout.rows, null);
    }
    Wine o = wine.get(position);

    if (o != null) {
      TextView nameTextView = (TextView) v.findViewById(R.id.name);
      TextView countryTextView = (TextView) v.findViewById(R.id.country);
      TextView wineColorTextView = (TextView) v.findViewById(R.id.wine_color);
      TextView yearTextView = (TextView) v.findViewById(R.id.year);
      TextView ratingTextView = (TextView) v.findViewById(R.id.rating);
      
      nameTextView.setText(o.getName());
      countryTextView.setText(o.getCountry());
      wineColorTextView.setText(o.getColor().toString());
      if (o.getYear() > 0)
        yearTextView.setText(o.getYear() + "");
      if (o.getRating() > 0)
        ratingTextView.setText(o.getRating() + "");
    }
    return v;
  }
}
