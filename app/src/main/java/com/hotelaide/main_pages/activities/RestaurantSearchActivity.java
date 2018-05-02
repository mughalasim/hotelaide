package com.hotelaide.main_pages.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.hotelaide.R;
import com.hotelaide.main_pages.adapters.RestaurantAdapter;
import com.hotelaide.main_pages.models.RestaurantModel;
import com.hotelaide.services.RestaurantService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.Database.RETRIEVE_ALL_RESTAURANTS;
import static com.hotelaide.utils.Helpers.ADAPTER_DEFAULT;
import static com.hotelaide.utils.Helpers.ADAPTER_DISTANCE;
import static com.hotelaide.utils.Helpers.FLAG_FALSE;
import static com.hotelaide.utils.Helpers.ORDER_ASCENDING;
import static com.hotelaide.utils.Helpers.SORT_A_Z;
import static com.hotelaide.utils.Helpers.SORT_FEATURED;
import static com.hotelaide.utils.Helpers.SORT_NEARBY;
import static com.hotelaide.utils.Helpers.SORT_RATING;

public class RestaurantSearchActivity extends ParentActivity {

    private ArrayList<RestaurantModel>
            restaurantsList = new ArrayList<>();

    private RecyclerView
            recyclerView;

    private final LinearLayoutManager
            layoutManager = new LinearLayoutManager(this);

    private SwipeRefreshLayout
            swiperefresh;

    private RestaurantAdapter
            restaurantAdapter;

    private boolean
            isBlockedScrollView = false,
            continue_pagination = true;

    private int
            pastVisibleItems,
            visibleItemCount,
            totalItemCount,
            CURRENT_PAGE = 0,
            LAST_PAGE = 1;

    private String
            STR_SEARCH_QUERRY = "",
            STR_SORT = "",
            STR_ORDER = "",
            STR_DISCOUNT_ONLY = "0",
            STR_OFFERS_ONLY = "0",
            STR_LATITUDE = "",
            STR_LONGITUDE = "";

    private final String
            TAG_CLICKED = "1",
            TAG_NOT_CLICKED = "0",
            TAG_LOG = "SEARCH";

    public static final String
            EXTRA_SEARCH_FIELD = "SEARCH_FIELD",
            EXTRA_CITY_ID = "CITY_ID",
            EXTRA_DISPLAY_NAME = "DISPLAY_NAME",
            EXTRA_TAG_VALUE = "TAG_VALUE",
            STR_SEARCH_CITY = "CITY",
            STR_SEARCH_AREA = "AREA",
            STR_SEARCH_TYPE = "TYPE",
            STR_SEARCH_CUISINE = "CUISINE";

    public static final int RESULT_CODE = 1;

    private RelativeLayout
            RL_search_city,
            RL_search_area,
            RL_search_type,
            RL_search_cuisine,
            RL_search;

    private ImageView search_bar_icon, search_bar_cancel;

    private TextView
            category_nearby,
            category_featured,
            category_az,
            category_rating,
            search_bar_text,
            search_by_city,
            search_by_area,
            search_by_type,
            search_by_cuisine;

    private EditText
            search_criteria;

    private LocationManager
            locationManager;

    private LocationListener
            locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_restaurant_search);
        try {

            initialize(R.id.find_restaurants, "");

            findAllViews();

            setListeners();

            updateDrawer();

            resetCategories();

            resetSearchBy();

            handleExtraBundles();

            helper.setTracker(TAG_LOG);

        } catch (Exception e) {
            e.printStackTrace();
            Helpers.LogThis(TAG_LOG, e.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        restaurantAdapter.notifyDataSetChanged();
        updateDrawer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.gc();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(RestaurantSearchActivity.this, HomeActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (RL_search.getVisibility() == View.VISIBLE) {
            showSearchScreen(false);
            setSearchCriteria();
            if (!search_bar_text.getText().toString().equals("")) {
                asyncGetSearchResults(true);
            }
        } else {
            disableFetchLocation();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE && data != null) {
            Helpers.LogThis(TAG_LOG, "RESULT CODE: " + resultCode);
            Helpers.LogThis(TAG_LOG, "DATA: " + data.getStringExtra(EXTRA_SEARCH_FIELD));
            Helpers.LogThis(TAG_LOG, "DATA: " + data.getStringExtra(EXTRA_DISPLAY_NAME));
            Helpers.LogThis(TAG_LOG, "DATA: " + data.getStringExtra(EXTRA_TAG_VALUE));

            setSearchBy(
                    data.getStringExtra(EXTRA_SEARCH_FIELD),
                    data.getStringExtra(EXTRA_DISPLAY_NAME),
                    data.getStringExtra(EXTRA_TAG_VALUE)
            );
        }
    }

    // BASIC FUNCTIONS =============================================================================

    private void findAllViews() {

        swiperefresh = findViewById(R.id.swiperefresh);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.application_recycler);
        restaurantAdapter = new RestaurantAdapter(RestaurantSearchActivity.this, restaurantsList, ADAPTER_DEFAULT);
        recyclerView.setAdapter(restaurantAdapter);

        search_bar_text = findViewById(R.id.search_bar_text);
        search_bar_icon = findViewById(R.id.search_bar_icon);
        search_bar_cancel = findViewById(R.id.search_bar_cancel);

        search_by_city = findViewById(R.id.search_by_city);
        search_by_area = findViewById(R.id.search_by_area);
        search_by_type = findViewById(R.id.search_by_type);
        search_by_cuisine = findViewById(R.id.search_by_cuisine);

        category_nearby = findViewById(R.id.category_nearby);
        category_featured = findViewById(R.id.category_featured);
        category_az = findViewById(R.id.category_az);
        category_rating = findViewById(R.id.category_rating);

        recyclerView.setHasFixedSize(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        search_criteria = findViewById(R.id.search_criteria);
        helper.setDefaultEditTextSelectionMode(search_criteria);

        RL_search = findViewById(R.id.RL_search);

        RL_search_city = findViewById(R.id.RL_search_city);
        RL_search_area = findViewById(R.id.RL_search_area);
        RL_search_type = findViewById(R.id.RL_search_type);
        RL_search_cuisine = findViewById(R.id.RL_search_cuisine);

        showSearchScreen(false);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String STR_BUNDLE_EXTRA = extras.getString("extra");

            if (STR_BUNDLE_EXTRA != null && !STR_BUNDLE_EXTRA.equals("")) {
                switch (STR_BUNDLE_EXTRA) {
                    case "offer":
                        View category_featured = findViewById(R.id.category_featured);
                        category_featured.setTag(TAG_NOT_CLICKED);
                        btnCategorize(category_featured);
                        break;

                    case "nearby":
                        View nearby_view = findViewById(R.id.category_nearby);
                        nearby_view.setTag(TAG_NOT_CLICKED);
                        btnCategorize(nearby_view);
                        break;

                    case "rating":
                        View rating_view = findViewById(R.id.category_rating);
                        rating_view.setTag(TAG_NOT_CLICKED);
                        btnCategorize(rating_view);
                        break;

                    default:
                        startUp();
                        break;
                }
            } else {
                startUp();
            }
        } else {
            startUp();
        }
    }

    private void startUp() {
        fetchFromDB_ALL_RESTAURANTS();
    }

    private void setListeners() {
        search_bar_cancel.setVisibility(View.GONE);
        search_bar_icon.setVisibility(View.VISIBLE);

        search_bar_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (search_bar_text.getText().toString().length() > 1) {
                    search_bar_icon.setVisibility(View.GONE);
                    search_bar_cancel.setVisibility(View.VISIBLE);
                } else {
                    search_bar_icon.setVisibility(View.VISIBLE);
                    search_bar_cancel.setVisibility(View.GONE);
                }
            }
        });

        search_bar_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSearchBy();
                search_criteria.setText("");
                setSearchCriteria();
                resetCategories();
                fetchFromDB_ALL_RESTAURANTS();
            }
        });

        search_criteria.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(search_criteria.getWindowToken(), 0);
                    }
                    search();
                    return true;
                }
                return false;
            }
        });

        search_criteria.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!search_criteria.getText().toString().equals("")) {
                    STR_SEARCH_QUERRY = search_criteria.getText().toString().trim();
                } else {
                    STR_SEARCH_QUERRY = "";
                }
            }
        });

        RL_search_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = RestaurantSearchActivity.this;
                activity.startActivityForResult(new Intent(RestaurantSearchActivity.this, ListActivity.class)
                        .putExtra(EXTRA_SEARCH_FIELD, STR_SEARCH_CITY), RESULT_CODE
                );
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        RL_search_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = RestaurantSearchActivity.this;
                activity.startActivityForResult(new Intent(RestaurantSearchActivity.this, ListActivity.class)
                        .putExtra(EXTRA_SEARCH_FIELD, STR_SEARCH_AREA)
                        .putExtra(EXTRA_CITY_ID, search_by_city.getTag().toString()), RESULT_CODE
                );
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        RL_search_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = RestaurantSearchActivity.this;
                activity.startActivityForResult(new Intent(RestaurantSearchActivity.this, ListActivity.class)
                        .putExtra(EXTRA_SEARCH_FIELD, STR_SEARCH_TYPE), RESULT_CODE
                );
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        RL_search_cuisine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = RestaurantSearchActivity.this;
                activity.startActivityForResult(new Intent(RestaurantSearchActivity.this, ListActivity.class)
                        .putExtra(EXTRA_SEARCH_FIELD, STR_SEARCH_CUISINE), RESULT_CODE
                );
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        asyncGetSearchResults(true);
                    }
                }
        );

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                    if (continue_pagination
                            && (visibleItemCount + pastVisibleItems) >= totalItemCount
                            && LAST_PAGE != CURRENT_PAGE) {

                        swiperefresh.setRefreshing(true);
                        asyncGetSearchResults(false);
                        Helpers.LogThis(TAG_LOG, "Load more Restaurants");
                        continue_pagination = false;
                    }
                }
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isBlockedScrollView;
            }
        });
    }

    private void fetchFromDB_ALL_RESTAURANTS() {
        restaurantsList.clear();
        restaurantsList = db.getAllRestaurants(RETRIEVE_ALL_RESTAURANTS, "");
        recyclerView.invalidate();
        restaurantAdapter.updateData(restaurantsList);
        restaurantAdapter.notifyDataSetChanged();
        CURRENT_PAGE = LAST_PAGE;
        continue_pagination = false;
    }

    private void noRestaurants() {
        recyclerView.invalidate();
        restaurantsList.clear();
        RestaurantModel restaurantModel = new RestaurantModel();
        restaurantsList.add(restaurantModel);
        restaurantAdapter.notifyDataSetChanged();
    }


    // SEARCH MANAGEMENT ===========================================================================

    public void btnSearch(View view) {
        search();
    }

    private void search() {
        setSearchCriteria();
        showSearchScreen(false);
        asyncGetSearchResults(true);
    }

    private void resetSearchBy() {
        search_by_city.setText(db.getCityNameByID(Database.userModel.dob));
        search_by_city.setTag(Database.userModel.dob);

        if (search_by_city.getText().toString().equals("")) {
            search_by_city.setText(getString(R.string.txt_search_all));
        }

        search_by_area.setText(getString(R.string.txt_search_all));
        search_by_area.setTag("0");

        search_by_type.setText(getString(R.string.txt_search_all));
        search_by_type.setTag("0");

        search_by_cuisine.setText(getString(R.string.txt_search_all));
        search_by_cuisine.setTag("0");

    }

    private void showSearchScreen(Boolean show) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            RL_search.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            helper.animate_fade_in_up(RL_search);
            search_criteria.requestFocus();
            if (imm != null) {
                imm.showSoftInput(search_criteria, InputMethodManager.SHOW_IMPLICIT);
            }
        } else {
            RL_search.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            helper.animate_fade_in_up(recyclerView);
            if (imm != null) {
                imm.hideSoftInputFromWindow(search_criteria.getWindowToken(), 0);
            }
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    public void btnShowSearchScreen(View view) {
        showSearchScreen(true);
    }

    private void setSearchCriteria() {
        String criteria_string = "", area_string = "", cuisine_string = "", type_string = "";

        if (!STR_SEARCH_QUERRY.equals("")) {
            criteria_string = " for \"" + STR_SEARCH_QUERRY + "\"";
        }

        if (!search_by_area.getTag().toString().equals("0")) {
            area_string = " in " + search_by_area.getText().toString();
        }

        if (!search_by_cuisine.getTag().toString().equals("0")) {
            cuisine_string = " for " + search_by_cuisine.getText().toString() + " Restaurants";
        }

        if (!search_by_type.getTag().toString().equals("0")) {
            type_string = " for " + search_by_type.getText().toString() + " Type";
        }

        criteria_string = criteria_string + area_string + cuisine_string + type_string;

        if (!criteria_string.equals("")) {
            search_bar_text.setText(getString(R.string.txt_results).concat(criteria_string));

        } else {
            search_bar_text.setText("");

        }
    }

    public void setSearchBy(String searchField, String displayName, String tagValue) {
        switch (searchField) {
            case STR_SEARCH_CITY:
                if (!search_by_city.getTag().toString().equals(tagValue)) {
                    search_by_area.setText(getString(R.string.txt_search_all));
                    search_by_area.setTag("0");
                }
                search_by_city.setText(db.getCityNameByID(tagValue));
                search_by_city.setTag(tagValue);
                break;

            case STR_SEARCH_AREA:
                search_by_area.setText(displayName);
                search_by_area.setTag(tagValue);
                break;

            case STR_SEARCH_TYPE:
                search_by_type.setText(displayName);
                search_by_type.setTag(tagValue);
                break;

            case STR_SEARCH_CUISINE:
                search_by_cuisine.setText(displayName);
                search_by_cuisine.setTag(tagValue);
                break;
        }

    }

    // CATEGORIES MANAGEMENT =======================================================================

    public void btnCategorize(View view) {
        if (view.getTag().equals(TAG_CLICKED)) {
            resetCategories();
            search();

        } else {

            resetCategories();
            switch (view.getId()) {
                case R.id.category_nearby:
                    category_nearby.setTag(TAG_CLICKED);
                    STR_SORT = SORT_NEARBY;
                    fetchLocation();
                    break;

                case R.id.category_featured:
                    STR_SORT = SORT_FEATURED;
                    STR_ORDER = ORDER_ASCENDING;
                    STR_DISCOUNT_ONLY = FLAG_FALSE;
                    STR_OFFERS_ONLY = FLAG_FALSE;
                    setCategoryButtonColor(category_featured, R.drawable.bckgrd_button_search_middle);
                    break;

                case R.id.category_az:
                    STR_SORT = SORT_A_Z;
                    STR_ORDER = ORDER_ASCENDING;
                    setCategoryButtonColor(category_az, R.drawable.bckgrd_button_search_middle);
                    break;

                case R.id.category_rating:
                    STR_SORT = SORT_RATING;
                    STR_ORDER = ORDER_ASCENDING;
                    setCategoryButtonColor(category_rating, R.drawable.bckgrd_button_search_right);
                    break;
            }
        }
    }

    private void setCategoryButtonColor(TextView textView, int drawable) {
        textView.setBackgroundResource(drawable);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTag(TAG_CLICKED);
        asyncGetSearchResults(true);
    }

    private void resetCategories() {
        disableFetchLocation();

        STR_SORT = "";
        STR_DISCOUNT_ONLY = FLAG_FALSE;
        STR_OFFERS_ONLY = FLAG_FALSE;

        category_nearby.setBackgroundResource(R.drawable.bckgrd_button_no_search_left);
        category_featured.setBackgroundResource(R.drawable.bckgrd_button_no_search_middle);
        category_az.setBackgroundResource(R.drawable.bckgrd_button_no_search_middle);
        category_rating.setBackgroundResource(R.drawable.bckgrd_button_no_search_right);

        category_nearby.setTextColor(getResources().getColor(R.color.dark_grey));
        category_featured.setTextColor(getResources().getColor(R.color.dark_grey));
        category_az.setTextColor(getResources().getColor(R.color.dark_grey));
        category_rating.setTextColor(getResources().getColor(R.color.dark_grey));

        category_nearby.setTag(TAG_NOT_CLICKED);
        category_featured.setTag(TAG_NOT_CLICKED);
        category_az.setTag(TAG_NOT_CLICKED);
        category_rating.setTag(TAG_NOT_CLICKED);

    }


    // LOCATIONS MANAGEMENT  =======================================================================

    private void fetchLocation() {
        if (locationManager != null) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                helper.dialogNoGPS(RestaurantSearchActivity.this);
            } else {
                category_nearby.setBackgroundResource(R.drawable.bckgrd_button_search_left);
                category_nearby.setTextColor(getResources().getColor(R.color.white));

                Helpers.LogThis(TAG_LOG, "GPS NEARBY CLICKED");
                helper.setProgressDialogMessage(getString(R.string.progress_loading_get_location));
                helper.progressDialog(true);

                locationListener = new MyLocationListener();
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new MyLocationListener(), null);

            }
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            STR_LONGITUDE = String.valueOf(location.getLongitude());
            STR_LATITUDE = String.valueOf(location.getLatitude());
            Helpers.LogThis(TAG_LOG, "GPS_LONGITUDE: " + STR_LONGITUDE);
            Helpers.LogThis(TAG_LOG, "GPS_LATITUDE: " + STR_LATITUDE);
            if (!STR_LONGITUDE.equals("")) {
                asyncGetSearchResults(true);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (category_nearby.getTag().equals(TAG_CLICKED)) {
                helper.ToastMessage(RestaurantSearchActivity.this, "GPS Disabled");
                helper.progressDialog(false);
                fetchFromDB_ALL_RESTAURANTS();
                resetCategories();
                STR_LATITUDE = "";
                STR_LONGITUDE = "";
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Helpers.LogThis(TAG_LOG, "GPS Status: " + status);

            if (status == LocationProvider.OUT_OF_SERVICE) {
                helper.progressDialog(false);
                Helpers.LogThis(TAG_LOG, "GPS Status: OUT OF SERVICE");
                helper.ToastMessage(RestaurantSearchActivity.this, "GPS Status: OUT OF SERVICE");
                noRestaurants();
                STR_LATITUDE = "";
                STR_LONGITUDE = "";
                resetCategories();
            } else {
                Helpers.LogThis(TAG_LOG, "GPS Status: OK");
            }
        }
    }

    private void disableFetchLocation() {
        if (locationListener != null && locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }


    // ASYNC FUNCTION ==============================================================================

    private void asyncGetSearchResults(final Boolean startAfresh) {
        isBlockedScrollView = true;
        if (startAfresh) {
            CURRENT_PAGE = 0;
            restaurantsList.clear();
        }

        if (CURRENT_PAGE >= 1) {
            helper.setProgressDialogMessage(getString(R.string.txt_loading_please_wait) + " (" + (CURRENT_PAGE + 1) + " / " + LAST_PAGE + ")");
            helper.progressDialog(true);
        }

        swiperefresh.setRefreshing(true);
        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);

        final Call<JsonObject> call = restaurantService.searchRestaurant(
                STR_SEARCH_QUERRY,
                String.valueOf(CURRENT_PAGE + 1),
                search_by_city.getTag().toString(),
                search_by_area.getTag().toString(), //db.getAreaIDByName(spinner_area.getSelectedItem().toString()),
                search_by_cuisine.getTag().toString(), //db.getCuisineIDByName(spinner_cuisine.getSelectedItem().toString()),
                search_by_type.getTag().toString(), //db.getTypeIDByName(spinner_type.getSelectedItem().toString()),
                STR_LATITUDE,
                STR_LONGITUDE,
                STR_SORT,
                STR_ORDER,
                STR_DISCOUNT_ONLY,
                STR_OFFERS_ONLY
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
                    if (!main.getBoolean("error")) {

                        Helpers.LogThis(TAG_LOG, "PAGE NUMBER CURRENT internal: " + CURRENT_PAGE);
                        Helpers.LogThis(TAG_LOG, "PAGE NUMBER CURRENT server: " + main.getInt("current_page"));
                        Helpers.LogThis(TAG_LOG, "PAGE NUMBER LAST Server: " + main.getInt("last_page"));

                        LAST_PAGE = main.getInt("last_page");
                        CURRENT_PAGE = main.getInt("current_page");

                        if (CURRENT_PAGE >= LAST_PAGE) {
                            continue_pagination = false;
                        }

                        JSONArray jArray = main.getJSONArray("result");
                        int result_length = jArray.length();

                        Helpers.LogThis(TAG_LOG, getString(R.string.log_response_length) + result_length);

                        if (result_length <= 0 && CURRENT_PAGE <= 1) {
                            Helpers.LogThis(TAG_LOG, "No Restaurants");
                            noRestaurants();
                        } else {
                            for (int i = 0; i < result_length; i++) {
                                RestaurantModel restaurantModel = db.setRestaurants(jArray.getJSONObject(i));
                                restaurantsList.add(restaurantModel);
                            }
                            continue_pagination = true;
                        }

                        if (startAfresh) {
                            restaurantAdapter.notifyDataSetChanged();
                            if (STR_SORT.equals(SORT_NEARBY) && !STR_LONGITUDE.equals("")) {
                                restaurantAdapter = new RestaurantAdapter(RestaurantSearchActivity.this, restaurantsList, ADAPTER_DISTANCE);
                            } else {
                                restaurantAdapter = new RestaurantAdapter(RestaurantSearchActivity.this, restaurantsList, ADAPTER_DEFAULT);
                            }
                            recyclerView.setAdapter(restaurantAdapter);
                        } else {
                            if (CURRENT_PAGE == 1) {
                                recyclerView.postInvalidate();
                                restaurantAdapter.updateData(restaurantsList);
                            }

                            recyclerView.getRecycledViewPool().clear();
                            restaurantAdapter.notifyDataSetChanged();
                        }
                    } else {
                        noRestaurants();
                    }
                } catch (JSONException e) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                    noRestaurants();

                } catch (Exception e) {
                    noRestaurants();
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                }

                swiperefresh.setRefreshing(false);
                helper.progressDialog(false);
                isBlockedScrollView = false;
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());

                helper.progressDialog(false);
                swiperefresh.setRefreshing(false);

                if (helper.validateInternetConnection()) {
                    fetchFromDB_ALL_RESTAURANTS();
                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.drawer_layout),
                            getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
                    snackBar.setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                            asyncGetSearchResults(true);
                        }
                    });
                    snackBar.show();
                } else {
                    noRestaurants();
                }
                isBlockedScrollView = false;
            }

        });
    }


}