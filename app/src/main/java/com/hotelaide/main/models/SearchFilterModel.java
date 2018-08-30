package com.hotelaide.main.models;


public class SearchFilterModel {
    public int id;
    public String name = "";

    @Override
    public String toString() {
        return this.name;            // What to display in the Spinner list.
    }
}