package com.hotelaide.start_up;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.services.LoginService;
import com.hotelaide.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StartUpForgotPassFragment extends Fragment {

    private View rootview;

    private MaterialButton
            btn_confirm;

    private EditText et_user_email;

    private Helpers helpers;

    private final String TAG_LOG = "FRAGMENT LOGIN";

    public StartUpForgotPassFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.fragment_startup_forgot_pass, container, false);
                helpers = new Helpers(getActivity());

                findAllViews();

                setListeners();



            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(rootview);
        }
        return rootview;
    }

    private void findAllViews() {
        et_user_email = rootview.findViewById(R.id.user_email);
        btn_confirm = rootview.findViewById(R.id.btn_confirm);
        btn_confirm.setText(getString(R.string.txt_reset));
    }

    private void setListeners() {

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpers.validateEmail(et_user_email)){
                    asyncResetPassword(et_user_email.getText().toString());
                }
            }
        });
    }


    // RESET ASYNC FUNCTION ========================================================================
    private void asyncResetPassword(String email) {

        helpers.setProgressDialogMessage("Sending Reset link, please wait...");
        helpers.progressDialog(true);

        LoginService loginService = LoginService.retrofit.create(LoginService.class);
        final Call<JsonObject> call = loginService.resetPassword(email);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.progressDialog(false);
                try {

                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success") && getActivity() != null) {
                        helpers.myDialog(getActivity(), getResources().getString(R.string.app_name), main.getString("message"));
                        et_user_email.setText("");
                    } else {
                        helpers.myDialog(getActivity(), getResources().getString(R.string.app_name), main.getString("message"));
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