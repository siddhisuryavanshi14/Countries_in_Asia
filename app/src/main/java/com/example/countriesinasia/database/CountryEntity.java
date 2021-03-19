package com.example.countriesinasia.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.countriesinasia.helper.Language;

import java.util.List;

@Entity(tableName = "country")
public class CountryEntity {

    @PrimaryKey @NonNull String name;
    @ColumnInfo(name = "capital") String capital;
    @ColumnInfo(name = "flag") String flag;
    @ColumnInfo(name = "region") String region;
    @ColumnInfo(name = "subregion") String subregion;
    @ColumnInfo(name = "population") int population;
    @ColumnInfo(name = "borders")  String borders;
    @ColumnInfo(name = "languages")  String languages;

    public CountryEntity(String name, String capital, String flag, String region, String subregion, int population, String borders, String languages) {
        this.name = name;
        this.capital = capital;
        this.flag = flag;
        this.region = region;
        this.subregion = subregion;
        this.population = population;
        this.borders = borders;
        this.languages = languages;
    }
}
