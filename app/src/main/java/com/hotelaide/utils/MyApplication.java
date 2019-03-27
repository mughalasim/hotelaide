package com.hotelaide.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;

import java.util.List;

import androidx.appcompat.app.AppCompatDelegate;

import com.facebook.stetho.Stetho;

import static com.hotelaide.utils.StaticVariables.ALLOW_PUSH_MESSAGES;
import static com.hotelaide.utils.StaticVariables.ALLOW_PUSH_NOTIFICATIONS;
import static com.hotelaide.utils.StaticVariables.ALLOW_UPDATE_APP;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_ADDRESS;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_DASH;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_MESSAGES;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_PROFILE;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_PROFILE_EDIT;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_SEARCH;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_SEARCH_MEMBERS;

public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        // then open this link in chrome -> chrome://inspect/#devices
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    public static void initFireBase() {
        FirebaseOptions builder = new FirebaseOptions.Builder()
                .setApplicationId(BuildConfig.FB_APP_ID)
                .setApiKey(getAppContext().getString(R.string.FB_API_KEY))
                .setDatabaseUrl(BuildConfig.FB_DB_URL)
                .setStorageBucket(BuildConfig.FB_STORE)
                .build();

        List<FirebaseApp> fire_base_app_list = FirebaseApp.getApps(MyApplication.getAppContext());

        if (fire_base_app_list.size() < 1) {
            FirebaseApp.initializeApp(getAppContext(), builder);
        }

    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public static void setFirstTimeTutorial(Boolean state){
        SharedPrefs.setBool(ALLOW_UPDATE_APP, true);
        SharedPrefs.setBool(ALLOW_PUSH_MESSAGES, true);
        SharedPrefs.setBool(ALLOW_PUSH_NOTIFICATIONS, true);

        SharedPrefs.setBool(FIRST_LAUNCH_DASH, state);
        SharedPrefs.setBool(FIRST_LAUNCH_PROFILE, state);
        SharedPrefs.setBool(FIRST_LAUNCH_PROFILE_EDIT, state);
        SharedPrefs.setBool(FIRST_LAUNCH_SEARCH, state);
        SharedPrefs.setBool(FIRST_LAUNCH_SEARCH_MEMBERS, state);
        SharedPrefs.setBool(FIRST_LAUNCH_MESSAGES, state);
        SharedPrefs.setBool(FIRST_LAUNCH_ADDRESS, state);
    }

}
