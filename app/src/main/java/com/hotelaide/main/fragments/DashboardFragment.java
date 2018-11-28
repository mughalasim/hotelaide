package com.hotelaide.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.hotelaide.R;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.makeramen.roundedimageview.RoundedImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_APPLIED;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_SAVED;
import static com.hotelaide.utils.StaticVariables.USER_IMG_AVATAR;

public class DashboardFragment extends Fragment {
    private View root_view;
    private Helpers helpers;
    private Database db;

    private TextView
            txt_welcome,
            txt_applied_jobs,
            txt_saved_jobs,
            txt_shortlisted,
            txt_interviews,
            txt_progress;

    private RelativeLayout
            rl_progress,
            rl_message;

    private RoundedImageView
            img_avatar;


    public DashboardFragment() {

    }

    // OVERRIDE METHODS ============================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            root_view = inflater.inflate(R.layout.frag_dashboard, container, false);

            helpers = new Helpers(getActivity());
            db = new Database();

            findAllViews();

        } else {
            container.removeView(root_view);
        }

        return root_view;
    }

    @Override
    public void onResume() {
        helpers.setWelcomeMessage(txt_welcome);
        updateDashboard();
        super.onResume();
    }

    // BASIC METHODS ===============================================================================
    private void findAllViews() {
        txt_welcome = root_view.findViewById(R.id.txt_welcome);
        txt_progress = root_view.findViewById(R.id.txt_progress);

        txt_applied_jobs = root_view.findViewById(R.id.txt_applied_jobs);
        txt_saved_jobs = root_view.findViewById(R.id.txt_saved_jobs);
        txt_shortlisted = root_view.findViewById(R.id.txt_shortlisted);
        txt_interviews = root_view.findViewById(R.id.txt_interviews);

        img_avatar = root_view.findViewById(R.id.img_avatar);
        rl_message = root_view.findViewById(R.id.rl_message);

        setTarget();
    }

    private void updateDashboard() {
        txt_applied_jobs.setText(db.getFilteredTableCount(FILTER_TYPE_APPLIED));
        txt_saved_jobs.setText(db.getFilteredTableCount(FILTER_TYPE_SAVED));

        Glide.with(this).load(SharedPrefs.getString(USER_IMG_AVATAR)).into(img_avatar);

    }

    private void setTarget() {
        if (getActivity() != null)
            new TapTargetSequence(getActivity())
                    .targets(
                            helpers.getTapTarget(root_view.findViewById(R.id.txt_applied_jobs), "Applied Jobs", "This will show you how many jobs you have applied for"),
                            helpers.getTapTarget(root_view.findViewById(R.id.txt_saved_jobs), "Saved Jobs", "This will show you how many jobs you have saved for later review"),
                            helpers.getTapTarget(root_view.findViewById(R.id.txt_shortlisted), "Shortlisted", "How many employers have shortlisted you, see that here"),
                            helpers.getTapTarget(root_view.findViewById(R.id.txt_interviews), "Interviews", "How many employers have invited you for an interview"),
                            helpers.getTapTarget(root_view.findViewById(R.id.rl_message), "Updates", "All your updates in one place, from Messages to notifications"),
                            helpers.getTapTarget(root_view.findViewById(R.id.rl_progress), "Profile Progress", "And lastly....Make sure you fill out your profile completely to increase your chances of getting employed")
                    ).start();
    }

}