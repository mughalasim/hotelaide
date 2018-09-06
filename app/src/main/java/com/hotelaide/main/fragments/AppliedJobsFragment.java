package com.hotelaide.main.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
    private SwipeRefreshLayout swipe_refresh;
    private LinearLayoutManager layoutManager;
    private RecyclerView recycler_view;
    private ArrayList<JobModel> model_list = new ArrayList<>();
    private FindJobsAdapter adapter;

    public AppliedJobsFragment() { }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_recycler_view, container, false);

                helpers = new Helpers(getActivity());

                db = new Database();

                findAllViews();

                setListeners();

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
        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
        recycler_view = root_view.findViewById(R.id.recycler_view);
        adapter = new FindJobsAdapter(model_list);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);

    }

    private void setListeners() {
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                asyncGetAppliedJobs();
            }
        });
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
        Call<JsonObject> call = hotelService.getAppliedJobs(SharedPrefs.getInt(USER_ID));
        swipe_refresh.setRefreshing(true);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (getActivity() != null) {
                    swipe_refresh.setRefreshing(false);
                    try {
                        JSONObject main = new JSONObject(String.valueOf(response.body()));
                        Helpers.LogThis(TAG_LOG, main.toString());

                        model_list.clear();
                        JSONObject data = main.getJSONObject("data");
                        JSONArray applications = data.getJSONArray("applications");
                        for (int i = 0; i < applications.length(); i++) {
                            JSONObject hit_object = applications.getJSONObject(i);
                            model_list.add(db.setJobFromJson(hit_object));
                        }

                        if (model_list.size() <= 0) {
                            noListItems();
                        }

                        adapter.notifyDataSetChanged();


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
                    if (helpers.validateInternetConnection()) {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                    } else {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                    }
                }
            }
        });
    }

}