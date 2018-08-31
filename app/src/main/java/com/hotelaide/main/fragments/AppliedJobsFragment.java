package com.hotelaide.main.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.main.adapters.FindJobsAdapter;
import com.hotelaide.main.models.JobModel;
import com.hotelaide.services.HotelService;
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

import static com.hotelaide.utils.SharedPrefs.USER_ID;


public class AppliedJobsFragment extends Fragment {

    private View root_view;
    private Helpers helpers;
    private final String
            TAG_LOG = "APPLIED JOBS";
    private Database db;

    // SEARCH ADAPTER ITEMS ------------------------------------------------------------------------
    private LinearLayoutManager layoutManager;
    private RecyclerView recycler_view;
    private ArrayList<JobModel> model_list = new ArrayList<>();
    private FindJobsAdapter adapter;

    public AppliedJobsFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

                helpers = new Helpers(getActivity());

                db = new Database();

                findAllViews();

                asyncGetAppliedJobs();


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
        // SEARCH FUNCTIONALITY --------------------------------------------------------------------
        recycler_view = root_view.findViewById(R.id.recycler_view);
        adapter = new FindJobsAdapter(model_list);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);

    }

    private void noListItems() {
        recycler_view.invalidate();
        model_list.clear();
        JobModel jobModel = new JobModel();
        model_list.add(jobModel);
        adapter.notifyDataSetChanged();
    }

    // ASYNC FETCH ALL APPLIED JOBS ================================================================
    private void asyncGetAppliedJobs() {
        HotelService hotelService = HotelService.retrofit.create(HotelService.class);

        Call<JsonObject> call = hotelService.getAppliedJobs(
                SharedPrefs.getInt(USER_ID)
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    Helpers.LogThis(TAG_LOG, main.toString());

                    model_list.clear();

                    JSONArray applications = main.getJSONArray("applications");
                    for (int i = 0; i < applications.length(); i++) {
                        JSONObject hit_object = applications.getJSONObject(i);
                        model_list.add(db.setJobFromJson(hit_object));
                    }

                    if (model_list.size() <= 0) {
                        noListItems();
                    }
                    recycler_view.invalidate();
                    adapter.updateData(model_list);
                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
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