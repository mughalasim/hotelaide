package com.hotelaide.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

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

        public static Context getAppContext() {
            return MyApplication.context;
        }

}
