package com.hotelaide.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.hotelaide.main.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SharedPrefs {
    @SuppressLint("StaticFieldLeak")
    private static final Context context = MyApplication.getAppContext();
    private static final String SHARED_PREFS = "SHARED_PREFS";
    private static final int MODE = Activity.MODE_PRIVATE;


    // INTEGER VARIABLE NAMES ======================================================================
    public static final String DATABASE_VERSION = "DATABASE_VERSION";
    public static final String USER_ID = "USER_ID";

    // STRING VARIABLE NAMES =======================================================================
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    public static final String USER_F_NAME = "USER_F_NAME";
    public static final String USER_L_NAME = "USER_L_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_IMG_AVATAR = "USER_IMG_AVATAR";
    public static final String USER_IMG_BANNER = "USER_IMG_BANNER";
    public static final String PROFILE_URL = "PROFILE_URL";
    public static final String USER_COUNTRY_CODE = "USER_COUNTRY_CODE";
    public static final String USER_PHONE = "USER_PHONE";
    public static final String USER_DOB = "USER_DOB";
    public static final String USER_FB_ID = "USER_FB_ID";
    public static final String USER_GOOGLE_ID = "USER_GOOGLE_ID";
    public static final String USER_LAT = "USER_LAT";
    public static final String USER_LNG = "USER_LNG";
    public static final String USER_POSTAL_CODE = "USER_POSTAL_CODE";
    public static final String USER_FULL_ADDRESS = "USER_FULL_ADDRESS";
    public static final String USER_COUNTY = "USER_COUNTY";
    public static final String USER_URL = "USER_URL";
    public static final String USER_PROFILE_COMPLETION = "USER_PROFILE_COMPLETION";

    public static final String EXPERIENCE_TYPE_WORK = "WORK_EXPERIENCE";
    public static final String EXPERIENCE_TYPE_EDUCATION = "EDUCATION_EXPERIENCE";


    // BOOLEAN VARIABLE NAMES ======================================================================
    public static final String ALLOW_UPDATE_APP = "ALLOW_UPDATE_APP";


    // GENERIC GET AND SET INTEGER VARIABLES =======================================================
    public static int getInt(String variableName) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE);
        return mySharedPreferences.getInt(variableName, 0);
    }

    public static void setInt(String variableName, int variableValue) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(variableName, variableValue);
        editor.apply();
    }


    // GENERIC GET AND SET STRING VARIABLES ========================================================
    public static String getString(String variableName) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE);
        return mySharedPreferences.getString(variableName, "");
    }

    public static void setString(String variableName, String variableValue) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(variableName, variableValue);
        editor.apply();
    }


    // GENERIC GET AND SET BOOLEAN VARIABLES =======================================================
    @NonNull
    public static Boolean getBool(String variableName) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE);
        return mySharedPreferences.getBoolean(variableName, false);
    }

    public static void setBool(String variableName, Boolean variableValue) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(variableName, variableValue);
        editor.apply();
    }


    // GENERIC GET AND SET DOUBLE ==================================================================
    public static Double getDouble(String variableName) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE);
        return Double.parseDouble(mySharedPreferences.getString(variableName, "0"));
    }

    public static void setDouble(String variableName, Double longitude) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(variableName, String.valueOf(longitude));
        editor.apply();
    }


    // DELETE FUNCTION =============================================================================
    public static void deleteAllSharedPrefs() {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS, MODE);
        settings.edit().clear().apply();
    }


    // USER FUNCTIONS ==============================================================================
    public static Boolean setUser(JSONObject user) {
        Helpers.LogThis(SHARED_PREFS, "USER SET");
        Helpers.LogThis(SHARED_PREFS, user.toString());
        try {
            setInt(USER_ID, user.getInt("id"));
            setString(USER_F_NAME, user.getString("first_name"));
            setString(USER_L_NAME, user.getString("last_name"));
            setString(USER_EMAIL, user.getString("email"));
            setString(USER_IMG_AVATAR, user.getString("avatar"));
            setString(USER_IMG_BANNER, user.getString("banner"));
            setString(PROFILE_URL, user.getString("profile_url"));
            setInt(USER_COUNTRY_CODE, user.getInt("country_code"));
            setInt(USER_PHONE, user.getInt("phone_number"));
            setString(USER_DOB, user.getString("dob"));
            setString(USER_FB_ID, user.getString("facebook_id"));
            setString(USER_GOOGLE_ID, user.getString("google_id"));
            setString(USER_URL, user.getString("profile_url"));

            if(!user.isNull("county")){
                JSONObject county_object = user.getJSONObject("county");
                setInt(USER_COUNTY, county_object.getInt("id"));
            }

            if(!user.isNull("profile_completion")){
                setInt(USER_PROFILE_COMPLETION, user.getInt("profile_completion"));
            }

            if (!user.isNull("lat"))
                setDouble(USER_LAT, user.getDouble("lat"));

            if (!user.isNull("lng"))
                setDouble(USER_LNG, user.getDouble("lng"));

            if (!user.isNull("full_address"))
                setString(USER_FULL_ADDRESS, user.getString("full_address"));

            if (!user.isNull("postal_code"))
                setString(USER_POSTAL_CODE, user.getString("postal_code"));

            JSONArray work_experience = user.getJSONArray("work_experience");
            if (work_experience != null && work_experience.length() > 0) {
                Database db = new Database();

                int array_length = work_experience.length();

                for (int i = 0; i < array_length; i++) {
                    db.setExperienceFromJson(work_experience.getJSONObject(i), EXPERIENCE_TYPE_WORK);
                }
            }

            JSONArray education_experience = user.getJSONArray("education_experience");
            if (education_experience != null && education_experience.length() > 0) {
                Database db = new Database();

                int array_length = education_experience.length();

                for (int i = 0; i < array_length; i++) {
                    db.setExperienceFromJson(education_experience.getJSONObject(i), EXPERIENCE_TYPE_EDUCATION);
                }
            }


            Helpers.LogThis(SHARED_PREFS, "AFTER UPDATE " +
                    getInt(USER_ID) + " - " +
                    getString(USER_F_NAME) + " - " +
                    getString(USER_L_NAME) + " - " +
                    getString(USER_EMAIL) + " - " +
                    getString(USER_IMG_AVATAR) + " - " +
                    getString(USER_IMG_BANNER) + " - " +
                    getInt(USER_COUNTRY_CODE) + " - " +
                    getInt(USER_PHONE) + " - " +
                    getString(USER_DOB) + " - " +
                    getString(USER_FB_ID) + " - " +
                    getString(USER_GOOGLE_ID) + " - " +
                    getDouble(USER_LAT) + " - " +
                    getDouble(USER_LNG)
            );

            return true;

        } catch (JSONException e) {
            Helpers.LogThis(SHARED_PREFS,  e.toString());
            return false;
        } catch (Exception e) {
            Helpers.LogThis(SHARED_PREFS,  e.toString());
            return false;
        }
    }

    public static UserModel getUser() {
        Helpers.LogThis(SHARED_PREFS, "USER GET");

        UserModel userModel = new UserModel();
        userModel.id = getInt(USER_ID);
        userModel.first_name = getString(USER_F_NAME);
        userModel.last_name = getString(USER_L_NAME);
        userModel.email = getString(USER_EMAIL);
        userModel.img_avatar = getString(USER_IMG_AVATAR);
        userModel.img_banner = getString(USER_IMG_BANNER);
        userModel.country_code = getInt(USER_COUNTRY_CODE);
        userModel.phone = getInt(USER_PHONE);
        userModel.dob = getString(USER_DOB);
        userModel.fb_id = getString(USER_FB_ID);
        userModel.google_id = getString(USER_GOOGLE_ID);
        userModel.geo_lat = getDouble(USER_LAT);
        userModel.geo_lng = getDouble(USER_LNG);


        Helpers.LogThis(SHARED_PREFS, "AFTER UPDATE " +
                getInt(USER_ID) + " - " +
                getString(USER_F_NAME) + " - " +
                getString(USER_L_NAME) + " - " +
                getString(USER_EMAIL) + " - " +
                getString(USER_IMG_AVATAR) + " - " +
                getString(USER_IMG_BANNER) + " - " +
                getInt(USER_COUNTRY_CODE) + " - " +
                getInt(USER_PHONE) + " - " +
                getString(USER_DOB) + " - " +
                getString(USER_FB_ID) + " - " +
                getString(USER_GOOGLE_ID) + " - " +
                getDouble(USER_LAT) + " - " +
                getDouble(USER_LNG)
        );

        return userModel;
    }

}
