package com.hotelaide.start_up;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.hotelaide.main_pages.activities.DashboardActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import com.hotelaide.R;
import com.hotelaide.main_pages.models.UserModel;
import com.hotelaide.services.UserService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.hotelaide.utils_external.LocationService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetAccount extends AppCompatActivity {
    private Helpers helper;

    private Database db;

    private EditText
            user_first_name,
            user_last_name,
            user_email;

    private Switch switch_notifs;

    private RoundedImageView
            user_image;

    private TextView
            user_phone,
            user_dob;


    private final String
            TAG_LOG = "SET ACCOUNT";

    private CallbackManager
            mCallbackManager;

    private FirebaseAuth
            mAuth;

    private UserModel userModelMyAccount = new UserModel();

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_account_inner_layout);

        helper = new Helpers(SetAccount.this);

        db = new Database();

        findAllViews();

        fetchFromDB();

        setToUI();

        setDates();

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton btn_facebook_login = findViewById(R.id.btn_facebook_login);
        btn_facebook_login.setReadPermissions("email", "public_profile");
        btn_facebook_login.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                helper.ToastMessage(SetAccount.this, "Login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                helper.ToastMessage(SetAccount.this, "Error " + error);
            }
        });

        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.animate_wobble(user_image);
            }
        });

        helper.setTracker(TAG_LOG);

    }

    @Override
    public void onResume() {
        super.onResume();
        setToUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.dismissProgressDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {

        user_image = findViewById(R.id.user_image);

        user_first_name = findViewById(R.id.user_first_name);
        user_last_name = findViewById(R.id.user_last_name);
        user_email = findViewById(R.id.user_email);
        user_phone = findViewById(R.id.user_phone);
        user_dob = findViewById(R.id.user_dob);

        switch_notifs = findViewById(R.id.switch_notifs);

        helper.setDefaultEditTextSelectionMode(user_first_name);
        helper.setDefaultEditTextSelectionMode(user_last_name);
        helper.setDefaultEditTextSelectionMode(user_email);

    }

    private void fetchFromDB() {
        userModelMyAccount = db.getUser();

        Helpers.LogThis(TAG_LOG, "ON START " +
                userModelMyAccount.user_id + " - " +
                userModelMyAccount.user_token + " - " +
                userModelMyAccount.first_name + " - " +
                userModelMyAccount.last_name + " - " +
                userModelMyAccount.email + " - " +
                userModelMyAccount.profile_pic + " - " +
                userModelMyAccount.banner_pic + " - " +
                userModelMyAccount.phone + " - " +
                userModelMyAccount.dob + " - " +
                userModelMyAccount.fb_id
        );
    }

    private void setToUI() {
        user_first_name.setText(userModelMyAccount.first_name);
        user_last_name.setText(userModelMyAccount.last_name);
        user_email.setText(userModelMyAccount.email);
        if (!userModelMyAccount.phone.equals("")) {
            user_phone.setText(
                    getString(R.string.txt_open_bracket)
                            .concat(userModelMyAccount.banner_pic)
                            .concat(getString(R.string.txt_closed_bracket))
                            .concat(userModelMyAccount.phone));
        }
        if (!userModelMyAccount.dob.equals("0000-00-00")) {
            user_dob.setText(userModelMyAccount.dob);
        }
        switch_notifs.setChecked(SharedPrefs.getAllowNearbyPushNotifs());
        switch_notifs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    switch_notifs.setChecked(false);
                    SharedPrefs.setAllowNearbyPushNotifs(false);
                    helper.ToastMessage(SetAccount.this, "Please Upgrade to Android 5.0 or higher to use this feature");

                } else{
                    Helpers.LogThis(TAG_LOG, "CHECKED: " + isChecked);
                    SharedPrefs.setAllowNearbyPushNotifs(isChecked);
                    if (isChecked) {
                        startService(new Intent(SetAccount.this, LocationService.class));
                    } else {
                        stopService(new Intent(SetAccount.this, LocationService.class));
                    }
                }
            }
        });


        Glide.with(SetAccount.this).load(userModelMyAccount.profile_pic).into(user_image);
        clearErrors();
    }

    private void clearErrors() {
        user_first_name.setError(null);
        user_last_name.setError(null);
        user_email.setError(null);
        user_phone.setError(null);
        user_dob.setError(null);
    }

    private void setDates() {

        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                String year = String.valueOf(selectedYear);
                String month, day;

                if (selectedMonth < 9) {
                    month = "0" + String.valueOf(selectedMonth + 1);
                } else {
                    month = String.valueOf(selectedMonth + 1);
                }

                if (selectedDay < 10) {
                    day = "0" + String.valueOf(selectedDay);
                } else {
                    day = String.valueOf(selectedDay);
                }

                user_dob.setText(year.concat("-").concat(month).concat("-").concat(day));
            }
        };


        user_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                DatePickerDialog datePicker = new DatePickerDialog(SetAccount.this,
                        R.style.AppTheme, datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Set Date Of Birth");
                datePicker.show();
                datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                datePicker.show();
            }
        });

    }

    public void updateDetails(View view) {
        String NULL = "";
        if (user_dob.getText().toString().length() < 1) {
            userModelMyAccount.dob = NULL;
        } else {
            userModelMyAccount.dob = user_dob.getText().toString();
        }

        if (user_email.getText().toString().length() < 1) {
            userModelMyAccount.email = NULL;
            mandatoryFields(userModelMyAccount);
        } else if (helper.validateEmail(user_email)) {
            mandatoryFields(userModelMyAccount);
        }
    }

    private void mandatoryFields(UserModel userModel) {
        if (user_first_name.getText().toString().length() < 1) {
            user_first_name.setError(getString(R.string.error_field_required));
        } else if (user_last_name.getText().toString().length() < 1) {
            user_last_name.setError(getString(R.string.error_field_required));
        } else {
            userModel.email = user_email.getText().toString();
            userModel.first_name = user_first_name.getText().toString();
            userModel.last_name = user_last_name.getText().toString();
            asyncSetUser();
            Helpers.LogThis(TAG_LOG,
                    userModel.user_id + " - " +
                            userModel.user_token + " - " +
                            userModel.dob + " - " +
                            userModel.dob + " - " +
                            userModel.last_name + " - " +
                            userModel.email + " - " +
                            userModel.profile_pic + " - " +
                            userModel.banner_pic + " - " +
                            userModel.phone + " - " +
                            userModel.dob + " - " +
                            userModel.dob + " - " +
                            userModel.dob + " - " +
                            userModel.fb_id
            );
        }

    }


    // HANDLE FACEBOOK SIGN IN AND OUT =============================================================
    private void handleFacebookAccessToken(final AccessToken token) {
        helper.setProgressDialogMessage(getString(R.string.progress_fetch_fb_details));
        helper.progressDialog(true);
        Helpers.LogThis(TAG_LOG, "Access Token: " + token.getToken());

        if (helper.validateGooglePlayServices(SetAccount.this)) {
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            mAuth.signInWithCredential(credential)
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helper.progressDialog(false);
                            helper.ToastMessage(SetAccount.this, "Failed to login with Facebook");
                            signOut();
                        }
                    })
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                try {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        Profile profile = Profile.getCurrentProfile();
                                        Helpers.LogThis(TAG_LOG, "FB USER ID: " + user.getUid());
                                        Helpers.LogThis(TAG_LOG, "FB PROFILE ID: " + profile.getId());

                                        userModelMyAccount.fb_id = profile.getId();

                                        if (user.getDisplayName() != null) {
                                            if (user.getDisplayName().contains(" ")) {
                                                String[] userFullName = user.getDisplayName().split(" ");
                                                userModelMyAccount.first_name = userFullName[0];
                                                userModelMyAccount.last_name = userFullName[1];
                                            } else {
                                                userModelMyAccount.first_name = user.getDisplayName();
                                            }
                                        }


//                                        userModelMyAccount.profile_pic = "https://graph.facebook.com/" + profile.getId() + "/picture?fields=url";

                                        Bundle params = new Bundle();
                                        params.putString("fields", "id,email,gender,cover,picture.type(large)");
                                        new GraphRequest(token, "me", params, HttpMethod.GET,
                                                new GraphRequest.Callback() {
                                                    @Override
                                                    public void onCompleted(GraphResponse response) {
                                                        if (response != null) {
                                                            try {
                                                                JSONObject data = response.getJSONObject();
                                                                if (data.has("picture")) {
                                                                    userModelMyAccount.profile_pic = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                                                    Helpers.LogThis(TAG_LOG, "FB PROFILE IMAGE URL: " + userModelMyAccount.profile_pic);
                                                                    setToUI();

                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }).executeAsync();

                                        userModelMyAccount.email = user.getEmail();

                                        setToUI();

                                        signOut();

                                    } else {
                                        signOut();
                                        helper.ToastMessage(SetAccount.this, "Failed to fetch details from Facebook, please try again later");
                                    }

                                } catch (NullPointerException e) {
                                    signOut();
                                    helper.ToastMessage(SetAccount.this, "Failed to fetch details from Facebook, please try again later");
                                }

                            } else {
                                helper.ToastMessage(SetAccount.this, getString(R.string.error_unknown));
                            }

                            helper.progressDialog(false);

                        }
                    });

        } else {
            helper.progressDialog(false);
            helper.ToastMessage(SetAccount.this, "Failed to fetch details from Facebook, Please update your Google Play Services");
            signOut();
        }
    }

    private void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }


    // ASYNC UPDATE DETAILS ========================================================================
    private void asyncSetUser() {
        helper.setProgressDialogMessage(getString(R.string.progress_loading_account_update));
        helper.progressDialog(true);
        UserService userService = UserService.retrofit.create(UserService.class);
        final Call<JsonObject> call = userService.updateUser(
                userModelMyAccount.dob,
                userModelMyAccount.first_name,
                userModelMyAccount.last_name,
                userModelMyAccount.email,
                userModelMyAccount.profile_pic,
                userModelMyAccount.banner_pic,
                userModelMyAccount.dob,
                userModelMyAccount.phone,
                userModelMyAccount.dob,
                userModelMyAccount.fb_id
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    if (db.setUser(main)) {
                        helper.progressDialog(false);
                        helper.ToastMessage(SetAccount.this, "Successfully Updated, Welcome to the EatOut App");
                        startActivity(new Intent(SetAccount.this, DashboardActivity.class));
                        finish();
                    } else {
                        helper.ToastMessage(SetAccount.this, "Invalid credentials entered, Please try again");
                    }
                } catch (JSONException e) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                    helper.ToastMessage(SetAccount.this, "Invalid credentials entered, Please try again");
                } catch (Exception e) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                    helper.ToastMessage(SetAccount.this, "Invalid credentials entered, Please try again");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());
                helper.progressDialog(false);
                if (helper.validateInternetConnection()) {
                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.coordinator_layout),
                            getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                    snackBar.show();
                }
            }
        });
    }
}
