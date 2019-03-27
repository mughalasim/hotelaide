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
import com.hotelaide.main.adapters.MembersAdapter;
import com.hotelaide.main.models.MemberModel;
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
import static com.hotelaide.BuildConfig.ALGOLIA_INDEX_MEMBERS;
import static com.hotelaide.BuildConfig.ALGOLIA_SEARCH_API_KEY;
import static com.hotelaide.utils.StaticVariables.CATEGORIES_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.COUNTY_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_SEARCH_MEMBERS;
import static com.hotelaide.utils.StaticVariables.JOB_TYPE_TABLE_NAME;

public class FindMembersActivity extends ParentActivity {

    private final String TAG_LOG = "FIND MEMBERS";

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
    private ArrayList<MemberModel> model_list = new ArrayList<>();
    private MembersAdapter adapter;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_find_members);

        initialize(R.id.drawer_find_members, getString(R.string.drawer_find_members));

        findAllViews();

        initializeAlgolia();

        setSearchListener();

        setListeners();

        setTextWatcher();

        clearAllFilters();

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
        btn_add_filter.setVisibility(View.GONE);
        et_search = findViewById(R.id.et_search);
        et_search.setHint(R.string.txt_search_members);
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
        adapter = new MembersAdapter(model_list);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(FindMembersActivity.this);
        recycler_view.setLayoutManager(layoutManager);


        helpers.setTarget(
                FindMembersActivity.this,
                FIRST_LAUNCH_SEARCH_MEMBERS,
                new View[]{
                        findViewById(R.id.et_search)
                },
                new String[]{
                        "Search"
                },
                new String[]{
                        "Instant search as you type if you're online or offline"
                }
        );
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
                }
//                else {
//                    searchDatabase();
//                }
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
                }
//                else {
//                    searchDatabase();
//                }
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
        index = client.getIndex(ALGOLIA_INDEX_MEMBERS);
        query = new Query();
        query.setAttributesToRetrieve("id", "avatar", "first_name", "last_name", "about_me");
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
            query.setQuery(fetchFromEditText(et_search));
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
                        Helpers.logThis(TAG_LOG, content.toString());

                        model_list.clear();

                        if (content.getInt("nbHits") > 0) {
                            JSONArray hits_array = content.getJSONArray("hits");
                            int length = hits_array.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject hit_object = hits_array.getJSONObject(i);

                                MemberModel member_model = new MemberModel();

                                member_model.id = hit_object.getInt("id");
                                member_model.first_name = hit_object.getString("first_name");
                                member_model.last_name = hit_object.getString("last_name");
                                member_model.avatar = hit_object.getString("avatar");
                                member_model.about_me = hit_object.getString("about_me");

                                model_list.add(member_model);
                            }
                        }

                        if (model_list.size() <= 0) {
                            noListItems();
                        }

//                        recycler_view.invalidate();
//
//                        adapter.updateData(model_list);
//
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
        model_list.add(new MemberModel());
        adapter.updateData(model_list);
        adapter.notifyDataSetChanged();
    }

}
