package com.hotelaide.main.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.EstablishmentInterface;
import com.hotelaide.main.adapters.FindJobsAdapter;
import com.hotelaide.main.models.JobModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.EXTRA_STRING;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_APPLIED;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_INTERVIEWS;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_SHORTLISTED;
import static com.hotelaide.utils.StaticVariables.USER_ID;
import static com.hotelaide.utils.StaticVariables.db;

public class FilteredJobsFragment extends Fragment {

    private View root_view;
    private Helpers helpers;
    private final String
            TAG_LOG = "FILTERED JOBS";


    // SEARCH ADAPTER ITEMS ------------------------------------------------------------------------
    private SwipeRefreshLayout swipe_refresh;
    private LinearLayoutManager layoutManager;
    private RecyclerView recycler_view;
    private ArrayList<JobModel> model_list = new ArrayList<>();
    private FindJobsAdapter adapter;
    private String FILTER_TYPE = "", STR_ERROR_MESSAGE = "";

    public FilteredJobsFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    FILTER_TYPE = bundle.getString(EXTRA_STRING);

                    if (FILTER_TYPE != null && FILTER_TYPE.equals(FILTER_TYPE_APPLIED)) {
                        STR_ERROR_MESSAGE = getString(R.string.error_no_jobs_applied);
                    } else if (FILTER_TYPE != null && FILTER_TYPE.equals(FILTER_TYPE_SHORTLISTED)) {
                        STR_ERROR_MESSAGE = "You have not been short listed for any jobs";
                    } else if (FILTER_TYPE != null && FILTER_TYPE.equals(FILTER_TYPE_INTERVIEWS)) {
                        STR_ERROR_MESSAGE = "You have no job interviews yet";
                    }

                    root_view = inflater.inflate(R.layout.frag_recycler_view, container, false);

                    helpers = new Helpers(getActivity());

                    findAllViews();

                    setListeners();

                    HelpersAsync.setTrackerPage(FILTER_TYPE);

                }

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            container.removeView(root_view);
        }
        return root_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        asyncGetAppliedJobs();
    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
        recycler_view = root_view.findViewById(R.id.recycler_view);
        adapter = new FindJobsAdapter(model_list, STR_ERROR_MESSAGE);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);

    }

    private void setListeners() {
        helpers.animateSwipeRefresh(swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                asyncGetAppliedJobs();
            }
        });
    }

    private void populateJobs() {
        if (helpers.validateInternetConnection()) {
            if (model_list.size() < 1) {
                noListItems();
            } else {
                adapter.notifyDataSetChanged();
            }
        } else {
            recycler_view.invalidate();
            model_list.clear();
            model_list = db.getAllFilteredJobs(FILTER_TYPE);
            if (model_list.size() < 1) {
                noListItems();
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void noListItems() {
        recycler_view.invalidate();
        model_list.clear();
        model_list.add(new JobModel());
        adapter.notifyDataSetChanged();
    }

    // ASYNC FETCH ALL JOBS ========================================================================
    private void asyncGetAppliedJobs() {
        EstablishmentInterface establishmentInterface = EstablishmentInterface.retrofit.create(EstablishmentInterface.class);

        Call<JsonObject> call;
        switch (FILTER_TYPE) {
            case FILTER_TYPE_APPLIED:
                call = establishmentInterface.getAppliedJobs(SharedPrefs.getInt(USER_ID));
                break;
            case FILTER_TYPE_SHORTLISTED:
                call = establishmentInterface.getShortlistedJobs(SharedPrefs.getInt(USER_ID));
                break;
            default:
                call = establishmentInterface.getJobInvites(SharedPrefs.getInt(USER_ID));
                break;
        }

        swipe_refresh.setRefreshing(true);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (getActivity() != null) {
                    swipe_refresh.setRefreshing(false);
                    try {
                        JSONObject main = new JSONObject(String.valueOf(response.body()));
                        Helpers.logThis(TAG_LOG, main.toString());

                        model_list.clear();

                        Object object = main.get("data");
                        JSONObject data;
                        JSONArray applications;

                        if (object instanceof JSONObject) {
                            data = main.getJSONObject("data");
                            applications = data.getJSONArray("applications");
                        } else {
                            applications = main.getJSONArray("data");
                        }

                        db.deleteFilteredJobTable(FILTER_TYPE);

                        for (int i = 0; i < applications.length(); i++) {
                            JSONObject hit_object = applications.getJSONObject(i);
                            model_list.add(db.setJobFromJson(hit_object, FILTER_TYPE));
                        }

                        populateJobs();

                    } catch (JSONException e) {
                        helpers.toastMessage(getString(R.string.error_server));
                        populateJobs();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (getActivity() != null) {
                    swipe_refresh.setRefreshing(false);
                    Helpers.logThis(TAG_LOG, t.toString());
                    if (helpers.validateInternetConnection()) {
                        helpers.toastMessage(getString(R.string.error_server));
                    } else {
                        helpers.toastMessage(getString(R.string.error_connection));
                    }
                    populateJobs();
                }
            }
        });
    }

}