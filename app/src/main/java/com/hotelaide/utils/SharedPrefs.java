package com.hotelaide.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;


public class SharedPrefs {
    @SuppressLint("StaticFieldLeak")
    private static final Context context = MyApplication.getAppContext();
    private static final String SHARED_PREFS = "SHARED_PREFS";

    // ASYNC CALLS ON FIRST ACTIVITY STARTUP =======================================================
    @NonNull
    public static Boolean getAsyncCallHomePage() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getBoolean("ASYNC_HOME", true);
    }

    public static void setAsyncCallHomePage(Boolean update) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean("ASYNC_HOME", update);
        editor.apply();
    }

    @NonNull
    public static Boolean getAsyncCallUserDetails() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getBoolean("ASYNC_USER_DETAILS", true);
    }

    public static void setAsyncCallUserDetails(Boolean update) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean("ASYNC_USER_DETAILS", update);
        editor.apply();
    }



    // OTHER FUNCTIONS =============================================================================
    public static void deleteAllSharedPrefs() {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        settings.edit().clear().apply();
    }



    // SHARED_PREFS ================================================================================
    public static String getToken() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("TOKEN", "");
    }

    public static void setToken(String token) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("TOKEN", token);
        editor.apply();
    }

    public static String getUserName() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("USERNAME", "");
    }

    public static void setUserName(String salutation, String fName, String lName) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("USERNAME", salutation + ". "+fName + " " + lName);
        editor.apply();
    }

    public static String getCountryFlag() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("COUNTRYFLAG", "");
    }

    public static void setCountryFlag(String countryFlag) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("COUNTRYFLAG", countryFlag);
        editor.apply();
    }

    public static String getSupportNumber() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("SUPPORTNUMBER", "");
    }

    public static void setSupportNumber(String support_number) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("SUPPORTNUMBER", support_number);
        editor.apply();
    }

    @NonNull
    public static Integer getOldDataBaseVersion() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt("DATABASE_VERSION", 0);
    }

    public static void setNewDataBaseVersion(int db_version) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("DATABASE_VERSION", db_version);
        editor.apply();
    }

    @NonNull
    public static Boolean getAllowUpdateApp() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getBoolean("UPDATEAPP", true);
    }

    public static void setAllowUpdateApp(Boolean update) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean("UPDATEAPP", update);
        editor.apply();
    }


    // NAVIGATION DATA =============================================================================
    public static String getNavigationData() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("NAVDATA", "");
    }

    public static void setNavigationData(String navigationData) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("NAVDATA", navigationData);
        editor.apply();
    }

    @NonNull
    public static Boolean getNavigationPushCLicked() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getBoolean("PUSHCLICKED", false);
    }

    public static void setNavigationPushCLicked(Boolean update) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean("PUSHCLICKED", update);
        editor.apply();
    }




    // GEO LOCATION TRACKING =======================================================================
    public static Double getLongitude() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        return Double.parseDouble(mySharedPreferences.getString("LONGITUDE", "0"));
    }

    public static void setLongitude(Double longitude) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("LONGITUDE", String.valueOf(longitude));
        editor.apply();
    }

    public static Double getLatitude() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        return Double.parseDouble(mySharedPreferences.getString("LATITUDE", "0"));
    }

    public static void setLatitude(Double Latitude) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("LATITUDE", String.valueOf(Latitude));
        editor.apply();
    }

    // ACCOUNT SETTINGS ============================================================================

    @NonNull
    public static Boolean getAllowNearbyPushNotifs() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getBoolean("NEARBYPUSH", false);
    }

    public static void setAllowNearbyPushNotifs(Boolean update) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean("NEARBYPUSH", update);
        editor.apply();
    }

}
