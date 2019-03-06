package com.hotelaide.main.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.android.material.chip.Chip;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main.adapters.FindJobsAdapter;
import com.hotelaide.main.models.JobModel;
import com.hotelaide.utils.Helpers;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.hotelaide.BuildConfig.ALGOLIA_APP_ID;
import static com.hotelaide.BuildConfig.ALGOLIA_INDEX_JOB;
import static com.hotelaide.BuildConfig.ALGOLIA_SEARCH_API_KEY;
import static com.hotelaide.utils.StaticVariables.CATEGORIES_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.COUNTY_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_FIRST_TIME;
import static com.hotelaide.utils.StaticVariables.EXTRA_STRING;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_SEARCH;
import static com.hotelaide.utils.StaticVariables.JOB_TYPE_TABLE_NAME;

public class FindJobsActivity extends ParentActivity {

    private final String TAG_LOG = "FIND JOBS";

    // HEADER ITEMS --------------------------------------------------------------------------------
    private ImageView
            btn_add_filter;
    private EditText
            et_search;
    private Chip
            txt_filter_location,
            txt_filter_category,
            txt_filter_type;


    // BOTTOM SLIDING PANEL ------------------------------------------------------------------------
    private SlidingUpPanelLayout
            sliding_panel;
    private Spinner
            spinner_location,
            spinner_category,
            spinner_type;
    private TextView
            btn_confirm,
            btn_cancel;


    // SEARCH TOOLS --------------------------------------------------------------------------------
//    private Client client;
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
    private CompletionHandler completionHandler;
    private static final int HITS_PER_PAGE = 20;


    // SEARCH ADAPTER ITEMS ------------------------------------------------------------------------
    private LinearLayoutManager layoutManager;
    private RecyclerView recycler_view;
    private ArrayList<JobModel> model_list = new ArrayList<>();
    private FindJobsAdapter adapter;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_find_jobs);

        initialize(R.id.drawer_find_jobs, getString(R.string.drawer_find_jobs));

        findAllViews();

        initializeAlgolia();

        setSearchListener();

        setListeners();

        setTextWatcher();

        searchDatabase();

        clearAllFilters();

        handleExtraBundles();

    }

    @Override
    public void onBackPressed() {
        if (sliding_panel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            finish();
        }
    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        sliding_panel = findViewById(R.id.sliding_panel);

        // HEADER ELEMENTS -------------------------------------------------------------------------
        btn_add_filter = findViewById(R.id.btn_add_filter);
        et_search = findViewById(R.id.et_search);
        txt_filter_location = findViewById(R.id.txt_filter_location);
        txt_filter_type = findViewById(R.id.txt_filter_type);
        txt_filter_category = findViewById(R.id.txt_filter_category);

        // FILTER ELEMENTS -------------------------------------------------------------------------
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_cancel.setText(getString(R.string.txt_clear));
        btn_confirm.setText(getString(R.string.txt_search));
        spinner_location = findViewById(R.id.spinner_county);
        spinner_category = findViewById(R.id.spinner_category);
        spinner_type = findViewById(R.id.spinner_type);

        spinner_location.setAdapter(new ArrayAdapter<>(
                this,
                R.layout.list_item_spinner,
                db.getAllFilterItems(COUNTY_TABLE_NAME)
        ));
        spinner_type.setAdapter(new ArrayAdapter<>(
                this,
                R.layout.list_item_spinner,
                db.getAllFilterItems(JOB_TYPE_TABLE_NAME)
        ));
        spinner_category.setAdapter(new ArrayAdapter<>(
                this,
                R.layout.list_item_spinner,
                db.getAllFilterItems(CATEGORIES_TABLE_NAME)
        ));


        // SEARCH FUNCTIONALITY --------------------------------------------------------------------
        recycler_view = findViewById(R.id.recycler_view);
        adapter = new FindJobsAdapter(model_list, getString(R.string.error_no_jobs_found));
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(FindJobsActivity.this);
        recycler_view.setLayoutManager(layoutManager);


        helpers.setTarget(
                FindJobsActivity.this,
                FIRST_LAUNCH_SEARCH,
                new View[]{
                        findViewById(R.id.btn_add_filter),
                        findViewById(R.id.et_search)
                },
                new String[]{
                        "Add a Filter",
                        "Search"
                },
                new String[]{
                        "This will allow you to add certain filters to your search criteria",
                        "Instant search as you type if youre online or offline"
                }
        );
    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString(EXTRA_STRING) != null) {
            et_search.setText(extras.getString(EXTRA_STRING));
        }
    }

    private void setTextWatcher() {
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
    }

    private void setListeners() {
        btn_add_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFilters();
                sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        setAllListenersForFilter(spinner_location, txt_filter_location);
        setAllListenersForFilter(spinner_category, txt_filter_category);
        setAllListenersForFilter(spinner_type, txt_filter_type);


    }

    private void setAllListenersForFilter(final Spinner spinner, final Chip chip) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    chip.setText("");
                    chip.setVisibility(View.GONE);
                } else {
                    chip.setText(spinner.getSelectedItem().toString());
                    chip.setVisibility(View.VISIBLE);
                }
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

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setSelection(0);
            }
        });

    }

    // SEARCH FUNCTIONALITY ------------------------------------------------------------------------
    private void initializeAlgolia() {
        Client client = new Client(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY);
        index = client.getIndex(ALGOLIA_INDEX_JOB);
        query = new Query();
        query.setAttributesToRetrieve("id", "title", "posted_on", "establishment.id", "establishment.image", "establishment.full_address");
        query.setHitsPerPage(HITS_PER_PAGE);
    }

    private void searchOnline() {
        if (spinner_location.getSelectedItemPosition() != 0) {
            setFilter(BuildConfig.FILTER_COUNTY, spinner_location);
        } else if (spinner_category.getSelectedItemPosition() != 0) {
            setFilter(BuildConfig.FILTER_CATEGORY, spinner_category);
        } else if (spinner_type.getSelectedItemPosition() != 0) {
            setFilter(BuildConfig.FILTER_JOB_TYPE, spinner_type);
        } else {
            query.setFilters("");
        }

        if (et_search.getText().toString().length() > 0) {
            query.setQuery(et_search.getText().toString());
        } else {
            query.setQuery("");
        }
        index.searchAsync(query, completionHandler);
    }

    private void setFilter(String filter_type, Spinner spinner) {
        try {
            query.setFilters(filter_type + URLEncoder.encode(spinner.getSelectedItem().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            query.setFilters("");
        }
    }

    private void setSearchListener() {

        completionHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                try {
                    if (content != null) {
//                        Helpers.logThis(TAG_LOG, content.toString());

                        model_list.clear();

                        if (content.getInt("nbHits") > 0) {
                            JSONArray hits_array = content.getJSONArray("hits");
                            for (int i = 0; i < hits_array.length(); i++) {
                                JSONObject hit_object = hits_array.getJSONObject(i);
                                model_list.add(db.setJobFromJson(hit_object, ""));
                            }
                        }

                        if (model_list.size() <= 0) {
                            noListItems();
                        }
                        recycler_view.invalidate();
                        adapter.updateData(model_list);
                        adapter.notifyDataSetChanged();

                    } else if (error != null) {
                        Helpers.logThis(TAG_LOG, error.toString());

                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Helpers.logThis(TAG_LOG, e.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Helpers.logThis(TAG_LOG, e.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Helpers.logThis(TAG_LOG, e.toString());
                }
            }
        };

        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                    if (helpers.validateInternetConnection()
                            && continue_pagination
                            && (visibleItemCount + pastVisibleItems) >= totalItemCount
                            && LAST_PAGE != CURRENT_PAGE) {
//                        loadMoreResults();
                        Helpers.logThis(TAG_LOG, "Load more");
                        continue_pagination = false;
                    }
                }
            }
        });


    }

//    private void loadMoreResults() {
//        Query loadMoreQuery = new Query(query);
//        loadMoreQuery.setPage(CURRENT_PAGE++);
//        index.searchAsync(loadMoreQuery, new CompletionHandler() {
//            @Override
//            public void requestCompleted(JSONObject content, AlgoliaException error) {
//                try {
//                    if (content != null && error == null) {
//                        if (content.getInt("nbHits") > 0) {
//                            JSONArray hits_array = content.getJSONArray("hits");
//                            for (int i = 0; i < hits_array.length(); i++) {
//                                JSONObject hit_object = hits_array.getJSONObject(i);
//                                model_list.add(db.setJobFromJson(hit_object));
//                            }
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                    Helpers.logThis(TAG_LOG, e.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Helpers.logThis(TAG_LOG, e.toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Helpers.logThis(TAG_LOG, e.toString());
//                }
//            }
//        });
//    }

    private void clearAllFilters() {
        spinner_location.setSelection(0);
        spinner_type.setSelection(0);
        spinner_category.setSelection(0);
    }

    private void searchDatabase() {
        model_list.clear();
        if (spinner_location.getSelectedItemPosition() != 0) {
            model_list = db.getAllJobModelsBySearch(fetchFromEditText(et_search), spinner_location.getSelectedItem().toString());
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
