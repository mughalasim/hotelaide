package com.hotelaide.main.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.UserInterface;
import com.hotelaide.main.activities.ProfileEditActivity;
import com.hotelaide.main.models.UserModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.rilixtech.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.USER_ABOUT;
import static com.hotelaide.utils.StaticVariables.USER_AVAILABILITY;
import static com.hotelaide.utils.StaticVariables.USER_COUNTRY_CODE;
import static com.hotelaide.utils.StaticVariables.USER_DOB;
import static com.hotelaide.utils.StaticVariables.USER_EMAIL;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_GENDER;
import static com.hotelaide.utils.StaticVariables.USER_ID;
import static com.hotelaide.utils.StaticVariables.USER_L_NAME;
import static com.hotelaide.utils.StaticVariables.USER_PHONE;

public class ProfileUpdateFragment extends Fragment {

    private View root_view;

    private Helpers helpers;

    private final String
            TAG_LOG = "PROFILE UPDATE";

    private EditText
            et_user_first_name,
            et_user_last_name,
            et_user_about,
            et_user_phone;

    private Switch
            switch_availability;

    private Spinner
            spinner_user_gender;

    private TextView
            txt_user_email,
            txt_user_dob;

    FloatingActionButton btn_update;

    private CountryCodePicker ccp_user_country_code;

    public ProfileUpdateFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_profile_info, container, false);

                helpers = new Helpers(getActivity());

                findAllViews();

                setFromSharedPrefs();

                setListeners();

                dropDownKeyboard(et_user_first_name);

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
        txt_user_email = root_view.findViewById(R.id.txt_user_email);
        txt_user_dob = root_view.findViewById(R.id.txt_user_dob);

        btn_update = root_view.findViewById(R.id.btn_update);

        setDates();

        switch_availability = root_view.findViewById(R.id.switch_availability);
        et_user_first_name = root_view.findViewById(R.id.et_user_first_name);
        et_user_last_name = root_view.findViewById(R.id.et_user_last_name);
        et_user_about = root_view.findViewById(R.id.et_user_about);
        spinner_user_gender = root_view.findViewById(R.id.spinner_user_gender);
        txt_user_email = root_view.findViewById(R.id.txt_user_email);
        et_user_phone = root_view.findViewById(R.id.et_user_phone);
        ccp_user_country_code = root_view.findViewById(R.id.ccp_user_country_code);
        ccp_user_country_code.registerPhoneNumberTextView(et_user_phone);
        txt_user_dob = root_view.findViewById(R.id.txt_user_dob);

        if (getActivity() != null)
            spinner_user_gender.setAdapter(new ArrayAdapter<>(
                    getActivity(),
                    R.layout.list_item_spinner,
                    getResources().getStringArray(R.array.select_gender)
            ));

    }

    private void setFromSharedPrefs() {
        switch_availability.setChecked(SharedPrefs.getInt(USER_AVAILABILITY) == 1);
        // SET TO EDIT TEXTS
        et_user_first_name.setText(SharedPrefs.getString(USER_F_NAME));
        et_user_last_name.setText(SharedPrefs.getString(USER_L_NAME));
        et_user_about.setText(SharedPrefs.getString(USER_ABOUT));
        txt_user_email.setText(SharedPrefs.getString(USER_EMAIL));
        et_user_phone.setText(String.valueOf(SharedPrefs.getInt(USER_PHONE)));
        ccp_user_country_code.setCountryForPhoneCode(SharedPrefs.getInt(USER_COUNTRY_CODE));
        // DOB
        if (SharedPrefs.getString(USER_DOB).equals("null")) {
            txt_user_dob.setText(getString(R.string.txt_not_set));
        } else {
            txt_user_dob.setText(helpers.formatDate(SharedPrefs.getString(USER_DOB)));
            txt_user_dob.setTag(SharedPrefs.getString(USER_DOB));
        }
        // GENDER
        spinner_user_gender.setSelection(SharedPrefs.getInt(USER_GENDER));

    }

    private void setListeners() {
        switch_availability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    asyncUpdateUserAvailability(1);
                } else {
                    asyncUpdateUserAvailability(0);
                }
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModel userModel = new UserModel();
                userModel.id = SharedPrefs.getInt(USER_ID);
                userModel.first_name = fetchFromEditText(et_user_first_name);
                userModel.last_name = fetchFromEditText(et_user_last_name);
                userModel.about = fetchFromEditText(et_user_about);
                userModel.email = txt_user_email.getText().toString();
                userModel.country_code = ccp_user_country_code.getSelectedCountryCodeAsInt();
                Helpers.LogThis(TAG_LOG, "GENDER POSITION: " + spinner_user_gender.getSelectedItemPosition());
                userModel.gender = spinner_user_gender.getSelectedItemPosition();

                if (!fetchFromEditText(et_user_phone).equals(""))
                    userModel.phone = Integer.parseInt(fetchFromEditText(et_user_phone));

                if (!txt_user_dob.getText().toString().equals(getString(R.string.txt_not_set))) {
                    userModel.dob = txt_user_dob.getTag().toString();
                }

                userModel.availability = SharedPrefs.getInt(USER_AVAILABILITY);

                asyncUpdateDetails(userModel);
            }
        });
    }

    private String fetchFromEditText(EditText editText) {
        String data = "";
        if (editText.getText().toString().length() > 1) {
            data = editText.getText().toString();
        }
        return data;
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

                String date = helpers.formatDate(day.concat(getString(R.string.txt_date_separator)).concat(month).concat(getString(R.string.txt_date_separator)).concat(year));
                txt_user_dob.setText(date);
                txt_user_dob.setTag(day.concat(getString(R.string.txt_date_separator)).concat(month).concat(getString(R.string.txt_date_separator)).concat(year));
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
                    datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                    datePicker.show();
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


    // ASYNC UPDATE DETAILS ========================================================================
    private void asyncUpdateDetails(final UserModel userModel) {

        helpers.setProgressDialogMessage("Updating your profile, please wait...");
        helpers.progressDialog(true);

        UserInterface userInterface = UserInterface.retrofit.create(UserInterface.class);
        final Call<JsonObject> call = userInterface.setUserDetails(
                userModel.id,
                userModel.first_name,
                userModel.last_name,
                userModel.about,
                userModel.country_code,
                userModel.phone,
                userModel.email,
                userModel.geo_lat,
                userModel.geo_lng,
                userModel.dob,
                userModel.fb_id,
                userModel.google_id,
                userModel.gender
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        if (SharedPrefs.setUser(main.getJSONObject("data"))) {
                            helpers.ToastMessage(getActivity(), main.getString("message"));
                            if (getActivity() != null) {
                                ((ProfileEditActivity) getActivity()).moveViewPagerNext();
                            }
                        } else {
                            helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                        }
                    } else {
                        helpers.handleErrorMessage(getActivity(), main.getJSONObject("data"));
                    }

                    setFromSharedPrefs();

                } catch (JSONException e) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.progressDialog(false);
                Helpers.LogThis(TAG_LOG, t.toString());
                setFromSharedPrefs();
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                } else {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                }

            }
        });

    }

    // ASYNC UPDATE AVAILABILITY ===================================================================
    private void asyncUpdateUserAvailability(final int availability) {
        UserInterface userInterface = UserInterface.retrofit.create(UserInterface.class);
        final Call<JsonObject> call = userInterface.setUserAvailability(
                SharedPrefs.getInt(USER_ID),
                availability);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    if (main.getBoolean("success")) {
                        SharedPrefs.setInt(USER_AVAILABILITY, availability);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (getActivity() != null) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                }
            }
        });

    }


}