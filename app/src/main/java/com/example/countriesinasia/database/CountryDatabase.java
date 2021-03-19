package com.example.countriesinasia.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CountryEntity.class}, version = 1)
public abstract class CountryDatabase extends RoomDatabase {

    public abstract CountryDao countryDao();
}
