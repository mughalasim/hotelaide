package com.hotelaide.startup.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.interfaces.LoginInterface;
import com.hotelaide.main.activities.DashboardActivity;
import com.hotelaide.main.models.UserModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.ACCESS_TOKEN;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_FIRST_TIME;

public class SignUpEmailFragment extends Fragment {
    private View rootview;
    private Helpers helpers;

    private TextView
            txt_user_dob;

    private MaterialButton
            btn_confirm;

    private CountryCodePicker ccp_user_country_code;

    private EditText
            et_user_first_name,
            et_user_last_name,
            et_user_email,
            et_user_phone,
            et_user_pass,
            et_user_pass_confirm;

    private Spinner
            spinner_user_gender;

    private final String
            TAG_LOG = "FRAGMENT SIGN UP EMAIL";

    public SignUpEmailFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.frag_startup_signup_email, container, false);

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

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        btn_confirm = rootview.findViewById(R.id.btn_confirm);

        btn_confirm.setVisibility(View.VISIBLE);

        btn_confirm.setText(getString(R.string.nav_sign_up));

        et_user_first_name = rootview.findViewById(R.id.et_user_first_name);
        et_user_last_name = rootview.findViewById(R.id.et_user_last_name);
        et_user_email = rootview.findViewById(R.id.et_user_email);

        et_user_phone = rootview.findViewById(R.id.et_user_phone);
        ccp_user_country_code = rootview.findViewById(R.id.ccp_user_country_code);
        ccp_user_country_code.registerCarrierNumberEditText(et_user_phone);

        et_user_pass = rootview.findViewById(R.id.et_user_pass);
        et_user_pass_confirm = rootview.findViewById(R.id.et_user_pass_confirm);
        txt_user_dob = rootview.findViewById(R.id.txt_user_dob);
        spinner_user_gender = rootview.findViewById(R.id.spinner_user_gender);
        if (getActivity() != null)
            spinner_user_gender.setAdapter(new ArrayAdapter<>(
                    getActivity(),
                    R.layout.list_item_spinner,
                    getResources().getStringArray(R.array.select_gender)
            ));

        setDates();

    }

    private void setListeners() {

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpers.validateEmptyEditText(et_user_first_name) &&
                        helpers.validateEmptyEditText(et_user_last_name) &&
                        helpers.validateEmail(et_user_email) &&
                        helpers.validateEmptyEditText(et_user_pass) &&
                        helpers.validateEmptyEditText(et_user_pass_confirm) &&
                        helpers.validateEmptyTextView(txt_user_dob, "Enter your Date of Birth") &&
                        helpers.validateEmptyEditText(et_user_phone)) {
                    if (!et_user_pass.getText().toString().equals(et_user_pass_confirm.getText().toString())) {
                        helpers.ToastMessage(getContext(), "Password and Confirm password do not match");
                    } else if (et_user_pass.getText().toString().length() < 8) {
                        helpers.ToastMessage(getContext(), "Password too short");
                    } else if (!ccp_user_country_code.isValidFullNumber()) {
                        helpers.ToastMessage(getContext(), "Phone number is invalid");
                    } else {
                        setToModelFromFields();
                    }
                }
            }
        });

    }

    private void setDates() {

        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                String year = String.valueOf(selectedYear);
                String month, day;

                if (selectedMonth < 9) {
                    month = "0" + String.valueOf(selectedMonth + 1);
                } else {
                    month = String.valueOf(selectedMonth + 1);
                }

                if (selectedDay < 10) {
                    day = "0" + String.valueOf(selectedDay);
                } else {
                    day = String.valueOf(selectedDay);
                }

                txt_user_dob.setText(day.concat(getString(R.string.txt_date_separator)).concat(month).concat(getString(R.string.txt_date_separator)).concat(year));
            }
        };


        txt_user_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    DatePickerDialog datePicker = new DatePickerDialog(
                            getActivity(), AlertDialog.THEME_HOLO_LIGHT,
                            datePickerListener,
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH));
                    datePicker.setCancelable(false);
                    datePicker.setTitle("Set Date Of Birth");
                    datePicker.show();
                    datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                    datePicker.show();
                }
            }
        });

    }

    private void setToModelFromFields() {
        UserModel userModel = new UserModel();
        userModel.first_name = et_user_first_name.getText().toString();
        userModel.last_name = et_user_last_name.getText().toString();
        userModel.country_code = ccp_user_country_code.getSelectedCountryCodeAsInt();
        userModel.phone = Integer.parseInt(et_user_phone.getText().toString());
        userModel.email = et_user_email.getText().toString();
        userModel.password = et_user_pass.getText().toString();
        userModel.dob = txt_user_dob.getText().toString();
        userModel.fb_id = "";
        userModel.google_id = "";
        userModel.gender = spinner_user_gender.getSelectedItemPosition();

        logRegModel(userModel);

        asyncRegister(userModel);
    }

    private void logRegModel(UserModel userModel) {
        Helpers.logThis(TAG_LOG,
                "\n\n First name: " + userModel.first_name
                        + "\n Last name: " + userModel.last_name
                        + "\n Country Code: " + userModel.country_code
                        + "\n Phone Number: " + userModel.phone
                        + "\n Email: " + userModel.email
                        + "\n Pass: " + userModel.password
                        + "\n DOB: " + userModel.dob
                        + "\n FB_ID: " + userModel.fb_id
                        + "\n G_ID: " + userModel.google_id
                        + "\n GENDER: " + userModel.gender
        );

    }

    // REGISTER ASYNC FUNCTIONS ====================================================================
    private void asyncRegister(UserModel userModel) {

        helpers.setProgressDialog(getString(R.string.progress_sign_up));
        logRegModel(userModel);

        LoginInterface loginInterface = LoginInterface.retrofit.create(LoginInterface.class);
        final Call<JsonObject> call = loginInterface.userRegister(
                userModel.first_name,
                userModel.last_name,
                userModel.country_code,
                userModel.phone,
                userModel.email,
                userModel.password,
                userModel.password,
                userModel.dob,
                userModel.fb_id,
                userModel.google_id,
                BuildConfig.ACCOUNT_TYPE,
                userModel.gender
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.dismissProgressDialog();
                try {

                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success") && getActivity() != null) {
                        JSONObject data = main.getJSONObject("data");
                        if (SharedPrefs.setUser(data.getJSONObject("user"))) {
                            SharedPrefs.setString(ACCESS_TOKEN, data.getString("token"));
                            startActivity(new Intent(getActivity(), DashboardActivity.class).putExtra(EXTRA_START_FIRST_TIME, EXTRA_START_FIRST_TIME));
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