package com.hotelaide.main.activities;

import android.os.Bundle;

import com.hotelaide.R;
import com.hotelaide.main.fragments.FilteredJobsFragment;

import androidx.fragment.app.Fragment;

import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_APPLIED;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_INTERVIEWS;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_SAVED;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_SHORTLISTED;

public class MyJobsActivity extends ParentActivity {

    private int[] fragment_title_list = {
            R.string.nav_applied,
//            R.string.nav_saved,
            R.string.nav_shortlisted,
            R.string.nav_interviews
    };

    private final String[] fragment_extras = {
            FILTER_TYPE_APPLIED,
//            FILTER_TYPE_SAVED,
            FILTER_TYPE_SHORTLISTED,
            FILTER_TYPE_INTERVIEWS,
            ""
    };

    //TODO - make sure all the fragments are added here correctly
    //-Applied
    //-Saved
    //-Shortlisted
    //-Invites
    //-Interviews
    private Fragment[] fragment_list = {
            new FilteredJobsFragment(),
//            new FilteredJobsFragment(),
            new FilteredJobsFragment(),
            new FilteredJobsFragment(),
//            new FilteredJobsFragment()
    };

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_pager);

        initialize(R.id.drawer_my_jobs, getString(R.string.drawer_my_jobs));

        setupViewPager(fragment_list, fragment_title_list, fragment_extras);

        handleExtraBundles();

        setUpHomeSearch();

    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString(FILTER_TYPE_APPLIED) != null) {
           view_pager.setCurrentItem(0);

        } else if (extras != null && extras.getString(FILTER_TYPE_SAVED) != null) {
            view_pager.setCurrentItem(1);

        }
    }

}
