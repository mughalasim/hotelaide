package com.hotelaide.main.activities;

import android.os.Bundle;

import com.hotelaide.R;
import com.hotelaide.main.fragments.AppliedJobsFragment;

import androidx.fragment.app.Fragment;

public class MyJobsActivity extends ParentActivity {

    private int[] fragment_title_list = {
            R.string.nav_applied
//            R.string.nav_saved,
//            R.string.nav_shortlisted,
//            R.string.nav_invites,
//            R.string.nav_interviews
    };

    private final String[] fragment_extras = {
            "",
            "",
            "",
            "",
            ""
    };

    //TODO - make sure all the fragments are added here correctly
    //-Applied
    //-Saved
    //-Shortlisted
    //-Invites
    //-Interviews
    private Fragment[] fragment_list = {
            new AppliedJobsFragment()
//            new AppliedJobsFragment(),
//            new AppliedJobsFragment(),
//            new AppliedJobsFragment(),
//            new AppliedJobsFragment()
    };

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_pager);

        initialize(R.id.drawer_my_jobs, getString(R.string.drawer_my_jobs));

        setupViewPager(fragment_list, fragment_title_list, fragment_extras);


    }

}
