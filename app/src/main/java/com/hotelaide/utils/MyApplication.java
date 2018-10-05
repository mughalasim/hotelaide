package com.hotelaide.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.hotelaide.BuildConfig;

import java.util.List;

//import com.facebook.stetho.Stetho;
//import com.hotelaide.BuildConfig;

public class MyApplication extends Application {

        @SuppressLint("StaticFieldLeak")
        private static Context context;

        public void onCreate() {
            super.onCreate();
            MyApplication.context = getApplicationContext();
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            // then open this link in chrome -> chrome://inspect/#devices
//            if(BuildConfig.LOGGING){
//                Stetho.initializeWithDefaults(this);
//            }
        }
    public static void initFireBase() {
        FirebaseOptions builder = new FirebaseOptions.Builder()
                .setApplicationId(BuildConfig.FB_APP_ID)
                .setApiKey(BuildConfig.FB_APP_KEY)
                .setDatabaseUrl(BuildConfig.FB_DB_URL)
                .setStorageBucket(BuildConfig.FB_STORE)
                .build();

        List<FirebaseApp> fire_base_app_list = FirebaseApp.getApps(MyApplication.getAppContext());

        if (fire_base_app_list.size()<1) {
            FirebaseApp.initializeApp(getAppContext(), builder);
        }
    }

        public static Context getAppContext() {
            return MyApplication.context;
        }

}
