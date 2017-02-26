package com.ng.countrywiki;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tuyenmonkey.mkloader.MKLoader;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<String> countryList;
    private Context context;

    RecyclerViewAdapter(Context context, List<String> countryList) {
        this.countryList = countryList;
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView countryName;

        ViewHolder(View itemView) {
            super(itemView);

            countryName = (TextView) itemView.findViewById(R.id.country_name);
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View countryView = inflater.inflate(R.layout.country_item, parent, false);

        return new ViewHolder(countryView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        TextView countryName = holder.countryName;

        countryName.setText(countryList.get(position));
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }
}
