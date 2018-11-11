package com.hotelaide.startup.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotelaide.R;
import com.hotelaide.utils.Helpers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import static com.hotelaide.startup.IntroActivity.EXTRA_DESC;
import static com.hotelaide.startup.IntroActivity.EXTRA_IMAGE;

public class StartUpIntroFragment extends Fragment {

    private View root_view;

    private int imageID = 0;

    private String
            desc = "";

    public StartUpIntroFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageID = getArguments().getInt(EXTRA_IMAGE);
            desc = getArguments().getString(EXTRA_DESC);

            String TAG_LOG = "INTRO FRAGMENT";
            Helpers.LogThis(TAG_LOG, getArguments().toString());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_startup_intro, container, false);


                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

                TextView description = root_view.findViewById(R.id.txt_desc);
                final ImageView image = root_view.findViewById(R.id.frag_image);

                description.setText(desc);
                image.setImageResource(imageID);


            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }

}