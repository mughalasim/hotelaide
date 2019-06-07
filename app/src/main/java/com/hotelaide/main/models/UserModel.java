package com.hotelaide.main.models;

import org.json.JSONArray;

import java.util.ArrayList;

public class UserModel {
    public int      id;

    public String   first_name = "";
    public String   last_name = "";
    public String   about = "";
    public String   email = "";
    public String   img_avatar = "";
    public String   img_banner = "";
    public String   phone = "";
    public String   share_link = "";

    public int      country_code;
    public int      county;

    public String   dob = "";
    public String   fb_id = "";
    public String   google_id = "";

    public double   geo_lat;
    public double   geo_lng;

    public String   password = "";

    public int      availability ;
    public int      gender ;

    public JSONArray work_experience;
    public JSONArray educational_experience;
    public JSONArray documents;
    public ArrayList<String> skills;

}