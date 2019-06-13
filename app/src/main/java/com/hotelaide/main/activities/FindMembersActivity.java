package com.hotelaide.main.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.hotelaide.R;
import com.hotelaide.main.adapters.MembersAdapter;
import com.hotelaide.main.models.MemberModel;
import com.hotelaide.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.hotelaide.BuildConfig.ALGOLIA_APP_ID;
import static com.hotelaide.BuildConfig.ALGOLIA_INDEX_MEMBERS;
import static com.hotelaide.BuildConfig.ALGOLIA_SEARCH_API_KEY;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_SEARCH_MEMBERS;

public class FindMembersActivity extends ParentActivity {

    private final String TAG_LOG = "FIND MEMBERS";

    // HEADER ITEMS --------------------------------------------------------------------------------
    private EditText
            et_search;

    // SEARCH TOOLS --------------------------------------------------------------------------------
    private Index index;
    private Query query;
    public static int
            CURRENT_PAGE = 0,
            LAST_PAGE = 1;

    private CompletionHandler completionHandler;
    private static final int HITS_PER_PAGE = 35;

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

        setListeners();

        search();

    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        // HEADER ELEMENTS -------------------------------------------------------------------------
        et_search = findViewById(R.id.et_search);
        et_search.setHint(R.string.txt_search_members);

        // SEARCH FUNCTIONALITY --------------------------------------------------------------------
//        FirebaseApp.initializeApp(FindMembersActivity.this);
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference parent_ref = database.getReference();
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
                        "Instant search as you type"
                }
        );
    }

    private void setListeners() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                search();
            }
        });

        completionHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                try {
                    handleResponse(content, error, true);
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

    }

    // SEARCH FUNCTIONALITY ------------------------------------------------------------------------
    private void initializeAlgolia() {
        Client client = new Client(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY);
        index = client.getIndex(ALGOLIA_INDEX_MEMBERS);
        query = new Query();
        query.setAttributesToRetrieve("id", "avatar", "first_name", "last_name", "about_me");
        query.setHitsPerPage(HITS_PER_PAGE);
    }

    private void search() {
        query.setFilters("");
        if (et_search.getText().toString().length() > 0) {
            query.setQuery(helpers.fetchFromEditText(et_search));
        } else {
            query.setQuery("");
        }
        index.searchAsync(query, completionHandler);
    }

    public void loadMoreResults() {
        helpers.toastMessage("Loading...");
        Query loadMoreQuery = new Query(query);
        loadMoreQuery.setPage(CURRENT_PAGE + 1);
        index.searchAsync(loadMoreQuery, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                try {
                    handleResponse(content, error, false);
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
        });
    }

    private void handleResponse(JSONObject content, AlgoliaException error, boolean afresh) throws JSONException {
        if (content != null) {
            if (afresh)
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

            CURRENT_PAGE = content.getInt("page");
            LAST_PAGE = content.getInt("nbPages");

            if (model_list.size() < 1) {
                noListItems();
            } else {
                adapter.notifyDataSetChanged();
            }
        } else if (error != null) {
            Helpers.logThis(TAG_LOG, error.toString());
        }
    }

    private void noListItems() {
        recycler_view.invalidate();
        model_list.clear();
        MemberModel member_model = new MemberModel();
        model_list.add(member_model);
        adapter.notifyDataSetChanged();
    }

}
