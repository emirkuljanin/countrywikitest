package com.ng.countrywiki;

import android.os.CountDownTimer;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

import static com.ng.countrywiki.RetrofitBuilder.retrofit;

@EActivity(resName = "activity_main")
public class MainActivity extends AppCompatActivity {

    @ViewById(resName = "countryRv")
    RecyclerView countryRv;
    @ViewById(resName = "progressBar")
    MKLoader progressBar;
    @ViewById(resName = "main_header")
    Toolbar toolbar;

    RecyclerViewAdapter rvAdapter;
    List<String> countryList = new ArrayList<>();
    String countryName;

    @AfterViews
    protected void initialize() {
        getCountries();
        setRecyclerView();
        setRecyclerOnClickListener();
        setSupportActionBar(toolbar);
    }

    protected void getCountries() {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<CountryResponse>> countriesCall = apiService.getAllCountries();
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
            newCountry.callingCodes = country.getCallingCodes();
            newCountry.area = country.getArea();
            newCountry.capital = country.getCapital();
            newCountry.currencies = country.getCurrencies();
            newCountry.languages = country.getLanguages();
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
        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                countryRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                countryRv.setAdapter(rvAdapter);

                progressBar.setVisibility(View.GONE);
                countryRv.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    protected void setRecyclerOnClickListener() {
        countryRv.addOnItemTouchListener(new RecyclerItemClickListener(this, countryRv, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                countryName = countryList.get(position);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.activity_main, CountryDetailFragment_.newInstance())
                        .commit();
                countryRv.setVisibility(View.GONE);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    public String getCountryName() {
        return countryName;
    }
}

