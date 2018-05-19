package com.hotelaide.main_pages.activities;

import android.os.Bundle;

import com.hotelaide.R;
//import com.hotelaide.main_pages.fragments.HomePageFragment;
//import com.hotelaide.main_pages.models.CollectionModel;
import com.hotelaide.utils.SharedPrefs;

import static com.hotelaide.utils.Helpers.STR_NAVIGATION_COLLECTION;
import static com.hotelaide.utils.SharedPrefs.IS_PUSH_CLICKED;
import static com.hotelaide.utils.SharedPrefs.NAV_DATA;

public class DashboardActivity extends ParentActivity {

    private final String TAG_LOG = "HOME";


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);

        initialize(R.id.drawer_dashboard, "DASHBOARD");

        findAllViews();

        helper.setTracker(TAG_LOG);

    }

    @Override
    protected void onStart() {
        if (!SharedPrefs.getString(NAV_DATA).equals("") && SharedPrefs.getBool(IS_PUSH_CLICKED)) {
            navigateToPage();
            SharedPrefs.setBool(IS_PUSH_CLICKED, false);
        }
        super.onStart();
    }


    // BASIC FUNCTIONS =============================================================================
    private void navigateToPage() {
        String Splits[] = SharedPrefs.getString(NAV_DATA).split("~");
        switch (Splits[0]) {
            case STR_NAVIGATION_COLLECTION:
                break;

            default:
                helper.ToastMessage(DashboardActivity.this, getString(R.string.error_unknown));
                break;

        }
        SharedPrefs.setString(NAV_DATA, "");
    }

    private void findAllViews() {


    }

}
