package com.hotelaide.startup;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.hotelaide.R;
import com.hotelaide.startup.fragments.StartUpAboutUsFragment;
import com.hotelaide.startup.fragments.StartUpContactUsFragment;
import com.hotelaide.startup.fragments.StartUpForgotPassFragment;
import com.hotelaide.startup.fragments.StartUpLoginFragment;
import com.hotelaide.startup.fragments.StartUpSignUpFragment;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class LoginActivity extends FragmentActivity {

    private Helpers
            helpers;

    private ViewPager viewPager;

    private int[] fragment_titles = {
            R.string.nav_login,
            R.string.nav_sign_up,
            R.string.nav_forgot_pass,
            R.string.nav_about_us,
            R.string.nav_contact_us
    };

    private Fragment[] fragments = {
            new StartUpLoginFragment(),
            new StartUpSignUpFragment(),
            new StartUpForgotPassFragment(),
            new StartUpAboutUsFragment(),
            new StartUpContactUsFragment()
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

    @Override
    public void onBackPressed() {
        if (this.isTaskRoot()) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_confirm);
            final TextView txt_message = dialog.findViewById(R.id.txt_message);
            final TextView btn_confirm = dialog.findViewById(R.id.btn_confirm);
            final TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
            final TextView txt_title = dialog.findViewById(R.id.txt_title);
            txt_title.setText(getString(R.string.txt_exit).concat(new String(Character.toChars(0x1F625))));
            txt_message.setText(getString(R.string.txt_exit_desc));
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    finish();
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            dialog.show();
        } else {
            finish();
        }
    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabs);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager, true);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        int frag_number = fragment_titles.length;

        for(int i=0; i<frag_number; i++){
            adapter.addFragment(fragments[i], getResources().getString(fragment_titles[i]));
        }

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);

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


    // SET ON CLICKS ===============================================================================
    public void openTermsAndConditions(View view) {
        helpers.ToastMessage(LoginActivity.this, "OPEN SOME TERMS AND CONDITIONS HERE");
    }

    public void navigateToLoginScreen(View view) {
        viewPager.setCurrentItem(0);
    }

    public void navigateToForgotPassScreen(View view) {
        viewPager.setCurrentItem(2);
    }

    public void navigateToSignUpScreen(View view) {
        viewPager.setCurrentItem(1);
    }


}

