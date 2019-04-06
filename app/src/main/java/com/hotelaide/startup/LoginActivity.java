package com.hotelaide.startup;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.eftimoff.viewpagertransformers.BackgroundToForegroundTransformer;
import com.hotelaide.R;
import com.hotelaide.startup.fragments.LoginEmailFragment;
import com.hotelaide.startup.fragments.LoginSocialFragment;
import com.hotelaide.startup.fragments.ResetPasswordFragment;
import com.hotelaide.startup.fragments.SignUpEmailFragment;
import com.hotelaide.startup.fragments.SignUpSocialFragment;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class LoginActivity extends FragmentActivity {

    private Helpers
            helpers;

    private ViewPager viewPager;

    private Fragment[] fragments = {
            new SignUpSocialFragment(),
            new LoginSocialFragment(),
            new SignUpEmailFragment(),
            new LoginEmailFragment(),
            new ResetPasswordFragment()
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
            if (viewPager.getCurrentItem() != 0) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            } else {
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_confirm);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
            }
        } else {
            finish();
        }
    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (Fragment fragment : fragments) {
            adapter.addFragment(fragment);
        }

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);

        viewPager.setPageTransformer(true, new BackgroundToForegroundTransformer());

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

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

        void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }


    // SET ON CLICKS ===============================================================================
    public void openTermsAndConditions(View view) {
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://hotelaide.com/terms"));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            helpers.toastMessage("No application can handle this request."
                    + " Please install a web browser");
            e.printStackTrace();
        }
    }

    public void navigateToLoginScreen(View view) {
        viewPager.setCurrentItem(3);
    }

    public void navigateToResetPassScreen(View view) {
        viewPager.setCurrentItem(4);
    }

    public void navigateToSignUpScreen(View view) {
        viewPager.setCurrentItem(2);
    }


}

