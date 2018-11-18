package com.hotelaide.main.activities;

import android.content.Intent;
import android.os.Bundle;

import com.hotelaide.R;
import com.hotelaide.main.fragments.NewsFeedFragment;
import com.hotelaide.services.BackgroundFetchService;
import com.hotelaide.utils.SharedPrefs;

import androidx.fragment.app.Fragment;

import static com.hotelaide.utils.StaticVariables.ALLOW_MESSAGE_PUSH;
import static com.hotelaide.utils.StaticVariables.ALLOW_UPDATE_APP;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_BASIC;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_FIRST_TIME;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_RETURN;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;

public class DashboardActivity extends ParentActivity {

    private final String[] fragment_extras = {
            "https://www.hotelmanagement.net/rss/xml",
            "https://www.hotelmanagement.net/rss/tech/xml",
            "https://www.hotelmanagement.net/rss/design/xml",
            "https://www.hotelmanagement.net/rss/operate/xml"
    };

    private int[] fragment_title_list = {
            R.string.nav_news_feed_latest,
            R.string.nav_news_feed_tech,
            R.string.nav_news_feed_design,
            R.string.nav_news_feed_operations
    };

    private Fragment[] fragment_list = {
            new NewsFeedFragment(),
            new NewsFeedFragment(),
            new NewsFeedFragment(),
            new NewsFeedFragment()
    };


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        initialize(R.id.drawer_dashboard, getString(R.string.drawer_dashboard));

        handleExtraBundles();

        setupViewPager(fragment_list, fragment_title_list, fragment_extras);

        if (helpers.validateServiceRunning(BackgroundFetchService.class)) {
            startService(new Intent(DashboardActivity.this, BackgroundFetchService.class));
        }

    }

    // BASIC FUNCTIONS =============================================================================
    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString(EXTRA_START_FIRST_TIME) != null) {
            if (SharedPrefs.getString(USER_F_NAME).equals("")) {
                startActivity(new Intent(DashboardActivity.this, ProfileEditActivity.class)
                        .putExtra(EXTRA_PROFILE_BASIC, EXTRA_PROFILE_BASIC));

            } else {
                helpers.myDialog(DashboardActivity.this,
                        "WELCOME", SharedPrefs.getString(USER_F_NAME) + ", thank you for joining "
                                + getString(R.string.app_name) +
                                ", You are on the Dashboard where you can easily navigate through the app.");
            }

            SharedPrefs.setBool(ALLOW_UPDATE_APP, true);
            SharedPrefs.setBool(ALLOW_MESSAGE_PUSH, true);
            setCountOnDrawerItem(menu_profile, "(Update Profile)");

        } else if (extras != null && extras.getString(EXTRA_START_RETURN) != null) {
            helpers.ToastMessage(DashboardActivity.this, "Welcome back " + SharedPrefs.getString(USER_F_NAME));
            setCountOnDrawerItem(menu_find_jobs, "(New)");

            SharedPrefs.setBool(ALLOW_UPDATE_APP, true);
            SharedPrefs.setBool(ALLOW_MESSAGE_PUSH, true);

        }
    }

}
