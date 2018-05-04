package com.hotelaide.main_pages.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hotelaide.R;
import com.hotelaide.utils.Helpers;


public class StartUpSignUpFragment extends Fragment {

    private View rootview;

    private TextView
            btn_confirm,
            btn_cancel;

    private Helpers helpers;

    private final String TAG_LOG = "FRAGMENT LOGIN";

    public StartUpSignUpFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.fragment_startup_signup, container, false);
                helpers = new Helpers(getActivity());
                btn_cancel = rootview.findViewById(R.id.btn_cancel);
                btn_confirm = rootview.findViewById(R.id.btn_confirm);

                btn_cancel.setVisibility(View.GONE);
                btn_confirm.setVisibility(View.VISIBLE);

                btn_confirm.setText(getString(R.string.nav_sign_up));

                setListerners();

                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(rootview);
        }
        return rootview;
    }

    private void setListerners() {
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}