package com.hotelaide.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.main.models.ExperienceModel;
import com.hotelaide.services.ExperienceService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.SharedPrefs.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.SharedPrefs.USER_ID;

public class ExperienceViewFragment extends Fragment {

    private View root_view;
    private Helpers helpers;
    private Database db;
    private final String TAG_LOG = "EXPERIENCE VIEW";
    private String EXPERIENCE_TYPE = "";

    // TOP PANEL ===================================================================================
    private RecyclerView recycler_view;
    private ArrayList<ExperienceModel> model_list = new ArrayList<>();
    private ExperienceAdapter adapter;
    private RelativeLayout no_list_items;

    // BOTTOM PANEL ================================================================================
    private TextView
            txt_no_results;


    public ExperienceViewFragment() {
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

                    root_view = inflater.inflate(R.layout.fragment_experience_view, container, false);

                    findAllViews();

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

        recycler_view = root_view.findViewById(R.id.recycler_view);
        adapter = new ExperienceAdapter(model_list);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);


        setTextViews();

    }

    private void setTextViews() {
        if (EXPERIENCE_TYPE.equals(EXPERIENCE_TYPE_WORK)) {
            txt_no_results.setText(getString(R.string.error_no_we));

        } else {
            txt_no_results.setText(getString(R.string.error_no_ee));

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

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if(getActivity()!=null) {
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
                holder.txt_no_results.setText(R.string.error_no_we);

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
                } else {
                    holder.txt_current.setVisibility(View.VISIBLE);
                    holder.txt_end_date.setVisibility(View.GONE);
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

                holder.btn_delete.setVisibility(View.GONE);

            }
        }


        @Override
        public int getItemCount() {
            return experienceModels.size();
        }


        void updateData(ArrayList<ExperienceModel> view_model) {
            experienceModels.clear();
            experienceModels.addAll(view_model);
            notifyDataSetChanged();
        }

    }
}