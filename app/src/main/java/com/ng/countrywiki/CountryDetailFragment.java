package com.ng.countrywiki;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
    GoogleMap map;

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
        ((Activity) getActivity()).overridePendingTransition(0, 0);
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
            countryInformation.add("No Capital");
        }
        countryInformation.add(String.valueOf(country.population));
        countryInformation.add(country.region);

        if (subregion != null && !subregion.isEmpty()) {
            countryInformation.add(subregion);
        } else {
            countryInformation.add("No Sub-region");
        }

        countryInformation.add("+ " + country.numericCode);
        if (!area.equalsIgnoreCase("null")) {
            countryInformation.add(area);
        } else {
            countryInformation.add("No Area Specified");
        }

        drawableIds.add(R.drawable.capital);
        drawableIds.add(R.drawable.population);
        drawableIds.add(R.drawable.region);
        drawableIds.add(R.drawable.subregion);
        drawableIds.add(R.drawable.telephone);
        drawableIds.add(R.drawable.area);

        gridView.setAdapter(new GridViewAdapter(getContext(), countryInformation, drawableIds));
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        Double lat = country.lat;
//        Double lng = country.lng;
//        if (lat != null && lng != null) {
//            LatLng location = new LatLng(lat, lng);
//            map = googleMap;
//            map.getUiSettings().setZoomControlsEnabled(true);
//            map.addMarker(new MarkerOptions().position(location));
//            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
//        } else {
//            Toast.makeText(getContext(), "Country has no coordinates available", Toast.LENGTH_SHORT).show();
//        }
//    }
}
