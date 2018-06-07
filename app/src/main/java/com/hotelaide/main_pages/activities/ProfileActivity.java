package com.hotelaide.main_pages.activities;

import android.os.Bundle;

import com.hotelaide.R;

public class ProfileActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile);

        String TAG_LOG = "MY PROFILE";
        initialize(R.id.drawer_my_profile, TAG_LOG);

//        helper.setTracker(TAG_LOG);

    }

}
