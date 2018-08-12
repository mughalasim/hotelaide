package com.hotelaide.main_pages.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
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
import com.hotelaide.R;
import com.hotelaide.main_pages.models.CountyModel;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import static com.hotelaide.utils.SharedPrefs.USER_LAT;
import static com.hotelaide.utils.SharedPrefs.USER_LNG;


public class AddressFragment extends Fragment implements OnMapReadyCallback {

    private View rootview;
    private Helpers helpers;
    private Database db;
    private final String TAG_LOG = "ADDRESS";
    private GoogleMap mMap;

    private TextView
            txt_longitude,
            txt_latitude;

    private Spinner
            spinner_county;

    private EditText
            et_full_address,
            et_postcode;

    private FloatingActionButton
            btn_find_location,
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

                rootview = inflater.inflate(R.layout.fragment_address, container, false);

                helpers = new Helpers(getActivity());

                db = new Database();

                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

                findAllViews();

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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);

                txt_latitude.setText(String.valueOf(latLng.latitude));
                txt_longitude.setText(String.valueOf(latLng.longitude));
            }
        });
    }


    // BASIC METHODS ===============================================================================
    private void findAllViews() {
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        spinner_county = rootview.findViewById(R.id.spinner_county);
        if (getActivity() != null) {
            ArrayAdapter<CountyModel> dataAdapter1 = new ArrayAdapter<>(getActivity(), R.layout.list_item_spinner, db.getAllCounties());
            spinner_county.setAdapter(dataAdapter1);
        }

        et_postcode = rootview.findViewById(R.id.et_postcode);
        et_full_address = rootview.findViewById(R.id.et_full_address);
        txt_longitude = rootview.findViewById(R.id.txt_longitude);
        txt_latitude = rootview.findViewById(R.id.txt_latitude);
        btn_update = rootview.findViewById(R.id.btn_update);
        btn_find_location = rootview.findViewById(R.id.btn_find_location);

    }

    private void setListeners(){
        btn_find_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setFromSharedPrefs() {

//        spinner_county.setSelection(getIndex(spinner_county, SharedPrefs.getInt(USER_COUNTY)));
//        et_postcode.setText(SharedPrefs.getString(USER_POSTCODE));

        txt_latitude.setText(String.valueOf(SharedPrefs.getDouble(USER_LAT)));
        txt_longitude.setText(String.valueOf(SharedPrefs.getDouble(USER_LNG)));
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }
}