package com.hotelaide.main_pages.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.text.method.PasswordTransformationMethod;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hotelaide.R;
import com.hotelaide.main_pages.activities.MyAccountActivity;
import com.hotelaide.utils.Helpers;

import java.util.Calendar;
import java.util.TimeZone;


public class StartUpSignUpFragment extends Fragment {

    private View rootview;

    private TextView
            txt_user_dob,
            btn_confirm,
            btn_cancel;

    private RadioGroup radio_group;

    private RadioButton
            radio_btn_job_seeker,
            radio_btn_employer;

    private ImageView img_user_pass_toggle;

    private EditText
            et_user_first_name,
            et_user_last_name,
            et_user_email,
            et_user_pass,
            et_user_pass_confirm;


    private Helpers helpers;

    private final String
            TAG_LOG = "FRAGMENT SIGN UP",
            TAG_PASS_HIDDEN = "0",
            TAG_PASS_SHOWN = "1";

    public StartUpSignUpFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.fragment_startup_signup, container, false);
                helpers = new Helpers(getActivity());

                findAllViews();

                setListerners();

                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(rootview);
        }
        return rootview;
    }

    private void findAllViews() {
        btn_cancel = rootview.findViewById(R.id.btn_cancel);
        btn_confirm = rootview.findViewById(R.id.btn_confirm);

        btn_cancel.setVisibility(View.GONE);
        btn_confirm.setVisibility(View.VISIBLE);

        btn_confirm.setText(getString(R.string.nav_sign_up));

        et_user_first_name = rootview.findViewById(R.id.et_user_first_name);
        et_user_last_name = rootview.findViewById(R.id.et_user_last_name);
        et_user_email = rootview.findViewById(R.id.et_user_email);
        et_user_pass = rootview.findViewById(R.id.et_user_pass);
        et_user_pass_confirm = rootview.findViewById(R.id.et_user_pass_confirm);
        txt_user_dob = rootview.findViewById(R.id.txt_user_dob);

        img_user_pass_toggle = rootview.findViewById(R.id.img_user_pass_toggle);
        img_user_pass_toggle.setTag(TAG_PASS_HIDDEN);
        img_user_pass_toggle.setImageResource(R.drawable.ic_pass_hide);

        setDates();

        radio_group = rootview.findViewById(R.id.radio_group);

    }

    private void setListerners() {

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedRadioBtn = radio_group.getCheckedRadioButtonId();
                if (selectedRadioBtn != R.id.radio_btn_employer && selectedRadioBtn != R.id.radio_btn_job_seeker) {
                    helpers.myDialog(getContext(), "Alert!",
                            "Please set whether you are applying as a Job Seeker or Employer.");
                } else if (
                        helpers.validateEmptyEditText(et_user_first_name) &&
                                helpers.validateEmptyEditText(et_user_last_name) &&
                                helpers.validateEmail(et_user_email) &&
                                helpers.validateEmptyEditText(et_user_pass) &&
                                helpers.validateEmptyEditText(et_user_pass_confirm) &&
                                helpers.validateEmptyTextView(txt_user_dob, "Enter your Date of Birth")
                        ) {
                    if (!et_user_pass.getText().toString().equals(et_user_pass_confirm.getText().toString())) {
                        helpers.ToastMessage(getContext(), "Password and Confirm password do not match");
                    } else if (et_user_pass.getText().toString().length() < 8) {
                        helpers.ToastMessage(getContext(), "Password too short");
                    } else {
                        helpers.ToastMessage(getContext(), "REGISTRATION IN PROGRESS");
                    }
                }
            }
        });


        img_user_pass_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_user_pass_toggle.getTag().toString().equals(TAG_PASS_HIDDEN)) {
                    img_user_pass_toggle.setImageResource(R.drawable.ic_pass_show);
                    img_user_pass_toggle.setTag(TAG_PASS_SHOWN);
                    et_user_pass.setTransformationMethod(null);
                    et_user_pass_confirm.setTransformationMethod(null);
                } else {
                    img_user_pass_toggle.setImageResource(R.drawable.ic_pass_hide);
                    img_user_pass_toggle.setTag(TAG_PASS_HIDDEN);
                    et_user_pass.setTransformationMethod(new PasswordTransformationMethod());
                    et_user_pass_confirm.setTransformationMethod(new PasswordTransformationMethod());
                }
                helpers.animate_wobble(img_user_pass_toggle);
            }
        });


        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_btn_job_seeker) {

                } else {

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

                txt_user_dob.setText(year.concat("-").concat(month).concat("-").concat(day));
            }
        };


        txt_user_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() != null) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                            R.style.AppTheme, datePickerListener,
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

}