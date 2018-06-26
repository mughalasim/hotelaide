package com.hotelaide.main_pages.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.main_pages.models.UserModel;
import com.hotelaide.services.UserService;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.rilixtech.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.SharedPrefs.USER_COUNTRY_CODE;
import static com.hotelaide.utils.SharedPrefs.USER_DOB;
import static com.hotelaide.utils.SharedPrefs.USER_EMAIL;
import static com.hotelaide.utils.SharedPrefs.USER_F_NAME;
import static com.hotelaide.utils.SharedPrefs.USER_ID;
import static com.hotelaide.utils.SharedPrefs.USER_L_NAME;
import static com.hotelaide.utils.SharedPrefs.USER_PHONE;

public class ProfileUpdateFragment extends Fragment implements View.OnClickListener {

    private View rootview;

    private Helpers helpers;

    private final String
            TAG_LOG = "PROFILE UPDATE",
            TAG_PASS_HIDDEN = "0",
            TAG_PASS_SHOWN = "1";

    private EditText
            et_user_first_name,
            et_user_last_name,
            et_user_email,
            et_user_phone;

    private TextView
            btn_confirm,
            btn_cancel,
            txt_user_first_name,
            txt_user_last_name,
            txt_user_email,
            txt_user_country_code,
            txt_user_phone,
            txt_user_dob;

    private LinearLayout ll_profile_update;


    private CountryCodePicker ccp_user_country_code;

    public ProfileUpdateFragment() {

    }


    // OVERRIDE METHODS ============================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.fragment_profile_update, container, false);

                helpers = new Helpers(getActivity());

                findAllViews();

                setFromSharedPrefs();

                hideEditTexts();

                setListeners();

                dropDownKeyboard(et_user_first_name);

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(rootview);
        }
        return rootview;
    }

    @Override
    public void onClick(View v) {
        hideEditTexts();
        switch (v.getId()) {
            case R.id.txt_user_f_name:
                et_user_first_name.setVisibility(View.VISIBLE);
                validateEmptyEditText(et_user_first_name, USER_F_NAME, true);
                helpers.animateFadeIn(et_user_first_name);
                break;

            case R.id.txt_user_l_name:
                et_user_last_name.setVisibility(View.VISIBLE);
                validateEmptyEditText(et_user_last_name, USER_L_NAME, true);
                helpers.animateFadeIn(et_user_last_name);
                break;

            case R.id.txt_user_email:
                et_user_email.setVisibility(View.VISIBLE);
                validateEmptyEditText(et_user_email, USER_EMAIL, true);
                helpers.animateFadeIn(et_user_email);
                break;

            case R.id.txt_user_phone:
                et_user_phone.setVisibility(View.VISIBLE);
                validateEmptyEditText(et_user_phone, USER_PHONE, false);
                helpers.animateFadeIn(et_user_phone);
                break;

            case R.id.txt_user_country_code:
                ccp_user_country_code.setVisibility(View.VISIBLE);
                helpers.animateFadeIn(ccp_user_country_code);
                break;

            case R.id.btn_confirm:
                UserModel userModel = new UserModel();
                userModel.id = SharedPrefs.getInt(USER_ID);
                userModel.first_name = txt_user_first_name.getText().toString();
                userModel.last_name = txt_user_last_name.getText().toString();
                userModel.email = txt_user_email.getText().toString();
                userModel.country_code = ccp_user_country_code.getSelectedCountryCodeAsInt();
                userModel.phone = Integer.parseInt(txt_user_phone.getText().toString());
                if (!txt_user_dob.getText().toString().equals(getString(R.string.txt_not_set))) {
                    userModel.dob = txt_user_dob.getText().toString();
                }
                asyncUpdateDetails(userModel);
                break;

        }
    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        ll_profile_update = rootview.findViewById(R.id.ll_profile_update);

        txt_user_first_name = rootview.findViewById(R.id.txt_user_f_name);
        txt_user_last_name = rootview.findViewById(R.id.txt_user_l_name);
        txt_user_email = rootview.findViewById(R.id.txt_user_email);
        txt_user_country_code = rootview.findViewById(R.id.txt_user_country_code);
        txt_user_phone = rootview.findViewById(R.id.txt_user_phone);
        txt_user_dob = rootview.findViewById(R.id.txt_user_dob);

        btn_cancel = rootview.findViewById(R.id.btn_cancel);
        btn_confirm = rootview.findViewById(R.id.btn_confirm);
        btn_cancel.setVisibility(View.GONE);
        btn_confirm.setText(getString(R.string.txt_update_details));

        setDates();

        ll_profile_update.setOnClickListener(this);

        txt_user_first_name.setOnClickListener(this);
        txt_user_last_name.setOnClickListener(this);
        txt_user_email.setOnClickListener(this);
        txt_user_country_code.setOnClickListener(this);
        txt_user_phone.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

        et_user_first_name = rootview.findViewById(R.id.et_user_first_name);
        et_user_last_name = rootview.findViewById(R.id.et_user_last_name);
        et_user_email = rootview.findViewById(R.id.et_user_email);

        et_user_phone = rootview.findViewById(R.id.et_user_phone);
        ccp_user_country_code = rootview.findViewById(R.id.ccp_user_country_code);
        ccp_user_country_code.registerPhoneNumberTextView(et_user_phone);

        txt_user_dob = rootview.findViewById(R.id.txt_user_dob);


    }

    private void setFromSharedPrefs() {
        txt_user_first_name.setText(SharedPrefs.getString(USER_F_NAME));
        txt_user_last_name.setText(SharedPrefs.getString(USER_L_NAME));
        txt_user_email.setText(SharedPrefs.getString(USER_EMAIL));
        txt_user_phone.setText(String.valueOf(SharedPrefs.getInt(USER_PHONE)));
        txt_user_country_code.setText(String.valueOf(SharedPrefs.getInt(USER_COUNTRY_CODE)));
        if (SharedPrefs.getString(USER_DOB).equals("null")) {
            txt_user_dob.setText(getString(R.string.txt_not_set));
        } else {
            txt_user_dob.setText(SharedPrefs.getString(USER_DOB));
        }

        // SET TO EDT TEXTS
        et_user_first_name.setText(SharedPrefs.getString(USER_F_NAME));
        et_user_last_name.setText(SharedPrefs.getString(USER_L_NAME));
        et_user_email.setText(SharedPrefs.getString(USER_EMAIL));
        et_user_phone.setText(String.valueOf(SharedPrefs.getInt(USER_PHONE)));
        ccp_user_country_code.setCountryForPhoneCode(SharedPrefs.getInt(USER_COUNTRY_CODE));

    }

    private void setListeners() {
        // ADD TEXT WATCHERS
        addTextChangeListener(et_user_first_name, txt_user_first_name, USER_F_NAME, true);
        addTextChangeListener(et_user_last_name, txt_user_last_name, USER_L_NAME, true);
        addTextChangeListener(et_user_email, txt_user_email, USER_EMAIL, true);
        addTextChangeListener(et_user_phone, txt_user_phone, USER_PHONE, false);

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

                txt_user_dob.setText(day.concat("-").concat(month).concat("-").concat(year));
            }
        };


        txt_user_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                            AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, datePickerListener,
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

    public void hideEditTexts() {
        et_user_first_name.setVisibility(View.GONE);
        et_user_last_name.setVisibility(View.GONE);
        et_user_email.setVisibility(View.GONE);
        et_user_phone.setVisibility(View.GONE);
        ccp_user_country_code.setVisibility(View.GONE);
        txt_user_country_code.setText(ccp_user_country_code.getSelectedCountryCode());
    }

    private void validateEmptyEditText(
            final EditText editText,
            final String sharedPrefName,
            final boolean isString) {
        if (editText.getText().toString().length() < 1) {
            if (isString) {
                editText.setText(SharedPrefs.getString(sharedPrefName));
            } else {
                editText.setText(String.valueOf(SharedPrefs.getInt(sharedPrefName)));
            }
        }
    }

    private void addTextChangeListener(
            EditText editText,
            final TextView textView,
            final String sharedPrefName,
            final boolean isString) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    textView.setText(s.toString());
                } else {
                    if (isString) {
                        textView.setText(SharedPrefs.getString(sharedPrefName));
                    } else {
                        textView.setText(String.valueOf(SharedPrefs.getInt(sharedPrefName)));
                    }
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

        helpers.setProgressDialogMessage("Updating profile, please wait...");
        helpers.progressDialog(true);

        UserService userService = UserService.retrofit.create(UserService.class);
        final Call<JsonObject> call = userService.setUserDetails(
                userModel.id,
                userModel.first_name,
                userModel.last_name,
                userModel.country_code,
                userModel.phone,
                userModel.email,
                userModel.geo_lat,
                userModel.geo_lng,
                userModel.dob,
                userModel.fb_id,
                userModel.google_id
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
                            setFromSharedPrefs();

                        } else {
                            helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                        }
                    } else {
                        helpers.handleErrorMessage(getActivity(), main.getJSONObject("data"));
                    }

                } catch (JSONException e) {
                    helpers.ToastMessage(getActivity(), e.toString());
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