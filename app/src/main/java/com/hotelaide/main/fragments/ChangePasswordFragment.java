package com.hotelaide.main.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.UserInterface;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.USER_ID;


public class ChangePasswordFragment extends Fragment {

    private View root_view;
    private Helpers helpers;
    private final String
            TAG_LOG = "CHANGE PASSWORD";
    private EditText
            et_user_pass_old,
            et_user_pass_new,
            et_user_pass_confirm;
    private FloatingActionButton btn_update;

    public ChangePasswordFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_profile_change_password, container, false);

                helpers = new Helpers(getActivity());



                findAllViews();

                setListeners();


            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        btn_update = root_view.findViewById(R.id.btn_update);

        et_user_pass_old = root_view.findViewById(R.id.et_user_pass_old);
        et_user_pass_new = root_view.findViewById(R.id.et_user_pass_new);
        et_user_pass_confirm = root_view.findViewById(R.id.et_user_pass_confirm);

    }

    private void setListeners() {
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
        UserInterface userInterface = UserInterface.retrofit.create(UserInterface.class);

        Call<JsonObject> call = userInterface.updateUserPassword(
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