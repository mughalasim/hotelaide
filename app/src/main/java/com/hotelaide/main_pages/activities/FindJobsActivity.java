package com.hotelaide.main_pages.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.algolia.instantsearch.helpers.InstantSearch;
import com.algolia.instantsearch.helpers.Searcher;
import com.hotelaide.R;
import com.hotelaide.utils.SharedPrefs;

import static com.hotelaide.BuildConfig.ALGOLIA_APP_ID;
import static com.hotelaide.BuildConfig.ALGOLIA_INDEX_NAME;
import static com.hotelaide.BuildConfig.ALGOLIA_SEARCH_API_KEY;
import static com.hotelaide.utils.Helpers.START_FIRST_TIME;
import static com.hotelaide.utils.Helpers.START_LAUNCH;
import static com.hotelaide.utils.Helpers.START_RETURN;

public class FindJobsActivity extends AppCompatActivity {

    private final String TAG_LOG = "FIND JOBS";
    Searcher searcher;

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searcher = Searcher.create(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME);
        InstantSearch helper = new InstantSearch(FindJobsActivity.this, searcher);

        helper.search();

        setContentView(R.layout.activity_find_jobs);


//        initialize(R.id.drawer_find_jobs, TAG_LOG);

        findAllViews();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        searcher.destroy();
        super.onDestroy();
    }

    // BASIC FUNCTIONS =============================================================================

    private void findAllViews() {

    }


}
