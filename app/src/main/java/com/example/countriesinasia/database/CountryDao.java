package com.example.countriesinasia.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.countriesinasia.helper.Country;

import java.util.List;

@Dao
public interface CountryDao {

    @Insert
    void insertAllData(CountryEntity countryEntity);

    @Query("DELETE FROM country")
    void deleteEntireData();

    @Query("SELECT * FROM country")
    List<Country> retrieveData();

}
