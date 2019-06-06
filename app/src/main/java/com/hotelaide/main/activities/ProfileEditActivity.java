package com.hotelaide.main.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.hotelaide.R;
import com.hotelaide.main.fragments.AddressFragment;
import com.hotelaide.main.fragments.ChangePasswordFragment;
import com.hotelaide.main.fragments.DocumentsFragment;
import com.hotelaide.main.fragments.ExperienceEditFragment;
import com.hotelaide.main.fragments.ProfileUpdateFragment;

import static com.hotelaide.main.activities.MapActivity.MAP_ACTIVITY_LATITUDE;
import static com.hotelaide.main.activities.MapActivity.MAP_ACTIVITY_LONGITUDE;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_ADDRESS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_BASIC;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_DOCUMENTS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_PASS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_WORK;

public class ProfileEditActivity extends FragmentActivity {
    private TextView toolbar_text;

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_edit);

        setUpToolBarAndTabs();

        handleExtraBundles();

    }

    @Override
    protected void onDestroy() {
        MAP_ACTIVITY_LATITUDE = 0.0;
        MAP_ACTIVITY_LONGITUDE = 0.0;
        super.onDestroy();
    }

    // BASIC FUNCTIONS =============================================================================
    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString(EXTRA_PROFILE_BASIC) != null) {
            inflateFragment(new ProfileUpdateFragment(), "");
            toolbar_text.setText(getString(R.string.nav_profile));

        } else if (extras != null && extras.getString(EXTRA_PROFILE_ADDRESS) != null) {
            inflateFragment(new AddressFragment(), "");
            toolbar_text.setText(getString(R.string.nav_address));

        } else if (extras != null && extras.getString(EXTRA_PROFILE_EDUCATION) != null) {
            inflateFragment(new ExperienceEditFragment(), EXPERIENCE_TYPE_EDUCATION);
            toolbar_text.setText(getString(R.string.nav_education));

        } else if (extras != null && extras.getString(EXTRA_PROFILE_WORK) != null) {
            inflateFragment(new ExperienceEditFragment(), EXPERIENCE_TYPE_WORK);
            toolbar_text.setText(getString(R.string.nav_work));

        } else if (extras != null && extras.getString(EXTRA_PROFILE_DOCUMENTS) != null) {
            inflateFragment(new DocumentsFragment(true, null), "");
            toolbar_text.setText(getString(R.string.nav_documents));

        } else if (extras != null && extras.getString(EXTRA_PROFILE_PASS) != null) {
            inflateFragment(new ChangePasswordFragment(), "");
            toolbar_text.setText(getString(R.string.nav_pass));
        }
    }

    private void setUpToolBarAndTabs() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar_text = findViewById(R.id.toolbar_text);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(ProfileEditActivity.this, R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void inflateFragment(Fragment fragment, String experience_type) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("EXPERIENCE_TYPE", experience_type);
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.ll_fragment, fragment)
                .commit();
    }

}
