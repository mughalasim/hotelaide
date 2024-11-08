package com.hotelaide.main.fragments;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.UserInterface;
import com.hotelaide.main.activities.MapActivity;
import com.hotelaide.main.activities.ProfileEditActivity;
import com.hotelaide.main.models.SearchFilterModel;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.MyApplication;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;
import static com.hotelaide.main.activities.MapActivity.MAP_ACTIVITY_LATITUDE;
import static com.hotelaide.main.activities.MapActivity.MAP_ACTIVITY_LONGITUDE;
import static com.hotelaide.utils.StaticVariables.COUNTY_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.FLOAT_GOOGLE_MAP_ZOOM;
import static com.hotelaide.utils.StaticVariables.INT_PERMISSIONS_LOCATIONS;
import static com.hotelaide.utils.StaticVariables.USER_COUNTY;
import static com.hotelaide.utils.StaticVariables.USER_FULL_ADDRESS;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_ID;
import static com.hotelaide.utils.StaticVariables.USER_LAT;
import static com.hotelaide.utils.StaticVariables.USER_LNG;
import static com.hotelaide.utils.StaticVariables.USER_POSTAL_CODE;
import static com.hotelaide.utils.StaticVariables.db;


public class AddressFragment extends Fragment implements OnMapReadyCallback {

    private Helpers helpers;

    private View root_view;
    private final String TAG_LOG = "ADDRESS";

    private GoogleMap google_map;
    MapView map_view;

    private Spinner
            spinner_county;

    private EditText
            et_full_address,
            et_postcode;

    private FloatingActionButton
            btn_update;

    public AddressFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {

                root_view = inflater.inflate(R.layout.frag_profile_address, container, false);

                helpers = new Helpers(getActivity());

                initializeMap(savedInstanceState);

                findAllViews();

                setListeners();

                setFromSharedPrefs();


            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            container.removeView(root_view);
        }
        return root_view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, getActivity());
        helpers.myPermissionsDialog(grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        map_view.onResume();
        if (MAP_ACTIVITY_LATITUDE != 0.0) {
            Helpers.logThis(TAG_LOG, "MAP LAT: " + MAP_ACTIVITY_LATITUDE);
            Helpers.logThis(TAG_LOG, "MAP LNG: " + MAP_ACTIVITY_LONGITUDE);
            Helpers.logThis(TAG_LOG, "ON RESUME, CHECK THE LOCATION");
            SharedPrefs.logUserModel();
            onMapReady(google_map);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        map_view.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map_view.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map_view.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {

            google_map = googleMap;

            if (MAP_ACTIVITY_LATITUDE != 0.0) {
                updateMapAndCamera(MAP_ACTIVITY_LATITUDE, MAP_ACTIVITY_LONGITUDE);
                asyncUpdateAddress(MAP_ACTIVITY_LATITUDE, MAP_ACTIVITY_LONGITUDE);
                MAP_ACTIVITY_LATITUDE = 0.0;
                MAP_ACTIVITY_LONGITUDE = 0.0;
            } else {
                updateMapAndCamera(SharedPrefs.getDouble(USER_LAT), SharedPrefs.getDouble(USER_LNG));
            }

            google_map.getUiSettings().setAllGesturesEnabled(false);

            google_map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    String[] perms = {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION};
                    if (getActivity() != null && EasyPermissions.hasPermissions(getActivity(), perms)) {
                        startMapActivity();
                    } else if (getActivity() != null) {
                        EasyPermissions.requestPermissions(getActivity(), getString(R.string.rationale_locations),
                                INT_PERMISSIONS_LOCATIONS, perms);
                    }
                }

                @AfterPermissionGranted(INT_PERMISSIONS_LOCATIONS)
                private void startMapActivity() {
                    startSetLocationActivity();
                }
            });
        }
    }

    private void startSetLocationActivity() {
        if (getActivity() != null) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                startActivity(new Intent(getActivity(), MapActivity.class));
            } else {
                helpers.dialogNoGPS();
            }
        }
    }


    private void updateMapAndCamera(double latitude, double longitude) {
        google_map.clear();
        LatLng location = new LatLng(latitude, longitude);
        google_map.moveCamera(CameraUpdateFactory.newLatLng(location));
        google_map.animateCamera(CameraUpdateFactory.zoomTo(FLOAT_GOOGLE_MAP_ZOOM));

        Marker my_marker = google_map.addMarker(new MarkerOptions().position(
                new LatLng(latitude, longitude))
                .title(SharedPrefs.getString(USER_F_NAME)));
        my_marker.setVisible(true);
        my_marker.showInfoWindow();
        my_marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            if (address != null)
                et_full_address.setText(address);
            if (postalCode != null)
                et_postcode.setText(postalCode + ", " + city + ", " + country);

        } catch (IOException e) {
            Helpers.logThis(TAG_LOG, e.toString());
        } catch (Exception e) {
            Helpers.logThis(TAG_LOG, e.toString());
        }
    }


    // BASIC METHODS ===============================================================================
    private void findAllViews() {
        spinner_county = root_view.findViewById(R.id.spinner_county);
        if (getActivity() != null) {
            ArrayAdapter<SearchFilterModel> data_adapter = new ArrayAdapter<>(
                    getActivity(),
                    R.layout.list_item_spinner,
                    db.getAllFilterItems(COUNTY_TABLE_NAME));
            spinner_county.setAdapter(data_adapter);
        }

        et_postcode = root_view.findViewById(R.id.et_postcode);
        et_full_address = root_view.findViewById(R.id.et_full_address);
        btn_update = root_view.findViewById(R.id.btn_update);

    }

    private void initializeMap(Bundle savedInstanceState) {
        map_view = root_view.findViewById(R.id.map_view);

        map_view.onCreate(savedInstanceState);

        map_view.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(MyApplication.getAppContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        map_view.getMapAsync(this);

    }

    private void setListeners() {
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpers.validateEmptyEditText(et_full_address) && helpers.validateEmptyEditText(et_postcode)) {
                    if (MAP_ACTIVITY_LATITUDE != 0.0) {
                        asyncUpdateAddress(MAP_ACTIVITY_LATITUDE, MAP_ACTIVITY_LONGITUDE);
                    } else {
                        asyncUpdateAddress(SharedPrefs.getDouble(USER_LAT), SharedPrefs.getDouble(USER_LNG));
                    }
                }
            }
        });
    }

    private void setFromSharedPrefs() {

        spinner_county.setSelection(getIndex(spinner_county, db.getFilterNameByID(COUNTY_TABLE_NAME, SharedPrefs.getInt(USER_COUNTY))));

        et_postcode.setText(SharedPrefs.getString(USER_POSTAL_CODE));

        et_full_address.setText(SharedPrefs.getString(USER_FULL_ADDRESS));

        logAddress();
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    private void logAddress() {
        Helpers.logThis(TAG_LOG,
                "COUNTY NAME: " + db.getFilterNameByID(COUNTY_TABLE_NAME, SharedPrefs.getInt(USER_COUNTY)) +
                        " POSTAL CODE: " + SharedPrefs.getString(USER_POSTAL_CODE) +
                        " LNG: " + SharedPrefs.getDouble(USER_LNG) +
                        " LAT: " + SharedPrefs.getDouble(USER_LAT) +
                        " FULL ADDRESS: " + SharedPrefs.getString(USER_FULL_ADDRESS)
        );
    }


    // ASYNC UPDATE ADDRESS ========================================================================
    private void asyncUpdateAddress(final Double latitude, final Double longitude) {
        final int county_id = db.getFilterIDByString(COUNTY_TABLE_NAME, spinner_county.getSelectedItem().toString());

        UserInterface.retrofit.create(UserInterface.class)
                .setUserAddress(
                        SharedPrefs.getInt(USER_ID),
                        county_id,
                        et_postcode.getText().toString(),
                        latitude,
                        longitude,
                        et_full_address.getText().toString()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (getActivity() != null) {
                    try {
                        JSONObject main = new JSONObject(String.valueOf(response.body()));
                        Helpers.logThis(TAG_LOG, main.toString());
                        if (main.getBoolean("success")) {
                            helpers.toastMessage(main.getString("message"));
                            SharedPrefs.setInt(USER_COUNTY, county_id);
                            SharedPrefs.setString(USER_POSTAL_CODE, et_postcode.getText().toString());
                            SharedPrefs.setDouble(USER_LAT, latitude);
                            SharedPrefs.setDouble(USER_LNG, longitude);
                            SharedPrefs.setString(USER_FULL_ADDRESS, et_full_address.getText().toString());
                            logAddress();

                        }
                    } catch (JSONException e) {
                        helpers.toastMessage(getString(R.string.error_server));
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (getActivity() != null) {
                    Helpers.logThis(TAG_LOG, t.toString());
                    if (helpers.validateInternetConnection()) {
                        helpers.toastMessage(getString(R.string.error_server));
                    } else {
                        helpers.toastMessage(getString(R.string.error_connection));
                    }
                }
            }
        });
    }


}