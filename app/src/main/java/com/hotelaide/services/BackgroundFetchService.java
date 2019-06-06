package com.hotelaide.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;
import static com.hotelaide.utils.StaticVariables.db;

public class BackgroundFetchService extends Service {
    private static final String TAG_LOG = "BACKGROUND SERVICE";


    // BASIC OVERRIDE METHODS ======================================================================
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Helpers.logThis(TAG_LOG, "ON_START");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        db = new Database();
        HelpersAsync.asyncGetUser();
        HelpersAsync.asyncGetCategories();
        HelpersAsync.asyncGetJobTypes();
        HelpersAsync.asyncGetEducationalLevels();
        HelpersAsync.asyncGetCounties();
        HelpersAsync.asyncGetAllDocuments();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Helpers.logThis(TAG_LOG, "ON_DESTROY");
        super.onDestroy();
    }



}
