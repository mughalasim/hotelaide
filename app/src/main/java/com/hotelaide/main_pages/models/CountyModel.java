package com.hotelaide.main_pages.models;


public class CountyModel {
    public int id;
    public String name = "";

    @Override
    public String toString() {
        return this.name;            // What to display in the Spinner list.
    }
}