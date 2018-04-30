package com.hotelaide.start_up;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.rilixtech.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import com.hotelaide.R;
import com.hotelaide.main_pages.eo_activities.HomeActivity;
import com.hotelaide.services.LoginService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText
            activation_code;

    private CountryCodePicker
            ccp;

    private AppCompatEditText
            user_phone;

    private TextView
            retry,
            passcode1,
            passcode2,
            passcode3,
            passcode4;

    private Helpers
            helper;

    private Database db;

    private LinearLayout
            LL_Phone,
            LL_activation;

    private final String
            TAG_LOG = "LOGIN",
            STR_PHONE = "PHONE",
            STR_ACTIVATION = "ACTIVATION";


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new Helpers(LoginActivity.this);

        db = new Database();

        setContentView(R.layout.activity_login);

        findAllViews();

        setListeners();

        refreshForms(STR_PHONE);

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
        unregisterReceiver(onBroadcastSMSReceived);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        registerReceiver(onBroadcastSMSReceived, new IntentFilter("SMSReceived"));
        super.onResume();
    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        user_phone = findViewById(R.id.user_phone);
        ccp = findViewById(R.id.ccp);
        ccp.registerPhoneNumberTextView(user_phone);

        passcode1 = findViewById(R.id.passcode1);
        passcode2 = findViewById(R.id.passcode2);
        passcode3 = findViewById(R.id.passcode3);
        passcode4 = findViewById(R.id.passcode4);

        retry = findViewById(R.id.retry);
        activation_code = findViewById(R.id.activation_code);

        helper.setDefaultEditTextSelectionMode(user_phone);
        helper.setDefaultEditTextSelectionMode(activation_code);

        LL_Phone = findViewById(R.id.LL_phone);
        LL_activation = findViewById(R.id.LL_activation);


    }

    private void setListeners() {
        activation_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Helpers.LogThis(TAG_LOG, "EDITABLE " + s.toString());

                if (s.toString().length() > 0) {

                    char code[] = s.toString().toCharArray();

                    switch (code.length) {
                        case 1:
                            resetPasscodeNumbers();
                            passcode1.setText(String.valueOf(code[0]));
                            break;

                        case 2:
                            resetPasscodeNumbers();
                            passcode1.setText(String.valueOf(code[0]));
                            passcode2.setText(String.valueOf(code[1]));
                            break;

                        case 3:
                            resetPasscodeNumbers();
                            passcode1.setText(String.valueOf(code[0]));
                            passcode2.setText(String.valueOf(code[1]));
                            passcode3.setText(String.valueOf(code[2]));
                            break;

                        case 4:
                            resetPasscodeNumbers();
                            passcode1.setText(String.valueOf(code[0]));
                            passcode2.setText(String.valueOf(code[1]));
                            passcode3.setText(String.valueOf(code[2]));
                            passcode4.setText(String.valueOf(code[3]));
                            break;
                    }

                } else {
                   resetPasscodeNumbers();
                }
            }
        });

        user_phone.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    verifyPhoneNumber();
                    return true;
                }
                return false;
            }
        });

        activation_code.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    verifyActivationCode();
                    return true;
                }
                return false;
            }
        });


    }

    private void resetPasscodeNumbers(){
        passcode1.setText("");
        passcode2.setText("");
        passcode3.setText("");
        passcode4.setText("");
    }

    private void refreshForms(String refresh) {
        switch (refresh) {
            case STR_PHONE:
                LL_Phone.setVisibility(View.VISIBLE);
                LL_activation.setVisibility(View.GONE);
                break;

            case STR_ACTIVATION:
                startCountDown();
                LL_Phone.setVisibility(View.GONE);
                LL_activation.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void startCountDown() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                retry.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                retry.setText(getString(R.string.txt_retry));
            }

        }.start();
    }


    //LISTEN TO SMS BROADCAST ======================================================================
    private final BroadcastReceiver onBroadcastSMSReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {
            String thesms = i.getStringExtra("SMSbody");
            activation_code.setText(thesms);
            verifyActivationCode();
        }
    };


    // ONCLICK VIEWS ===============================================================================
    public void sendPhoneNumber(View view) {
        verifyPhoneNumber();
    }

    private void verifyPhoneNumber() {
        if (user_phone.getText().toString().length() < 1) {
            user_phone.setError(getString(R.string.error_field_required));
        } else if (!ccp.isValid()) {
            user_phone.setError(getString(R.string.error_field_invalid));
        } else if (helper.validateMobileNumber(user_phone.getText().toString())) {
            user_phone.setError(getString(R.string.error_field_invalid));
        } else {
            if (Database.userModel.phone.equals(user_phone.getText().toString())) {
                helper.ToastMessage(LoginActivity.this, "Welcome Back");
                startUp();
            } else {
                dropDownKeyboard(user_phone);
                asyncSetPhoneNumber(
                        user_phone.getText().toString(),
                        ccp.getSelectedCountryCode()
                );
            }
        }
    }

    public void sendActivationCode(View view) {
        verifyActivationCode();
    }

    private void verifyActivationCode() {
        if (activation_code.getText().toString().length() < 3) {
            helper.ToastMessage(LoginActivity.this, getString(R.string.error_field_invalid));
        } else {
            asyncSetActivationCode(activation_code.getText().toString(),
                    user_phone.getText().toString());
        }
    }

    public void retryLogin(View view) {
        if (retry.getText().toString().equals(getString(R.string.txt_retry))) {
            refreshForms(STR_PHONE);
            activation_code.setText("");
            passcode1.setText("");
            passcode2.setText("");
            passcode3.setText("");
            passcode4.setText("");
        }
    }

    private void dropDownKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void termsAndConditions(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://eatout.co.ke/terms"));
        startActivity(intent);
    }


    // LOGIN ASYNC FUNCTIONS =======================================================================
    private void asyncSetPhoneNumber(final String phone, final String country_code) {

        user_phone.setError(null);

        helper.setProgressDialogMessage("Sending Phone number, please wait...");
        helper.progressDialog(true);

        LoginService loginService = LoginService.retrofit.create
                (LoginService.class);

        final Call<JsonObject> call = loginService.sendPhone(phone, country_code);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    if (main.getBoolean("success")) {
                        Helpers.LogThis(TAG_LOG, main.getString("otp"));
                        refreshForms(STR_ACTIVATION);
                    } else {
                        helper.ToastMessage(LoginActivity.this, "Invalid Phone Number, Please try again");
                    }
                } catch (JSONException e) {
                    helper.ToastMessage(LoginActivity.this, e.toString());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helper.progressDialog(false);
                if (helper.validateInternetConnection()) {
                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.activity_login),
                            getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                    snackBar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                        }
                    });
                    snackBar.show();
                } else {
                    helper.ToastMessage(LoginActivity.this, getString(R.string.error_500));
                }

            }
        });

    }

    private void asyncSetActivationCode(final String otp, final String phone) {

        user_phone.setError(null);

        helper.setProgressDialogMessage("Sending Activation Code, please wait...");
        helper.progressDialog(true);

        LoginService loginService = LoginService.retrofit.create
                (LoginService.class);

        final Call<JsonObject> call = loginService.sendCode(otp, phone);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    if (db.setUser(main)) {
                        startUp();

                    } else {
                        helper.ToastMessage(LoginActivity.this, "Invalid Activation Code, Please try again");
                    }
                } catch (JSONException e) {
                    helper.ToastMessage(LoginActivity.this, e.toString());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helper.progressDialog(false);
                if (helper.validateInternetConnection()) {
                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.activity_login),
                            getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
                    snackBar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                        }
                    });
                    snackBar.show();
                } else {
                    helper.ToastMessage(LoginActivity.this, getString(R.string.error_500));
                }

            }
        });

    }

    private void startUp() {
        finish();
        if (db.validateUserName()) {
            startActivity(new Intent(LoginActivity.this, SetLocation.class));
        } else {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
    }

}

