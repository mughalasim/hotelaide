package com.hotelaide.main_pages.models;


public class UserModel {
    public int      user_id;
    public String   user_token = "";
    public String   first_name = "";
    public String   last_name = "";
    public String   email = "";
    public String   password = "";
    public String   phone = "";
    public String   dob = "";
    public String   profile_pic = "";
    public String   banner_pic = "";
    public boolean  account_type = false;
    public String   fb_id = "";
    public String   google_id = "";
    public boolean  available_to_hire = false;
    public Float    geo_lat = 0.0f;
    public Float    geo_lng = 0.0f;
}