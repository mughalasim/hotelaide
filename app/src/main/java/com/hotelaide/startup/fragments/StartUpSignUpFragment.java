package com.hotelaide.startup.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.interfaces.LoginInterface;
import com.hotelaide.main.activities.DashboardActivity;
import com.hotelaide.main.models.UserModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.rilixtech.CountryCodePicker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.ACCESS_TOKEN;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_FIRST_TIME;

;

public class StartUpSignUpFragment extends Fragment {
    private View rootview;
    private Helpers helpers;

    private TextView
            txt_user_dob;

    private MaterialButton
            btn_confirm;

    private SlidingUpPanelLayout sliding_panel;

    private CountryCodePicker ccp_user_country_code;

    private MaterialButton
            btn_open_social_media,
            btn_login_facebook,
            btn_login_google;

    private EditText
            et_user_first_name,
            et_user_last_name,
            et_user_email,
            et_user_phone,
            et_user_pass,
            et_user_pass_confirm;

    private Spinner
            spinner_user_gender;

    private final String
            TAG_LOG = "FRAGMENT SIGN UP";
    private final String LOGIN_REGISTER = "REGISTER";
    private final String LOGIN_FACEBOOK = "FACEBOOK";

    // FACEBOOK
    private CallbackManager callback_manager;
    private FirebaseAuth fire_base_auth;
    private UserModel global_user_model;

    // GOOGLE
    private GoogleSignInClient google_sign_in_client;
    private int GOOGLE_REQUEST_CODE = 999;

    public StartUpSignUpFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.frag_startup_signup, container, false);

                helpers = new Helpers(getActivity());

                findAllViews();

                setListeners();

                initializeFacebook(getActivity());

                initializeGoogle(getActivity());

                global_user_model = new UserModel();

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(rootview);
        }
        return rootview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Helpers.LogThis(TAG_LOG, "ACTIVITY RESULT " + data.toString() + " : " + requestCode + " : " + resultCode);
        if (requestCode == GOOGLE_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleAccessToken(task);
        } else {
            callback_manager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        btn_confirm = rootview.findViewById(R.id.btn_confirm);

        btn_confirm.setVisibility(View.VISIBLE);

        btn_confirm.setText(getString(R.string.nav_sign_up));

        et_user_first_name = rootview.findViewById(R.id.et_user_first_name);
        et_user_last_name = rootview.findViewById(R.id.et_user_last_name);
        et_user_email = rootview.findViewById(R.id.et_user_email);

        et_user_phone = rootview.findViewById(R.id.et_user_phone);
        ccp_user_country_code = rootview.findViewById(R.id.ccp_user_country_code);
        ccp_user_country_code.registerPhoneNumberTextView(et_user_phone);

        et_user_pass = rootview.findViewById(R.id.et_user_pass);
        et_user_pass_confirm = rootview.findViewById(R.id.et_user_pass_confirm);
        txt_user_dob = rootview.findViewById(R.id.txt_user_dob);
        spinner_user_gender = rootview.findViewById(R.id.spinner_user_gender);
        if (getActivity() != null)
            spinner_user_gender.setAdapter(new ArrayAdapter<>(
                    getActivity(),
                    R.layout.list_item_spinner,
                    getResources().getStringArray(R.array.select_gender)
            ));

        // SOCIAL MEDIA LOGIN ====================================================
        sliding_panel = rootview.findViewById(R.id.sliding_panel);
        btn_open_social_media = rootview.findViewById(R.id.btn_open_social_media);
        btn_login_google = rootview.findViewById(R.id.btn_login_google);
        btn_login_facebook = rootview.findViewById(R.id.btn_login_facebook);

        setDates();

    }

    private void setListeners() {

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpers.validateEmptyEditText(et_user_first_name) &&
                        helpers.validateEmptyEditText(et_user_last_name) &&
                        helpers.validateEmail(et_user_email) &&
                        helpers.validateEmptyEditText(et_user_pass) &&
                        helpers.validateEmptyEditText(et_user_pass_confirm) &&
                        helpers.validateEmptyTextView(txt_user_dob, "Enter your Date of Birth") &&
                        helpers.validateEmptyEditText(et_user_phone)) {
                    if (!et_user_pass.getText().toString().equals(et_user_pass_confirm.getText().toString())) {
                        helpers.ToastMessage(getContext(), "Password and Confirm password do not match");
                    } else if (et_user_pass.getText().toString().length() < 8) {
                        helpers.ToastMessage(getContext(), "Password too short");
                    } else if (!ccp_user_country_code.isValid()) {
                        helpers.ToastMessage(getContext(), "Phone number is invalid");
                    } else {
                        showDialogSetAccountPassword(getActivity(), LOGIN_REGISTER);
                    }
                }
            }
        });

        btn_open_social_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

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

                txt_user_dob.setText(day.concat(getString(R.string.txt_date_separator)).concat(month).concat(getString(R.string.txt_date_separator)).concat(year));
            }
        };


        txt_user_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    DatePickerDialog datePicker = new DatePickerDialog(
                            getActivity(), AlertDialog.THEME_HOLO_LIGHT,
                            datePickerListener,
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH));
                    datePicker.setCancelable(false);
                    datePicker.setTitle("Set Date Of Birth");
                    datePicker.show();
                    datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                    datePicker.show();
                }
            }
        });

    }

    private void showDialogSetAccountPassword(final Activity activity, final String loginType) {
        if (loginType.equals(LOGIN_REGISTER)) {
            setToModelFromFields();
        } else {
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.dialog_account_password);
            dialog.setCancelable(false);
            final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
            final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
            final EditText et_user_pass = dialog.findViewById(R.id.et_user_pass);
            final EditText et_user_pass_confirm = dialog.findViewById(R.id.et_user_pass_confirm);

            btn_cancel.setVisibility(View.VISIBLE);
            btn_cancel.setText(getString(R.string.txt_cancel));
            btn_confirm.setText(getString(R.string.nav_sign_up));

            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (helpers.validateEmptyEditText(et_user_pass) && helpers.validateEmptyEditText(et_user_pass_confirm)) {
                        if (!et_user_pass.getText().toString().equals(et_user_pass_confirm.getText().toString())) {
                            helpers.ToastMessage(getContext(), "Password and Confirm password do not match");
                        } else if (et_user_pass.getText().toString().length() < 8) {
                            helpers.ToastMessage(getContext(), "Password too short");
                        } else {
                            global_user_model.password = et_user_pass.getText().toString();
                            logRegModel(global_user_model);
                            asyncRegister(global_user_model);
                        }
                    }
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

    }

    private void setToModelFromFields() {
        UserModel userModel = new UserModel();
        userModel.first_name = et_user_first_name.getText().toString();
        userModel.last_name = et_user_last_name.getText().toString();
        userModel.country_code = ccp_user_country_code.getSelectedCountryCodeAsInt();
        userModel.phone = Integer.parseInt(et_user_phone.getText().toString());
        userModel.email = et_user_email.getText().toString();
        userModel.password = et_user_pass.getText().toString();
        userModel.dob = txt_user_dob.getText().toString();
        userModel.fb_id = "";
        userModel.google_id = "";
        userModel.gender = spinner_user_gender.getSelectedItemPosition();

        logRegModel(userModel);

        asyncRegister(userModel);
    }

    private void logRegModel(UserModel userModel) {
        Helpers.LogThis(TAG_LOG,
                "\n\n First name: " + userModel.first_name
                        + "\n Last name: " + userModel.last_name
                        + "\n Country Code: " + userModel.country_code
                        + "\n Phone Number: " + userModel.phone
                        + "\n Email: " + userModel.email
                        + "\n Pass: " + userModel.password
                        + "\n DOB: " + userModel.dob
                        + "\n FB_ID: " + userModel.fb_id
                        + "\n G_ID: " + userModel.google_id
                        + "\n GENDER: " + userModel.gender
        );

    }


    // FACEBOOK SET UP =============================================================================
    private void initializeFacebook(final Activity activity) {

        fire_base_auth = FirebaseAuth.getInstance();

        callback_manager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callback_manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                handleFacebookAccessToken(activity, loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                helpers.ToastMessage(activity, "Login Cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                helpers.ToastMessage(activity, "Error " + exception);
            }
        });

        btn_login_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
                }
            }
        });
    }

    private void handleFacebookAccessToken(final Activity activity, final AccessToken token) {
        helpers.setProgressDialogMessage(getString(R.string.progress_fetch_fb_details));
        helpers.progressDialog(true);
        Helpers.LogThis(TAG_LOG, "Access Token: " + token.getToken());

        if (helpers.validateGooglePlayServices(activity)) {
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            fire_base_auth.signInWithCredential(credential)
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helpers.progressDialog(false);
                            helpers.ToastMessage(activity, "Failed to login with Facebook");
                            signOutFaceBook();
                        }
                    })
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                try {
                                    FirebaseUser user = fire_base_auth.getCurrentUser();
                                    if (user != null) {
                                        Profile profile = Profile.getCurrentProfile();
                                        Helpers.LogThis(TAG_LOG, "FB USER ID: " + user.getUid());
                                        Helpers.LogThis(TAG_LOG, "FB PROFILE ID: " + profile.getId());

                                        global_user_model.fb_id = profile.getId();

                                        global_user_model.gender = spinner_user_gender.getSelectedItemPosition();

                                        if (user.getDisplayName() != null) {
                                            if (user.getDisplayName().contains(" ")) {
                                                String[] userFullName = user.getDisplayName().split(" ");
                                                global_user_model.first_name = userFullName[0];
                                                global_user_model.last_name = userFullName[1];
                                            } else {
                                                global_user_model.first_name = user.getDisplayName();
                                            }
                                        }

                                        Bundle params = new Bundle();
                                        params.putString("fields", "id,email,picture.type(large)");
                                        new GraphRequest(token, "me", params, HttpMethod.GET,
                                                new GraphRequest.Callback() {
                                                    @Override
                                                    public void onCompleted(GraphResponse response) {
                                                        if (response != null) {
                                                            try {
                                                                JSONObject data = response.getJSONObject();
                                                                if (data.has("picture")) {
                                                                    global_user_model.img_avatar = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                                                    Helpers.LogThis(TAG_LOG, "FB PROFILE IMAGE URL: " + global_user_model.img_avatar);
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }).executeAsync();

                                        global_user_model.email = user.getEmail();

                                        logRegModel(global_user_model);

                                        showDialogSetAccountPassword(getActivity(), LOGIN_FACEBOOK);

                                        signOutFaceBook();

                                    } else {
                                        signOutFaceBook();
                                        helpers.ToastMessage(activity, "Failed to fetch details from Facebook, please try again later1");
                                        Helpers.LogThis(TAG_LOG, "USER NULL");
                                    }

                                } catch (NullPointerException e) {
                                    signOutFaceBook();
                                    Helpers.LogThis(TAG_LOG, e.toString());
                                    helpers.ToastMessage(activity, "Failed to fetch details from Facebook, please try again later2");
                                }

                            } else {
                                helpers.ToastMessage(activity, getString(R.string.error_unknown));
                                Helpers.LogThis(TAG_LOG, task.toString());
                            }

                            helpers.progressDialog(false);

                        }
                    });

        } else {
            helpers.progressDialog(false);
            helpers.ToastMessage(activity, "Failed to fetch details from Facebook, Please update your Google Play Services");
            signOutFaceBook();
        }
    }

    private void signOutFaceBook() {
        fire_base_auth.signOut();
        LoginManager.getInstance().logOut();
    }


    // GOOGLE SET UP ===============================================================================
    private void initializeGoogle(final Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        google_sign_in_client = GoogleSignIn.getClient(activity, gso);
        btn_login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = google_sign_in_client.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE);
            }
        });
    }

    private void handleGoogleAccessToken(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            global_user_model.google_id = account.getId();
            global_user_model.email = account.getEmail();
            global_user_model.first_name = account.getGivenName();
            global_user_model.last_name = account.getFamilyName();
            if (account.getPhotoUrl() != null)
                global_user_model.img_avatar = account.getPhotoUrl().toString();
            global_user_model.gender = spinner_user_gender.getSelectedItemPosition();
            String LOGIN_GOOGLE = "GOOGLE";
            showDialogSetAccountPassword(getActivity(), LOGIN_GOOGLE);
            signOutGoogle();

        } catch (ApiException e) {
            Helpers.LogThis(TAG_LOG, "signInResult : CODE: " + e.getStatusCode());
            helpers.ToastMessage(getActivity(), getResources().getString(R.string.error_sign_in_cancelled));
        }
    }

    private void signOutGoogle() {
        google_sign_in_client.signOut();
    }

    // REGISTER ASYNC FUNCTIONS ====================================================================
    private void asyncRegister(UserModel userModel) {

        helpers.setProgressDialogMessage(getString(R.string.progress_sign_up));
        helpers.progressDialog(true);
        logRegModel(userModel);

        LoginInterface loginInterface = LoginInterface.retrofit.create(LoginInterface.class);
        final Call<JsonObject> call = loginInterface.userRegister(
                userModel.first_name,
                userModel.last_name,
                userModel.country_code,
                userModel.phone,
                userModel.email,
                userModel.password,
                userModel.password,
                userModel.dob,
                userModel.fb_id,
                userModel.google_id,
                BuildConfig.ACCOUNT_TYPE,
                userModel.gender
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.progressDialog(false);
                try {

                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success") && getActivity() != null) {
                        JSONObject data = main.getJSONObject("data");
                        if (SharedPrefs.setUser(data.getJSONObject("user"))) {
                            SharedPrefs.setString(ACCESS_TOKEN, data.getString("token"));
                            startActivity(new Intent(getActivity(), DashboardActivity.class).putExtra(EXTRA_START_FIRST_TIME, EXTRA_START_FIRST_TIME));
                            getActivity().finish();
                        } else {
                            helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                        }
                    } else {
                        helpers.handleErrorMessage(getActivity(), main.getJSONObject("data"));
                    }
                } catch (JSONException e) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.progressDialog(false);
                Helpers.LogThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                } else {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                }

            }
        });

    }


}