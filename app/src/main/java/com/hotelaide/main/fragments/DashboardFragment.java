package com.hotelaide.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotelaide.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class DashboardFragment extends Fragment {
    private View root_view;

    public DashboardFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            root_view = inflater.inflate(R.layout.frag_dashboard, container, false);


        } else {
            container.removeView(root_view);
        }

        return root_view;
    }

}