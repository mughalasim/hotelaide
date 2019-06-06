package com.hotelaide.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;

import java.util.Calendar;

import static com.hotelaide.utils.StaticVariables.APP_IS_RUNNING;
import static com.hotelaide.utils.StaticVariables.db;

public class UserIsOnlineService extends Service {
    private static final String TAG_LOG = "USER IS ONLINE";


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
        APP_IS_RUNNING = true;
        Helpers.logThis(TAG_LOG, "ONLINE");
        Helpers.updateUserOnlineStatus("Online");
    }

    @Override
    public void onDestroy() {
        APP_IS_RUNNING = false;
        Helpers.logThis(TAG_LOG, "OFFLINE");
        Helpers.updateUserOnlineStatus(Calendar.getInstance().getTimeInMillis());
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        APP_IS_RUNNING = false;
        Helpers.logThis(TAG_LOG, "OFFLINE");
        Helpers.updateUserOnlineStatus(Calendar.getInstance().getTimeInMillis());
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }
}
