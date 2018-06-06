package com.hotelaide.start_up;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.main_pages.activities.DashboardActivity;
import com.hotelaide.main_pages.fragments.StartUpAboutUsFragment;
import com.hotelaide.main_pages.fragments.StartUpContactUsFragment;
import com.hotelaide.main_pages.fragments.StartUpForgotPassFragment;
import com.hotelaide.main_pages.fragments.StartUpLoginFragment;
import com.hotelaide.main_pages.fragments.StartUpSignUpFragment;
import com.hotelaide.main_pages.models.UserModel;
import com.hotelaide.services.LoginService;
import com.hotelaide.services.UserService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Helpers
            helpers;

    public TextView
            btn_confirm,
            btn_cancel;

    private final String
            TAG_LOG = "LOGIN";

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private int[] navLabels = {
            R.string.nav_login,
            R.string.nav_sign_up,
            R.string.nav_forgot_pass,
            R.string.nav_about_us,
            R.string.nav_contact_us
    };


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helpers = new Helpers(LoginActivity.this);

        setContentView(R.layout.activity_login);

        findAllViews();
    }

    @Override
    protected void onDestroy() {
        if (helpers != null) {
            helpers.dismissProgressDialog();
        }
        super.onDestroy();
    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager, true);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Fragment fragment1 = new StartUpLoginFragment();
        adapter.addFragment(fragment1, getResources().getString(navLabels[0]));

        Fragment fragment2 = new StartUpSignUpFragment();
        adapter.addFragment(fragment2, getResources().getString(navLabels[1]));

        Fragment fragment3 = new StartUpForgotPassFragment();
        adapter.addFragment(fragment3, getResources().getString(navLabels[2]));

        Fragment fragment4 = new StartUpAboutUsFragment();
        adapter.addFragment(fragment4, getResources().getString(navLabels[3]));

        Fragment fragment5 = new StartUpContactUsFragment();
        adapter.addFragment(fragment5, getResources().getString(navLabels[4]));

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }


    // SET ON CLICKS ===============================================================================
    public void TERMS_CONDITIONS(View view) {
        helpers.ToastMessage(LoginActivity.this, "OPEN SOME TERMS AND CONDITONS HERE");
    }

    public void LOGIN(View view) {
        viewPager.setCurrentItem(0);
    }

    public void FORGOT_PASS(View view) {
        viewPager.setCurrentItem(2);
    }

    public void SIGN_UP(View view) {
        viewPager.setCurrentItem(1);
    }

    public void MAKE_CALL(View view) {
        TextView textView = (TextView) view;
        helpers.dialogMakeCall(LoginActivity.this, textView.getText().toString());
    }

}

