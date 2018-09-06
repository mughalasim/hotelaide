package com.hotelaide.startup;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.hotelaide.R;
import com.hotelaide.startup.fragments.StartUpAboutUsFragment;
import com.hotelaide.startup.fragments.StartUpContactUsFragment;
import com.hotelaide.startup.fragments.StartUpForgotPassFragment;
import com.hotelaide.startup.fragments.StartUpLoginFragment;
import com.hotelaide.startup.fragments.StartUpSignUpFragment;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Helpers
            helpers;

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
    public void TERMS_CONDITIONS(View view) {
        helpers.ToastMessage(LoginActivity.this, "OPEN SOME TERMS AND CONDITIONS HERE");
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

