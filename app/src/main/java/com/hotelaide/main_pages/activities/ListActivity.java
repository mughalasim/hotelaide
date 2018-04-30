package com.hotelaide.main_pages.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hotelaide.R;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;

//import static com.hotelaide.main_pages.eo_activities.RestaurantSearchActivity.EXTRA_CITY_ID;
//import static com.hotelaide.main_pages.eo_activities.RestaurantSearchActivity.EXTRA_DISPLAY_NAME;
//import static com.hotelaide.main_pages.eo_activities.RestaurantSearchActivity.EXTRA_SEARCH_FIELD;
//import static com.hotelaide.main_pages.eo_activities.RestaurantSearchActivity.EXTRA_TAG_VALUE;
//import static com.hotelaide.main_pages.eo_activities.RestaurantSearchActivity.STR_SEARCH_AREA;
//import static com.hotelaide.main_pages.eo_activities.RestaurantSearchActivity.STR_SEARCH_CITY;
//import static com.hotelaide.main_pages.eo_activities.RestaurantSearchActivity.STR_SEARCH_CUISINE;
//import static com.hotelaide.main_pages.eo_activities.RestaurantSearchActivity.STR_SEARCH_TYPE;
import static com.hotelaide.utils.Database.AREA_NAME;
import static com.hotelaide.utils.Database.AREA_TABLE_ID;
import static com.hotelaide.utils.Database.AREA_TABLE_NAME;
import static com.hotelaide.utils.Database.CITY_NAME;
import static com.hotelaide.utils.Database.CITY_TABLE_ID;
import static com.hotelaide.utils.Database.CITY_TABLE_NAME;
import static com.hotelaide.utils.Database.CUISINE_NAME;
import static com.hotelaide.utils.Database.CUISINE_TABLE_ID;
//import static eatout.restaurantguide.utils.Database.CUISINE_TABLE_NAME;
//import static eatout.restaurantguide.utils.Database.TYPE_NAME;
//import static eatout.restaurantguide.utils.Database.TYPE_TABLE_ID;
//import static eatout.restaurantguide.utils.Database.TYPE_TABLE_NAME;

public class ListActivity extends AppCompatActivity {

    private Database db;

    private Helpers helper;

    private Toolbar toolbar;

    private ListView list_items;

    private TextView toolbar_text;

    private String
            PAGE_TITLE = "SELECT ",
            SEARCH_FIELD = "",
            SEARCH_CITY_ID = "";

    private final String
            TAG_LOG = "LIST PAGE";

    private SwipeRefreshLayout swiperefresh;

    private BroadcastReceiver receiver;
    private IntentFilter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        helper = new Helpers(ListActivity.this);

        db = new Database();

        findAllViews();

        setUpToolBar();

        setExtraBundles();

        setListeners();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            if(receiver!=null){
                unregisterReceiver(receiver);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenAsyncCompletedBroadcast();
    }

    private void findAllViews() {
        toolbar = findViewById(R.id.toolbar);
        list_items = findViewById(R.id.list_items);
        swiperefresh = findViewById(R.id.swiperefresh);
    }

    private void setUpToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            final LinearLayout toolbar_image = toolbar.findViewById(R.id.toolbar_image);
            toolbar_image.setVisibility(View.GONE);

            toolbar_text = toolbar.findViewById(R.id.toolbar_text);
            toolbar_text.setVisibility(View.VISIBLE);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void setListeners() {
        list_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                Helpers.LogThis(TAG_LOG, text);
                Intent intent = new Intent();
//                intent.putExtra(EXTRA_SEARCH_FIELD, SEARCH_FIELD);
//                intent.putExtra(EXTRA_DISPLAY_NAME, text);

                String tagValue = "";
//                switch (SEARCH_FIELD) {
//                    case STR_SEARCH_CITY:
//                        tagValue = db.getListIDByName(CITY_TABLE_NAME, CITY_NAME, CITY_TABLE_ID, text);
//                        break;
//
//                    case STR_SEARCH_AREA:
//                        tagValue = db.getListIDByName(AREA_TABLE_NAME, AREA_NAME, AREA_TABLE_ID, text);
//                        break;
//
//                    case STR_SEARCH_TYPE:
//                        tagValue = db.getListIDByName(TYPE_TABLE_NAME, TYPE_NAME, TYPE_TABLE_ID, text);
//                        break;
//
//                    case STR_SEARCH_CUISINE:
//                        tagValue = db.getListIDByName(CUISINE_TABLE_NAME, CUISINE_NAME, CUISINE_TABLE_ID, text);
//                        break;
//                }
//                intent.putExtra(EXTRA_TAG_VALUE, tagValue);
//                setResult(1, intent);
                finish();

            }
        });

        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        asyncGetListItems();
                    }
                }
        );
    }

    private void setExtraBundles() {
        Bundle extras = getIntent().getExtras();
//        if (extras != null && extras.getString(EXTRA_SEARCH_FIELD) != null) {
//            SEARCH_FIELD = extras.getString(EXTRA_SEARCH_FIELD);
//
//            if (extras.getString(EXTRA_CITY_ID) != null) {
//                SEARCH_CITY_ID = extras.getString(EXTRA_CITY_ID);
//            }
//
//            assert SEARCH_FIELD != null;
//            setUpListViewFromDB();
//
//        } else {
//            helper.ToastMessage(ListActivity.this, getString(R.string.error_unknown));
//            finish();
//        }
    }

    private void setUpListViewFromDB() {
        ArrayAdapter<String> adapter;
//        switch (SEARCH_FIELD) {
//            case STR_SEARCH_CITY:
//                toolbar_text.setText(PAGE_TITLE.concat(STR_SEARCH_CITY));
//                adapter = new ArrayAdapter<>(this, R.layout.list_item_select,
//                        db.getListItems(CITY_TABLE_NAME, CITY_NAME));
//                list_items.setAdapter(adapter);
//                getListCount();
//                break;
//
//            case STR_SEARCH_AREA:
//                toolbar_text.setText(PAGE_TITLE.concat(STR_SEARCH_AREA));
//                adapter = new ArrayAdapter<>(this, R.layout.list_item_select,
//                        db.getAreasBasedOnCity(SEARCH_CITY_ID));
//                Helpers.LogThis(TAG_LOG, "CITY ID: " + SEARCH_CITY_ID);
//                list_items.setAdapter(adapter);
//                getListCount();
//                break;
//
//            case STR_SEARCH_TYPE:
//                toolbar_text.setText(PAGE_TITLE.concat(STR_SEARCH_TYPE));
//                adapter = new ArrayAdapter<>(this, R.layout.list_item_select,
//                        db.getListItems(TYPE_TABLE_NAME, TYPE_NAME));
//                list_items.setAdapter(adapter);
//                getListCount();
//                break;
//
//            case STR_SEARCH_CUISINE:
//                toolbar_text.setText(PAGE_TITLE.concat(STR_SEARCH_CUISINE));
//                adapter = new ArrayAdapter<>(this, R.layout.list_item_select,
//                        db.getListItems(CUISINE_TABLE_NAME, CUISINE_NAME));
//                list_items.setAdapter(adapter);
//                getListCount();
//                break;
//        }
    }

    private void getListCount() {
        Helpers.LogThis(TAG_LOG, "LIST ADAPTER COUNT " + list_items.getAdapter().getCount());

        if (list_items.getAdapter().getCount() <= 1) {
            Helpers.LogThis(TAG_LOG, "LIST EMPTY");
            asyncGetListItems();
            swiperefresh.setRefreshing(true);
        } else {
            swiperefresh.setRefreshing(false);
        }
    }

    private void asyncGetListItems() {
//        switch (SEARCH_FIELD) {
//            case STR_SEARCH_CITY:
//                helper.asyncGetCities();
//                break;
//
//            case STR_SEARCH_AREA:
//                helper.asyncGetCities();
//                break;
//
//            case STR_SEARCH_TYPE:
//                helper.asyncGetTypes();
//                break;
//
//            case STR_SEARCH_CUISINE:
//                helper.asyncGetCuisines();
//                break;
//        }
    }

    private void listenAsyncCompletedBroadcast() {
        filter = new IntentFilter();
        filter.addAction(Helpers.BroadcastValueAsyncCompleted);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Helpers.LogThis(TAG_LOG, "ASYNC COMPLETED");
                swiperefresh.setRefreshing(false);
                setUpListViewFromDB();
            }
        };
        registerReceiver(receiver, filter);
    }

}
