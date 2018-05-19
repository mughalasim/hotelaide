package com.hotelaide.utils;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hotelaide.BuildConfig;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HotelAide.db";
    private static final String TAG_LOG = "DATABASE";


    public Database() {
        super(MyApplication.getAppContext(), DATABASE_NAME, null, BuildConfig.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }


    // DELETE ALL FUNCTIONS ========================================================================
    public void deleteAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }

}