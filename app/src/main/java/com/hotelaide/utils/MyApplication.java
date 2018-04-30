package com.hotelaide.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;


public class MyApplication extends Application{

        @SuppressLint("StaticFieldLeak")
        private static Context context;

        public void onCreate() {
            super.onCreate();
            MyApplication.context = getApplicationContext();
            // then open this link in chrome -> chrome://inspect/#devices
//            if(BuildConfig.LOGGING){
//                Stetho.initializeWithDefaults(this);
//            }
        }

        public static Context getAppContext() {
            return MyApplication.context;
        }

}
