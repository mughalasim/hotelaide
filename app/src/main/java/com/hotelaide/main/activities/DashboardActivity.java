package com.hotelaide.main.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.hotelaide.R;
import com.hotelaide.main.fragments.AppliedJobsFragment;
import com.hotelaide.main.fragments.MessageFragment;
import com.hotelaide.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

import static com.hotelaide.utils.Helpers.START_FIRST_TIME;
import static com.hotelaide.utils.Helpers.START_RETURN;
import static com.hotelaide.utils.SharedPrefs.ALLOW_MESSAGE_PUSH;
import static com.hotelaide.utils.SharedPrefs.ALLOW_UPDATE_APP;

public class DashboardActivity extends ParentActivity {

    private final String TAG_LOG = "DASHBOARD";
    private int[] dashboardTitleList = {
            R.string.nav_applied,
            R.string.nav_messages
    };
    private Fragment[] dashboardFragments = {
            new AppliedJobsFragment(),
            new MessageFragment(),
    };
    private TabLayout tab_layout;
    private ViewPager view_pager;

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        initialize(R.id.drawer_dashboard, TAG_LOG);

        handleExtraBundles();

        findAllViews();

        helpers.asyncGetUser();

        helpers.asyncGetCounties();

        helpers.asyncGetJobTypes();

        helpers.asyncGetCategories();

    }


    // BASIC FUNCTIONS =============================================================================
    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString(START_FIRST_TIME) != null) {

            helpers.myDialog(DashboardActivity.this,
                    "WELCOME", SharedPrefs.getString(SharedPrefs.USER_F_NAME) + ", thank you for joining "
                    + getString(R.string.app_name) +
                    ", You are on the Dashboard where you can easily navigate through the app.");

            SharedPrefs.setBool(ALLOW_UPDATE_APP, true);
            SharedPrefs.setBool(ALLOW_MESSAGE_PUSH, true);
            setCountOnDrawerItem(menu_profile, "(Update Profile)");

        } else if (extras != null && extras.getString(START_RETURN) != null) {
            helpers.ToastMessage(DashboardActivity.this, "Welcome back " + SharedPrefs.getString(SharedPrefs.USER_F_NAME));
            setCountOnDrawerItem(menu_find_jobs, "(New)");

        }
    }

    private void findAllViews(){
            view_pager = findViewById(R.id.view_pager);
            tab_layout = findViewById(R.id.tabs);

            setupViewPager(view_pager);
            tab_layout.setupWithViewPager(view_pager, true);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i <= dashboardTitleList.length - 1; i++) {
            Fragment fragment = dashboardFragments[i];
//            if (i == 2) {
//                Bundle bundle = new Bundle();
//                bundle.putString("EXPERIENCE_TYPE", EXPERIENCE_TYPE_EDUCATION);
//                fragment.setArguments(bundle);
//            } else if (i == 3) {
//                Bundle bundle = new Bundle();
//                bundle.putString("EXPERIENCE_TYPE", EXPERIENCE_TYPE_WORK);
//                fragment.setArguments(bundle);
//            }
            adapter.addFragment(fragment, getResources().getString(dashboardTitleList[i]));
        }

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

}
