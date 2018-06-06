package com.hotelaide.main_pages.activities;

import android.os.Bundle;

import com.hotelaide.R;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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


        // TODO - image uploading
//        File file = new File();// initialize file here;
//
//        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));


    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    // BASIC FUNCTIONS =============================================================================

    private void findAllViews() {


    }

}
