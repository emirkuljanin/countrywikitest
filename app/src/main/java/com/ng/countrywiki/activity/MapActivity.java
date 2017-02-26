package com.ng.countrywiki.activity;

import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ng.countrywiki.database.Country;
import com.ng.countrywiki.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(resName = "activity_map")
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    @ViewById(resName = "country_name_tv")
    TextView countryNameTv;
    @ViewById(resName = "country_flag")
    ImageView countryFlag;

    private GoogleMap mMap;
    private Country country;

    @AfterViews
    protected void initialize() {
        setHeaderTitleAndFlag();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Double lat = country.lat;
        Double lng = country.lng;

        LatLng latLng = new LatLng(lat, lng);

        mMap.addMarker(new MarkerOptions().position(latLng).title(country.name));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4.7f));

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
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }
}
