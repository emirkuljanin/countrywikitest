package com.ng.countrywiki.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "Countries")
public class Country extends Model {

    @Column(name = "Name")
    public String name;

    @Column(name = "Alpha2Code")
    public String alpha2Code;

    @Column(name = "Capital")
    public String capital;

    @Column(name = "Region")
    public String region;

    @Column(name = "SubRegion")
    public String subregion;

    @Column(name = "Population")
    public Integer population;

    @Column(name = "Lat")
    public Double lat;

    @Column(name = "Lng")
    public Double lng;

    @Column(name = "Area")
    public Double area;

    @Column(name = "NumericCode")
    public String numericCode;

    public Country() {
        super();
    }

    public static List<Country> getAllCountries() {
        return new Select().from(Country.class).execute();
    }

    public static void clearDb() {
        new Delete().from(Country.class).execute();
    }

    public static Country getCountryByName(String countryName) {
        return new Select()
                .from(Country.class)
                .where("Name = ?", countryName)
                .executeSingle();
    }

}
