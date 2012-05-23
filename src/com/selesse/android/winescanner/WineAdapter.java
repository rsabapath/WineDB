package com.selesse.android.winescanner;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

      TextView nameView = (TextView) v.findViewById(R.id.rowNameText);
      nameView.setTag(position);
      TextView countryView = (TextView) v.findViewById(R.id.rowCountryText);
      TextView yearView = (TextView) v.findViewById(R.id.rowYearText);
      TextView descView = (TextView) v.findViewById(R.id.rowDescText);
      TextView ratingView = (TextView) v.findViewById(R.id.rowRatingText);
      TextView priceView = (TextView) v.findViewById(R.id.rowPriceText);
      TextView commentView = (TextView) v.findViewById(R.id.rowCommentText);

      // TODO bad practice, should fix this
      nameView.setText(o.getName().substring(0, Math.min(45, o.getName().length())));
      if (o.getCountry().equals("null"))
        countryView.setVisibility(View.GONE);
      else
        countryView.setText(o.getCountry());
      if (o.getYear().equals("null"))
        yearView.setVisibility(View.GONE);
      else
        yearView.setText(o.getYear());
      if (o.getDescription().equals("null"))
        descView.setVisibility(View.GONE);
      else
        descView.setText(o.getDescription().substring(0, Math.min(45, o.getDescription().length())));

      if (o.getRating().equals("null"))
        ratingView.setVisibility(View.GONE);
      else
        ratingView.setText(o.getRating());

      if (o.getPrice().equals("null"))
        priceView.setVisibility(View.GONE);
      else
        priceView.setText(o.getPrice());
      if (o.getComment().equals("null"))
        commentView.setVisibility(View.GONE);
      else
        commentView.setText(o.getComment().substring(0, Math.min(45, o.getComment().length())));
    }
    return v;
  }
}
