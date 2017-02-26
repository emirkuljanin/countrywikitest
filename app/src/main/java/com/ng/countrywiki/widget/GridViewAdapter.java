package com.ng.countrywiki.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ng.countrywiki.R;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    Context context;
    List<String> values;
    List<Integer> drawableResIds;

    public GridViewAdapter(Context context, List<String> values, List<Integer> drawableResIds) {
        this.context = context;
        this.values = values;
        this.drawableResIds = drawableResIds;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Holder holder = new Holder();
        View gridView;

        if (convertView == null) {
            gridView = new View(context);

            gridView = inflater.inflate(R.layout.gridview_item, null);
            holder.text = (TextView) gridView.findViewById(R.id.grid_text);
            holder.text.setText(values.get(position));

            holder.image = (ImageView) gridView.findViewById(R.id.grid_image);
            holder.image.setBackgroundResource(drawableResIds.get(position));

        } else {
            gridView = (View) convertView;
        }
        return gridView;
    }

    static class Holder {
        ImageView image;
        TextView text;
    }
}
