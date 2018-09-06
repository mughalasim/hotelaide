package com.hotelaide.main.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.main.models.ExperienceModel;
import com.hotelaide.services.ExperienceService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.SharedPrefs.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.SharedPrefs.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.SharedPrefs.USER_ID;

public class ExperienceFragment extends Fragment {

    private View root_view;
    private Helpers helpers;
    private Database db;
    private final String TAG_LOG = "EXPERIENCE";
    private String EXPERIENCE_TYPE = "";

    // TOP PANEL ===================================================================================
    private RecyclerView recycler_view;
    private FloatingActionButton
            btn_add;
    private ArrayList<ExperienceModel> model_list = new ArrayList<>();
    private ExperienceAdapter adapter;
    private SwipeRefreshLayout swipe_refresh;
    private RelativeLayout no_list_items;

    // BOTTOM PANEL ================================================================================
    SlidingUpPanelLayout sliding_panel;
    private TextView
            txt_no_results,
            txt_id,
            txt_title,
            txt_start_date,
            txt_end_date,
            btn_cancel,
            btn_confirm;
    private EditText
            et_name,
            et_position_level,
            et_responsibilities_field;
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
    DatePickerDialog.OnDateSetListener
            datePickerListener;

    public ExperienceFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                helpers = new Helpers(getActivity());
                db = new Database();

                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    EXPERIENCE_TYPE = bundle.getString("EXPERIENCE_TYPE");

                    root_view = inflater.inflate(R.layout.fragment_experience, container, false);

                    findAllViews();

                    setListeners();

                    setDates();

                    asyncGetAllWorkExperience();
                }

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }


    // BASIC METHODS ===============================================================================
    private void findAllViews() {
        // TOP PANEL =============================================================
        no_list_items = root_view.findViewById(R.id.no_list_items);
        txt_no_results = root_view.findViewById(R.id.txt_no_results);

        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
        recycler_view = root_view.findViewById(R.id.recycler_view);
        btn_add = root_view.findViewById(R.id.btn_add);
        adapter = new ExperienceAdapter(model_list);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);

        // BOTTOM PANEL =============================================================
        sliding_panel = root_view.findViewById(R.id.sliding_panel);
        txt_id = root_view.findViewById(R.id.txt_id);
        txt_title = root_view.findViewById(R.id.txt_title);
        txt_start_date = root_view.findViewById(R.id.txt_start_date);
        txt_end_date = root_view.findViewById(R.id.txt_end_date);
        btn_cancel = root_view.findViewById(R.id.btn_cancel);
        btn_confirm = root_view.findViewById(R.id.btn_confirm);
        rl_end_date = root_view.findViewById(R.id.rl_end_date);
        radio_group = root_view.findViewById(R.id.radio_group);

        et_name = root_view.findViewById(R.id.et_name);
        et_position_level = root_view.findViewById(R.id.et_position_level);
        et_responsibilities_field = root_view.findViewById(R.id.et_responsibilities_field);
        radio_btn_no = root_view.findViewById(R.id.radio_btn_no);
        radio_btn_yes = root_view.findViewById(R.id.radio_btn_yes);

        setTextViews();

    }

    private void setTextViews() {
        if (EXPERIENCE_TYPE.equals(EXPERIENCE_TYPE_WORK)) {
            txt_no_results.setText(getString(R.string.error_no_we));

            et_name.setHint(getString(R.string.txt_company_name));
            et_position_level.setHint(getString(R.string.txt_position_held));
            et_responsibilities_field.setHint(getString(R.string.txt_responsibilities));

        } else {
            txt_no_results.setText(getString(R.string.error_no_ee));

            et_name.setHint(getString(R.string.txt_institution_name));
            et_position_level.setHint(getString(R.string.txt_education_level));
            et_responsibilities_field.setHint(getString(R.string.txt_field_study));
        }
    }

    private void setListeners() {

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                asyncGetAllWorkExperience();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
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
                    txt_end_date.setText(getString(R.string.txt_select_date));
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
                if (txt_start_date.getText().toString().equals(getString(R.string.txt_select_date))) {
                    helpers.ToastMessage(getActivity(), getString(R.string.txt_select_date));
                } else if (txt_start_date.getText().toString().equals(txt_end_date.getText().toString())) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_same_date));
                } else if (helpers.validateEmptyEditText(et_name) &&
                        helpers.validateEmptyEditText(et_position_level) &&
                        helpers.validateEmptyEditText(et_responsibilities_field)) {
                    ExperienceModel experienceModel = new ExperienceModel();
                    if (!txt_id.getText().toString().equals(""))
                        experienceModel.experience_id = Integer.valueOf(txt_id.getText().toString());
                    experienceModel.name = et_name.getText().toString();
                    experienceModel.position_level = et_position_level.getText().toString();
                    experienceModel.responsibilities_field = et_responsibilities_field.getText().toString();

                    if (!txt_start_date.getText().toString().equals(getString(R.string.txt_select_date)))
                        experienceModel.start_date = txt_start_date.getText().toString();

                    if (!txt_end_date.getText().toString().equals(getString(R.string.txt_select_date))) {
                        experienceModel.end_date = txt_end_date.getText().toString();
                    } else {
                        experienceModel.end_date = txt_start_date.getText().toString();
                    }

                    if (radio_btn_yes.isChecked())
                        experienceModel.current = 1;

                    asyncUpdateAddWE(experienceModel,
                            btn_confirm.getText().toString().equals(getString(R.string.txt_update)));

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

                String date_to_set =
                        day
                                .concat(getString(R.string.txt_date_separator))
                                .concat(month)
                                .concat(getString(R.string.txt_date_separator))
                                .concat(year);

                if (STR_DATE_TYPE.equals(STR_DATE_START)) {
                    txt_start_date.setText(date_to_set);
                } else {
                    txt_end_date.setText(date_to_set);
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
            DatePickerDialog datePicker = new DatePickerDialog(
                    new ContextThemeWrapper(getActivity(), getActivity().getTheme()),
                    datePickerListener,
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

    private void populateExperienceFromDB() {
        no_list_items.setVisibility(View.GONE);
        model_list.clear();
        model_list = db.getAllExperience(EXPERIENCE_TYPE);
        recycler_view.invalidate();
        adapter.updateData(model_list);
        adapter.notifyDataSetChanged();
        if (model_list.size() <= 0) {
            noListItems();
        }

    }

    private void noListItems() {
        no_list_items.setVisibility(View.VISIBLE);
        recycler_view.invalidate();
        model_list.clear();
        ExperienceModel experienceModel = new ExperienceModel();
        model_list.add(experienceModel);
        adapter.notifyDataSetChanged();
    }

    private void clearBottomPanel() {
        txt_id.setText("");
        txt_title.setText("");
        et_name.setText("");
        et_position_level.setText("");
        txt_start_date.setText(R.string.txt_select_date);
        txt_end_date.setText(R.string.txt_select_date);
        radio_btn_no.setChecked(true);
        et_responsibilities_field.setText("");
    }

    private void openBottomPanel(ExperienceModel experienceModel, Boolean isUpdate) {
        sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

        if (EXPERIENCE_TYPE.equals(EXPERIENCE_TYPE_EDUCATION)) {
            radio_btn_no.setText(R.string.txt_study_completed);
            radio_btn_yes.setText(R.string.txt_study_incomplete);
        }

        if (isUpdate) {
            txt_title.setText(R.string.txt_edit_exp);
            txt_id.setText(String.valueOf(experienceModel.experience_id));
            et_name.setText(experienceModel.name);
            et_position_level.setText(experienceModel.position_level);
            txt_start_date.setText(experienceModel.start_date);
            txt_end_date.setText(experienceModel.end_date);

            if (experienceModel.current == 1) {
                radio_btn_yes.setChecked(true);
            } else {
                radio_btn_no.setChecked(true);
            }

            et_responsibilities_field.setText(experienceModel.responsibilities_field);
            btn_confirm.setText(R.string.txt_update);

        } else {
            txt_title.setText(R.string.txt_add_exp);
            btn_confirm.setText(R.string.txt_add);
        }

    }

    private void logWorkExperienceModel(ExperienceModel experienceModel) {
        Helpers.LogThis(TAG_LOG,
                experienceModel.experience_id + " - " +
                        experienceModel.name + " - " +
                        experienceModel.position_level + " - " +
                        experienceModel.start_date + " - " +
                        experienceModel.end_date + " - " +
                        experienceModel.responsibilities_field + " - " +
                        experienceModel.current + " - " +
                        experienceModel.type
        );


    }


    // ASYNC GET ALL EXPERIENCES ===================================================================
    private void asyncGetAllWorkExperience() {
        ExperienceService experienceService = ExperienceService.retrofit.create(ExperienceService.class);

        Call<JsonObject> call;
        if (EXPERIENCE_TYPE.equals(EXPERIENCE_TYPE_WORK)) {
            call = experienceService.getAllWorkExperiences(SharedPrefs.getInt(USER_ID));
        } else {
            call = experienceService.getAllEducationExperiences(SharedPrefs.getInt(USER_ID));
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (getActivity() != null) {
                    swipe_refresh.setRefreshing(false);
                    try {
                        JSONObject main = new JSONObject(String.valueOf(response.body()));
                        Helpers.LogThis(TAG_LOG, main.toString());
                        if (main.getBoolean("success")) {
                            db.deleteExperienceTableByType(EXPERIENCE_TYPE);
                            JSONArray work_object = main.getJSONArray("data");
                            int length = work_object.length();
                            if (length > 0) {
                                for (int i = 0; i < length; i++) {
                                    db.setExperienceFromJson(work_object.getJSONObject(i), EXPERIENCE_TYPE);
                                }
                            }
                            populateExperienceFromDB();
                        } else {
                            helpers.handleErrorMessage(getActivity(), main.getJSONObject("data"));
                        }

                    } catch (JSONException e) {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (getActivity() != null) {
                    swipe_refresh.setRefreshing(false);
                    Helpers.LogThis(TAG_LOG, t.toString());
                    populateExperienceFromDB();
                    if (helpers.validateInternetConnection()) {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                    } else {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                    }
                }
            }
        });
    }


    // ASYNC UPDATE / ADD EXPERIENCE ===============================================================
    private void asyncUpdateAddWE(final ExperienceModel experienceModel, final Boolean isUpdate) {

        Call<JsonObject> call;
        logWorkExperienceModel(experienceModel);

        if (isUpdate) {
            helpers.setProgressDialogMessage("Updating experience, please wait...");
            ExperienceService experienceService = ExperienceService.retrofit.create(ExperienceService.class);

            if (EXPERIENCE_TYPE.equals(EXPERIENCE_TYPE_WORK)) {
                call = experienceService.updateWorkExperience(
                        SharedPrefs.getInt(USER_ID),
                        experienceModel.experience_id,
                        experienceModel.name,
                        experienceModel.position_level,
                        experienceModel.start_date,
                        experienceModel.end_date,
                        experienceModel.responsibilities_field,
                        experienceModel.current
                );
            } else {
                call = experienceService.updateEducationExperience(
                        SharedPrefs.getInt(USER_ID),
                        experienceModel.experience_id,
                        experienceModel.name,
                        experienceModel.position_level,
                        experienceModel.start_date,
                        experienceModel.end_date,
                        experienceModel.responsibilities_field,
                        experienceModel.current
                );
            }

        } else {
            helpers.setProgressDialogMessage("Adding experience, please wait...");
            ExperienceService experienceService = ExperienceService.retrofit.create(ExperienceService.class);

            if (EXPERIENCE_TYPE.equals(EXPERIENCE_TYPE_WORK)) {
                call = experienceService.setWorkExperience(
                        SharedPrefs.getInt(USER_ID),
                        experienceModel.name,
                        experienceModel.position_level,
                        experienceModel.start_date,
                        experienceModel.end_date,
                        experienceModel.responsibilities_field,
                        experienceModel.current
                );
            } else {
                call = experienceService.setEducationExperience(
                        SharedPrefs.getInt(USER_ID),
                        experienceModel.name,
                        experienceModel.position_level,
                        experienceModel.start_date,
                        experienceModel.end_date,
                        experienceModel.responsibilities_field,
                        experienceModel.current
                );
            }
        }

        helpers.progressDialog(true);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (getActivity() != null) {
                    helpers.progressDialog(false);
                    try {
                        JSONObject main = new JSONObject(String.valueOf(response.body()));
                        Helpers.LogThis(TAG_LOG, main.toString());
                        if (main.getBoolean("success")) {
                            if (db.setExperienceFromJson(main.getJSONObject("data"), EXPERIENCE_TYPE)) {
                                helpers.ToastMessage(getActivity(), getString(R.string.txt_success));
                                populateExperienceFromDB();
                                sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (getActivity() != null) {
                    helpers.progressDialog(false);
                    Helpers.LogThis(TAG_LOG, t.toString());
                    if (helpers.validateInternetConnection()) {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                    } else {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                    }
                }
            }
        });

    }


    // ASYNC DELETE WORK EXPERIENCE ================================================================
    private void deleteExperience(final int experienceId, final int position) {
        ExperienceService experienceService = ExperienceService.retrofit.create(ExperienceService.class);

        Call<JsonObject> call;
        if (EXPERIENCE_TYPE.equals(EXPERIENCE_TYPE_WORK)) {
            call = experienceService.deleteOneWorkExperience(
                    SharedPrefs.getInt(USER_ID),
                    experienceId
            );
        } else {
            call = experienceService.deleteOneEducationExperience(
                    SharedPrefs.getInt(USER_ID),
                    experienceId
            );
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (getActivity() != null) {
                    swipe_refresh.setRefreshing(false);
                    try {
                        JSONObject main = new JSONObject(String.valueOf(response.body()));
                        Helpers.LogThis(TAG_LOG, main.toString());
                        if (main.getBoolean("success")) {
                            db.deleteExperienceByID(String.valueOf(experienceId), EXPERIENCE_TYPE);
                            adapter.removeItem(position);
                        }
                        populateExperienceFromDB();
                    } catch (JSONException e) {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (getActivity() != null) {
                    swipe_refresh.setRefreshing(false);
                    Helpers.LogThis(TAG_LOG, t.toString());
                    populateExperienceFromDB();
                    if (helpers.validateInternetConnection()) {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                    } else {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                    }
                }
            }
        });
    }


    // ADAPTER CLASS ===============================================================================
    public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ViewHolder> {
        private final ArrayList<ExperienceModel> experienceModels;
        private Context context;

        class ViewHolder extends RecyclerView.ViewHolder {
            CardView
                    list_item;
            RelativeLayout
                    no_list_item;
            final TextView
                    txt_no_results,
                    txt_name,
                    txt_position_level,
                    txt_start_date,
                    txt_end_date,
                    txt_current,
                    txt_duration,
                    txt_responsibilities_field_label,
                    txt_responsibilities_field;

            final ImageView btn_delete;

            ViewHolder(View v) {
                super(v);
                // LIST ITEM
                list_item = v.findViewById(R.id.list_item);
                txt_name = v.findViewById(R.id.txt_name);
                txt_position_level = v.findViewById(R.id.txt_position_level);
                txt_start_date = v.findViewById(R.id.txt_start_date);
                txt_end_date = v.findViewById(R.id.txt_end_date);
                txt_current = v.findViewById(R.id.txt_current);
                txt_duration = v.findViewById(R.id.txt_duration);
                txt_responsibilities_field_label = v.findViewById(R.id.txt_responsibilities_field_label);
                txt_responsibilities_field = v.findViewById(R.id.txt_responsibilities_field);
                btn_delete = v.findViewById(R.id.btn_delete);
                // NO LIST ITEM
                no_list_item = v.findViewById(R.id.no_list_items);
                txt_no_results = v.findViewById(R.id.txt_no_results);

            }


        }

        ExperienceAdapter(ArrayList<ExperienceModel> experienceModels) {
            this.experienceModels = experienceModels;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_experience, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            context = holder.itemView.getContext();
            helpers = new Helpers(context);

            final ExperienceModel experienceModel = experienceModels.get(position);

            if (experienceModel.name.equals("")) {
                holder.no_list_item.setVisibility(View.VISIBLE);
                holder.list_item.setVisibility(View.GONE);

            } else {
                holder.no_list_item.setVisibility(View.GONE);
                holder.list_item.setVisibility(View.VISIBLE);

                holder.txt_name.setText(experienceModel.name);
                holder.txt_position_level.setText(experienceModel.position_level);
                holder.txt_start_date.setText(experienceModel.start_date);

                if (experienceModel.current == 0) {
                    holder.txt_current.setVisibility(View.GONE);
                    holder.txt_end_date.setVisibility(View.VISIBLE);
                    holder.txt_end_date.setText(experienceModel.end_date);
                    holder.txt_duration.setText(helpers.calculateDateInterval(experienceModel.start_date, experienceModel.end_date));
                } else {
                    holder.txt_current.setVisibility(View.VISIBLE);
                    holder.txt_end_date.setVisibility(View.GONE);
                    holder.txt_duration.setText(helpers.calculateAge(experienceModel.start_date));
                }

                if (holder.txt_duration.getText().toString().length() < 1) {
                    holder.txt_duration.setVisibility(View.GONE);
                } else {
                    holder.txt_duration.setVisibility(View.VISIBLE);
                }

                if (experienceModel.type.equals(EXPERIENCE_TYPE_WORK)) {
                    holder.txt_responsibilities_field_label.setText(R.string.txt_responsibilities);
                } else {
                    holder.txt_responsibilities_field_label.setText(R.string.txt_field_study);
                }

                holder.txt_responsibilities_field.setText(experienceModel.responsibilities_field);

                holder.txt_responsibilities_field.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.txt_responsibilities_field.getMaxLines() == 3) {
                            holder.txt_responsibilities_field.setMaxLines(Integer.MAX_VALUE);
                        } else {
                            holder.txt_responsibilities_field.setMaxLines(3);
                        }
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openBottomPanel(experienceModel, true);
                    }
                });

                holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getActivity() != null) {
                            final Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.dialog_confirm);
                            final TextView txt_message = dialog.findViewById(R.id.txt_message);
                            final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
                            final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                            final TextView txt_title = dialog.findViewById(R.id.txt_title);
                            txt_title.setText(getString(R.string.txt_delete));
                            txt_message.setText(getString(R.string.txt_delete_experience).concat(experienceModel.name));
                            btn_cancel.setVisibility(View.VISIBLE);
                            btn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.cancel();
                                }
                            });
                            btn_confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteExperience(experienceModel.experience_id, holder.getAdapterPosition());
                                    dialog.cancel();
                                }
                            });
                            dialog.show();
                        }
                    }
                });

            }
        }


        @Override
        public int getItemCount() {
            return experienceModels.size();
        }

        public void removeItem(int position) {
            experienceModels.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, experienceModels.size());
            if (experienceModels.size() <= 0) {
                populateExperienceFromDB();
            }
        }

        void updateData(ArrayList<ExperienceModel> view_model) {
            experienceModels.clear();
            experienceModels.addAll(view_model);
            notifyDataSetChanged();
        }

    }
}