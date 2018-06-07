package com.hotelaide.main_pages.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main_pages.activities.DashboardActivity;
import com.hotelaide.main_pages.models.UserModel;
import com.hotelaide.services.LoginService;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.rilixtech.CountryCodePicker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.Helpers.START_FIRST_TIME;


public class StartUpSignUpFragment extends Fragment {
    private View rootview;
    private Helpers helpers;

    private TextView
            panel_title,
            btn_login_google,
            txt_user_dob,
            btn_confirm,
            btn_cancel;

    private SlidingUpPanelLayout sliding_panel;

    private CountryCodePicker ccp_user_country_code;

    private ImageView img_user_pass_toggle;

    private EditText
            et_user_first_name,
            et_user_last_name,
            et_user_email,
            et_user_phone,
            et_user_pass,
            et_user_pass_confirm;

    private final String
            TAG_LOG = "FRAGMENT SIGN UP",
            LOGIN_REGISTER = "REGISTER",
            LOGIN_FACEBOOK = "FACEBOOK",
            LOGIN_GOOGLE = "GOOGLE",
            TAG_PASS_HIDDEN = "0",
            TAG_PASS_SHOWN = "1";

    // FACEBOOK STUFF
    private LoginButton btn_login_facebook;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private UserModel userModel;

    public StartUpSignUpFragment() {

    }


    // OVERRIDE METHODS ============================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.fragment_startup_signup, container, false);

                helpers = new Helpers(getActivity());

                mAuth = FirebaseAuth.getInstance();

                mCallbackManager = CallbackManager.Factory.create();

                findAllViews();

                setListeners();

                initializeFacebook(getActivity());

                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

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
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        btn_cancel = rootview.findViewById(R.id.btn_cancel);
        btn_confirm = rootview.findViewById(R.id.btn_confirm);

        btn_cancel.setVisibility(View.GONE);
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

        img_user_pass_toggle = rootview.findViewById(R.id.img_user_pass_toggle);
        img_user_pass_toggle.setTag(TAG_PASS_HIDDEN);
        img_user_pass_toggle.setImageResource(R.drawable.ic_pass_hide);

        // SOCIAL MEDIA LOGIN ====================================================
        sliding_panel = rootview.findViewById(R.id.sliding_panel);
        panel_title = rootview.findViewById(R.id.panel_title);

        btn_login_facebook = rootview.findViewById(R.id.btn_login_facebook);
        btn_login_google = rootview.findViewById(R.id.btn_login_google);
        btn_login_facebook.setFragment(this);

        setDates();

    }

    private void setListeners() {

        // TODO - Delete later
        et_user_first_name.setText("Asim");
        et_user_last_name.setText("Mughal");
        et_user_phone.setText("0716140603");
        et_user_pass.setText("ppppppppp");
        et_user_pass_confirm.setText("ppppppppp");
        et_user_email.setText("asimkenya@gmail.com");


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpers.validateEmptyEditText(et_user_first_name) &&
                        helpers.validateEmptyEditText(et_user_last_name) &&
                        helpers.validateEmail(et_user_email) &&
                        helpers.validateEmptyEditText(et_user_pass) &&
                        helpers.validateEmptyEditText(et_user_pass_confirm) &&
                        helpers.validateEmptyTextView(txt_user_dob, "Enter your Date of Birth") &&
                        helpers.validateEmptyEditText(et_user_phone)
                        ) {
                    if (!et_user_pass.getText().toString().equals(et_user_pass_confirm.getText().toString())) {
                        helpers.ToastMessage(getContext(), "Password and Confirm password do not match");
                    } else if (et_user_pass.getText().toString().length() < 8) {
                        helpers.ToastMessage(getContext(), "Password too short");
                    } else if (!ccp_user_country_code.isValid()) {
                        helpers.ToastMessage(getContext(), "Phone number is invalid");
                    } else {
                        showDialogSetAccountType(getActivity(), LOGIN_REGISTER);
                    }
                }
            }
        });


        img_user_pass_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_user_pass_toggle.getTag().toString().equals(TAG_PASS_HIDDEN)) {
                    img_user_pass_toggle.setImageResource(R.drawable.ic_pass_show);
                    img_user_pass_toggle.setTag(TAG_PASS_SHOWN);
                    et_user_pass.setTransformationMethod(null);
                    et_user_pass_confirm.setTransformationMethod(null);
                } else {
                    img_user_pass_toggle.setImageResource(R.drawable.ic_pass_hide);
                    img_user_pass_toggle.setTag(TAG_PASS_HIDDEN);
                    et_user_pass.setTransformationMethod(new PasswordTransformationMethod());
                    et_user_pass_confirm.setTransformationMethod(new PasswordTransformationMethod());
                }
                helpers.animateWobble(img_user_pass_toggle);
            }
        });

        btn_login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSetAccountType(getActivity(), LOGIN_GOOGLE);
            }
        });

        sliding_panel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    panel_title.setTextColor(getResources().getColor(R.color.colorAccent));
                    panel_title.setText("SOCIAL MEDIA SIGN UP");
                    panel_title.setTypeface(panel_title.getTypeface(), Typeface.BOLD);
                    panel_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_large));
                } else {
                    panel_title.setTextColor(getResources().getColor(R.color.colorPrimary));
                    panel_title.setText("Sign up with social Media instead");
                    panel_title.setTypeface(panel_title.getTypeface(), Typeface.NORMAL);
                    panel_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_normal));
                }
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

                txt_user_dob.setText(year.concat("-").concat(month).concat("-").concat(day));
            }
        };


        txt_user_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() != null) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    DatePickerDialog datePicker = new DatePickerDialog(getContext(),
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
            }
        });

    }

    private void showDialogSetAccountType(final Activity activity, final String loginType) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_account_type);
        dialog.setCancelable(false);
        final TextView btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final RadioGroup radio_group = dialog.findViewById(R.id.radio_group);
        final RadioButton radio_btn_job_seeker = dialog.findViewById(R.id.radio_btn_job_seeker);
        final RadioButton radio_btn_employer = dialog.findViewById(R.id.radio_btn_employer);
        btn_cancel.setVisibility(View.VISIBLE);
        btn_cancel.setText(getString(R.string.txt_back));

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedRadioBtn = radio_group.getCheckedRadioButtonId();
                if (selectedRadioBtn != R.id.radio_btn_employer && selectedRadioBtn != R.id.radio_btn_job_seeker) {
                    helpers.ToastMessage(getContext(), "No selection has been made");
                    helpers.animate_flash(radio_btn_employer);
                    helpers.animate_flash(radio_btn_job_seeker);
                } else {
                    String selectedAccountType = "";
                    if (selectedRadioBtn == R.id.radio_btn_employer) {
                        selectedAccountType = BuildConfig.ACCOUNT_TYPE_EMPLOYEER;
                    } else {
                        selectedAccountType = BuildConfig.ACCOUNT_TYPE_JOB;
                    }
                    dialog.cancel();

                    switch (loginType) {
                        case LOGIN_REGISTER:
                            setToModelFromFields(selectedAccountType);
                            break;

                        case LOGIN_FACEBOOK:
                            helpers.ToastMessage(activity, "OPEN FACEBOOK TO LOGIN");
                            break;

                        case LOGIN_GOOGLE:
                            helpers.ToastMessage(activity, "OPEN GOOGLE TO LOGIN");
                            break;
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

    private void setToModelFromFields(String accountType) {
        userModel = new UserModel();
        userModel.first_name = et_user_first_name.getText().toString();
        userModel.last_name = et_user_last_name.getText().toString();
        userModel.country_code = ccp_user_country_code.getDefaultCountryCodeAsInt();
        userModel.phone = Integer.parseInt(et_user_phone.getText().toString());
        userModel.email = et_user_email.getText().toString();
        userModel.password = et_user_pass.getText().toString();
        userModel.account_type = accountType;
        userModel.dob = txt_user_dob.getText().toString();
        userModel.fb_id = "";
        userModel.google_id = "";

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
                        + "\n Account Type: " + userModel.account_type
                        + "\n DOB: " + userModel.dob
                        + "\n FB_ID: " + userModel.fb_id
                        + "\n G_ID: " + userModel.google_id
        );

    }


    // FACEBOOK SET UP =============================================================================
    private void initializeFacebook(final Activity activity) {

        btn_login_facebook.setReadPermissions("email", "public_profile");

        btn_login_facebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(activity, loginResult.getAccessToken());
                sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }

            @Override
            public void onCancel() {
                helpers.ToastMessage(activity, "Login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                helpers.ToastMessage(activity, "Error " + error);
            }
        });
    }


    // HANDLE FACEBOOK SIGN IN AND OUT =============================================================
    private void handleFacebookAccessToken(final Activity activity, final AccessToken token) {
        helpers.setProgressDialogMessage(getString(R.string.progress_fetch_fb_details));
        helpers.progressDialog(true);
        Helpers.LogThis(TAG_LOG, "Access Token: " + token.getToken());

        if (helpers.validateGooglePlayServices(activity)) {
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            mAuth.signInWithCredential(credential)
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helpers.progressDialog(false);
                            helpers.ToastMessage(activity, "Failed to login with Facebook");
                            signOut();
                        }
                    })
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

//                            Helpers.LogThis(TAG_LOG, " " + task.getResult());

                            if (task.isSuccessful()) {
                                try {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        Profile profile = Profile.getCurrentProfile();
                                        Helpers.LogThis(TAG_LOG, "FB USER ID: " + user.getUid());
                                        Helpers.LogThis(TAG_LOG, "FB PROFILE ID: " + profile.getId());

                                        final UserModel userModel = new UserModel();

                                        userModel.fb_id = profile.getId();

                                        if (user.getDisplayName() != null) {
                                            if (user.getDisplayName().contains(" ")) {
                                                String[] userFullName = user.getDisplayName().split(" ");
                                                userModel.first_name = userFullName[0];
                                                userModel.last_name = userFullName[1];
                                            } else {
                                                userModel.first_name = user.getDisplayName();
                                            }
                                        }

//                                        userModelMyAccount.image = "https://graph.facebook.com/" + profile.getId() + "/picture?fields=url";

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
                                                                    userModel.img_avatar = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                                                    Helpers.LogThis(TAG_LOG, "FB PROFILE IMAGE URL: " + userModel.img_avatar);

                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }).executeAsync();

                                        userModel.email = user.getEmail();

                                        logRegModel(userModel);

                                        showDialogSetAccountType(getActivity(), LOGIN_FACEBOOK);

                                        signOut();

                                    } else {
                                        signOut();
                                        helpers.ToastMessage(activity, "Failed to fetch details from Facebook, please try again later");
                                    }

                                } catch (NullPointerException e) {
                                    signOut();
                                    helpers.ToastMessage(activity, "Failed to fetch details from Facebook, please try again later");
                                }

                            } else {
                                helpers.ToastMessage(activity, getString(R.string.error_unknown));
                            }

                            helpers.progressDialog(false);

                        }
                    });

        } else {
            helpers.progressDialog(false);
            helpers.ToastMessage(activity, "Failed to fetch details from Facebook, Please update your Google Play Services");
            signOut();
        }
    }

    private void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }


    // LOGIN ASYNC FUNCTIONS =======================================================================
    private void asyncRegister(UserModel userModel) {

        helpers.setProgressDialogMessage("Creating your account, please wait...");
        helpers.progressDialog(true);
        logRegModel(userModel);

        LoginService loginService = LoginService.retrofit.create(LoginService.class);
        final Call<JsonObject> call = loginService.userRegister(
                userModel.first_name,
                userModel.last_name,
                userModel.country_code,
                userModel.phone,
                userModel.email,
                userModel.password,
                userModel.password,
                userModel.account_type,
                userModel.dob,
                userModel.fb_id,
                userModel.google_id
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
                            SharedPrefs.setString(SharedPrefs.ACCESS_TOKEN, data.getString("token"));
                            startActivity(new Intent(getActivity(), DashboardActivity.class).putExtra(START_FIRST_TIME, START_FIRST_TIME));
                            getActivity().finish();
                        } else {
                            helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                        }
                    } else {
                        helpers.handleErrorMessage(getActivity(), main.getJSONObject("data"));
                    }
                } catch (JSONException e) {
                    helpers.ToastMessage(getActivity(), e.toString());
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