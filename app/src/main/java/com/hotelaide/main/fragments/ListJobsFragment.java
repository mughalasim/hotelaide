package com.hotelaide.main.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotelaide.R;
import com.hotelaide.services.UserService;
import com.hotelaide.utils.Helpers;


public class ListJobsFragment extends Fragment {

    private View root_view;
    private Helpers helpers;
    private final String
            TAG_LOG = "LIST JOBS";


    public ListJobsFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

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


    }

    private void setListeners() {

    }

    // ASYNC UPDATE PASSWORD =======================================================================
    private void asyncUpdatePassword() {
        UserService userService = UserService.retrofit.create(UserService.class);

//        Call<JsonObject> call = userService.updateUserPassword(
//                SharedPrefs.getInt(USER_ID),
//                et_user_pass_old.getText().toString(),
//                et_user_pass_new.getText().toString(),
//                et_user_pass_confirm.getText().toString()
//        );

//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
//                try {
//                    JSONObject main = new JSONObject(String.valueOf(response.body()));
//                    Helpers.LogThis(TAG_LOG, main.toString());
//                    if (main.getBoolean("success")) {
//                        helpers.ToastMessage(getActivity(), main.getString("message"));
//                    }
//                } catch (JSONException e) {
//                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
//                Helpers.LogThis(TAG_LOG, t.toString());
//                if (helpers.validateInternetConnection()) {
//                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
//                } else {
//                    helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
//                }
//
//            }
//        });
    }

}