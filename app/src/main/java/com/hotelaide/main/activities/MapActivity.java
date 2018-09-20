package com.hotelaide.main.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hotelaide.R;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import pub.devrel.easypermissions.EasyPermissions;

import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_LAT;
import static com.hotelaide.utils.StaticVariables.USER_LNG;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Helpers helpers;

    private Toolbar toolbar;

    private MaterialButton btn_confirm;

    private double
            LATITUDE = 0,
            LONGITUDE = 0;

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        helpers = new Helpers(MapActivity.this);

        findAllViews();

        setUpToolBar();

        startUpMap();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        helpers.myPermissionsDialog(MapActivity.this, grantResults);
    }

    @Override
    public void onBackPressed() {
        sendBackResult();
    }

    @Override
    public void onMapReady(final GoogleMap google_map) {
        if (google_map != null) {

            google_map.clear();

            if (LATITUDE != 0) {
                LatLng location = new LatLng(LATITUDE, LONGITUDE);

                google_map.moveCamera(CameraUpdateFactory.newLatLng(location));
                google_map.animateCamera(CameraUpdateFactory.zoomTo(14f));

                Marker my_marker = google_map.addMarker(new MarkerOptions().position(
                        new LatLng(LATITUDE, LONGITUDE))
                        .title(SharedPrefs.getString(USER_F_NAME)));
                my_marker.setVisible(true);
                my_marker.showInfoWindow();
                my_marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                google_map.setMyLocationEnabled(true);
            }

            google_map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    google_map.clear();
                    Marker my_marker = google_map.addMarker(new MarkerOptions().position(latLng)
                            .title(SharedPrefs.getString(USER_F_NAME)));
                    my_marker.setVisible(true);
                    my_marker.showInfoWindow();
                    my_marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    LATITUDE = latLng.latitude;
                    LONGITUDE = latLng.latitude;
                }
            });

            google_map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        helpers.dialogNoGPS(MapActivity.this);
                    }
                    return false;
                }
            });
        }
    }


    // BASIC FUNCTIONS =============================================================================
    private void setUpToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setTitle("SET LOCATION");
        }
        toolbar.setTitle("SET LOCATION");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void findAllViews() {
        // Top Generic Relative Layout
        toolbar = findViewById(R.id.toolbar);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setText("SET NEW LOCATION");
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackResult();
            }
        });

    }

    private void startUpMap() {
        if (helpers.validateGooglePlayServices(MapActivity.this)) {
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            assert fm != null;
            fm.getMapAsync(this);
        } else {
            helpers.ToastMessage(MapActivity.this, getString(R.string.error_update_google_play));
            onBackPressed();
        }
    }

    private void sendBackResult() {
        SharedPrefs.setDouble(USER_LAT, LATITUDE);
        SharedPrefs.setDouble(USER_LNG, LONGITUDE);
        finish();
    }

}
