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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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

    private Database db;

    private final String
            TAG_LOG = "LOGIN";


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
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tabs);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager, true);

    }

    private void setupViewPager(ViewPager viewPager) {
        LoginActivity.ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Fragment fragment1 = new StartUpLoginFragment();
        adapter.addFragment(fragment1);

        Fragment fragment2 = new StartUpSignUpFragment();
        adapter.addFragment(fragment2);

        Fragment fragment3 = new StartUpAboutUsFragment();
        adapter.addFragment(fragment3);

        Fragment fragment4 = new StartUpForgotPassFragment();
        adapter.addFragment(fragment4);

        Fragment fragment5 = new StartUpContactUsFragment();
        adapter.addFragment(fragment5);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
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

        private void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }





    // ONCLICK VIEWS ===============================================================================

    private void dropDownKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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

        helper.setProgressDialogMessage("Regsiteration in progress... please wait...");
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

