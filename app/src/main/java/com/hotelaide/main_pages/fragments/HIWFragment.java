package com.hotelaide.main_pages.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotelaide.R;
import com.hotelaide.utils.Helpers;

//import static com.hotelaide.main_pages.eo_activities.HowItWorksActivity.BTN_TXT;
//import static com.hotelaide.main_pages.eo_activities.HowItWorksActivity.DESC;
//import static com.hotelaide.main_pages.eo_activities.HowItWorksActivity.IMAGE;

public class HIWFragment extends Fragment {

    private View rootview;

    private int imageID = 0;

    private String
            desc = "",
            btn_txt = "";

    private final String TAG_LOG =
            "HIW FRAGMENT";

    public HIWFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            imageID = getArguments().getInt(IMAGE);
//            desc = getArguments().getString(DESC);
//            btn_txt = getArguments().getString(BTN_TXT);

            Helpers.LogThis(TAG_LOG, getArguments().toString());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.fragment_hiw, container, false);
                final Helpers helpers = new Helpers(getActivity());

                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

                TextView button1 = rootview.findViewById(R.id.button);
                TextView description = rootview.findViewById(R.id.description);
                final ImageView image = rootview.findViewById(R.id.image);

                button1.setText(btn_txt);
                button1.setTag(btn_txt);
                description.setText(desc);
                image.setImageResource(imageID);

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(rootview);
        }
        return rootview;
    }

}