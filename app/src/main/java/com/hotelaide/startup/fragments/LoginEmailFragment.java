package com.hotelaide.startup.fragments;

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

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.LoginInterface;
import com.hotelaide.main.activities.DashboardActivity;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.ACCESS_TOKEN;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_FIRST_TIME;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_RETURN;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;

public class LoginEmailFragment extends Fragment {

    private View rootview;

    private MaterialButton
            btn_confirm;

    private EditText
            et_user_pass,
            et_user_email;

    private Helpers helpers;

    private final String
            TAG_LOG = "FRAGMENT LOGIN";

    public LoginEmailFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.frag_startup_login_email, container, false);
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
    private void asyncLogin(final String email,
                            final String password) {

        Helpers.logThis(TAG_LOG, email);
        Helpers.logThis(TAG_LOG, password);

        helpers.setProgressDialog("Validating your credentials, please wait...");

        LoginInterface loginInterface = LoginInterface.retrofit.create(LoginInterface.class);
        final Call<JsonObject> call = loginInterface.userLogin(
                email,
                password,
                "",
                ""
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
                                startActivity(new Intent(getActivity(), DashboardActivity.class).putExtra(EXTRA_START_FIRST_TIME, EXTRA_START_FIRST_TIME));
                            } else {
                                startActivity(new Intent(getActivity(), DashboardActivity.class).putExtra(EXTRA_START_RETURN, EXTRA_START_RETURN));
                            }
                            getActivity().finish();
                        } else {
                            helpers.ToastMessage(getActivity(), getString(R.string.error_invalid_user));
                        }
                    } else {
                        helpers.handleErrorMessage(getActivity(), main.getJSONObject("data"));
                    }

                    helpers.dismissProgressDialog();

                } catch (JSONException e) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.dismissProgressDialog();
                Helpers.logThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                } else {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                }

            }
        });

    }

}