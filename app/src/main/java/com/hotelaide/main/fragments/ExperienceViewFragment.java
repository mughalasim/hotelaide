package com.hotelaide.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.hotelaide.R;
import com.hotelaide.main.models.ExperienceModel;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.hotelaide.utils.StaticVariables.EDUCATION_LEVEL_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.StaticVariables.db;

public class ExperienceViewFragment extends Fragment {

    private View root_view;
    private Helpers helpers;

    private final String
            TAG_LOG = "EXPERIENCE VIEW";
    private String EXPERIENCE_TYPE = "";

    // TOP PANEL ===================================================================================
    private RecyclerView recycler_view;
    private ArrayList<ExperienceModel> model_list = new ArrayList<>();
    private ExperienceAdapter adapter;
    private RelativeLayout rl_no_list_items;
    private JSONArray experience_array;

    // BOTTOM PANEL ================================================================================
    private TextView
            txt_no_results;


    public ExperienceViewFragment(JSONArray experience_array, String EXPERIENCE_TYPE) {
        this.EXPERIENCE_TYPE = EXPERIENCE_TYPE;
        this.experience_array = experience_array;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                helpers = new Helpers(getActivity());

                root_view = inflater.inflate(R.layout.frag_experience_view, container, false);

                findAllViews();

                populateExperienceFromDB();

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            container.removeView(root_view);
        }
        return root_view;
    }


    // BASIC METHODS ===============================================================================
    private void findAllViews() {
        // TOP PANEL =============================================================
        rl_no_list_items = root_view.findViewById(R.id.rl_no_list_items);
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
        rl_no_list_items.setVisibility(View.GONE);
        model_list.clear();
        if (experience_array == null) {
            model_list = db.getAllExperience(EXPERIENCE_TYPE);
        } else {
            model_list = parseJsonArray();
        }
        recycler_view.invalidate();
        adapter.updateData(model_list);
        adapter.notifyDataSetChanged();
        if (model_list.size() <= 0) {
            noListItems();
        }

    }

    private void noListItems() {
        rl_no_list_items.setVisibility(View.VISIBLE);
        recycler_view.invalidate();
        model_list.clear();
        ExperienceModel experienceModel = new ExperienceModel();
        model_list.add(experienceModel);
        adapter.notifyDataSetChanged();
    }

    private ArrayList<ExperienceModel> parseJsonArray() {
        ArrayList<ExperienceModel> list = new ArrayList<>();
        int array_length = experience_array.length();
        try {
            for (int i = 0; i < array_length; i++) {
                JSONObject work_object = experience_array.getJSONObject(i);
                ExperienceModel experienceModel = new ExperienceModel();
                if (EXPERIENCE_TYPE.equals(EXPERIENCE_TYPE_WORK)) {
                    experienceModel.experience_id = work_object.getInt("id");
                    experienceModel.name = work_object.getString("company_name");
                    experienceModel.position = work_object.getString("position");
                    experienceModel.start_date = work_object.getString("start_date");
                    experienceModel.end_date = work_object.getString("end_date");
                    experienceModel.responsibilities_field = work_object.getString("responsibilities");
                    experienceModel.current = work_object.getInt("current");
                    experienceModel.type = EXPERIENCE_TYPE_WORK;

                } else {
                    experienceModel.experience_id = work_object.getInt("id");
                    experienceModel.name = work_object.getString("institution_name");
                    experienceModel.education_level = work_object.getInt("education_level");
                    experienceModel.start_date = work_object.getString("start_date");
                    experienceModel.end_date = work_object.getString("end_date");
                    experienceModel.responsibilities_field = work_object.getString("study_field");
                    experienceModel.current = work_object.getInt("current");

                    experienceModel.type = EXPERIENCE_TYPE_EDUCATION;
                }
                list.add(experienceModel);
            }

        } catch (JSONException e) {
            Helpers.logThis(TAG_LOG, e.toString());
        } catch (Exception e) {
            Helpers.logThis(TAG_LOG, e.toString());
        }
        return list;
    }

    // ADAPTER CLASS ===============================================================================
    public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ViewHolder> {
        private final ArrayList<ExperienceModel> experienceModels;
        private Context context;

        class ViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout
                    list_item,
                    no_list_item;
            final TextView
                    txt_no_results,
                    txt_name,
                    txt_position,
                    txt_start_date,
                    txt_end_date,
                    txt_current,
                    txt_duration,
                    txt_responsibilities_field_label,
                    txt_responsibilities_field;

            ViewHolder(View v) {
                super(v);
                // LIST ITEM
                list_item = v.findViewById(R.id.list_item);
                txt_name = v.findViewById(R.id.txt_name);
                txt_position = v.findViewById(R.id.txt_position);
                txt_start_date = v.findViewById(R.id.txt_start_date);
                txt_end_date = v.findViewById(R.id.txt_end_date);
                txt_current = v.findViewById(R.id.txt_current);
                txt_duration = v.findViewById(R.id.txt_duration);
                txt_responsibilities_field_label = v.findViewById(R.id.txt_responsibilities_field_label);
                txt_responsibilities_field = v.findViewById(R.id.txt_responsibilities_field);
                // NO LIST ITEM
                no_list_item = v.findViewById(R.id.rl_no_list_items);
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
                holder.txt_position.setText(experienceModel.position);
                holder.txt_start_date.setText(helpers.formatDate(experienceModel.start_date));

                if (experienceModel.current == 0) {
                    holder.txt_current.setVisibility(View.GONE);
                    holder.txt_end_date.setVisibility(View.VISIBLE);
                    holder.txt_end_date.setText(helpers.formatDate(experienceModel.end_date));
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
                    holder.txt_position.setText(experienceModel.position);
                } else {
                    holder.txt_position.setText(db.getFilterNameByID(EDUCATION_LEVEL_TABLE_NAME, experienceModel.education_level));
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