package com.ng.countrywiki.activity;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ng.countrywiki.api.ApiService;
import com.ng.countrywiki.database.Country;
import com.ng.countrywiki.fragment.CountryDetailFragment_;
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

    RecyclerViewAdapter rvAdapter;
    List<String> countryList = new ArrayList<>();
    String countryName;
    List<String> filteredList = new ArrayList<>();

    @AfterViews
    protected void initialize() {
        getCountries();
        setRecyclerView();
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
        new CountDownTimer(5500, 1000) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                countryRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                countryRv.setAdapter(rvAdapter);
                if (rvAdapter == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
                countryRv.setVisibility(View.VISIBLE);
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
                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.activity_main, CountryDetailFragment_.newInstance())
                        .commit();
                hideKeyboard(MainActivity.this);
                contentLayout.setVisibility(View.GONE);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    public String getCountryName() {
        return countryName;
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}