package com.hotelaide.main.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotelaide.R;
import com.hotelaide.utils.Helpers;


public class DocumentsFragment extends Fragment {

    private View rootview;

    private Helpers helpers;

    private final String TAG_LOG = "DOCUMENTS";

    public DocumentsFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.frag_profile_documents, container, false);

                helpers = new Helpers(getActivity());





            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(rootview);
        }
        return rootview;
    }

}