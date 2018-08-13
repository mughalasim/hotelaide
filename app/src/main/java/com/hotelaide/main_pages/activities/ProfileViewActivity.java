package com.hotelaide.main_pages.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hotelaide.R;

public class ProfileViewActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile_view);

        String TAG_LOG = "PROFILE VIEW";
        initialize(R.id.drawer_my_profile, TAG_LOG);


    }

    public void profileEdit(View view) {
        startActivity(new Intent(ProfileViewActivity.this, ProfileActivity.class));
    }

}
