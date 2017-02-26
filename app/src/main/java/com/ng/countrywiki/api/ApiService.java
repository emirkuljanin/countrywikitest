package com.ng.countrywiki.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("all")
    Call<List<CountryResponse>> getAllCountries();
}
