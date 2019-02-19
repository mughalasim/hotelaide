package com.hotelaide.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.MyApplication;

public class FileUploadService extends Service {
    private static final String TAG_LOG = "BACKGROUND SERVICE";


    // OVERRIDE METHODS ============================================================================
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

    }

    @Override
    public void onDestroy() {
        Helpers.logThis(TAG_LOG, "ON_DESTROY");
        super.onDestroy();
    }



}
