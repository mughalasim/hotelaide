package com.hotelaide.main_pages.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelaide.R;
import com.hotelaide.main_pages.activities.ProfileActivity;
import com.hotelaide.main_pages.models.WorkExperienceModel;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;


public class WorkExperienceFragment extends Fragment {

    private View rootview;

    private Helpers helpers;

    private Database db;

    private final String TAG_LOG = "WORK EXPERIENCE";

    private LinearLayout ll_work_experience;

    private TextView btn_add_work_experience;


    public WorkExperienceFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.fragment_work_experience, container, false);
                helpers = new Helpers(getActivity());
                db = new Database();

                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

                findAllViews();

                populateWorkExperience();

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(rootview);
        }
        return rootview;
    }

    private void findAllViews() {

        ll_work_experience = rootview.findViewById(R.id.ll_work_experience);

        btn_add_work_experience = rootview.findViewById(R.id.btn_add_work_experience);
    }


    private void populateWorkExperience() {
        ArrayList<WorkExperienceModel> workExperienceModelArrayList = db.getAllWorkExperience();
        LayoutInflater linf;
        linf = LayoutInflater.from(getActivity());

        int array_size = workExperienceModelArrayList.size();

        for (int v = 0; v < array_size; v++) {
            View child = linf.inflate(R.layout.list_item_work_experience, null);

            final TextView txt_company_name = child.findViewById(R.id.txt_company_name);
            final TextView txt_position = child.findViewById(R.id.txt_position);
            final TextView txt_start_date = child.findViewById(R.id.txt_start_date);
            final TextView txt_end_date = child.findViewById(R.id.txt_end_date);
            final TextView txt_current = child.findViewById(R.id.txt_current);
            final TextView txt_responsibilities = child.findViewById(R.id.txt_responsibilities);
            final TextView txt_responsibilities_show = child.findViewById(R.id.txt_responsibilities_show);

            WorkExperienceModel workExperienceModel = workExperienceModelArrayList.get(v);

            txt_company_name.setText(workExperienceModel.company_name);
            txt_position.setText(workExperienceModel.position);
            txt_start_date.setText(workExperienceModel.start_date);

            if (workExperienceModel.current) {
                txt_current.setVisibility(View.VISIBLE);
                txt_end_date.setVisibility(View.GONE);
            } else {
                txt_current.setVisibility(View.GONE);
                txt_end_date.setVisibility(View.VISIBLE);
                txt_end_date.setText(workExperienceModel.end_date);
            }

            txt_responsibilities.setText(workExperienceModel.responsibilities);
            if (workExperienceModel.responsibilities.length() > 50) {
                txt_responsibilities_show.setVisibility(View.VISIBLE);
            } else {
                txt_responsibilities_show.setVisibility(View.GONE);
            }

            txt_responsibilities_show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(txt_responsibilities_show.getText().toString().equals(getResources().getString(R.string.txt_more))){
                        txt_responsibilities.setMaxLines(Integer.MAX_VALUE);
                        txt_responsibilities_show.setText(getResources().getString(R.string.txt_less));
                    }else{
                        txt_responsibilities.setMaxLines(3);
                        txt_responsibilities_show.setText(getResources().getString(R.string.txt_more));
                    }
                }
            });

            ll_work_experience.addView(child);

        }

    }

}