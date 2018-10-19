package com.hotelaide.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.MyApplication;

public class BackgroundFetchService extends Service {
    private static final String TAG_LOG = "BACKGROUND SERVICE";


    // BASIC OVERRIDE METHODS ======================================================================
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Helpers.LogThis(TAG_LOG, "ON_START");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Helpers helpers = new Helpers(MyApplication.getAppContext());
        helpers.asyncGetUser();
        helpers.asyncGetCategories();
        helpers.asyncGetJobTypes();
        helpers.asyncGetEducationalLevels();
        helpers.asyncGetCounties();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Helpers.LogThis(TAG_LOG, "ON_DESTROY");
        super.onDestroy();
    }



}
