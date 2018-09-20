package com.hotelaide.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.hotelaide.utils.Helpers;

import static com.hotelaide.utils.StaticVariables.BROADCAST_GPS;
import static com.hotelaide.utils.StaticVariables.USER_LAT;
import static com.hotelaide.utils.StaticVariables.USER_LNG;


public class SingleLocationService extends Service {
    private static final String TAG_LOG = "SINGLE LOCATION SERVICE";
    private LocationManager mLocationManager = null;
    private static double DOUBLE_LATITUDE = 0;
    private static double DOUBLE_LONGITUDE = 0;
    private static boolean GPS = false;
    private CountDownTimer countDownTimer;
    private boolean BOOL_COUNT_DOWN_STARTED = false;
    private LocationListener mLocationListeners = new LocationListener(LocationManager.NETWORK_PROVIDER);



    // BASIC OVERRIDE METHODS ======================================================================
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Helpers.LogThis(TAG_LOG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        Helpers.LogThis(TAG_LOG, "onCreate");

        initializeLocationManager();

        try {
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                GPS = true;
                startCountDown();
                Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null && location.getLongitude() != location.getLatitude()) {
                    DOUBLE_LATITUDE = location.getLatitude();
                    DOUBLE_LONGITUDE = location.getLongitude();
                    sendBroadCast();
                } else {
                    mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListeners, null);
                }
            } else {
                GPS = false;
                sendBroadCast();
            }
        } catch (SecurityException ex) {
            Log.i(TAG_LOG, "fail to request location update, ignore", ex);
            sendBroadCast();
        } catch (IllegalArgumentException ex) {
            Log.d(TAG_LOG, "network provider does not exist, " + ex.getMessage());
            sendBroadCast();
        }
    }

    @Override
    public void onDestroy() {
        Helpers.LogThis(TAG_LOG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListeners);
                stopCountDown();
            } catch (Exception ex) {
                Log.i(TAG_LOG, "failed to remove location listeners, ignore", ex);
            }
        }
    }


    // BASIC FUNCTIONS =============================================================================
    private void initializeLocationManager() {
        Helpers.LogThis(TAG_LOG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void sendBroadCast() {
        BOOL_COUNT_DOWN_STARTED = false;
        sendBroadcast(new Intent().setAction(BROADCAST_GPS)
                .putExtra(USER_LAT, DOUBLE_LATITUDE)
                .putExtra(USER_LNG, DOUBLE_LONGITUDE)
                .putExtra("GPS", GPS)
        );
        Helpers.LogThis(TAG_LOG, "LOCATION: " + DOUBLE_LONGITUDE);
        Helpers.LogThis(TAG_LOG, "LATITUDE: " + DOUBLE_LATITUDE);
        Helpers.LogThis(TAG_LOG, "GPS ENABLED: " + GPS);
        stopSelf();
    }

    private void startCountDown() {
        BOOL_COUNT_DOWN_STARTED = true;
        final int INT_COUNT_DOWN_TIMER = 24000;
        countDownTimer = new CountDownTimer(INT_COUNT_DOWN_TIMER, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if (BOOL_COUNT_DOWN_STARTED) {
                    sendBroadCast();
                }
            }

        }.start();
    }

    private void stopCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }


    // LOCATION LISTENER ===========================================================================
    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        private LocationListener(String provider) {
            Helpers.LogThis(TAG_LOG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {

            DOUBLE_LATITUDE = location.getLatitude();
            DOUBLE_LONGITUDE = location.getLongitude();

            sendBroadCast();

            mLastLocation.set(location);

        }

        @Override
        public void onProviderDisabled(String provider) {
            Helpers.LogThis(TAG_LOG, "onProviderDisabled: " + provider);
            sendBroadCast();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Helpers.LogThis(TAG_LOG, "onProviderEnabled: " + provider);
            sendBroadCast();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Helpers.LogThis(TAG_LOG, "onStatusChanged: " + provider);
            sendBroadCast();
        }

    }
}
