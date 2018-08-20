package com.hotelaide.main_pages.activities;

import android.os.Bundle;

import com.hotelaide.R;
import com.hotelaide.utils.SharedPrefs;

import static com.hotelaide.utils.Helpers.START_FIRST_TIME;
import static com.hotelaide.utils.Helpers.START_RETURN;

public class DashboardActivity extends ParentActivity {

    private final String TAG_LOG = "DASHBOARD";


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        initialize(R.id.drawer_dashboard, TAG_LOG);

        handleExtraBundles();

        helpers.asyncGetUser();

        helpers.asyncGetCounties();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    // BASIC FUNCTIONS =============================================================================
    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString(START_FIRST_TIME) != null) {

            helpers.myDialog(DashboardActivity.this,
                    "WELCOME", SharedPrefs.getString(SharedPrefs.USER_F_NAME) + ", thank you for joining "
                    + getString(R.string.app_name) +
                    ", You are on the Dashboard where you can easily navigate through the app.");

        } else if (extras != null && extras.getString(START_RETURN) != null) {
            helpers.ToastMessage(DashboardActivity.this, "Welcome back " + SharedPrefs.getString(SharedPrefs.USER_F_NAME));

        }
    }

}
