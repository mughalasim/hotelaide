package com.hotelaide.main.models;


import android.support.annotation.NonNull;

public class SearchFilterModel {
    public int      id;

    public String   name = "";

    @NonNull
    @Override
    public String toString() {
        return this.name;            // What to display in the Spinner list.
    }
}