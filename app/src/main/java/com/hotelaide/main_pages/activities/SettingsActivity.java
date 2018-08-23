package com.hotelaide.main_pages.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

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

import static com.hotelaide.utils.SharedPrefs.ALLOW_UPDATE_APP;
import static com.hotelaide.utils.SharedPrefs.USER_ID;

public class SettingsActivity extends ParentActivity {


    String TAG_LOG = "SETTINGS";

    private Switch
            switch_app_updates;

    private TextView delete_account;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        initialize(R.id.drawer_settings, TAG_LOG);

        findAllViews();


    }

    @Override
    protected void onStart() {
        super.onStart();
        setListeners();
    }


    // BASIC METHODS ===============================================================================
    private void findAllViews() {
        switch_app_updates = findViewById(R.id.switch_app_updates);
        delete_account = findViewById(R.id.delete_account);
    }

    private void setListeners() {
        switch_app_updates.setChecked(SharedPrefs.getBool(ALLOW_UPDATE_APP));
        switch_app_updates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefs.setBool(ALLOW_UPDATE_APP, isChecked);
            }
        });

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(SettingsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_confirm);
                final TextView txt_message = dialog.findViewById(R.id.txt_message);
                final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
                final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                final TextView txt_title = dialog.findViewById(R.id.txt_title);
                txt_title.setText(R.string.txt_alert);
                txt_message.setText(R.string.txt_delete_account);
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        asyncDeleteUser();
                        dialog.cancel();
                    }
                });
                btn_cancel.setVisibility(View.VISIBLE);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }

    // ASYNC DELETE USER ===========================================================================
    public void asyncDeleteUser() {
        helpers.setProgressDialogMessage("Deleting Account, Please wait...");
        helpers.progressDialog(true);

        UserService userService = UserService.retrofit.create(UserService.class);
        final Call<JsonObject> call = userService.deleteUser(SharedPrefs.getInt(USER_ID));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        Helpers.sessionExpiryBroadcast();
                    }

                } catch (JSONException e) {
                    helpers.ToastMessage(SettingsActivity.this, getString(R.string.error_server));
                    Helpers.LogThis(TAG_LOG, e.toString());

                } catch (Exception e) {
                    helpers.ToastMessage(SettingsActivity.this, getString(R.string.error_server));
                    Helpers.LogThis(TAG_LOG, e.toString());
                }
                helpers.progressDialog(false);
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.progressDialog(false);
                Helpers.LogThis(TAG_LOG, t.toString());
                Helpers.LogThis(TAG_LOG, call.toString());
            }

        });
    }

}
