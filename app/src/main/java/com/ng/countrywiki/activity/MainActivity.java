package com.ng.countrywiki.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.ng.countrywiki.api.ApiService;
import com.ng.countrywiki.database.Country;
import com.ng.countrywiki.api.CountryResponse;
import com.ng.countrywiki.R;
import com.ng.countrywiki.widget.RecyclerItemClickListener;
import com.ng.countrywiki.widget.RecyclerViewAdapter;
import com.tuyenmonkey.mkloader.MKLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ng.countrywiki.api.RetrofitBuilder.retrofit;

@EActivity(resName = "activity_main")
public class MainActivity extends AppCompatActivity {

    @ViewById(resName = "countryRv")
    RecyclerView countryRv;
    @ViewById(resName = "progressBar")
    MKLoader progressBar;
    @ViewById(resName = "main_header")
    Toolbar toolbar;
    @ViewById(resName = "search_bar")
    EditText searchBar;
    @ViewById(resName = "content")
    RelativeLayout contentLayout;
    @ViewById(resName = "overlay")
    RelativeLayout overlay;

    RecyclerViewAdapter rvAdapter;
    List<String> countryList = new ArrayList<>();
    String countryName;
    List<String> filteredList = new ArrayList<>();
    boolean showOverlay = true;

    @AfterViews
    protected void initialize() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            showOverlay = extras.getBoolean("showOverlay");
        }
        if (showOverlay) {
            getCountries();
            setOverlay();
            setRecyclerView();
        } else {
            countryList.clear();
            List<Country> cl = Country.getAllCountries();
            for (Country c : cl) {
                countryList.add(c.name);
            }
            rvAdapter = new RecyclerViewAdapter(this, countryList);
            countryRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            countryRv.setAdapter(rvAdapter);
            progressBar.setVisibility(View.GONE);
            countryRv.setVisibility(View.VISIBLE);
        }
        setSupportActionBar(toolbar);
        setRecyclerOnClickListener();
        searchTextListener();
    }

    protected void getCountries() {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<CountryResponse>> countriesCall = apiService.getAllCountries();
        progressBar.setVisibility(View.VISIBLE);
        countriesCall.enqueue(new Callback<List<CountryResponse>>() {
            @Override
            public void onResponse(Call<List<CountryResponse>> call, Response<List<CountryResponse>> response) {
                storeCountriesInDb(response);
            }

            @Override
            public void onFailure(Call<List<CountryResponse>> call, Throwable t) {
            }
        });
    }

    @Background
    protected void storeCountriesInDb(Response<List<CountryResponse>> response) {
        List<CountryResponse> countries = response.body();
        Country.clearDb();
        for (CountryResponse country : countries) {
            Country newCountry = new Country();
            List<Double> coordinates = country.getLatlng();
            newCountry.name = country.getName();
            newCountry.alpha2Code = country.getAlpha2Code();
            newCountry.area = country.getArea();
            newCountry.capital = country.getCapital();
            newCountry.region = country.getRegion();
            newCountry.subregion = country.getSubregion();
            newCountry.numericCode = country.getNumericCode();
            newCountry.population = country.getPopulation();
            if (coordinates != null && !coordinates.isEmpty()) {
                newCountry.lat = coordinates.get(0);
                newCountry.lng = coordinates.get(1);
            }
            newCountry.save();
        }

        List<Country> cl = Country.getAllCountries();
        for (Country c : cl) {
            countryList.add(c.name);
        }
        rvAdapter = new RecyclerViewAdapter(getApplicationContext(), countryList);
    }

    protected void setRecyclerView() {
        new CountDownTimer(6000, 1000) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                countryRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                countryRv.setAdapter(rvAdapter);
                countryRv.setHasFixedSize(true);
                progressBar.setVisibility(View.GONE);
                countryRv.setVisibility(View.VISIBLE);
                hideKeyboard();
                if (rvAdapter == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
    }

    protected void setRecyclerOnClickListener() {
        countryRv.addOnItemTouchListener(new RecyclerItemClickListener(this, countryRv, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (filteredList.size() > 0) {
                    countryName = filteredList.get(position);
                } else {
                    countryName = countryList.get(position);
                }
                Bundle bundle = new Bundle();
                bundle.putString("countryName", countryName);

                Intent i = new Intent(MainActivity.this, CountryDetailActivity_.class);
                i.putExtras(bundle);
                startActivity(i);
                hideKeyboard();
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    public void searchTextListener() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence query, int i, int i1, int i2) {
                query = query.toString().toLowerCase();
                filteredList = new ArrayList<>();

                for (int u = 0; u < countryList.size(); u++) {
                    final String text = countryList.get(u).toLowerCase();
                    if (text.contains(query)) {
                        filteredList.add(countryList.get(u));
                    }
                }
                countryRv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                rvAdapter = new RecyclerViewAdapter(MainActivity.this, filteredList);
                countryRv.setAdapter(rvAdapter);
                setRecyclerOnClickListener();
                rvAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void hideKeyboard() throws NullPointerException {
        final InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }

    public void setOverlay() {
        overlay.setVisibility(View.VISIBLE);
        overlay.bringToFront();
        final View overlayView = overlay;
        final ViewTarget target = new ViewTarget(overlayView);
        final ShowcaseView sv = new ShowcaseView.Builder(MainActivity.this)
                .setTarget(target)
                .hideOnTouchOutside()
                .replaceEndButton(R.layout.overlay_button)
                .withHoloShowcase()
                .blockAllTouches()
                .build();
        sv.overrideButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay.setVisibility(View.GONE);
                sv.setVisibility(View.GONE);
            }
        });
    }
}