package com.hotelaide.main.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.UserInterface;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.ALLOW_PUSH_MESSAGES;
import static com.hotelaide.utils.StaticVariables.ALLOW_PUSH_NOTIFICATIONS;
import static com.hotelaide.utils.StaticVariables.ALLOW_PUSH_REMINDERS;
import static com.hotelaide.utils.StaticVariables.ALLOW_UPDATE_APP;
import static com.hotelaide.utils.StaticVariables.USER_ID;

public class SettingsActivity extends ParentActivity {


    String TAG_LOG = "SETTINGS";

    private Switch
            switch_app_updates,
            switch_push_messages,
            switch_push_reminders,
            switch_push_notifications;

    private TextView delete_account;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        initialize(R.id.drawer_settings, getString(R.string.drawer_settings));

        findAllViews();

        HelpersAsync.setTrackerPage(TAG_LOG);

    }

    @Override
    protected void onStart() {
        super.onStart();
        setListeners();
    }


    // BASIC METHODS ===============================================================================
    private void findAllViews() {
        switch_app_updates = findViewById(R.id.switch_app_updates);
        switch_push_notifications = findViewById(R.id.switch_push_notifications);
        switch_push_reminders = findViewById(R.id.switch_push_reminders);
        switch_push_messages = findViewById(R.id.switch_push_messages);
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

        switch_push_notifications.setChecked(SharedPrefs.getBool(ALLOW_PUSH_NOTIFICATIONS));
        switch_push_notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefs.setBool(ALLOW_PUSH_NOTIFICATIONS, isChecked);
            }
        });

        switch_push_reminders.setChecked(SharedPrefs.getBool(ALLOW_PUSH_REMINDERS));
        switch_push_reminders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefs.setBool(ALLOW_PUSH_REMINDERS, isChecked);
            }
        });

        switch_push_messages.setChecked(SharedPrefs.getBool(ALLOW_PUSH_MESSAGES));
        switch_push_messages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefs.setBool(ALLOW_PUSH_MESSAGES, isChecked);
            }
        });

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(SettingsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_confirm);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final TextView txt_message = dialog.findViewById(R.id.txt_message);
                final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
                final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                final TextView txt_title = dialog.findViewById(R.id.txt_title);
                txt_title.setText(R.string.txt_alert);
                txt_message.setText(R.string.txt_delete_account);
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helpers.toastMessage("Coming soon :)");
//                        asyncDeleteUser();
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
        helpers.setProgressDialog("Deleting Account, Please wait...");

        UserInterface.retrofit.create(UserInterface.class)
                .deleteUser(SharedPrefs.getInt(USER_ID)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        Helpers.sessionExpiryBroadcast();
                    }

                } catch (JSONException e) {
                    helpers.toastMessage(getString(R.string.error_server));
                    Helpers.logThis(TAG_LOG, e.toString());

                } catch (Exception e) {
                    helpers.toastMessage(getString(R.string.error_server));
                    Helpers.logThis(TAG_LOG, e.toString());
                }
                helpers.dismissProgressDialog();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.dismissProgressDialog();
                Helpers.logThis(TAG_LOG, t.toString());
                Helpers.logThis(TAG_LOG, call.toString());
            }

        });
    }

}
