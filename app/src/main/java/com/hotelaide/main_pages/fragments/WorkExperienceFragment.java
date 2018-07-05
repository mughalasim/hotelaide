package com.hotelaide.main_pages.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
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
import com.hotelaide.main_pages.models.WorkExperienceModel;
import com.hotelaide.services.WorkExperienceService;
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


public class WorkExperienceFragment extends Fragment {

    private View rootview;
    private Helpers helpers;
    private Database db;
    private final String TAG_LOG = "WORK EXPERIENCE";
    // TOP PANEL ===================================================================================
    private RecyclerView recycler_view;
    private TextView
            btn_add_work_experience;
    private ArrayList<WorkExperienceModel> model_list = new ArrayList<>();
    private WorkExperienceAdapter adapter;
    private SwipeRefreshLayout swipe_refresh;

    // BOTTOM PANEL ================================================================================
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
    DatePickerDialog.OnDateSetListener
            datePickerListener;
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

                asyncGetAllWorkExperience();

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
        swipe_refresh = rootview.findViewById(R.id.swipe_refresh);
        recycler_view = rootview.findViewById(R.id.work_experience_recycler);
        btn_add_work_experience = rootview.findViewById(R.id.btn_add_work_experience);
        adapter = new WorkExperienceAdapter(model_list);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);

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

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                asyncGetAllWorkExperience();
            }
        });

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
                    if(txt_end_date.getText().toString().equals("")){
                        txt_end_date.setText(R.string.txt_select_date);
                    }
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
                if (txt_start_date.getText().toString().equals(getString(R.string.txt_select_date))) {
                    helpers.ToastMessage(getActivity(), getString(R.string.txt_select_date));
                } else if (txt_start_date.getText().toString().equals(txt_end_date.getText().toString())) {
                    helpers.ToastMessage(getActivity(), "Start date cannot be the same as the end date");
                } else if (helpers.validateEmptyEditText(et_company_name) &&
                        helpers.validateEmptyEditText(et_position) &&
                        helpers.validateEmptyEditText(et_responsibilities)) {
                    WorkExperienceModel workExperienceModel = new WorkExperienceModel();
                    if (!txt_id.getText().toString().equals(""))
                        workExperienceModel.id = Integer.valueOf(txt_id.getText().toString());
                    workExperienceModel.company_name = et_company_name.getText().toString();
                    workExperienceModel.position = et_position.getText().toString();
                    workExperienceModel.responsibilities = et_responsibilities.getText().toString();
                    workExperienceModel.start_date = txt_start_date.getText().toString();
                    workExperienceModel.end_date = txt_end_date.getText().toString();
                    workExperienceModel.current = radio_btn_yes.isChecked();

                    asyncUpdateAddWE(workExperienceModel,
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
                        year
                                .concat(getString(R.string.txt_date_separator))
                                .concat(month)
                                .concat(getString(R.string.txt_date_separator))
                                .concat(day);

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
        model_list.clear();
        model_list = db.getAllWorkExperience();
        recycler_view.invalidate();
        adapter.updateData(model_list);
        adapter.notifyDataSetChanged();
//        if (model_list.size() <= 0) {
//            noRestaurants(true);
//        } else {
//            noRestaurants(false);
//        }

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
            txt_title.setText(R.string.txt_edit_we);
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
            btn_confirm.setText(R.string.txt_update);

        } else {
            clearBottomPanel();
            txt_title.setText(R.string.txt_add_we);
            btn_confirm.setText(R.string.txt_add);
        }

    }

    private void logWorkExperienceModel(WorkExperienceModel workExperienceModel) {
        Helpers.LogThis(TAG_LOG,
                workExperienceModel.id + " - " +
                        workExperienceModel.company_name + " - " +
                        workExperienceModel.position + " - " +
                        workExperienceModel.start_date + " - " +
                        workExperienceModel.end_date + " - " +
                        workExperienceModel.responsibilities + " - " +
                        workExperienceModel.current
        );


    }



    // ASYNC GET ALL  WORK EXPERIENCES =============================================================
    private void asyncGetAllWorkExperience() {
        WorkExperienceService workExperienceService = WorkExperienceService.retrofit.create(WorkExperienceService.class);
        final Call<JsonObject> call = workExperienceService.getAllWorkExperiences(SharedPrefs.getInt(SharedPrefs.USER_ID));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                swipe_refresh.setRefreshing(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    Helpers.LogThis(TAG_LOG, main.toString());
                    if (main.getBoolean("success")) {
                        db.deleteExperienceTable();
                        JSONArray work_object = main.getJSONArray("data");
                        int length = work_object.length();
                        if (length > 0) {
                            for (int i = 0; i < length; i++) {
                                db.setWorkExperienceFromJson(work_object.getJSONObject(i));
                            }
                        }
                        populateWorkExperience();
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
                swipe_refresh.setRefreshing(false);
                Helpers.LogThis(TAG_LOG, t.toString());
                populateWorkExperience();
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                } else {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                }

            }
        });
    }


    // ASYNC UPDATE / ADD WORK EXPERIENCE ==========================================================
    private void asyncUpdateAddWE(final WorkExperienceModel workExperienceModel, final Boolean isUpdate) {

        Call<JsonObject> call;
        logWorkExperienceModel(workExperienceModel);

        if (isUpdate) {
            helpers.setProgressDialogMessage("Updating work experience, please wait...");
            WorkExperienceService workExperienceService = WorkExperienceService.retrofit.create(WorkExperienceService.class);
            call = workExperienceService.updateWorkExperience(
                    SharedPrefs.getInt(SharedPrefs.USER_ID),
                    workExperienceModel.id,
                    workExperienceModel.company_name,
                    workExperienceModel.position,
                    workExperienceModel.start_date,
                    workExperienceModel.end_date,
                    workExperienceModel.responsibilities,
                    workExperienceModel.current
            );
        } else {
            helpers.setProgressDialogMessage("Adding work experience, please wait...");
            WorkExperienceService workExperienceService = WorkExperienceService.retrofit.create(WorkExperienceService.class);
            call = workExperienceService.setWorkExperience(
                    SharedPrefs.getInt(SharedPrefs.USER_ID),
                    workExperienceModel.company_name,
                    workExperienceModel.position,
                    workExperienceModel.start_date,
                    workExperienceModel.end_date,
                    workExperienceModel.responsibilities,
                    workExperienceModel.current
            );
        }
        helpers.progressDialog(true);


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
                            populateWorkExperience();
                            sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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
    private void deleteWorkExperience(int workExperienceId, final int position) {
        WorkExperienceService workExperienceService = WorkExperienceService.retrofit.create(WorkExperienceService.class);
        final Call<JsonObject> call = workExperienceService.deleteOneWorkExperience(workExperienceId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                swipe_refresh.setRefreshing(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    Helpers.LogThis(TAG_LOG, main.toString());
                    if (main.getBoolean("success")) {
                        adapter.removeItem(position);
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
                swipe_refresh.setRefreshing(false);
                Helpers.LogThis(TAG_LOG, t.toString());
                populateWorkExperience();
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                } else {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                }

            }
        });
    }




//==================================================================================================
//==================================================================================================
    // ADAPTER CLASS ===============================================================================
    public class WorkExperienceAdapter extends RecyclerView.Adapter<WorkExperienceAdapter.ViewHolder> {
        private final ArrayList<WorkExperienceModel> workExperienceModels;
        private final String TAG_LOG = "W/E ADAPTER";
        private Context context;
        private Helpers helpers;

        class ViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout
                    no_list_item,
                    list_item;

            final TextView
                    txt_no_results,
                    txt_company_name,
                    txt_position,
                    txt_start_date,
                    txt_end_date,
                    txt_current,
                    txt_responsibilities;
            final ImageView btn_delete;

            ViewHolder(View v) {
                super(v);
                txt_no_results = v.findViewById(R.id.txt_no_results);
                txt_company_name = v.findViewById(R.id.txt_company_name);
                txt_position = v.findViewById(R.id.txt_position);
                txt_start_date = v.findViewById(R.id.txt_start_date);
                txt_end_date = v.findViewById(R.id.txt_end_date);
                txt_current = v.findViewById(R.id.txt_current);
                txt_responsibilities = v.findViewById(R.id.txt_responsibilities);
                btn_delete = v.findViewById(R.id.btn_delete);
                no_list_item = v.findViewById(R.id.no_list_items);
                list_item = v.findViewById(R.id.list_item);
            }

        }

        public WorkExperienceAdapter(ArrayList<WorkExperienceModel> workExperienceModels) {
            this.workExperienceModels = workExperienceModels;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_work_experience, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            context = holder.itemView.getContext();
            helpers = new Helpers(context);

            final WorkExperienceModel workExperienceModel = workExperienceModels.get(position);

            if (workExperienceModel.company_name.equals("")) {
                holder.no_list_item.setVisibility(View.VISIBLE);
                holder.list_item.setVisibility(View.GONE);
                holder.txt_no_results.setText(R.string.error_no_we);

            } else {
                holder.no_list_item.setVisibility(View.GONE);
                holder.list_item.setVisibility(View.VISIBLE);

                holder.txt_company_name.setText(workExperienceModel.company_name);
                holder.txt_position.setText(workExperienceModel.position);
                txt_start_date.setText(workExperienceModel.start_date);

                if (workExperienceModel.current) {
                    holder.txt_current.setVisibility(View.VISIBLE);
                    txt_end_date.setVisibility(View.GONE);
                } else {
                    holder.txt_current.setVisibility(View.GONE);
                    holder.txt_end_date.setVisibility(View.VISIBLE);
                    holder.txt_end_date.setText(workExperienceModel.end_date);
                }

                holder.txt_responsibilities.setText(workExperienceModel.responsibilities);

                holder.txt_responsibilities.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.txt_responsibilities.getMaxLines() == 3) {
                            holder.txt_responsibilities.setMaxLines(Integer.MAX_VALUE);
                        } else {
                            holder.txt_responsibilities.setMaxLines(3);
                        }
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openBottomPanel(workExperienceModel, true);
                    }
                });

                holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteWorkExperience(workExperienceModel.id, holder.getAdapterPosition());
                    }
                });

            }
        }


        @Override
        public int getItemCount() {
            return workExperienceModels.size();
        }

        public void removeItem(int position) {
            workExperienceModels.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, workExperienceModels.size());
            populateWorkExperience();
        }

    public void updateData(ArrayList<WorkExperienceModel> view_model) {
        workExperienceModels.clear();
        workExperienceModels.addAll(view_model);
        notifyDataSetChanged();
    }

    }
}