package com.hotelaide.main_pages.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.text.method.PasswordTransformationMethod;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.services.UserService;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.SharedPrefs.USER_ID;


public class ChangePasswordFragment extends Fragment {

    private View rootview;

    private Helpers helpers;

    private final String
            TAG_LOG = "CHANGE PASSWORD",
            TAG_PASS_HIDDEN = "0",
            TAG_PASS_SHOWN = "1";

    private ImageView img_user_pass_toggle;

    private EditText
            et_user_pass_old,
            et_user_pass_new,
            et_user_pass_confirm;

    private FloatingActionButton btn_update;

    public ChangePasswordFragment() {}


    // OVERRIDE FUNCTIONS ==========================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.fragment_change_password, container, false);

                helpers = new Helpers(getActivity());

                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

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


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        btn_update = rootview.findViewById(R.id.btn_update);

        et_user_pass_old = rootview.findViewById(R.id.et_user_pass_old);
        et_user_pass_new = rootview.findViewById(R.id.et_user_pass_new);
        et_user_pass_confirm = rootview.findViewById(R.id.et_user_pass_confirm);

        img_user_pass_toggle = rootview.findViewById(R.id.img_user_pass_toggle);
        img_user_pass_toggle.setTag(TAG_PASS_HIDDEN);
        img_user_pass_toggle.setImageResource(R.drawable.ic_pass_hide);
    }

    private void setListeners() {
        img_user_pass_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_user_pass_toggle.getTag().toString().equals(TAG_PASS_HIDDEN)) {
                    img_user_pass_toggle.setImageResource(R.drawable.ic_pass_show);
                    img_user_pass_toggle.setTag(TAG_PASS_SHOWN);
                    et_user_pass_old.setTransformationMethod(null);
                    et_user_pass_new.setTransformationMethod(null);
                    et_user_pass_confirm.setTransformationMethod(null);
                } else {
                    img_user_pass_toggle.setImageResource(R.drawable.ic_pass_hide);
                    img_user_pass_toggle.setTag(TAG_PASS_HIDDEN);
                    et_user_pass_old.setTransformationMethod(new PasswordTransformationMethod());
                    et_user_pass_new.setTransformationMethod(new PasswordTransformationMethod());
                    et_user_pass_confirm.setTransformationMethod(new PasswordTransformationMethod());
                }
                helpers.animateWobble(img_user_pass_toggle);
            }
        });


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                et_user_pass_old.setError(null);
                et_user_pass_new.setError(null);
                et_user_pass_confirm.setError(null);

                if (et_user_pass_old.getText().toString().length() < 8) {
                    et_user_pass_old.setError(getString(R.string.error_field_length));

                } else if (et_user_pass_new.getText().toString().length() < 8) {
                    et_user_pass_new.setError(getString(R.string.error_field_length));

                } else if (et_user_pass_confirm.getText().toString().length() < 8) {
                    et_user_pass_confirm.setError(getString(R.string.error_field_length));

                } else if (!et_user_pass_new.getText().toString().equals(et_user_pass_confirm.getText().toString())) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_pass));

                } else {
                    asyncUpdatePassword();
                }
            }

        });
    }

    // ASYNC UPDATE PASSWORD =======================================================================
    private void asyncUpdatePassword() {
        UserService userService = UserService.retrofit.create(UserService.class);

        Call<JsonObject> call = userService.updateUserPassword(
                SharedPrefs.getInt(USER_ID),
                et_user_pass_old.getText().toString(),
                et_user_pass_new.getText().toString(),
                et_user_pass_confirm.getText().toString()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    Helpers.LogThis(TAG_LOG, main.toString());
                    if (main.getBoolean("success")) {
                        helpers.ToastMessage(getActivity(), main.getString("message"));
                    }
                } catch (JSONException e) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
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