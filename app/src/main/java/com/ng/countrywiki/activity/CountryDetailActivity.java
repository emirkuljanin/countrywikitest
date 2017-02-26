package com.ng.countrywiki.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ng.countrywiki.R;
import com.ng.countrywiki.database.Country;
import com.ng.countrywiki.widget.GridViewAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(resName = "activity_country_detail")
public class CountryDetailActivity extends AppCompatActivity {

    @ViewById(resName = "country_name_tv")
    TextView countryNameTv;
    @ViewById(resName = "country_flag")
    ImageView countryFlag;
    @ViewById(resName = "grid_view")
    GridView gridView;

    Country country;

    @AfterViews
    protected void initialize() {
        setHeaderTitleAndFlag();
        setUpGridView();
    }

    protected void setHeaderTitleAndFlag() {
        Bundle extras = getIntent().getExtras();
        String countryName = extras.getString("countryName");

        country = Country.getCountryByName(countryName);
        String countryCode = country.alpha2Code.toLowerCase();

        Resources res = this.getResources();
        int resID = res.getIdentifier(countryCode, "drawable", this.getPackageName());

        countryNameTv.setText(countryName);
        countryFlag.setBackgroundResource(resID);
    }

    @Click(resName = "back_button")
    void backToMain() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("showOverlay", false);

        Intent i = new Intent(this, MainActivity_.class);
        i.putExtras(bundle);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
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

        gridView.setAdapter(new GridViewAdapter(this, countryInformation, drawableIds));
    }

    @Click(resName = "showMapButton")
    protected void showMap() {
        Bundle bundle = new Bundle();
        bundle.putString("countryName", country.name);

        Intent i = new Intent(this, MapActivity_.class);
        i.putExtras(bundle);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }
}
