package com.hotelaide.main_pages.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hotelaide.R;
import com.hotelaide.utils.Helpers;


public class AddressFragment extends Fragment implements OnMapReadyCallback {

    private View rootview;
    private Helpers helpers;
    private final String TAG_LOG = "ADDRESS";
    private GoogleMap mMap;


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
                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

                SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);



                PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                        getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
                    }

                    @Override
                    public void onError(Status status) {

                    }
                });

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

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}