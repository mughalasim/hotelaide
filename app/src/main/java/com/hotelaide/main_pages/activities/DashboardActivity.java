package com.hotelaide.main_pages.activities;

import android.os.Bundle;

import com.hotelaide.R;

public class DashboardActivity extends ParentActivity {

    private final String TAG_LOG = "DASHBOARD";


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);

        initialize(R.id.drawer_dashboard, TAG_LOG);

        findAllViews();

//        helper.setTracker(TAG_LOG);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    // BASIC FUNCTIONS =============================================================================

    private void findAllViews() {


    }

}
