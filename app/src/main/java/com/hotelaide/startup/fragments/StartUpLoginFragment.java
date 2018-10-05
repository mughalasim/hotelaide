package com.hotelaide.startup.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.main.activities.DashboardActivity;
import com.hotelaide.services.LoginService;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.ACCESS_TOKEN;
import static com.hotelaide.utils.StaticVariables.START_RETURN;


public class StartUpLoginFragment extends Fragment {

    private View rootview;

    private MaterialButton
            btn_confirm;

    private EditText
            et_user_pass,
            et_user_email;

    private Helpers helpers;

    private final String
            TAG_LOG = "FRAGMENT LOGIN";

    public StartUpLoginFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.frag_startup_login, container, false);
                helpers = new Helpers(getActivity());

                findAllViews();

                setListeners();

                dropDownKeyboard(et_user_email);

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(rootview);
        }
        return rootview;
    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        btn_confirm = rootview.findViewById(R.id.btn_confirm);

        et_user_email = rootview.findViewById(R.id.et_user_email);
        et_user_pass = rootview.findViewById(R.id.et_user_password);

    }

    private void setListeners() {
        btn_confirm.setText(getString(R.string.nav_login));

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpers.validateEmptyEditText(et_user_pass)
                        && helpers.validateEmptyEditText(et_user_email)
                        && helpers.validateEmail(et_user_email)) {
                    SharedPrefs.setString(ACCESS_TOKEN, "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImMzMGFjZTVlMzM1ZWMzODRjYjQ4Zjc5NGRmNzUwOGNmZmViNjQxZjFiYmI0NzJiYmVlYTU3NjIzYzM4YWM0OGVmZTQ4NjA5ZGZlMWEwZTU2In0.eyJhdWQiOiI1IiwianRpIjoiYzMwYWNlNWUzMzVlYzM4NGNiNDhmNzk0ZGY3NTA4Y2ZmZWI2NDFmMWJiYjQ3MmJiZWVhNTc2MjNjMzhhYzQ4ZWZlNDg2MDlkZmUxYTBlNTYiLCJpYXQiOjE1Mzg2NjA4NjAsIm5iZiI6MTUzODY2MDg2MCwiZXhwIjoxNTcwMTk2ODYwLCJzdWIiOiIxMSIsInNjb3BlcyI6W119.mlGOiAZDU8fQP4yIwG7fwnzPNmx1ebfeSlKAJd6LXk6jZQKS2zL9uZwu3eyWN5bgdwxO-oxZWqxYe0yWKLiH2uuboRiSI5daSya_AwtBPB3x-uCXr1ex-LrLsv2xfE93dNbA9EqrFLA7n8dBtJj2lkrkRo19CK5OUMKvHbUMIP-p3ZEqCz5gmv-z92FjwT4zvexn1VzQ48wNkoDEAJ_mcOy-tHMvfn9lxPFFtXT38LVWUdVEYoH0wXYMAVGk8xWPgfgG3WIIa5WfNRrHPUJqvnE0HdF0GynpBc0Xv65dwfQ4V-YbBcJzjaWs4aqQ96ykT1qjw8gmOPjBTxQFMZTmrp3M-ETvLCLIeXxHCC9aqwJkRHmbUcOs5H_xeKYxKjUnuj0sfV1cDWXSyMT8roMxYM1onbDufl-h5iI3SiDebz67S1QyN-Xs1cTqS2JDj5--Agn38MistaMHCQlM8CvoENREXH8EtfJ-KYXLIezJzBqQCpW0FW2fnPNUi0zT1DVkyeQbVXiod0T0JOkhN4VUcQZ0UAabjHQiRy5ji7uiU9f8YDKlrnlvXlZD1QHX6FzJWNgGm96e5U22mbGjwsfvt3P_GQEslPMCFsLsI5ztiDG5BRy-zZag1L44A7Ofm-md5-Sv9dz1Eb6ARAZeIPSB6FaRn2CH1uNTSpDyiQC5Ja8");
                    startActivity(new Intent(getActivity(), DashboardActivity.class).putExtra(START_RETURN, START_RETURN));
                    if (getActivity() != null)
                        getActivity().finish();
//                    asyncLogin(et_user_email.getText().toString(), et_user_pass.getText().toString());
                }
            }
        });

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


    // LOGIN ASYNC FUNCTIONS =======================================================================
    private void asyncLogin(final String email, final String password) {

        helpers.setProgressDialogMessage("Validating your credentials, please wait...");
        helpers.progressDialog(true);

        LoginService loginService = LoginService.retrofit.create(LoginService.class);
        final Call<JsonObject> call = loginService.userLogin(
                email,
                password
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
                            startActivity(new Intent(getActivity(), DashboardActivity.class).putExtra(START_RETURN, START_RETURN));
                            getActivity().finish();
                        } else {
                            helpers.ToastMessage(getActivity(), getString(R.string.error_invalid_user));
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