package com.hotelaide.main_pages.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.bumptech.glide.Glide;
import com.hotelaide.R;
import com.hotelaide.main_pages.models.CountyModel;
import com.hotelaide.main_pages.models.JobModel;
import com.hotelaide.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.hotelaide.BuildConfig.ALGOLIA_APP_ID;
import static com.hotelaide.BuildConfig.ALGOLIA_INDEX_NAME;
import static com.hotelaide.BuildConfig.ALGOLIA_SEARCH_API_KEY;

public class FindJobsActivity extends ParentActivity {

    private final String TAG_LOG = "FIND JOBS";
    private EditText et_search;

    // SEARCH TOOLS -------------------------------------
    private Client client;
    private Index index;
    private Query query;
    private int
            pastVisibleItems,
            visibleItemCount,
            totalItemCount,
            CURRENT_PAGE = 0,
            LAST_PAGE = 1;
    private boolean
            continue_pagination = true;
    private LinearLayoutManager layoutManager;
    private CompletionHandler completionHandler;
    private static final int HITS_PER_PAGE = 20;

    private Spinner
            spinner_county;

    // SEARCH FUNCTIONALITY ------------------------------
    private RecyclerView recycler_view;
    private ArrayList<JobModel> model_list = new ArrayList<>();
    private FindJobAdapter adapter;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_find_jobs);

        initialize(R.id.drawer_find_jobs, TAG_LOG);

        findAllViews();

        initializeAlgolia();

        setUpSearchListener();

        setUpTextWatcher();

        searchDatabase();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        et_search = findViewById(R.id.et_search);

        // SEARCH FUNCTIONALITY ------------------------------
        recycler_view = findViewById(R.id.recycler_view);
        adapter = new FindJobAdapter(model_list);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(FindJobsActivity.this);
        recycler_view.setLayoutManager(layoutManager);

        spinner_county = findViewById(R.id.spinner_county);
        ArrayAdapter<CountyModel> dataAdapter1 = new ArrayAdapter<>(
                this,
                R.layout.list_item_spinner,
                db.getAllCounties()
        );
        spinner_county.setAdapter(dataAdapter1);


    }

    private void setUpTextWatcher() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (helpers.validateInternetConnection()) {
                    searchOnline();
                } else {
                    searchDatabase();
                }
            }
        });

        spinner_county.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (helpers.validateInternetConnection()) {
                    searchOnline();
                } else {
                    searchDatabase();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initializeAlgolia() {
        Client client = new Client(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY);
        index = client.getIndex(ALGOLIA_INDEX_NAME);
        query = new Query();
        query.setAttributesToRetrieve("id", "title", "posted_on", "hotel", "location");
        query.setHitsPerPage(HITS_PER_PAGE);
    }

    private void searchOnline() {
        if (spinner_county.getSelectedItemPosition() == 0) {
            query.setFilters("");
        } else {
            try {
                query.setFilters("location.county_name:" + URLEncoder.encode(spinner_county.getSelectedItem().toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                query.setFilters("");
            }
        }
        if (et_search.getText().toString().length() > 0) {
            query.setQuery(et_search.getText().toString());
        } else {
            query.setQuery("");
        }
        index.searchAsync(query, completionHandler);
    }

    private void setUpSearchListener() {

        completionHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                try {
                    if (content != null) {
                        Helpers.LogThis(TAG_LOG, content.toString());

                        model_list.clear();

                        if (content.getInt("nbHits") > 0) {
                            JSONArray hits_array = content.getJSONArray("hits");
                            for (int i = 0; i < hits_array.length(); i++) {
                                JSONObject hit_object = hits_array.getJSONObject(i);
                                model_list.add(db.setJobFromJson(hit_object));
                            }
                        }

                        if (model_list.size() <= 0) {
                            noListItems();
                        }
                        recycler_view.invalidate();
                        adapter.updateData(model_list);
                        adapter.notifyDataSetChanged();

                    } else if (error != null) {
                        Helpers.LogThis(TAG_LOG, error.toString());

                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Helpers.LogThis(TAG_LOG, e.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Helpers.LogThis(TAG_LOG, e.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Helpers.LogThis(TAG_LOG, e.toString());
                }
            }
        };

        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                    if (helpers.validateInternetConnection()
                            && continue_pagination
                            && (visibleItemCount + pastVisibleItems) >= totalItemCount
                            && LAST_PAGE != CURRENT_PAGE) {

//                        loadMoreResults();
                        Helpers.LogThis(TAG_LOG, "Load more");
                        continue_pagination = false;
                    }
                }
            }
        });


    }

    private void loadMoreResults() {
        Query loadMoreQuery = new Query(query);
        loadMoreQuery.setPage(CURRENT_PAGE++);
        index.searchAsync(loadMoreQuery, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                try {
                    if (content != null && error == null) {
                        if (content.getInt("nbHits") > 0) {
                            JSONArray hits_array = content.getJSONArray("hits");
                            for (int i = 0; i < hits_array.length(); i++) {
                                JSONObject hit_object = hits_array.getJSONObject(i);
                                model_list.add(db.setJobFromJson(hit_object));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Helpers.LogThis(TAG_LOG, e.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Helpers.LogThis(TAG_LOG, e.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Helpers.LogThis(TAG_LOG, e.toString());
                }
            }
        });
    }


    //==============================================================================================
    //==============================================================================================
    // ADAPTER CLASS ===============================================================================
    public class FindJobAdapter extends RecyclerView.Adapter<FindJobAdapter.ViewHolder> {
        private final ArrayList<JobModel> jobModels;
        private final String TAG_LOG = "FIND JOB ADAPTER";
        private Context context;

        class ViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout
                    no_list_item;
            CardView
                    list_item;
            final TextView
                    txt_no_results,
                    txt_name,
                    txt_location,
                    txt_posted_on;
            final ImageView
                    img_image;

            ViewHolder(View v) {
                super(v);
                txt_no_results = v.findViewById(R.id.txt_no_results);
                txt_name = v.findViewById(R.id.txt_name);
                txt_location = v.findViewById(R.id.txt_location);
                txt_posted_on = v.findViewById(R.id.txt_posted_on);
                img_image = v.findViewById(R.id.img_image);
                no_list_item = v.findViewById(R.id.no_list_items);
                list_item = v.findViewById(R.id.list_item);
            }

        }

        public FindJobAdapter(ArrayList<JobModel> jobModels) {
            this.jobModels = jobModels;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_find_jobs, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            context = holder.itemView.getContext();
            helpers = new Helpers(context);

            final JobModel jobModel = jobModels.get(position);

            if (jobModel.id == 0) {
                holder.no_list_item.setVisibility(View.VISIBLE);
                holder.list_item.setVisibility(View.GONE);
                if (helpers.validateInternetConnection()) {
                    holder.txt_no_results.setText(R.string.error_no_jobs);
                } else {
                    holder.txt_no_results.setText(R.string.error_connection);
                }

            } else {
                holder.no_list_item.setVisibility(View.GONE);
                holder.list_item.setVisibility(View.VISIBLE);

                holder.txt_name.setText(jobModel.name);
                holder.txt_posted_on.setText(jobModel.posted_on);
                holder.txt_location.setText(jobModel.hotel_location);
                Glide.with(context).load(jobModel.hotel_image).into(holder.img_image);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return jobModels.size();
        }

        public void removeItem(int position) {
            jobModels.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, jobModels.size());
        }

        public void updateData(ArrayList<JobModel> view_model) {
            jobModels.clear();
            jobModels.addAll(view_model);
            notifyDataSetChanged();
        }

    }


    private void searchDatabase() {
        model_list.clear();
        if (spinner_county.getSelectedItemPosition() != 0) {
            model_list = db.getAllJobModelsBySearch(fetchFromEditText(et_search), spinner_county.getSelectedItem().toString());
        } else {
            model_list = db.getAllJobModelsBySearch(fetchFromEditText(et_search), "");
        }
        recycler_view.invalidate();
        adapter.updateData(model_list);
        adapter.notifyDataSetChanged();
        if (model_list.size() < 1) {
            noListItems();
        }
    }

    private String fetchFromEditText(EditText editText) {
        String data = "";
        if (editText.getText().toString().length() > 1) {
            data = editText.getText().toString();
        }
        return data;
    }

    private void noListItems() {
        recycler_view.invalidate();
        model_list.clear();
        JobModel jobModel = new JobModel();
        model_list.add(jobModel);
        adapter.notifyDataSetChanged();
    }

}
