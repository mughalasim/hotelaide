package com.hotelaide.main_pages.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.hotelaide.R;

public class AboutUsActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_us);

        String TAG_LOG = "ABOUT US";
        initialize(R.id.drawer_about_us, TAG_LOG);

//        helper.setTracker(TAG_LOG);

    }

}
