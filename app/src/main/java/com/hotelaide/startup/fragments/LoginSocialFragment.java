package com.hotelaide.startup.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.LoginInterface;
import com.hotelaide.main.activities.DashboardActivity;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.ACCESS_TOKEN;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_FIRST_TIME;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_RETURN;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;

public class LoginSocialFragment extends Fragment {

    private View rootview;

    private Helpers helpers;

    private final String
            TAG_LOG = "FRAGMENT LOGIN";

    private TextView
            btn_facebook,
            btn_google;

    // FACEBOOK
    private CallbackManager callback_manager;
    private FirebaseAuth fire_base_auth;
    private LoginManager login_manager;

    // GOOGLE
    private GoogleSignInClient google_sign_in_client;
    private int GOOGLE_REQUEST_CODE = 777;

    public LoginSocialFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.frag_startup_login_social, container, false);
                helpers = new Helpers(getActivity());

                findAllViews();

                initializeFacebook(getActivity());

                initializeGoogle(getActivity());

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
        Helpers.logThis(TAG_LOG, "ACTIVITY RESULT " + data.toString() + " : " + requestCode + " : " + resultCode);
        if (requestCode == GOOGLE_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleAccessToken(task);
        } else {
            callback_manager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        // SOCIAL MEDIA LOGIN ====================================================
        btn_google = rootview.findViewById(R.id.btn_google);
        btn_facebook = rootview.findViewById(R.id.btn_facebook);

    }

    private void dropDownKeyboard(EditText editText) {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }


    // FACEBOOK SET UP =============================================================================
    private void initializeFacebook(final Activity activity) {

        fire_base_auth = FirebaseAuth.getInstance();

        callback_manager = CallbackManager.Factory.create();

        login_manager = LoginManager.getInstance();

        login_manager.registerCallback(callback_manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(activity, loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                helpers.toastMessage("Login cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                helpers.toastMessage("Error " + exception);
            }
        });

        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    login_manager.logInWithReadPermissions(LoginSocialFragment.this, Arrays.asList("email", "public_profile"));
                }
            }
        });
    }

    private void handleFacebookAccessToken(final Activity activity, final AccessToken token) {
        helpers.setProgressDialog(getString(R.string.progress_fetch_fb_details));
        Helpers.logThis(TAG_LOG, "Access Token: " + token.getToken());

        if (helpers.validateGooglePlayServices(activity)) {
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            fire_base_auth.signInWithCredential(credential)
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helpers.dismissProgressDialog();
                            helpers.toastMessage("Failed to login with Facebook");
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
                                        Helpers.logThis(TAG_LOG, "FB USER ID: " + user.getUid());
                                        Helpers.logThis(TAG_LOG, "FB PROFILE ID: " + profile.getId());

                                        asyncLogin(user.getEmail(), "", profile.getId(), "");

                                        signOutFaceBook();

                                    } else {
                                        signOutFaceBook();
                                        helpers.toastMessage("Failed to fetch details from Facebook, please try again later1");
                                        Helpers.logThis(TAG_LOG, "USER NULL");
                                    }

                                } catch (NullPointerException e) {
                                    signOutFaceBook();
                                    Helpers.logThis(TAG_LOG, e.toString());
                                    helpers.toastMessage("Failed to fetch details from Facebook, please try again later2");
                                }

                            } else {
                                helpers.toastMessage(getString(R.string.error_unknown));
                                Helpers.logThis(TAG_LOG, task.toString());
                            }

                            helpers.dismissProgressDialog();

                        }
                    });

        } else {
            helpers.dismissProgressDialog();
            helpers.toastMessage("Failed to fetch details from Facebook, Please update your Google Play Services");
            signOutFaceBook();
        }
    }

    private void signOutFaceBook() {
//        fire_base_auth.signOut();
//        LoginManager.getInstance().logOut();
    }

    // GOOGLE SET UP ===============================================================================
    private void initializeGoogle(final Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        google_sign_in_client = GoogleSignIn.getClient(activity, gso);
        btn_google.setOnClickListener(new View.OnClickListener() {
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
            if (account != null) {
                asyncLogin(account.getEmail(), "", "", account.getId());
            } else {
                helpers.toastMessage(getResources().getString(R.string.error_unknown));
            }
            signOutGoogle();

        } catch (ApiException e) {
            Helpers.logThis(TAG_LOG, "signInResult : CODE: " + e.getStatusCode());
            helpers.toastMessage(getResources().getString(R.string.error_sign_in_cancelled));
        }
    }

    private void signOutGoogle() {
//        google_sign_in_client.signOut();
    }


    // LOGIN ASYNC FUNCTIONS =======================================================================
    private void asyncLogin(final String email,
                            final String password,
                            final String fb_id,
                            final String google_id) {

        Helpers.logThis(TAG_LOG, email);
        Helpers.logThis(TAG_LOG, password);
        Helpers.logThis(TAG_LOG, fb_id);
        Helpers.logThis(TAG_LOG, google_id);

        helpers.setProgressDialog("Logging you in, please wait...");

        LoginInterface loginInterface = LoginInterface.retrofit.create(LoginInterface.class);
        final Call<JsonObject> call = loginInterface.userLogin(
                email,
                password,
                fb_id,
                google_id
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    Helpers.logThis(TAG_LOG, response.toString());

                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    if (main.getBoolean("success") && getActivity() != null) {
                        JSONObject data = main.getJSONObject("data");
                        if (SharedPrefs.setUser(data.getJSONObject("user"))) {
                            SharedPrefs.setString(ACCESS_TOKEN, data.getString("token"));
                            if (SharedPrefs.getString(USER_F_NAME).equals("")) {
                                startActivity(new Intent(getActivity(), DashboardActivity.class)
                                        .putExtra(EXTRA_START_FIRST_TIME, EXTRA_START_FIRST_TIME));
                            } else {
                                startActivity(new Intent(getActivity(), DashboardActivity.class)
                                        .putExtra(EXTRA_START_RETURN, EXTRA_START_RETURN));
                            }
                            getActivity().finish();
                        } else {
                            helpers.toastMessage(getString(R.string.error_invalid_user));
                        }
                    } else {
                        helpers.handleErrorMessage(getActivity(), main.getJSONObject("data"));
                    }

                    helpers.dismissProgressDialog();

                } catch (JSONException e) {
                    helpers.toastMessage(getString(R.string.error_server));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.dismissProgressDialog();
                Helpers.logThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.toastMessage(getString(R.string.error_server));
                } else {
                    helpers.toastMessage(getString(R.string.error_connection));
                }

            }
        });

    }

}