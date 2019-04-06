package com.hotelaide.startup.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.LoginInterface;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ResetPasswordFragment extends Fragment {

    private View rootview;

    private MaterialButton
            btn_confirm;

    private EditText et_user_email;

    private Helpers helpers;

    private final String TAG_LOG = "RESET PASSWORD";

    public ResetPasswordFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.frag_startup_forgot_pass, container, false);
                helpers = new Helpers(getActivity());

                findAllViews();

                setListeners();

                HelpersAsync.setTrackerPage(TAG_LOG);

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(rootview);
        }
        return rootview;
    }

    private void findAllViews() {
        et_user_email = rootview.findViewById(R.id.et_user_email);
        btn_confirm = rootview.findViewById(R.id.btn_confirm);
        btn_confirm.setText(getString(R.string.txt_reset));
    }

    private void setListeners() {

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpers.validateEmail(et_user_email)) {
                    asyncResetPassword(et_user_email.getText().toString());
                }
            }
        });
    }


    // RESET ASYNC FUNCTION ========================================================================
    private void asyncResetPassword(String email) {

        helpers.setProgressDialog("Sending Reset link, please wait...");

        LoginInterface.retrofit.create(LoginInterface.class)
                .resetPassword(email).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.dismissProgressDialog();
                try {

                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success") && getActivity() != null) {
                        helpers.myDialog(getResources().getString(R.string.app_name), main.getString("message"));
                        et_user_email.setText("");
                        HelpersAsync.setTrackerEvent(TAG_LOG, true);
                    } else {
                        helpers.myDialog(getResources().getString(R.string.app_name), main.getString("message"));
                        HelpersAsync.setTrackerEvent(TAG_LOG, false);
                    }
                } catch (JSONException e) {
                    helpers.toastMessage(getString(R.string.error_server));
                    HelpersAsync.setTrackerEvent(TAG_LOG, false);
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
                HelpersAsync.setTrackerEvent(TAG_LOG, false);
            }
        });

    }
}