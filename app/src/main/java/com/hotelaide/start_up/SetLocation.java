package com.hotelaide.start_up;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hotelaide.R;
import com.hotelaide.main_pages.models.CityModel;
import com.hotelaide.services.GeneralService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetLocation extends AppCompatActivity {

    private Helpers
            helper;

    private Database db;

    private BroadcastReceiver
            receiver;

    private final String
            STATE_NO_GPS = "0",
            STATE_LOADING = "1",
            TAG_LOG = "SET LOCATION";

    private LocationManager
            locationManager;

    private LocationListener
            locationListener;

    private RoundedImageView
            country_flag;

    private float
            FLT_LATITUDE,
            FLT_LONGITUDE;

    private LinearLayout
            LL_no_gps,
            LL_loading,
            LL_proceed;

    private TextView
            txtSkip,
            txtMessage,
            txtOk;


    // OVERRIDE FUNCTIONS ==========================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);

        helper = new Helpers(SetLocation.this);

        db = new Database();

        listenExitBroadcast();

        findAllViews();

        fetchLocation();

        helper.setTracker(TAG_LOG);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        helper.dismissProgressDialog();
        disableFetchLocation();
        super.onDestroy();
    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        txtMessage = findViewById(R.id.txtMessage);
        txtOk = findViewById(R.id.txtOk);
        txtSkip = findViewById(R.id.txtSkip);

        LL_proceed = findViewById(R.id.LL_proceed);
        LL_loading = findViewById(R.id.LL_loading);
        LL_no_gps = findViewById(R.id.LL_no_gps);

        country_flag = findViewById(R.id.country_flag);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    private void updateScreen(String state) {
        switch (state) {
            case STATE_NO_GPS:
                LL_no_gps.setVisibility(View.VISIBLE);
                LL_loading.setVisibility(View.GONE);
                LL_proceed.setVisibility(View.GONE);
                helper.animate_fade_in_up(LL_no_gps);
                helper.animate_fade_in_up(LL_no_gps);
                break;

            case STATE_LOADING:
                LL_no_gps.setVisibility(View.GONE);
                LL_loading.setVisibility(View.VISIBLE);
                LL_proceed.setVisibility(View.GONE);
                helper.animate_fade_in_up(LL_loading);
                helper.animate_fade_in_up(LL_loading);
                break;

            default:
                LL_no_gps.setVisibility(View.GONE);
                LL_loading.setVisibility(View.GONE);
                LL_proceed.setVisibility(View.VISIBLE);
                helper.animate_fade_in_up(LL_proceed);
                helper.animate_fade_in_up(LL_proceed);
                break;

        }
    }

    private void readyToSkip() {
        SharedPrefs.setSupportNumber("254711222222");
        txtSkip.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                txtSkip.setVisibility(View.VISIBLE);
                txtSkip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Database db = new Database();
                        finish();
                        disableFetchLocation();
                        startActivity(new Intent(SetLocation.this, SetAccount.class)
                                .putExtra("city_id", Database.userModel.city_id)
                        );
                    }
                });
            }
        }, 15000);
    }

    private void listenExitBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Helpers.BroadcastValue);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(receiver, filter);
    }

    private void saveAndUpdate(final CityModel cityModel) {
        SharedPrefs.setSupportNumber(cityModel.support_number);

        Glide.with(SetLocation.this).load(cityModel.country_flag).into(country_flag);

        String Message = getString(R.string.txt_setlocation_1)
                + cityModel.city_name
                + getString(R.string.txt_setlocation_2)
                + cityModel.country_name + getString(R.string.txt_setlocation_3);

        txtMessage.setText(Message);

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                disableFetchLocation();
                startActivity(new Intent(SetLocation.this, SetAccount.class)
                        .putExtra("city_id", String.valueOf(cityModel.city_id))
                );
            }
        });
    }


    // LOCATIONS MANAGEMENT  =======================================================================
    private void fetchLocation() {
        if (locationManager != null) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                helper.dialogNoGPS(SetLocation.this);
                updateScreen(STATE_NO_GPS);
            } else {
                locationListener = new MyLocationListener();
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new MyLocationListener(), null);
                updateScreen(STATE_LOADING);
                readyToSkip();
            }
        }
    }

    public void retryFetchLocation(View view) {
        fetchLocation();
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            FLT_LONGITUDE = (float) location.getLongitude();
            FLT_LATITUDE = (float) location.getLatitude();
            Helpers.LogThis(TAG_LOG, "LONGITUDE: " + FLT_LONGITUDE);
            Helpers.LogThis(TAG_LOG, "LATITUDE: " + FLT_LATITUDE);
            asyncGetCountries();
        }

        @Override
        public void onProviderDisabled(String provider) {
            failedLocationFetch();
        }

        @Override
        public void onProviderEnabled(String provider) {
            fetchLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Helpers.LogThis(TAG_LOG, "Status: " + status);
            if (status == LocationProvider.OUT_OF_SERVICE) {
                failedLocationFetch();
            } else if (status == LocationProvider.AVAILABLE) {
                fetchLocation();
            }
        }
    }

    private void disableFetchLocation() {
        if (locationListener != null && locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }


    // ERROR CAPTURE ===============================================================================
    private void failedInternetConnection() {
        final Snackbar snackBar = Snackbar.make(findViewById(R.id.drawer_layout),
                getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
                fetchLocation();
            }
        });
        snackBar.show();
    }

    private void failedLocationFetch() {
        final Snackbar snackBar = Snackbar.make(findViewById(R.id.drawer_layout),
                getString(R.string.error_location_fetch), Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
                fetchLocation();
                helper.ToastMessage(SetLocation.this, "Locating... Please wait...");
            }
        });
        snackBar.show();
        disableFetchLocation();
    }


    // ASYNC GET ALL COUNTRIES =====================================================================
    private void asyncGetCountries() {

        GeneralService generalService = GeneralService.retrofit.create(GeneralService.class);
        final Call<JsonObject> call = generalService.getCountries();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
                    if (!main.getBoolean("error")) {

                        JSONArray jArray = main.getJSONArray("result");
                        int result_length = jArray.length();
                        Map<CityModel, Float> aMap = new HashMap<>();

                        for (int i = 0; i < result_length; i++) {
                            JSONObject countryObject = jArray.getJSONObject(i);

                            final int COUNTRY_ID = countryObject.getInt("id");
                            final String COUNTRY_NAME = countryObject.getString("name");
                            final String SUPPORT_NUMBER = countryObject.getString("support_number");
                            final String COUNTRY_CODE = countryObject.getString("code");
                            final String COUNTRY_FLAG = countryObject.getString("country_flag");

                            JSONArray active_cities = countryObject.getJSONArray("active_cities");
                            int result_length2 = active_cities.length();
                            for (int w = 0; w < result_length2; w++) {
                                JSONObject cityObject = active_cities.getJSONObject(w);
                                final String CITY_ID = cityObject.getString("id");
                                final String CITY_NAME = cityObject.getString("name");
                                final float CITY_LAT = Float.parseFloat(cityObject.getString("geolat"));
                                final float CITY_LNG = Float.parseFloat(cityObject.getString("geolng"));

                                Location loc2 = new Location("");
                                loc2.setLatitude(CITY_LAT);
                                loc2.setLongitude(CITY_LNG);

                                Location loc1 = new Location("");
                                loc1.setLatitude(FLT_LATITUDE);
                                loc1.setLongitude(FLT_LONGITUDE);

                                float distance = loc1.distanceTo(loc2);

                                CityModel cityModel = new CityModel();
                                cityModel.city_id = CITY_ID;
                                cityModel.city_name = CITY_NAME;
                                cityModel.country_id = COUNTRY_ID;
                                cityModel.country_name = COUNTRY_NAME;
                                cityModel.country_code = COUNTRY_CODE;
                                cityModel.country_flag = COUNTRY_FLAG;
                                cityModel.support_number = SUPPORT_NUMBER;

                                db.setCity(CITY_ID, CITY_NAME);

                                helper.asyncGetAreas(CITY_ID);

                                aMap.put(cityModel, distance);

                                Helpers.LogThis(TAG_LOG, CITY_NAME + " - " + String.valueOf(distance));

                            }
                        }

                        // Organize HashMap to the smallest calculated distance
                        Set<Map.Entry<CityModel, Float>> mapEntries = aMap.entrySet();
                        List<Map.Entry<CityModel, Float>> aList = new LinkedList<>(mapEntries);

                        Collections.sort(aList, new Comparator<Map.Entry<CityModel, Float>>() {
                            @Override
                            public int compare(Map.Entry<CityModel, Float> ele1, Map.Entry<CityModel, Float> ele2) {
                                return ele1.getValue().compareTo(ele2.getValue());
                            }
                        });

                        // Storing the list into Linked HashMap to preserve the order of insertion.
                        Map<CityModel, Float> aMap2 = new LinkedHashMap<>();
                        for (Map.Entry<CityModel, Float> entry : aList) {
                            aMap2.put(entry.getKey(), entry.getValue());
                        }

                        Map.Entry<CityModel, Float> entry = aMap2.entrySet().iterator().next();
                        saveAndUpdate(entry.getKey());

                        updateScreen("");

                    } else {
                        failedInternetConnection();
                    }

                } catch (JSONException e) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                    failedInternetConnection();

                } catch (Exception e) {
                    failedInternetConnection();
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());
                failedInternetConnection();
            }
        });
    }


}
