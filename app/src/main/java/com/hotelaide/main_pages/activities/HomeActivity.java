package com.hotelaide.main_pages.activities;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.hotelaide.R;
//import com.hotelaide.main_pages.fragments.HomePageFragment;
//import com.hotelaide.main_pages.models.CollectionModel;
import com.hotelaide.services.RestaurantService;
import com.hotelaide.services.UserService;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.Database.userModel;
import static com.hotelaide.utils.Helpers.FLAG_TRUE;
import static com.hotelaide.utils.Helpers.STR_NAVIGATION_COLLECTION;
import static com.hotelaide.utils.Helpers.STR_NAVIGATION_REST;
import static com.hotelaide.utils.Helpers.STR_NAVIGATION_SEARCH;

public class HomeActivity extends ParentActivity {

    private SwipeRefreshLayout swiperefresh;

    private ViewPager
            viewPager;

    private RelativeLayout
            no_list_items;

    private ImageView
            scrollPageBack,
            scrollPageFront;

    private int
            viewPagerLastPage = 0;


    private final String TAG_LOG = "HOME";


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);

        initialize(R.id.home, "");

        findAllViews();

        asyncGetCollections();

        asyncGetLikedRestaurants();

        if (SharedPrefs.getAsyncCallUserDetails()) {
            helper.asyncGetUser();
        }

        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        asyncGetCollections();
                    }
                }
        );

        helper.setTracker(TAG_LOG);

    }

    @Override
    protected void onStart() {
        if (!SharedPrefs.getNavigationData().equals("") && SharedPrefs.getNavigationPushCLicked()) {
            navigateToPage();
            SharedPrefs.setNavigationPushCLicked(false);
        }
        super.onStart();
    }


    // BASIC FUNCTIONS =============================================================================
    private void navigateToPage() {
        String Splits[] = SharedPrefs.getNavigationData().split("~");
        switch (Splits[0]) {
            case STR_NAVIGATION_COLLECTION:
//                startActivity(new Intent(HomeActivity.this, HomePageCollectionActivity.class)
//                        .putExtra("collection_id", Splits[1])
//                );
                break;

            case STR_NAVIGATION_REST:
                startActivity(new Intent(HomeActivity.this, RestaurantDetailsActivity.class)
                        .putExtra("restaurant_id", Splits[1])
                        .putExtra("restaurant_name", Splits[2])
                        .putExtra("restaurant_desc", Splits[3])
                        .putExtra("offer", "false")
                );
                break;

            case STR_NAVIGATION_SEARCH:
                startActivity(new Intent(HomeActivity.this, RestaurantSearchActivity.class)
                        .putExtra("extra", "offer"));
                break;

            default:
                helper.ToastMessage(HomeActivity.this, getString(R.string.error_unknown));
                break;

        }
        SharedPrefs.setNavigationData("");
    }

    private void findAllViews() {

        swiperefresh = findViewById(R.id.swiperefresh);
        no_list_items = findViewById(R.id.no_list_items);
        no_list_items.setVisibility(View.GONE);

        viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tabs);

        scrollPageBack = findViewById(R.id.scrollPageBack);
        scrollPageFront = findViewById(R.id.scrollPageFront);

        scrollPageBack.setVisibility(View.GONE);
        scrollPageFront.setVisibility(View.GONE);

        tabLayout.setupWithViewPager(viewPager, true);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    scrollPageBack.setVisibility(View.GONE);
                } else {
                    scrollPageBack.setVisibility(View.VISIBLE);
                }

                if (position == viewPagerLastPage) {
                    scrollPageFront.setVisibility(View.GONE);
                } else {
                    scrollPageFront.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    // BUTTON CLICKS ===============================================================================
    public void BTN_FAVOURITES(View view) {
//        startActivity(new Intent(HomeActivity.this, FavoritesActivity.class));
    }

    public void BTN_SEARCH(View view) {
        startActivity(new Intent(HomeActivity.this, RestaurantSearchActivity.class));
    }

    public void BTN_NEARBY(View view) {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager != null) {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                helper.dialogNoGPS(HomeActivity.this);
            } else {
                startActivity(new Intent(HomeActivity.this, RestaurantSearchActivity.class)
                        .putExtra("extra", "nearby"));
            }
        }
    }

    public void BTN_DISCOUNTS(View view) {
        startActivity(new Intent(HomeActivity.this, RestaurantSearchActivity.class)
                .putExtra("extra", "offer"));
    }

    public void MoveNext(View view) {
        //it doesn't matter if you're already in the last item
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

    }

    public void MovePrevious(View view) {
        //it doesn't matter if you're already in the first item
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }


    // VIEWPAGER ADAPTER ===========================================================================
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }


    // NO COLLECTIONS ==============================================================================
    private void noRestaurants() {
        viewPager.removeAllViews();
        no_list_items.setVisibility(View.VISIBLE);
        scrollPageBack.setVisibility(View.GONE);
        scrollPageFront.setVisibility(View.GONE);
    }


    // GET COLLECTIONS =============================================================================
    private void asyncGetCollections() {
        swiperefresh.setRefreshing(true);
        no_list_items.setVisibility(View.GONE);

        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.getAllRestaurantCollections(
                FLAG_TRUE,
                userModel.city_id
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                swiperefresh.setRefreshing(false);
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
                    if (!main.getBoolean("error")) {

                        JSONArray jArray = main.getJSONArray("result");
                        int result_length = jArray.length();

                        viewPagerLastPage = (result_length - 1);

                        Helpers.LogThis(TAG_LOG, getString(R.string.log_response_length) + result_length);

                        db.deleteCollectionTable();

                        HomeActivity.ViewPagerAdapter viewPageradapter = new HomeActivity.ViewPagerAdapter(getSupportFragmentManager());

                        for (int i = 0; i < result_length; i++) {
                            JSONObject json_data = jArray.getJSONObject(i);
//                            final CollectionModel collectionModel = new CollectionModel();

//                            collectionModel.collection_id = json_data.getString("id");
//                            collectionModel.collection_title = json_data.getString("title");
//                            collectionModel.collection_desc = json_data.getString("description");
//                            collectionModel.collection_image = json_data.getString("image");
//                            collectionModel.collection_url = json_data.getString("url");

//                            JSONArray rest_ids = json_data.getJSONArray("rest_ids");
//                            if (!rest_ids.isNull(0)) {
//                                for (int x = 0; x < rest_ids.length(); x++) {
//                                    collectionModel.collection_rest_id = rest_ids.optString(x);
//                                    db.setCollectionRestaurants(collectionModel);
//                                }
//                            }

//                            Bundle bundle = new Bundle();
//                            bundle.putString("collection_id", collectionModel.collection_id);
//                            bundle.putString("collection_title", collectionModel.collection_title);
//                            bundle.putString("collection_desc", collectionModel.collection_desc);
//                            bundle.putString("collection_image", collectionModel.collection_image);
//                            Fragment fragment = new HomePageFragment();
//                            fragment.setArguments(bundle);
//                            viewPageradapter.addFragment(fragment);
                        }

                        viewPager.setAdapter(viewPageradapter);
                        if (viewPagerLastPage == 0) {
                            scrollPageFront.setVisibility(View.GONE);
                        } else {
                            scrollPageFront.setVisibility(View.VISIBLE);
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
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());

                helper.progressDialog(false);
                swiperefresh.setRefreshing(false);

                if (helper.validateInternetConnection()) {
                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.drawer_layout),
                            getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
                    snackBar.setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                            asyncGetCollections();
                        }
                    });
                    snackBar.show();
                    noRestaurants();
                }
            }

        });
    }


    // SET LIKED RESTAURANTS =======================================================================
    private void asyncGetLikedRestaurants() {
        UserService userService = UserService.retrofit.create(UserService.class);
        final Call<JsonObject> call = userService.getAllLikedRestaurants(userModel.user_id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
                    if (!main.getBoolean("error")) {

                        JSONArray jArray = main.getJSONArray("result");
                        int result_length = jArray.length();

                        Helpers.LogThis(TAG_LOG, getString(R.string.log_response_length) + result_length);

                        for (int i = 0; i < result_length; i++) {
//                            Helpers.LogThis("FAVOURITES:: " + jArray.getString(i));
                            db.setLikedRestaurant(jArray.getString(i));
                        }
                    }

                } catch (JSONException e) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());

                } catch (Exception e) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());

            }

        });
    }


}
