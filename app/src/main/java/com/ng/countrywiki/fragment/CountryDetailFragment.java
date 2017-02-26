package com.ng.countrywiki.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ng.countrywiki.database.Country;
import com.ng.countrywiki.fragment.CountryDetailFragment_;
import com.ng.countrywiki.widget.GridViewAdapter;
import com.ng.countrywiki.activity.MainActivity_;
import com.ng.countrywiki.activity.MapActivity_;
import com.ng.countrywiki.R;
import com.ng.countrywiki.activity.MainActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(resName = "fragment_country_detail")
public class CountryDetailFragment extends Fragment {

    @ViewById(resName = "country_name_tv")
    TextView countryNameTv;
    @ViewById(resName = "country_flag")
    ImageView countryFlag;
    @ViewById(resName = "grid_view")
    GridView gridView;

    Country country;

    public static CountryDetailFragment_ newInstance() {
        return new CountryDetailFragment_();
    }

    @AfterViews
    protected void initialize() {
        setHeaderTitleAndFlag();
        setUpGridView();
    }

    protected void setHeaderTitleAndFlag() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            String countryName = activity.getCountryName();

            country = Country.getCountryByName(countryName);
            String countryCode = country.alpha2Code.toLowerCase();

            Resources res = this.getResources();
            int resID = res.getIdentifier(countryCode, "drawable", getContext().getPackageName());

            countryNameTv.setText(countryName);
            countryFlag.setBackgroundResource(resID);
        }
    }

    @Click(resName = "back_button")
    void backToMain() {
        Intent i = new Intent(getActivity(), MainActivity_.class);
        startActivity(i);
        getActivity().overridePendingTransition(0, 0);
    }

    protected void setUpGridView() {
        List<String> countryInformation = new ArrayList<>();
        List<Integer> drawableIds = new ArrayList<>();
        String capital = country.capital;
        String subregion = country.subregion;
        String area = String.valueOf(country.area);

        if (capital != null && !capital.isEmpty()) {
            countryInformation.add(capital);
        } else {
            countryInformation.add(getString(R.string.no_capital));
        }
        countryInformation.add(String.valueOf(country.population));
        countryInformation.add(country.region);

        if (subregion != null && !subregion.isEmpty()) {
            countryInformation.add(subregion);
        } else {
            countryInformation.add(getString(R.string.no_subregion));
        }

        countryInformation.add("+ " + country.numericCode);
        if (!"null".equalsIgnoreCase(area)) {
            countryInformation.add(area);
        } else {
            countryInformation.add(getString(R.string.no_area));
        }

        drawableIds.add(R.drawable.capital);
        drawableIds.add(R.drawable.population);
        drawableIds.add(R.drawable.region);
        drawableIds.add(R.drawable.subregion);
        drawableIds.add(R.drawable.telephone);
        drawableIds.add(R.drawable.area);

        gridView.setAdapter(new GridViewAdapter(getContext(), countryInformation, drawableIds));
    }

    @Click(resName = "showMapButton")
    void showMap() {
        Bundle bundle = new Bundle();
        bundle.putString("countryName", country.name);

        Intent i = new Intent(getActivity(), MapActivity_.class);
        i.putExtras(bundle);
        startActivity(i);
        ((Activity) getActivity()).overridePendingTransition(0, 0);
    }
}
