package com.hotelaide.main.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.hotelaide.R;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import pub.devrel.easypermissions.EasyPermissions;

import static com.hotelaide.utils.StaticVariables.FLOAT_GOOGLE_MAP_ZOOM;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_LAT;
import static com.hotelaide.utils.StaticVariables.USER_LNG;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Helpers helpers;

    private Toolbar toolbar;

    public static double
            MAP_ACTIVITY_LATITUDE = 0.0,
            MAP_ACTIVITY_LONGITUDE = 0.0;

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MAP_ACTIVITY_LATITUDE = SharedPrefs.getDouble(USER_LAT);
        MAP_ACTIVITY_LONGITUDE = SharedPrefs.getDouble(USER_LNG);

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
    public void onMapReady(final GoogleMap google_map) {
        if (google_map != null) {

            google_map.clear();

            if (MAP_ACTIVITY_LATITUDE != 0) {
                LatLng location = new LatLng(MAP_ACTIVITY_LATITUDE, MAP_ACTIVITY_LONGITUDE);

                google_map.moveCamera(CameraUpdateFactory.newLatLng(location));
                google_map.animateCamera(CameraUpdateFactory.zoomTo(FLOAT_GOOGLE_MAP_ZOOM));

                Marker my_marker = google_map.addMarker(new MarkerOptions().position(
                        new LatLng(MAP_ACTIVITY_LATITUDE, MAP_ACTIVITY_LONGITUDE))
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
                    MAP_ACTIVITY_LATITUDE = latLng.latitude;
                    MAP_ACTIVITY_LONGITUDE = latLng.longitude;
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
        MaterialButton btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setText("SET NEW LOCATION");
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void startUpMap() {
        if (helpers.validateGooglePlayServices(MapActivity.this)) {
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
            assert fm != null;
            fm.getMapAsync(this);
        } else {
            helpers.ToastMessage(MapActivity.this, getString(R.string.error_update_google_play));
            onBackPressed();
        }
    }

}
