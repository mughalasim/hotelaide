package com.hotelaide.main.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.main.models.SearchFilterModel;
import com.hotelaide.services.UserService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.Database.COUNTY_TABLE_NAME;
import static com.hotelaide.utils.Helpers.INT_PERMISSIONS_LOCATIONS;
import static com.hotelaide.utils.SharedPrefs.USER_COUNTY;
import static com.hotelaide.utils.SharedPrefs.USER_FULL_ADDRESS;
import static com.hotelaide.utils.SharedPrefs.USER_ID;
import static com.hotelaide.utils.SharedPrefs.USER_LAT;
import static com.hotelaide.utils.SharedPrefs.USER_LNG;
import static com.hotelaide.utils.SharedPrefs.USER_POSTAL_CODE;


public class AddressFragment extends Fragment implements OnMapReadyCallback {

    private View rootview;
    private Helpers helpers;
    private Database db;
    private final String TAG_LOG = "ADDRESS";
    private GoogleMap mMap;

    private TextView
            txt_longitude,
            txt_latitude;

    private MaterialButton btn_confirm;

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
        if (rootview == null && getActivity() != null) {
            try {

                rootview = inflater.inflate(R.layout.frag_profile_address, container, false);

                helpers = new Helpers(getActivity());

                db = new Database();


                findAllViews();

                setListeners();

                setFromSharedPrefs();


            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(rootview);
        }
        return rootview;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null && helpers.validateEmptyTextView(txt_latitude, "") && helpers.validateEmptyTextView(txt_longitude, "")) {
            LatLng latLng = new LatLng(
                    Double.parseDouble(txt_latitude.getText().toString()),
                    Double.parseDouble(txt_longitude.getText().toString())
            );
            updateMapAndCamera(latLng);
        }

        if (getActivity() != null && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            btn_confirm.setVisibility(View.VISIBLE);

            mMap.setMyLocationEnabled(true);

            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    Helpers.LogThis(TAG_LOG, "MAP BUTTON CLICKED");
                    Location location = mMap.getMyLocation();
                    updateMapAndCamera(new LatLng(location.getLatitude(), location.getLongitude()));
                    return false;
                }
            });
        }
    }

    private void updateMapAndCamera(LatLng latLng) {
        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }


    // BASIC METHODS ===============================================================================
    private void findAllViews() {
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        spinner_county = rootview.findViewById(R.id.spinner_location);
        if (getActivity() != null) {
            ArrayAdapter<SearchFilterModel> dataAdapter1 = new ArrayAdapter<>(getActivity(), R.layout.list_item_spinner, db.getAllFilterItems(COUNTY_TABLE_NAME));
            spinner_county.setAdapter(dataAdapter1);
        }

        et_postcode = rootview.findViewById(R.id.et_postcode);
        et_full_address = rootview.findViewById(R.id.et_full_address);
        txt_longitude = rootview.findViewById(R.id.txt_longitude);
        txt_latitude = rootview.findViewById(R.id.txt_latitude);
        btn_update = rootview.findViewById(R.id.btn_update);
        btn_confirm = rootview.findViewById(R.id.btn_confirm);
        btn_confirm.setText("Find me");
        btn_confirm.setVisibility(View.GONE);
    }

    private void setListeners() {
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] perms = {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION};
                if (getActivity() != null && EasyPermissions.hasPermissions(getActivity(), perms)) {
                    try {
                        LocationManager locationManager = null;
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            helpers.dialogNoGPS(getActivity());
                        } else {

                        }
                    } catch (NullPointerException e) {
                        helpers.dialogNoGPS(getActivity());
                    }

                } else if (getActivity() != null) {
                    EasyPermissions.requestPermissions(getActivity(), getString(R.string.rationale_locations),
                            INT_PERMISSIONS_LOCATIONS, perms);
                }
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpers.validateEmptyEditText(et_full_address) && helpers.validateEmptyEditText(et_postcode)) {
                    asyncUpdateAddress();
                }
            }
        });
    }

    private void setFromSharedPrefs() {

        spinner_county.setSelection(getIndex(spinner_county, db.getFilterNameByID(COUNTY_TABLE_NAME, SharedPrefs.getInt(USER_COUNTY))));

        et_postcode.setText(SharedPrefs.getString(USER_POSTAL_CODE));

        et_full_address.setText(SharedPrefs.getString(USER_FULL_ADDRESS));

        txt_latitude.setText(String.valueOf(SharedPrefs.getDouble(USER_LAT)));

        txt_longitude.setText(String.valueOf(SharedPrefs.getDouble(USER_LNG)));

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
        Helpers.LogThis(TAG_LOG,
                "COUNTY NAME: " + db.getFilterNameByID(COUNTY_TABLE_NAME, SharedPrefs.getInt(USER_COUNTY)) +
                        " POSTAL CODE: " + SharedPrefs.getString(USER_POSTAL_CODE) +
                        " LNG: " + SharedPrefs.getDouble(USER_LNG) +
                        " LAT: " + SharedPrefs.getDouble(USER_LAT) +
                        " FULL ADDRESS: " + SharedPrefs.getString(USER_FULL_ADDRESS)
        );
    }


    // ASYNC UPDATE ADDRESS ========================================================================

    private void asyncUpdateAddress() {
        UserService userService = UserService.retrofit.create(UserService.class);

        final int county_id = db.getFilterIDByString(COUNTY_TABLE_NAME, spinner_county.getSelectedItem().toString());

        Call<JsonObject> call = userService.setUserAddress(
                SharedPrefs.getInt(USER_ID),
                county_id,
                et_postcode.getText().toString(),
                Double.parseDouble(txt_latitude.getText().toString()),
                Double.parseDouble(txt_longitude.getText().toString()),
                et_full_address.getText().toString()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    Helpers.LogThis(TAG_LOG, main.toString());
                    if (main.getBoolean("success")) {
                        helpers.ToastMessage(getActivity(), main.getString("message"));
                        SharedPrefs.setInt(USER_COUNTY, county_id);
                        SharedPrefs.setString(USER_POSTAL_CODE, et_postcode.getText().toString());
                        SharedPrefs.setDouble(USER_LAT, Double.parseDouble(txt_latitude.getText().toString()));
                        SharedPrefs.setDouble(USER_LNG, Double.parseDouble(txt_longitude.getText().toString()));
                        SharedPrefs.setString(USER_FULL_ADDRESS, et_full_address.getText().toString());
                        logAddress();
                    }
                } catch (JSONException e) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Helpers.LogThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                } else {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                }

            }
        });
    }


}