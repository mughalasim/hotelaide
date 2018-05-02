package com.hotelaide.utils_external;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hotelaide.R;
import com.hotelaide.main_pages.models.RestaurantModel;
import com.hotelaide.services.RestaurantService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.Helpers.FLAG_FALSE;
import static com.hotelaide.utils.Helpers.FLAG_TRUE;
import static com.hotelaide.utils.Helpers.SORT_NEARBY;
import static com.hotelaide.utils.Helpers.STR_NAVIGATION_REST;

public class LocationService extends Service {
    private static final String TAG_LOG = "LOCATION SERVICE";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 600 * 1000;
    private static final float LOCATION_DISTANCE = 15f;
    private static final int LOCATION_RADIUS = 150;
    private Helpers helper;
    private Database db;
    static String STR_REST_ID_STATIC = "";
    LocationListener mLocationListeners = new LocationListener(LocationManager.NETWORK_PROVIDER);


    // BASIC OVERRIDE METHIDS ======================================================================
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
        helper = new Helpers(LocationService.this);
        db = new Database();

        Helpers.LogThis(TAG_LOG, "onCreate");

        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners);

        } catch (SecurityException ex) {
            Log.i(TAG_LOG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG_LOG, "network provider does not exist, " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Helpers.LogThis(TAG_LOG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListeners);
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



    // LOCATION LISTENER ===========================================================================
    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Helpers.LogThis(TAG_LOG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {

            Location loc1 = new Location("");
            loc1.setLatitude(SharedPrefs.getLatitude());
            loc1.setLongitude(SharedPrefs.getLongitude());

            Location loc2 = new Location("");
            loc2.setLatitude(location.getLatitude());
            loc2.setLongitude(location.getLongitude());

            Helpers.LogThis(TAG_LOG, "LOCATION 1: " + loc1);
            Helpers.LogThis(TAG_LOG, "LOCATION 2: " + loc2);

            float distanceInMeters = loc1.distanceTo(loc2);
            Helpers.LogThis(TAG_LOG, "DISTANCE: " + distanceInMeters);

            if (distanceInMeters > LOCATION_RADIUS) {
                asyncSearchRestaurantsWithDiscounts(location.getLongitude(), location.getLatitude());
            }

            SharedPrefs.setLatitude(location.getLatitude());
            SharedPrefs.setLongitude(location.getLongitude());

            mLastLocation.set(location);

        }

        @Override
        public void onProviderDisabled(String provider) {
            Helpers.LogThis(TAG_LOG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Helpers.LogThis(TAG_LOG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Helpers.LogThis(TAG_LOG, "onStatusChanged: " + provider);
        }

    }


    // ASYNC TASK TO FETCH RESTAURANTS WITH DISCOUNTS ==============================================
    private void asyncSearchRestaurantsWithDiscounts(double longitude, double latitude) {
        Helpers.LogThis(TAG_LOG, "USER TOKEN: " + SharedPrefs.getToken());

        Database.userModel.user_token = SharedPrefs.getToken();

        if (helper.validateInternetConnection() && !Database.userModel.user_token.equals("")) {
            RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
            final Call<JsonObject> call = restaurantService.searchRestaurant(
                    "",
                    "",
                    Database.userModel.dob,
                    "",
                    "",
                    "",
                    String.valueOf(latitude),
                    String.valueOf(longitude),
                    SORT_NEARBY,
                    "",
                    FLAG_TRUE,
                    FLAG_FALSE
            );

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    try {
                        JSONObject main = new JSONObject(String.valueOf(response.body()));
                        Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
                        if (!main.getBoolean("error")) {

                            JSONArray jArray = main.getJSONArray("result");
                            int result_length = jArray.length();

                            Helpers.LogThis(TAG_LOG, getString(R.string.log_response_length) + result_length);
                            Helpers.LogThis(TAG_LOG, "REST ID: " + STR_REST_ID_STATIC);

                            if (result_length > 0) {
                                for (int i = 0; i < 1; i++) {
                                    RestaurantModel restaurantModel = db.setRestaurants(jArray.getJSONObject(i));
                                    if (!restaurantModel.distance.equals("Unknown")) {
                                        if (!STR_REST_ID_STATIC.equals(restaurantModel.id)) {
                                            STR_REST_ID_STATIC = restaurantModel.id;

                                            JSONObject json_data = jArray.getJSONObject(i);
                                            if (json_data.getDouble("distance") < LOCATION_RADIUS) {
                                                Helpers.setAppNavigation(STR_NAVIGATION_REST, restaurantModel.id, restaurantModel.restaurant_name, "You are nearby this restaurant");
                                                helper.createNotification(LocationService.this,
                                                        SharedPrefs.getUserName(),
                                                        "According to our calculations you are near " + restaurantModel.restaurant_name + ", so why not claim a discount?",
                                                        new Bundle());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());

                    } catch (Exception e) {
                        Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());
                }
            });

        }
    }
}
