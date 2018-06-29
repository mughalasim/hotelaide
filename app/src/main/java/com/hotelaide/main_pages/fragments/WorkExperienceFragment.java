package com.hotelaide.main_pages.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.main_pages.models.UserModel;
import com.hotelaide.main_pages.models.WorkExperienceModel;
import com.hotelaide.services.UserService;
import com.hotelaide.services.WorkExperienceService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WorkExperienceFragment extends Fragment {

    private View rootview;
    private Helpers helpers;
    private Database db;
    private final String TAG_LOG = "WORK EXPERIENCE";
    // TOP PANEL ====================================
    private LinearLayout ll_work_experience;
    private RelativeLayout no_list_items;
    private TextView
            txt_no_results,
            btn_add_work_experience;
    // BOTTOM PANEL =================================
    SlidingUpPanelLayout sliding_panel;
    private TextView
            txt_id,
            txt_title,
            txt_start_date,
            txt_end_date,
            btn_cancel,
            btn_confirm;
    private EditText
            et_company_name,
            et_position,
            et_responsibilities;
    private RelativeLayout
            rl_end_date;
    private RadioGroup
            radio_group;
    private RadioButton
            radio_btn_no,
            radio_btn_yes;
    private final String
            STR_DATE_START = "START_DATE",
            STR_DATE_END = "END_DATE";
    private String
            STR_DATE_TYPE = "";
    DatePickerDialog.OnDateSetListener datePickerListener;

    public WorkExperienceFragment() {
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
                rootview = inflater.inflate(R.layout.fragment_work_experience, container, false);
                helpers = new Helpers(getActivity());
                db = new Database();

                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

                findAllViews();

                setListeners();

                setDates();

                populateWorkExperience();

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(rootview);
        }
        return rootview;
    }


    // BASIC METHODS ===============================================================================

    private void findAllViews() {
        // TOP PANEL =============================================================
        ll_work_experience = rootview.findViewById(R.id.ll_work_experience);
        txt_no_results = rootview.findViewById(R.id.txt_no_results);
        no_list_items = rootview.findViewById(R.id.no_list_items);
        btn_add_work_experience = rootview.findViewById(R.id.btn_add_work_experience);
        // BOTTOM PANEL =============================================================
        sliding_panel = rootview.findViewById(R.id.sliding_panel);
        txt_id = rootview.findViewById(R.id.txt_id);
        txt_title = rootview.findViewById(R.id.txt_title);
        txt_start_date = rootview.findViewById(R.id.txt_start_date);
        txt_end_date = rootview.findViewById(R.id.txt_end_date);
        btn_cancel = rootview.findViewById(R.id.btn_cancel);
        btn_confirm = rootview.findViewById(R.id.btn_confirm);
        et_company_name = rootview.findViewById(R.id.et_company_name);
        et_position = rootview.findViewById(R.id.et_position);
        et_responsibilities = rootview.findViewById(R.id.et_responsibilities);
        rl_end_date = rootview.findViewById(R.id.rl_end_date);
        radio_group = rootview.findViewById(R.id.radio_group);
        radio_btn_no = rootview.findViewById(R.id.radio_btn_no);
        radio_btn_yes = rootview.findViewById(R.id.radio_btn_yes);


    }

    private void setListeners() {
        btn_add_work_experience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomPanel(null, false);
            }
        });


        sliding_panel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState.equals(SlidingUpPanelLayout.PanelState.COLLAPSED)) {
                    clearBottomPanel();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_btn_no) {
                    rl_end_date.setVisibility(View.VISIBLE);
                } else {
                    rl_end_date.setVisibility(View.GONE);
                }
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rl_end_date.getVisibility() == View.VISIBLE) {
                    if (txt_end_date.getText().toString().equals(getString(R.string.txt_select_date))) {
                        helpers.ToastMessage(getActivity(), getString(R.string.txt_select_date));
                    } else {
                        generalChecks();
                    }
                } else {
                    generalChecks();
                }
            }

            private void generalChecks() {
                if (txt_start_date.getText().toString().equals(getString(R.string.txt_select_date))){
                    helpers.ToastMessage(getActivity(), getString(R.string.txt_select_date));
                }else if (txt_start_date.getText().toString().equals(txt_end_date.getText().toString())){
                    helpers.ToastMessage(getActivity(), "Start date cannot be the same as the end date");
                } else if (helpers.validateEmptyEditText(et_company_name) &&
                        helpers.validateEmptyEditText(et_position) &&
                        helpers.validateEmptyEditText(et_responsibilities)){
                    WorkExperienceModel workExperienceModel = new WorkExperienceModel();
                    workExperienceModel.company_name = et_company_name.getText().toString();
                    workExperienceModel.position = et_position.getText().toString();
                    workExperienceModel.responsibilities = et_responsibilities.getText().toString();
                    workExperienceModel.start_date = txt_start_date.getText().toString();
                    workExperienceModel.end_date = txt_end_date.getText().toString();
                    workExperienceModel.current = radio_btn_yes.isChecked();

                    asyncUpdateDetails(workExperienceModel);

                }
            }
        });


    }

    private void setDates() {

        datePickerListener = new DatePickerDialog.OnDateSetListener() {
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
                if (STR_DATE_TYPE.equals(STR_DATE_START)) {
                    txt_start_date.setText(day.concat("-").concat(month).concat("-").concat(year));
                } else {
                    txt_end_date.setText(day.concat("-").concat(month).concat("-").concat(year));
                }
            }
        };


        txt_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(STR_DATE_START, "Set Start Date");

            }
        });

        txt_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(STR_DATE_END, "Set End Date");
            }
        });

    }

    private void showDatePicker(String type, String title) {
        if (getActivity() != null) {
            STR_DATE_TYPE = type;
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, datePickerListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));
            datePicker.setCancelable(false);
            datePicker.setTitle(title);
            datePicker.show();
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePicker.show();
        }
    }

    private void populateWorkExperience() {
        ArrayList<WorkExperienceModel> workExperienceModelArrayList = db.getAllWorkExperience();
        LayoutInflater linf;
        linf = LayoutInflater.from(getActivity());

        ll_work_experience.removeAllViews();

        int array_size = workExperienceModelArrayList.size();

        for (int v = 0; v < array_size; v++) {
            @SuppressLint("InflateParams") final View child = linf.inflate(R.layout.list_item_work_experience, null);

            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 20, 0, 0);
            child.setLayoutParams(params);

            final TextView txt_company_name = child.findViewById(R.id.txt_company_name);
            final TextView txt_position = child.findViewById(R.id.txt_position);
            final TextView txt_start_date = child.findViewById(R.id.txt_start_date);
            final TextView txt_end_date = child.findViewById(R.id.txt_end_date);
            final TextView txt_current = child.findViewById(R.id.txt_current);
            final TextView txt_responsibilities = child.findViewById(R.id.txt_responsibilities);
            final ImageView btn_delete = child.findViewById(R.id.btn_delete);

            final WorkExperienceModel workExperienceModel = workExperienceModelArrayList.get(v);

            txt_company_name.setText(workExperienceModel.company_name);
            txt_position.setText(workExperienceModel.position);
            txt_start_date.setText(workExperienceModel.start_date);

            if (workExperienceModel.current) {
                txt_current.setVisibility(View.VISIBLE);
                txt_end_date.setVisibility(View.GONE);
            } else {
                txt_current.setVisibility(View.GONE);
                txt_end_date.setVisibility(View.VISIBLE);
                txt_end_date.setText(workExperienceModel.end_date);
            }

            txt_responsibilities.setText(workExperienceModel.responsibilities);

            txt_responsibilities.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txt_responsibilities.getMaxLines() == 3) {
                        txt_responsibilities.setMaxLines(Integer.MAX_VALUE);
                    } else {
                        txt_responsibilities.setMaxLines(3);
                    }
                }
            });

            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openBottomPanel(workExperienceModel, true);
                }
            });

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteWorkExperience(workExperienceModel.id, child.getId());
                }
            });

            child.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deleteWorkExperience(workExperienceModel.id, child.getId());
                    return false;
                }
            });

            ll_work_experience.addView(child);

        }

        if (ll_work_experience.getChildCount() <= 0) {
            no_list_items.setVisibility(View.VISIBLE);
            txt_no_results.setText("Oops! No Work Experience has been added to your profile");
        } else {
            no_list_items.setVisibility(View.GONE);
        }

    }

    private void clearBottomPanel() {
        txt_id.setText("");
        txt_title.setText("");
        et_company_name.setText("");
        et_position.setText("");
        txt_start_date.setText(R.string.txt_select_date);
        txt_end_date.setText(R.string.txt_select_date);
        radio_btn_no.setChecked(true);
        et_responsibilities.setText("");
    }

    private void openBottomPanel(WorkExperienceModel workExperienceModel, Boolean isUpdate) {
        sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        if (isUpdate) {
            txt_title.setText("EDIT WORK EXPERIENCE");
            txt_id.setText(String.valueOf(workExperienceModel.id));
            et_company_name.setText(workExperienceModel.company_name);
            et_position.setText(workExperienceModel.position);
            txt_start_date.setText(workExperienceModel.start_date);
            txt_end_date.setText(workExperienceModel.end_date);
            if (workExperienceModel.current) {
                radio_btn_yes.setChecked(true);
            } else {
                radio_btn_no.setChecked(true);
            }
            txt_end_date.setText(workExperienceModel.end_date);
            et_responsibilities.setText(workExperienceModel.responsibilities);
            btn_confirm.setText("UPDATE");

        } else {
            clearBottomPanel();
            txt_title.setText("ADD NEW WORK EXPERIENCE");
            btn_confirm.setText("ADD");
        }

    }

    private void deleteWorkExperience(int workExperienceId, int childId) {
        // TODO - REMOVE A CHILD VIEW FROM A LINEAR LAYOUT
        ll_work_experience.removeView(ll_work_experience.findViewById(childId));
    }

    // ASYNC UPDATE / ADD WORK EXPERIENCE ==========================================================
    // TODO - CALL TO ADD / UPDATE W/E
    private void asyncUpdateDetails(final WorkExperienceModel workExperienceModel) {

        helpers.setProgressDialogMessage("Updating profile, please wait...");
        helpers.progressDialog(true);

        WorkExperienceService workExperienceService = WorkExperienceService.retrofit.create(WorkExperienceService.class);
        final Call<JsonObject> call = workExperienceService.setWorkExperience(
                workExperienceModel.id,
                workExperienceModel.company_name,
                workExperienceModel.position,
                workExperienceModel.start_date,
                workExperienceModel.end_date,
                workExperienceModel.responsibilities,
                workExperienceModel.current
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        if (db.setWorkExperienceFromJson(main.getJSONObject("data"))) {
                            helpers.ToastMessage(getActivity(), getString(R.string.txt_success));
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


    // ASYNC DELETE WORK EXPERIENCE ================================================================
    // TODO - CALL TO DELETE W/E

}