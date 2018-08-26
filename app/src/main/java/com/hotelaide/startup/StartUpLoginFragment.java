package com.hotelaide.startup;

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

import static com.hotelaide.utils.Helpers.START_RETURN;


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
                rootview = inflater.inflate(R.layout.fragment_startup_login, container, false);
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
                    asyncLogin(et_user_email.getText().toString(), et_user_pass.getText().toString());
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
                            SharedPrefs.setString(SharedPrefs.ACCESS_TOKEN, data.getString("token"));
                            startActivity(new Intent(getActivity(), DashboardActivity.class).putExtra(START_RETURN, START_RETURN));
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