package com.hotelaide.startup.fragments;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hotelaide.R;
import com.hotelaide.utils.Helpers;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.hotelaide.utils.StaticVariables.INT_PERMISSIONS_CALL;


public class StartUpContactUsFragment extends Fragment {

    private View rootview;
    private Helpers helpers;
    private TextView txt_mobile, txt_land_line;
    private String STR_PHONE_NUMBER = "";

    public StartUpContactUsFragment() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        helpers.myPermissionsDialog(getActivity(), grantResults);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.frag_startup_contact_us, container, false);
                helpers = new Helpers(getActivity());
                txt_land_line = rootview.findViewById(R.id.txt_land_line);
                txt_mobile = rootview.findViewById(R.id.txt_mobile);

                txt_mobile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        STR_PHONE_NUMBER = txt_mobile.getText().toString();
                        makeCall();
                    }
                });

                txt_land_line.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        STR_PHONE_NUMBER = txt_land_line.getText().toString();
                        makeCall();
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

    @AfterPermissionGranted(INT_PERMISSIONS_CALL)
    public void makeCall() {
        if (getActivity() != null) {
            final String[] perms = {Manifest.permission.CALL_PHONE};
            if (EasyPermissions.hasPermissions(getActivity(), perms)) {
                helpers.dialogMakeCall(getActivity(), STR_PHONE_NUMBER);
            } else {
                EasyPermissions.requestPermissions(getActivity(), getString(R.string.rationale_call),
                        INT_PERMISSIONS_CALL, perms);
            }
        }
    }

}