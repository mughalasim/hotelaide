package com.hotelaide.main.fragments;

import android.Manifest;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hotelaide.R;
import com.hotelaide.utils.Helpers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class ContactUsFragment extends Fragment {

    private View rootview;
    private Helpers helpers;
    private TextView txt_mobile, txt_land_line;

    public ContactUsFragment() {

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
                        helpers.dialogMakeCall(txt_mobile.getText().toString());
                    }
                });

                txt_land_line.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helpers.dialogMakeCall(txt_land_line.getText().toString());
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

}