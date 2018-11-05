package com.hotelaide.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hotelaide.BuildConfig;
import com.hotelaide.main.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;

import static com.hotelaide.utils.StaticVariables.BROADCAST_SET_USER_COMPLETE;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.StaticVariables.EXTRA_FAILED;
import static com.hotelaide.utils.StaticVariables.EXTRA_PASSED;
import static com.hotelaide.utils.StaticVariables.PROFILE_URL;
import static com.hotelaide.utils.StaticVariables.USER_ABOUT;
import static com.hotelaide.utils.StaticVariables.USER_AVAILABILITY;
import static com.hotelaide.utils.StaticVariables.USER_COUNTRY_CODE;
import static com.hotelaide.utils.StaticVariables.USER_COUNTY;
import static com.hotelaide.utils.StaticVariables.USER_DOB;
import static com.hotelaide.utils.StaticVariables.USER_EMAIL;
import static com.hotelaide.utils.StaticVariables.USER_FB_ID;
import static com.hotelaide.utils.StaticVariables.USER_FULL_ADDRESS;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_GENDER;
import static com.hotelaide.utils.StaticVariables.USER_GOOGLE_ID;
import static com.hotelaide.utils.StaticVariables.USER_ID;
import static com.hotelaide.utils.StaticVariables.USER_IMG_AVATAR;
import static com.hotelaide.utils.StaticVariables.USER_IMG_BANNER;
import static com.hotelaide.utils.StaticVariables.USER_LAT;
import static com.hotelaide.utils.StaticVariables.USER_LNG;
import static com.hotelaide.utils.StaticVariables.USER_L_NAME;
import static com.hotelaide.utils.StaticVariables.USER_PHONE;
import static com.hotelaide.utils.StaticVariables.USER_POSTAL_CODE;
import static com.hotelaide.utils.StaticVariables.USER_PROFILE_COMPLETION;
import static com.hotelaide.utils.StaticVariables.USER_URL;

public class SharedPrefs {
    @SuppressLint("StaticFieldLeak")
    private static final Context context = MyApplication.getAppContext();
    private static final String SHARED_PREFS = "SHARED_PREFS";
    private static final int MODE = Activity.MODE_PRIVATE;


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
        try {
            if (user.getString("account_type").equals(BuildConfig.ACCOUNT_TYPE)) {

                setInt(USER_ID, user.getInt("id"));
                setString(USER_F_NAME, user.getString("first_name"));
                setString(USER_L_NAME, user.getString("last_name"));
                setString(USER_ABOUT, user.getString("about_me"));
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
                setInt(USER_AVAILABILITY, user.getInt("availability"));

                if (!user.isNull("gender")) {
                    setInt(USER_GENDER, user.getInt("gender"));
                } else {
                    setInt(USER_GENDER, 0);
                }

                if (!user.isNull("county")) {
                    JSONObject county_object = user.getJSONObject("county");
                    setInt(USER_COUNTY, county_object.getInt("id"));
                }

                if (!user.isNull("profile_progress")) {
                    setInt(USER_PROFILE_COMPLETION, user.getInt("profile_progress"));
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

                context.sendBroadcast(new Intent().setAction(BROADCAST_SET_USER_COMPLETE).putExtra(EXTRA_PASSED, EXTRA_PASSED));

                logUserModel();

                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            Helpers.LogThis(SHARED_PREFS, e.toString());
            context.sendBroadcast(new Intent().setAction(BROADCAST_SET_USER_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
            return false;
        } catch (Exception e) {
            Helpers.LogThis(SHARED_PREFS, e.toString());
            context.sendBroadcast(new Intent().setAction(BROADCAST_SET_USER_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
            return false;
        }
    }

    public static UserModel getUser() {
        Helpers.LogThis(SHARED_PREFS, "USER GET");

        UserModel userModel = new UserModel();
        userModel.id = getInt(USER_ID);
        userModel.first_name = getString(USER_F_NAME);
        userModel.last_name = getString(USER_L_NAME);
        userModel.gender = getInt(USER_GENDER);
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

        logUserModel();

        return userModel;
    }

    public static void logUserModel() {
        Helpers.LogThis(SHARED_PREFS,
                "\n UID: " + getInt(USER_ID)
                        + "\n F_NAME: " + getString(USER_F_NAME)
                        + "\n L_NAME: " + getString(USER_L_NAME)
                        + "\n ABOUT: " + getString(USER_ABOUT)
                        + "\n GENDER: " + getInt(USER_GENDER)
                        + "\n EMAIL: " + getString(USER_EMAIL)
                        + "\n AVATAR: " + getString(USER_IMG_AVATAR)
                        + "\n BANNER: " + getString(USER_IMG_BANNER)
                        + "\n COUNTRY CODE: " + getInt(USER_COUNTRY_CODE)
                        + "\n PHONE: " + getInt(USER_PHONE)
                        + "\n DOB: " + getString(USER_DOB)
                        + "\n FB_ID: " + getString(USER_FB_ID)
                        + "\n GOOGLE_ID: " + getString(USER_GOOGLE_ID)
                        + "\n LAT: " + getDouble(USER_LAT)
                        + "\n LNG: " + getDouble(USER_LNG)
                        + "\n AVAILABILITY: " + getInt(USER_AVAILABILITY)
        );
    }

}
