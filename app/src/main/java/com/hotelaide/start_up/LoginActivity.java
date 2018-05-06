package com.hotelaide.start_up;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotelaide.R;
import com.hotelaide.main_pages.activities.HomeActivity;
import com.hotelaide.main_pages.fragments.StartUpAboutUsFragment;
import com.hotelaide.main_pages.fragments.StartUpContactUsFragment;
import com.hotelaide.main_pages.fragments.StartUpForgotPassFragment;
import com.hotelaide.main_pages.fragments.StartUpLoginFragment;
import com.hotelaide.main_pages.fragments.StartUpSignUpFragment;
import com.hotelaide.main_pages.models.UserModel;
import com.hotelaide.services.LoginService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Helpers
            helper;

    public TextView
            btn_confirm,
            btn_cancel;

    private Database db;

    private ImageView
            login_background,
            login_background2;

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

        helper = new Helpers(LoginActivity.this);

        db = new Database();

        setContentView(R.layout.activity_login);

        findAllViews();

        helper.setTracker(TAG_LOG);


    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (helper != null) {
            helper.dismissProgressDialog();
        }
        super.onDestroy();
    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        login_background = findViewById(R.id.login_background);
        login_background2 = findViewById(R.id.login_background2);

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

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                helper.animate_fade_in(login_background);
                if (position == 0) {
                    login_background.setImageResource(R.drawable.bckgrd_login);
                } else if (position == 1) {
                    login_background.setImageResource(R.drawable.bckgrd_login);
                } else if (position == 2) {
                    login_background.setImageResource(R.drawable.bckgrd_login);
                } else if (position == 3) {
                    login_background.setImageResource(R.drawable.bckgrd_login);
                } else if (position == 4) {
                    login_background.setImageResource(R.drawable.bckgrd_login);
                }
                login_background.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
        helper.ToastMessage(LoginActivity.this, "OPEN SOME TERMS AND CONDITONS HERE");
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



    // LOGIN ASYNC FUNCTIONS =======================================================================
    private void asyncLogin(final String email, final String password) {

        helper.setProgressDialogMessage("Logging in, please wait...");
        helper.progressDialog(true);

        LoginService loginService = LoginService.retrofit.create
                (LoginService.class);

//        final Call<JsonObject> call = loginService.sendPhone(phone, country_code);

//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
//                helper.progressDialog(false);
//                try {
//                    JSONObject main = new JSONObject(String.valueOf(response.body()));
//                    if (main.getBoolean("success")) {
//                        Helpers.LogThis(TAG_LOG, main.getString("otp"));
//                        refreshForms(STR_ACTIVATION);
//                    } else {
//                        helper.ToastMessage(LoginActivity.this, "Invalid Phone Number, Please try again");
//                    }
//                } catch (JSONException e) {
//                    helper.ToastMessage(LoginActivity.this, e.toString());
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
//                helper.progressDialog(false);
//                if (helper.validateInternetConnection()) {
//                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.activity_login),
//                            getString(R.string.error_connection), Snackbar.LENGTH_LONG);
//                    snackBar.setAction("Dismiss", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            snackBar.dismiss();
//                        }
//                    });
//                    snackBar.show();
//                } else {
//                    helper.ToastMessage(LoginActivity.this, getString(R.string.error_500));
//                }
//
//            }
//        });

    }

    private void asyncRegisterUser(final UserModel userModel) {

        helper.setProgressDialogMessage("Registration in progress... please wait...");
        helper.progressDialog(true);

        LoginService loginService = LoginService.retrofit.create
                (LoginService.class);

//        final Call<JsonObject> call = loginService.sendCode(otp, phone);

//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
//                helper.progressDialog(false);
//                try {
//                    JSONObject main = new JSONObject(String.valueOf(response.body()));
//                    if (db.setUser(main)) {
//                        startUp();
//
//                    } else {
//                        helper.ToastMessage(LoginActivity.this, "Invalid Activation Code, Please try again");
//                    }
//                } catch (JSONException e) {
//                    helper.ToastMessage(LoginActivity.this, e.toString());
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
//                helper.progressDialog(false);
//                if (helper.validateInternetConnection()) {
//                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.activity_login),
//                            getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
//                    snackBar.setAction("Dismiss", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            snackBar.dismiss();
//                        }
//                    });
//                    snackBar.show();
//                } else {
//                    helper.ToastMessage(LoginActivity.this, getString(R.string.error_500));
//                }
//
//            }
//        });

    }

    private void startUp() {
        finish();
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }

}

