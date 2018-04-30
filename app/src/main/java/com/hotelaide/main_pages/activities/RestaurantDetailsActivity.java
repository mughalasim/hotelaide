package com.hotelaide.main_pages.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.hotelaide.BuildConfig;
import com.hotelaide.R;
//import com.hotelaide.main_pages.adapters.GalleryAdapter;
import com.hotelaide.main_pages.adapters.RestaurantAdapter;
//import com.hotelaide.main_pages.adapters.ReviewAdapter;
//import com.hotelaide.main_pages.models.DynamicCardModel;
//import com.hotelaide.main_pages.models.EventModel;
//import com.hotelaide.main_pages.models.GalleryModel;
import com.hotelaide.main_pages.models.RestaurantModel;
//import com.hotelaide.main_pages.models.ReviewModel;
import com.hotelaide.services.RestaurantService;
import com.hotelaide.services.TrackingService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import static com.hotelaide.BuildConfig.YUMMY_CARD;
import static com.hotelaide.utils.Helpers.ADAPTER_DISTANCE;
import static com.hotelaide.utils.Helpers.FLAG_FALSE;
import static com.hotelaide.utils.Helpers.FLAG_TRUE;
import static com.hotelaide.utils.Helpers.SORT_NEARBY;

public class RestaurantDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Helpers helper;

    private Toolbar toolbar;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private BroadcastReceiver receiver;

    private static final int
            CLAIMING_DISTANCE_ALLOWANCE = 250;

    private ImageView
            btn_like,
            picture_restaurant,
            LL_map;

    private RecyclerView
            menu_recycler_view,
            restaurant_recyclerView,
            review_recyclerView;

//    private final ArrayList<GalleryModel>
//            menuList = new ArrayList<>(),
//            galleryList = new ArrayList<>();
//
    private final ArrayList<RestaurantModel>
            restaurantsList = new ArrayList<>();

//    private final ArrayList<ReviewModel>
//            reviewList = new ArrayList<>();

//    private GalleryAdapter
//            menuAdapter,
//            galleryAdapter;

    private RestaurantAdapter restaurant_adapter;

//    private ReviewAdapter review_adapter;

    private final LinearLayoutManager
            gallery_layoutManager = new LinearLayoutManager(this),
            restaurant_layoutManager = new LinearLayoutManager(this),
            menu_layoutManager = new LinearLayoutManager(this),
            review_layoutManager = new LinearLayoutManager(this);

    private Database db;

    private TextView
            restaurant_rating,
            restaurant_rating2,
            txt_restaurant_name,
            txt_restaurant_cuisine,
            txt_restaurant_description,
            txt_restaurant_reviews,
            txt_restaurant_location,
            txt_address_location,
            txt_address_area,
            txt_address_city,
            txt_menu_title,
            txt_offer_title,
            txt_offer_desc,
            btn_post_review,
            btn_read_review,
            link_telephone,
            link_website,
            link_facebook;

    private WebView
            txt_offer_desc_web_view,
            txt_menu;

    private RelativeLayout
            LL_review;

    private Float
            RESTAURANT_LATITUDE = 0.0f,
            RESTAURANT_LONGITUDE = 0.0f,
            USER_LATITUDE = 0.0f,
            USER_LONGITUDE = 0.0f;

    private LinearLayout
            btn_call_support,
            btn_make_reservation,
            restaurant_cuisine_layout,
            LL_information,
            LL_error,
            LL_events,
            LL_other_offers,
            LL_gallery,
            LL_menu,
            LL_offers,
            LL_offers_children,
            LL_working_hours,
            LL_dialog_loading;

    private final static String
            TAG_LOG = "RESTAURANT DETAILS",
            STATE_INFORMATION = "INFORMATION",
            STATE_REVIEWS = "REVIEWS",
            STATE_OTHER_OFFERS = "OTHER_OFFERS",
            STATE_ERROR = "ERROR";

    private String
            STR_RESTAURANT_ID = "",
            STR_DELIVERY_PARTNER_ID = "",
            STR_DELIVERY_PARTNER_NAME = "",
            STR_DELIVERY_PARTNER_PHONE = "",
            STR_RESTAURANT_NAME = "",
            STR_RESTAURANT_DESCRIPTION = "",
            STR_RESTAURANT_IMAGE = "",
            STR_MESSAGE_BODY = "";

    public static String STR_SHARE_LINK = "Hey, check out this restaurant on the EatOut App: https://eatout.co.ke";

    private int
            CURRENT_PAGE = 0,
            LAST_PAGE = 1,
            pastVisibleItems,
            visibleItemCount,
            totalItemCount,
            INT_OFFER_ID;
//
//    private DynamicCardModel globalDynamicCardModel;

    private boolean
            bool_loading = true,
            MPESA_CHECKOUT_ENABLED = false;

    private SwipeRefreshLayout
            review_swiperefresh;

    private AppBarLayout appBarLayout;

    private LocationListener locationListener;

    private LocationManager locationManager;

    private final static String
            CLAIM_OFFER_CHECKING = "checking",
            CLAIM_OFFER_CLAIMING = "claiming";

    public final static String
            GALLERY_MENU = "MENU",
            GALLERY_IMAGE = "IMAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_restaurant_details);

        try {

            System.gc();

            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            helper = new Helpers(RestaurantDetailsActivity.this);
            db = new Database();

            listenExitBroadcast();

            findAllViews();

            STR_RESTAURANT_ID = getIntent().getStringExtra("restaurant_id");
            STR_RESTAURANT_NAME = getIntent().getStringExtra("restaurant_name");
            STR_RESTAURANT_DESCRIPTION = getIntent().getStringExtra("restaurant_desc");
            INT_OFFER_ID = getIntent().getIntExtra("offer_id", 0);

            if (BuildConfig.DEBUG) {
                helper.ToastMessage(RestaurantDetailsActivity.this, "REST ID: " + STR_RESTAURANT_ID);
            }

            txt_restaurant_name.setText(STR_RESTAURANT_NAME);

            hideAllScreens();

            setUpToolBarAndTabs();

            setOnclickListeners();

            setUpLikeButton();

            asyncGetRestaurant();

            asyncGetReview(true);

            helper.setTracker(TAG_LOG + " - " + STR_RESTAURANT_ID);

        } catch (OutOfMemoryError error) {
            helper.ToastMessage(RestaurantDetailsActivity.this,
                    getString(R.string.error_memory));
            finish();
        }

    }

    private void findAllViews() {

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);

        // Collapsing ToolBar Items
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        txt_restaurant_name = findViewById(R.id.restaurant_name);
        txt_restaurant_cuisine = findViewById(R.id.restaurant_cuisine);
        txt_restaurant_reviews = findViewById(R.id.restaurant_reviews);
        txt_restaurant_location = findViewById(R.id.restaurant_location);
        picture_restaurant = findViewById(R.id.restaurant_image);
        btn_call_support = findViewById(R.id.btn_call_support);
        btn_make_reservation = findViewById(R.id.btn_make_reservation);
        btn_like = findViewById(R.id.btn_like);
        btn_post_review = findViewById(R.id.btn_post_review);
        btn_read_review = findViewById(R.id.btn_read_review);

        // Information Screen items
        restaurant_rating = findViewById(R.id.restaurant_rating);
        restaurant_rating2 = findViewById(R.id.restaurant_rating2);
        txt_restaurant_description = findViewById(R.id.description);
        restaurant_cuisine_layout = findViewById(R.id.restaurant_cuisine_layout);
        link_website = findViewById(R.id.link_website);
        link_facebook = findViewById(R.id.link_facebook);
        link_telephone = findViewById(R.id.link_telephone);
        txt_offer_title = findViewById(R.id.txt_offer_title);
        txt_offer_desc = findViewById(R.id.txt_offer_desc);
        txt_offer_desc_web_view = findViewById(R.id.txt_offer_desc_web_view);

        // Map Items
        txt_address_area = findViewById(R.id.address_area);
        txt_address_city = findViewById(R.id.address_city);
        txt_address_location = findViewById(R.id.address_location);

        // Review Screen Items
        review_swiperefresh = findViewById(R.id.swiperefresh);
        review_recyclerView = findViewById(R.id.review_recycler);
//        review_adapter = new ReviewAdapter(reviewList);
//        review_recyclerView.setAdapter(review_adapter);
        review_recyclerView.setHasFixedSize(true);
        review_recyclerView.setLayoutManager(review_layoutManager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(review_recyclerView.getContext(),
                        review_layoutManager.getOrientation());
        review_recyclerView.addItemDecoration(dividerItemDecoration);

        // More Offers
        restaurant_recyclerView = findViewById(R.id.restaurant_recyclerView);
        restaurant_recyclerView.setHasFixedSize(true);
        restaurant_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        restaurant_recyclerView.setLayoutManager(restaurant_layoutManager);
        restaurant_adapter = new RestaurantAdapter(RestaurantDetailsActivity.this,
                restaurantsList, ADAPTER_DISTANCE);
        restaurant_recyclerView.setAdapter(restaurant_adapter);

        // Showing Screens
        LL_information = findViewById(R.id.LL_information);
        LL_review = findViewById(R.id.LL_review);
        LL_offers = findViewById(R.id.LL_offers);
        LL_offers_children = findViewById(R.id.LL_offers_children);
        LL_menu = findViewById(R.id.LL_menu);
        LL_error = findViewById(R.id.LL_error);
        LL_other_offers = findViewById(R.id.LL_other_offers);
        LL_events = findViewById(R.id.LL_events);
        LL_gallery = findViewById(R.id.LL_gallery_small);
        LL_map = findViewById(R.id.LL_map);
        LL_dialog_loading = findViewById(R.id.LL_dialog_loading);

        // Gallery Images
        RecyclerView gallery_recyclerView = findViewById(R.id.gallery_recycler_small);
//        galleryAdapter = new GalleryAdapter(galleryList);
//        gallery_recyclerView.setAdapter(galleryAdapter);
        gallery_recyclerView.setHasFixedSize(true);
        gallery_layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        gallery_recyclerView.setLayoutManager(gallery_layoutManager);


        // Menu Items
        txt_menu = findViewById(R.id.txt_menu);
        txt_menu_title = findViewById(R.id.txt_menu_title);

        // Menu Gallery Images
        menu_recycler_view = findViewById(R.id.menu_recycler_view);
//        menuAdapter = new GalleryAdapter(menuList);
//        menu_recycler_view.setAdapter(menuAdapter);
        menu_recycler_view.setHasFixedSize(true);
        menu_layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        menu_recycler_view.setLayoutManager(menu_layoutManager);

        // Working Hours
        LL_working_hours = findViewById(R.id.LL_working_hours);

    }

    private void setUpToolBarAndTabs() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setOnclickListeners() {

        picture_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!STR_RESTAURANT_IMAGE.equals("")) {
                    ArrayList<String> image_urls = new ArrayList<>();
                    image_urls.add(STR_RESTAURANT_IMAGE);
                    Activity activity = RestaurantDetailsActivity.this;
                    activity.startActivity(new Intent(RestaurantDetailsActivity.this, GalleryViewActivity.class)
                            .putExtra("image_urls", image_urls)
                            .putExtra("selected_position", 1)
                            .putExtra("rest_id", STR_RESTAURANT_ID)
                            .putExtra("delivery_id", "")
                            .putExtra("delivery_name", "")
                            .putExtra("delivery_phone", "")
                    );
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        txt_restaurant_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateScreen(STATE_REVIEWS);
            }
        });

        btn_call_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_call_support.getTag().toString().equals("")) {
                    helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_call));

                } else if (btn_call_support.getTag().toString().equals(SharedPrefs.getSupportNumber())) {
                    dialogMakeCall("Calling Eatout", btn_call_support.getTag().toString());

                } else {
                    dialogMakeCall("Calling " + txt_restaurant_name.getText().toString()
                            , btn_call_support.getTag().toString());
                }
            }
        });

        btn_make_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Database.userModel.email.equals("")
                        || Database.userModel.first_name.equals("")
                        || Database.userModel.last_name.equals("")) {
                    helper.myDialog(RestaurantDetailsActivity.this, getString(R.string.txt_alert),
                            "Please make sure you have updated all your details before you may proceed");

                } else {
//                    startActivity(new Intent(RestaurantDetailsActivity.this,
//                            BookReservationActivity.class)
//                            .putExtra("restaurant_id", STR_RESTAURANT_ID)
//                            .putExtra("restaurant_name", STR_RESTAURANT_NAME)
//                            .putExtra("restaurant_image", STR_RESTAURANT_IMAGE)
//                    );
                }
            }

        });

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncSetLike();
            }
        });

        btn_post_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePartialReview();
            }
        });

        btn_read_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateScreen(STATE_REVIEWS);
                helper.setTracker(TAG_LOG + " - " + STR_RESTAURANT_ID + " :READ REVIEWS");
            }
        });

        LL_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (locationManager != null && helper.validateInternetConnection()) {
                    Activity activity = RestaurantDetailsActivity.this;
//                    activity.startActivity(new Intent(RestaurantDetailsActivity.this, MapActivity.class)
//                            .putExtra("restaurant_latitude", RESTAURANT_LATITUDE)
//                            .putExtra("restaurant_longitude", RESTAURANT_LONGITUDE)
//                            .putExtra("restaurant_name", STR_RESTAURANT_NAME)
//                            .putExtra("restaurant_desc", STR_RESTAURANT_DESCRIPTION)
//                            .putExtra("draw_route", locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)));
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    helper.setTracker(TAG_LOG + " - " + STR_RESTAURANT_ID + " :MAP VIEW");
                }
            }
        });


        review_swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        asyncGetReview(true);
                    }
                }
        );

        review_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = review_layoutManager.getChildCount();
                    totalItemCount = review_layoutManager.getItemCount();
                    pastVisibleItems = review_layoutManager.findFirstVisibleItemPosition();
                    if (bool_loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            asyncGetReview(false);
                        }
                    }
                }
            }
        });


        link_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(link_website.getTag().toString()));
                    startActivity(i);
                    helper.setTracker(TAG_LOG + " - " + STR_RESTAURANT_ID + " :OPEN WEBSITE");
                } catch (Exception e) {
                    e.printStackTrace();
                    helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_unknown));
                }
            }
        });


        link_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(link_facebook.getTag().toString()));
                    startActivity(i);
                    helper.setTracker(TAG_LOG + " - " + STR_RESTAURANT_ID + " :OPEN FACEBOOK");
                } catch (Exception e) {
                    e.printStackTrace();
                    helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_unknown));
                }
            }
        });


        link_telephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMakeCall(
                        "Are you sure you wish to call " + txt_restaurant_name.getText().toString(),
                        link_telephone.getTag().toString());
            }
        });


        txt_offer_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(txt_offer_desc.getTag().toString()));
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_unknown));
                }
            }
        });

    }


    // HANDLE THE LIKE BUTTON ======================================================================
    private void setUpLikeButton() {
        helper.animate_wobble(btn_like);
        if (db.getLikedRestaurantIDMatch(STR_RESTAURANT_ID)) {
            setLikeButtonColor(true);
        } else {
            setLikeButtonColor(false);
        }
    }

    private void setLikeButtonColor(Boolean pressed) {
        if (pressed) {
            btn_like.setImageResource(R.drawable.ic_favorite_white_full);
        } else {
            btn_like.setImageResource(R.drawable.ic_favorite_white);
        }
    }


    // OVERRIDE FUNCTIONS ==========================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                final Dialog dialog = new Dialog(RestaurantDetailsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_share);
                final ImageView share_facebook = dialog.findViewById(R.id.share_facebook);
                final ImageView share_email = dialog.findViewById(R.id.share_email);
                final ImageView share_messenger = dialog.findViewById(R.id.share_messenger);
                final ImageView share_sms = dialog.findViewById(R.id.share_sms);
                final ImageView share_whatsapp = dialog.findViewById(R.id.share_whatsapp);

                share_facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (helper.validateAppIsInstalled("com.facebook.katana")) {
                            ShareLinkContent content = new ShareLinkContent.Builder()
                                    .setContentUrl(Uri.parse(STR_SHARE_LINK))
                                    .build();
                            ShareDialog.show(RestaurantDetailsActivity.this, content);
                            dialog.cancel();
                            helper.setTracker(TAG_LOG + " - " + STR_RESTAURANT_ID + " :SHARED VIA FB");
                        } else {
                            helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_app_not_installed));
                        }
                    }
                });

                share_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setType("text/html");
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                            emailIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARE_LINK);
                            startActivity(Intent.createChooser(emailIntent, "Send Email"));
                            helper.setTracker(TAG_LOG + " - " + STR_RESTAURANT_ID + " :SHARED VIA EMAIL");
                            dialog.cancel();
                        } catch (Exception e) {
                            helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_app_not_installed));
                        }
                    }
                });

                share_messenger.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (helper.validateAppIsInstalled("com.facebook.orca")) {
                            Intent messengerIntent = new Intent();
                            messengerIntent.setAction(Intent.ACTION_SEND);
                            messengerIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARE_LINK);
                            messengerIntent.setType("text/plain");
                            messengerIntent.setPackage("com.facebook.orca");
                            startActivity(messengerIntent);
                            helper.setTracker(TAG_LOG + " - " + STR_RESTAURANT_ID + " :SHARED VIA MESSENGER");
                        } else {
                            helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_app_not_installed));
                        }
                    }
                });

                share_sms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.putExtra("sms_body", STR_SHARE_LINK);
                            smsIntent.setType("vnd.android-dir/mms-sms");
                            startActivity(smsIntent);
                            dialog.cancel();
                            helper.setTracker(TAG_LOG + " - " + STR_RESTAURANT_ID + " :SHARED VIA SMS");
                        } catch (Exception e) {
                            helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_app_not_installed));
                        }
                    }
                });

                share_whatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (helper.validateAppIsInstalled("com.whatsapp")) {
                            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                            whatsappIntent.setType("text/plain");
                            whatsappIntent.setPackage("com.whatsapp");
                            whatsappIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARE_LINK);
                            startActivity(whatsappIntent);
                            dialog.cancel();
                            helper.setTracker(TAG_LOG + " - " + STR_RESTAURANT_ID + " :SHARED VIA WHATSAPP");
                        } else {
                            helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_app_not_installed));
                        }
                    }
                });

                dialog.setCancelable(true);
                dialog.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if (googleMap != null) {
            if (ActivityCompat.checkSelfPermission(RestaurantDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(RestaurantDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                LatLng mylocation = new LatLng(RESTAURANT_LATITUDE, RESTAURANT_LONGITUDE);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
                Float ZOOM_IN = 15.5f;
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_IN));

                Marker restaurant_marker = googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(RESTAURANT_LATITUDE, RESTAURANT_LONGITUDE))
                        .title(STR_RESTAURANT_NAME)
                        .snippet(STR_RESTAURANT_DESCRIPTION));
                restaurant_marker.setVisible(true);

            } else {
                helper.myDialog(RestaurantDetailsActivity.this,
                        getString(R.string.txt_permission_title),
                        getString(R.string.txt_permission_desc));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (LL_review.getVisibility() == View.VISIBLE) {
            updateScreen(STATE_INFORMATION);
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        disableFetchLocation();
        helper.dismissProgressDialog();
        super.onDestroy();

    }


    // DIALOG HANDLING =============================================================================
    private void dialogMakeCall(final String Message, final String TelephoneNumber) {
        if (ActivityCompat.checkSelfPermission
                (RestaurantDetailsActivity.this, Manifest.permission.CALL_PHONE) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + TelephoneNumber));
            startActivity(intent);
            helper.ToastMessage(RestaurantDetailsActivity.this, Message);
            TrackCallsMade();
            helper.setTracker(TAG_LOG + " - " + STR_RESTAURANT_ID + " :CALLING");

        } else {
            helper.myDialog(RestaurantDetailsActivity.this,
                    getString(R.string.txt_alert), getString(R.string.txt_call_permissions));
        }
    }


    // REVIEW FUNCTIONS  ===========================================================================
    private void capturePartialReview() {
        final Dialog dialog = new Dialog(RestaurantDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_post_partial_review);
        final TextView txtOk = dialog.findViewById(R.id.txtOk);
        final TextView txtCancel = dialog.findViewById(R.id.txtBack);
        final RatingBar ratingBar = dialog.findViewById(R.id.rating);
        ratingBar.setRating(2.5f);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
//                captureFullReview(rating_average.getProgress());
            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getProgress() == 0) {
                    helper.ToastMessage(RestaurantDetailsActivity.this, "Please set a restaurant_rating");
                } else {
                    dialog.cancel();
                    asyncSetReview(
                            ratingBar.getProgress(),
                            ratingBar.getProgress(),
                            ratingBar.getProgress(),
                            ratingBar.getProgress(),
                            ratingBar.getProgress(),
                            ""
                    );
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    // TRACKING CALLS ==============================================================================
    // TRACK CALLS MADE
    private void TrackCallsMade() {
        TrackingService trackingService = TrackingService.retrofit.create(TrackingService.class);
        final Call<JsonObject> call = trackingService.trackRestaurantCall(STR_RESTAURANT_ID);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
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

    // TRACK MENU VIEWS
    public void TrackMenuViews() {
        TrackingService trackingService = TrackingService.retrofit.create(TrackingService.class);
        final Call<JsonObject> call = trackingService.trackRestaurantMenuView(STR_RESTAURANT_ID);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
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


    // MAP FUNCTIONALITY  ==========================================================================
    private void startUpMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(RestaurantDetailsActivity.this);
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, status, requestCode);
            dialog.show();
        } else {
            if (ActivityCompat.checkSelfPermission(RestaurantDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(RestaurantDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                fm.getMapAsync(this);


            } else {
                helper.myDialog(RestaurantDetailsActivity.this,
                        getString(R.string.txt_permission_title),
                        getString(R.string.txt_permission_desc));
            }
        }

    }


    // BROADCAST LISTENING =========================================================================
    private void listenExitBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Helpers.BroadcastValue);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(receiver, filter);
    }


    // NO ADAPTER DATA TO DISPLAY ==================================================================
    private void noReviews() {
//        reviewList.clear();
//        ReviewModel reviewModel = new ReviewModel();
//        reviewList.add(reviewModel);
        review_recyclerView.getRecycledViewPool().clear();
//        review_adapter.notifyDataSetChanged();
    }

    private void noRestaurants() {
        LL_dialog_loading.setVisibility(View.GONE);
        restaurantsList.clear();
        RestaurantModel restaurantModel = new RestaurantModel();
        restaurantsList.add(restaurantModel);
        restaurant_recyclerView.getRecycledViewPool().clear();
        restaurant_adapter.notifyDataSetChanged();
    }


    // UPDATE SCREEN FUNCTIONS =====================================================================
    public void updateScreen(String state) {
        hideAllScreens();

        appBarLayout.setExpanded(true);

        switch (state) {
            case STATE_INFORMATION:
                helper.animate_fade_in_up(LL_information);
                helper.animate_fade_in_up(collapsingToolbarLayout);
                collapsingToolbarLayout.setVisibility(View.VISIBLE);
                LL_information.setVisibility(View.VISIBLE);
                break;

            case STATE_REVIEWS:
                LL_review.setVisibility(View.VISIBLE);
                helper.animate_fade_in_up(LL_review);
                collapsingToolbarLayout.setVisibility(View.VISIBLE);
                break;

            case STATE_ERROR:
                LL_error.setVisibility(View.VISIBLE);
                helper.animate_fade_in_up(LL_error);
                btn_call_support.setClickable(false);
                btn_call_support.setVisibility(View.GONE);
                btn_make_reservation.setVisibility(View.GONE);
                restaurant_rating.setVisibility(View.GONE);
                restaurant_rating2.setVisibility(View.GONE);
                txt_restaurant_reviews.setVisibility(View.GONE);
                restaurant_cuisine_layout.setVisibility(View.GONE);
                picture_restaurant.setImageDrawable(getResources().getDrawable(R.drawable.login_background));
                break;

            case STATE_OTHER_OFFERS:
                LL_other_offers.setVisibility(View.VISIBLE);
                helper.animate_fade_in_up(collapsingToolbarLayout);
                collapsingToolbarLayout.setVisibility(View.VISIBLE);
                btn_call_support.setTag(SharedPrefs.getSupportNumber());
                btn_make_reservation.setVisibility(View.GONE);
                restaurant_rating.setVisibility(View.GONE);
                restaurant_rating2.setVisibility(View.GONE);
                txt_restaurant_reviews.setVisibility(View.GONE);
                restaurant_cuisine_layout.setVisibility(View.GONE);
//                asyncGetRestaurants();
                break;
        }

    }

    private void hideAllScreens() {
        LL_information.setVisibility(View.GONE);
        LL_review.setVisibility(View.GONE);
        LL_error.setVisibility(View.GONE);
        LL_other_offers.setVisibility(View.GONE);
        collapsingToolbarLayout.setVisibility(View.GONE);
    }

    public void BTN_Back(View view) {
        finish();
    }

    public void BTN_Retry(View view) {
        asyncGetRestaurant();
        asyncGetReview(true);
    }


    // LOCATION TRACKING BEFORE CLAIMING AN OFFER ==================================================
    private void fetchLocationAndClaimOffer() {
        if (RESTAURANT_LATITUDE == 0 || RESTAURANT_LONGITUDE == 0) {
//            asyncGetClaimOfferCount(globalDynamicCardModel);
        } else {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager != null && helper.validateInternetConnection()) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    helper.dialogNoGPS(RestaurantDetailsActivity.this);
                } else {
                    locationListener = new MyLocationListener();
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new MyLocationListener(), null);
                    helper.setProgressDialogMessage(getString(R.string.progress_loading_get_location));
                    helper.progressDialog(true);
                }
            } else {
                helper.myDialog(RestaurantDetailsActivity.this, getString(R.string.txt_alert), getString(R.string.error_connection));
            }
        }
    }

    private void disableFetchLocation() {
        if (locationListener != null && locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            USER_LONGITUDE = Float.parseFloat(String.valueOf(location.getLongitude()));
            USER_LATITUDE = Float.parseFloat(String.valueOf(location.getLatitude()));

            Helpers.LogThis(TAG_LOG, "DISTANCE CALCULATIONS =====================================");
            Helpers.LogThis(TAG_LOG, "USER_LONGITUDE: " + USER_LONGITUDE);
            Helpers.LogThis(TAG_LOG, "USER_LATITUDE: " + USER_LATITUDE);

            Helpers.LogThis(TAG_LOG, "RESTAURANT_LONGITUDE: " + RESTAURANT_LONGITUDE);
            Helpers.LogThis(TAG_LOG, "RESTAURANT_LATITUDE: " + RESTAURANT_LATITUDE);

            if (USER_LONGITUDE != 0.0f && RESTAURANT_LONGITUDE != 0.0f) {
                Location loc1 = new Location("");
                loc1.setLatitude(USER_LATITUDE);
                loc1.setLongitude(USER_LONGITUDE);

                Location loc2 = new Location("");
                loc2.setLatitude(RESTAURANT_LATITUDE);
                loc2.setLongitude(RESTAURANT_LONGITUDE);

                float distanceInMeters = loc1.distanceTo(loc2);

                Helpers.LogThis(TAG_LOG, "DISTANCE: " + String.valueOf(distanceInMeters));

                if (distanceInMeters < CLAIMING_DISTANCE_ALLOWANCE) {
//                    asyncGetClaimOfferCount(globalDynamicCardModel);
                } else {
                    helper.myDialog(RestaurantDetailsActivity.this, getString(R.string.txt_alert),
                            "You are not within the range of this restaurant in order to claim its offer");
                    helper.progressDialog(false);
                }

            } else {
                helper.progressDialog(false);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (status == LocationProvider.OUT_OF_SERVICE) {
                Helpers.LogThis(TAG_LOG, "GPS Status: DISABLED");
                USER_LATITUDE = 0.0f;
                USER_LONGITUDE = 0.0f;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            helper.ToastMessage(RestaurantDetailsActivity.this, "GPS Disabled");
            USER_LATITUDE = 0.0f;
            USER_LONGITUDE = 0.0f;
            onBackPressed();
        }
    }


    // MAIN ASYNC FUNCTIONS ========================================================================
    private void asyncGetRestaurant() {
        helper.setProgressDialogMessage(getString(R.string.progress_loading_restaurant_info));
        helper.progressDialog(true);
        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.getOneRestaurant(STR_RESTAURANT_ID);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
                    if (!main.getBoolean("error")) {

                        JSONObject json_data = main.getJSONObject("result");


                        // SETUP BASIC RESTAURANT INFORMATION ======================================
                        STR_RESTAURANT_NAME = json_data.getString("name");
                        txt_restaurant_name.setText(STR_RESTAURANT_NAME);

                        JSONArray cuisine_name_array = json_data.getJSONArray("cuisines");

                        STR_RESTAURANT_DESCRIPTION = "";
                        for (int v = 0; v < cuisine_name_array.length(); v++) {
                            JSONObject cuisine_name = cuisine_name_array.getJSONObject(v);
                            if (!cuisine_name.isNull("name")) {
                                STR_RESTAURANT_DESCRIPTION = STR_RESTAURANT_DESCRIPTION
                                        .concat(cuisine_name.getString("name").concat(" | "));
                            }
                        }

                        if (!STR_RESTAURANT_DESCRIPTION.equals(""))
                            txt_restaurant_cuisine.setText(STR_RESTAURANT_DESCRIPTION
                                    .substring(0, STR_RESTAURANT_DESCRIPTION.length() - 2));

                        STR_RESTAURANT_IMAGE = json_data.getString("full_image_url");
                        Glide.with(RestaurantDetailsActivity.this).load(STR_RESTAURANT_IMAGE)
                                .into(picture_restaurant);

                        JSONObject city_name = json_data.getJSONObject("city");
                        if (!city_name.isNull("name")) {
                            txt_address_city.setText(city_name.getString("name"));
                        }

                        JSONObject area_name = json_data.getJSONObject("area");
                        if (!area_name.isNull("name")) {
                            txt_address_area.setText(area_name.getString("name"));
                            txt_restaurant_location.setText(area_name.getString("name").concat(", ")
                                    .concat(txt_address_city.getText().toString()));
                        }

                        txt_address_location.setText(json_data.getString("address"));


                        // GET MAP LOCATION OF THE RESTAURANT ======================================
                        RESTAURANT_LATITUDE = Float.valueOf(json_data.getString("geolat"));
                        RESTAURANT_LONGITUDE = Float.valueOf(json_data.getString("geolng"));

                        // SUPPORT NUMBER ======================================================
                        String activeNumber = json_data.getString("active_phone_number");
                        if (json_data.getString("active_phone_number").equals("")) {
                            btn_call_support.setVisibility(View.GONE);
                        } else {
                            btn_call_support.setTag(activeNumber);
                        }

                        // IF RESTAURANT IS PREMIUM ================================================

                        if (json_data.getInt("premium_level") != 0) {
                            txt_restaurant_description.setText(json_data.getString("description"));
                            restaurant_rating.setText(json_data.getString("average_rating"));

                            if ((!json_data.getString("active_phone_number").equals(""))) {
                                link_telephone.setTag(json_data.getString("active_phone_number"));
                                link_telephone.setText(getString(R.string.link_call_us).concat(
                                        json_data.getString("active_phone_number")));
                            } else {
                                link_telephone.setVisibility(View.GONE);
                            }

                            if ((!json_data.getString("facebook").equals(""))) {
                                link_facebook.setTag(json_data.getString("facebook"));
                            } else {
                                link_facebook.setVisibility(View.GONE);
                            }

                            if ((!json_data.getString("website").equals(""))) {
                                link_website.setTag(json_data.getString("website"));
                            } else {
                                link_website.setVisibility(View.GONE);
                            }


                            // CHECK IF RESTAURANT CAN PAY VIA MPESA ===============================
                            MPESA_CHECKOUT_ENABLED = json_data.getInt("mpesa_checkout") == 1;


                            // ALL OFFERS, TITLE AND DESCRIPTION ===================================
                            txt_offer_title.setText(json_data.getString("offers_title"));
                            if (json_data.getString("offers_description").equals("")) {
                                txt_offer_desc.setVisibility(View.GONE);
                            } else {
                                txt_offer_desc.setVisibility(View.VISIBLE);
                                txt_offer_desc.setText(json_data.getString("offers_description"));
                                txt_offer_desc.setTag(json_data.getString("offers_terms_url"));
                            }

                            if (!json_data.isNull("offers_description_html")) {
                                if (json_data.getString("offers_description_html").equals("<p><br></p>")
                                        || json_data.getString("offers_description_html").equals("")) {
                                    txt_offer_desc_web_view.setVisibility(View.GONE);
                                } else {
                                    txt_offer_desc_web_view.setVisibility(View.VISIBLE);
                                    txt_offer_desc.setVisibility(View.GONE);
                                    final WebSettings webSettings = txt_offer_desc_web_view.getSettings();
                                    Resources res = getResources();
                                    float fontSize = res.getDimension(R.dimen.text_size_web);
                                    webSettings.setDefaultFontSize((int) fontSize);
                                    txt_offer_desc_web_view.loadData("<style>ul{padding-left: 10px;}</style>"
                                            + json_data.getString("offers_description_html"), "text/html; charset=utf-8", "UTF-8");
                                    txt_offer_desc_web_view.setVisibility(View.VISIBLE);
                                }
                            }


                            // LOOP THROUGH ACTIVE OFFERS ==========================================
                            JSONArray active_offersArray = json_data.getJSONArray("active_offers");
                            handleDisplayOfActiveOffers(active_offersArray);

                            // LOOP THROUGH EVENTS =================================================
                            JSONArray eventsArray = json_data.getJSONArray("events");
                            handleEventsJSonData(eventsArray);

                            // MAP LOCATION ========================================================
                            startUpMap();

                            // CLICK TO ORDER NUMBER ===============================================
                            if (json_data.getJSONObject("delivery_partner").has("id")) {
                                JSONObject delivery_object = json_data.getJSONObject("delivery_partner");
                                STR_DELIVERY_PARTNER_ID = delivery_object.getString("id");
                                STR_DELIVERY_PARTNER_NAME = delivery_object.getString("name");
                                STR_DELIVERY_PARTNER_PHONE = delivery_object.getString("phone_number");
                            } else {
                                STR_DELIVERY_PARTNER_ID = "";
                                STR_DELIVERY_PARTNER_NAME = "";
                                STR_DELIVERY_PARTNER_PHONE = "";
                            }

                            // GALLERY =============================================================
//                            galleryList.clear();
                            JSONArray galleryImageArrays = json_data.getJSONArray("gallery");
                            if (!galleryImageArrays.isNull(0)) {
                                LL_gallery.setVisibility(View.VISIBLE);
                                for (int v = 0; v < galleryImageArrays.length(); v++) {
                                    JSONObject galleryObject = galleryImageArrays.getJSONObject(v);
//                                    GalleryModel galleryModel = new GalleryModel();
//                                    galleryModel.image = galleryObject.getString("full_thumbnail_url");
//                                    galleryModel.image_large = galleryObject.getString("full_image_url");
//                                    galleryModel.type = GALLERY_IMAGE;
//                                    galleryModel.rest_id = STR_RESTAURANT_ID;
//                                    galleryModel.delivery_id = "";
//                                    galleryModel.delivery_name = "";
//                                    galleryModel.delivery_phone = "";
//                                    galleryList.add(galleryModel);
                                }
//                                galleryAdapter.notifyDataSetChanged();
                            } else {
                                LL_gallery.setVisibility(View.GONE);
                            }

                            // MENU DISPLAY ========================================================
                            txt_menu_title.setText(json_data.getString("menu_title"));
                            Boolean show_menu_text = false, show_menu_gallery = false;

                            // MENU WEB VIEW TEXT
                            if (!json_data.get("text_menu").equals("<p><br></p>") &&
                                    !json_data.get("text_menu").equals("")) {
                                final WebSettings webSettings = txt_menu.getSettings();
                                Resources res = getResources();
                                float fontSize = res.getDimension(R.dimen.text_size_web);
                                webSettings.setDefaultFontSize((int) fontSize);
                                txt_menu.loadData("<style>body{padding-left: -5px;}</style>"
                                        + json_data.getString("text_menu"),
                                        "text/html; charset=utf-8", "UTF-8");
                                txt_menu.setVisibility(View.VISIBLE);
                                show_menu_text = true;
                            }

                            // MENU GALLERY
//                            menuList.clear();
                            JSONArray menuImageArrays = json_data.getJSONArray("menu_gallery");
                            if (!menuImageArrays.isNull(0)) {
                                for (int v = 0; v < menuImageArrays.length(); v++) {
                                    JSONObject galleryObject = menuImageArrays.getJSONObject(v);
//                                    GalleryModel galleryModel = new GalleryModel();
//                                    galleryModel.image = galleryObject.getString("full_thumbnail_url");
//                                    galleryModel.image_large = galleryObject.getString("full_image_url");
//                                    galleryModel.type = GALLERY_MENU;
//                                    galleryModel.rest_id = STR_RESTAURANT_ID;
//                                    galleryModel.delivery_id = STR_DELIVERY_PARTNER_ID;
//                                    galleryModel.delivery_name = STR_DELIVERY_PARTNER_NAME;
//                                    galleryModel.delivery_phone = STR_DELIVERY_PARTNER_PHONE;
//                                    menuList.add(galleryModel);
                                }
//                                menuAdapter.notifyDataSetChanged();
                                menu_recycler_view.setVisibility(View.VISIBLE);
                                show_menu_gallery = true;
                            } else {
                                menu_recycler_view.setVisibility(View.GONE);
                            }

                            if(show_menu_gallery || show_menu_text){
                                LL_menu.setVisibility(View.VISIBLE);
                            }else{
                                LL_menu.setVisibility(View.GONE);
                            }


                            // WORKING HOURS =======================================================
                            JSONArray operating_hours = json_data.getJSONArray("operating_hours");
                            int COUNTER_DOW = 0;
                            String ARRAY_HOURS_ONE[] = new String[13];
                            String ARRAY_HOURS_TWO[] = new String[13];

                            if (!operating_hours.isNull(0)) {
                                for (int v = 0; v < operating_hours.length(); v++) {
                                    JSONObject operating_hoursObject = operating_hours.getJSONObject(v);
                                    int day_of_week = operating_hoursObject.getInt("day_of_week");
                                    String open_time = operating_hoursObject.getString("open_time");
                                    String close_time = operating_hoursObject.getString("close_time");

                                    if (COUNTER_DOW == day_of_week) {
                                        ARRAY_HOURS_TWO[day_of_week - 1] = open_time + " "
                                                + getString(R.string.txt_to) + " " + close_time;

                                    } else {
                                        COUNTER_DOW = day_of_week;
                                        ARRAY_HOURS_ONE[day_of_week - 1] = open_time + " "
                                                + getString(R.string.txt_to) + " " + close_time;

                                    }

                                }
                            }

                            LayoutInflater layoutInflater;
                            layoutInflater = LayoutInflater.from(RestaurantDetailsActivity.this);
                            LL_working_hours.removeAllViews();
                            for (int v = 0; v < 7; v++) {
                                @SuppressLint("InflateParams") final View child = layoutInflater
                                        .inflate(R.layout.list_item_working_hours, null);
                                final TextView txt_day_of_week = child.findViewById(R.id.day_of_week);
                                final TextView txt_time_open = child.findViewById(R.id.time_open);


                                switch (v) {
                                    case 0:
                                        txt_day_of_week.setText(getString(R.string.txt_mon));
                                        break;
                                    case 1:
                                        txt_day_of_week.setText(getString(R.string.txt_tue));
                                        break;
                                    case 2:
                                        txt_day_of_week.setText(getString(R.string.txt_wed));
                                        break;
                                    case 3:
                                        txt_day_of_week.setText(getString(R.string.txt_thu));
                                        break;
                                    case 4:
                                        txt_day_of_week.setText(getString(R.string.txt_fri));
                                        break;
                                    case 5:
                                        txt_day_of_week.setText(getString(R.string.txt_sat));
                                        break;
                                    default:
                                        txt_day_of_week.setText(getString(R.string.txt_sun));
                                        break;
                                }


                                String open_close_hours;

                                if (ARRAY_HOURS_ONE[v] == null) {
                                    txt_time_open.setText(getString(R.string.txt_closed));

                                } else if (ARRAY_HOURS_TWO[v] == null) {
                                    open_close_hours = ARRAY_HOURS_ONE[v];
                                    txt_time_open.setText(open_close_hours);

                                } else {
                                    open_close_hours = ARRAY_HOURS_ONE[v] + ", " + ARRAY_HOURS_TWO[v];
                                    txt_time_open.setText(open_close_hours);
                                }

                                LL_working_hours.addView(child);
                            }

                            // SHARE LINK URL ======================================================
                            STR_SHARE_LINK = STR_SHARE_LINK + json_data.getString("url");


                            // RESERVATIONS ========================================================
//                            if (json_data.getInt("allow_booking") == 1) {
//                                btn_make_reservation.setVisibility(View.VISIBLE);
//                            } else {
//                                btn_make_reservation.setVisibility(View.GONE);
//                            }
                            btn_make_reservation.setVisibility(View.GONE);

                            updateScreen(STATE_INFORMATION);

                        } else {

                            updateScreen(STATE_OTHER_OFFERS);

                        }

                    } else {
                        db.deleteOneRestaurant(STR_RESTAURANT_ID);
                        helper.ToastMessage(RestaurantDetailsActivity.this, "Seems that this Restaurant is temporarily closed");
                        finish();
                    }
                } catch (JSONException e) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                    updateScreen(STATE_ERROR);

                } catch (Exception e) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                    updateScreen(STATE_ERROR);
                }
                helper.progressDialog(false);
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());

                helper.progressDialog(false);
                updateScreen(STATE_ERROR);
            }
        });
    }

    private void asyncSetLike() {
        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.favoriteOneRestaurant(STR_RESTAURANT_ID, Database.userModel.user_id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
                    Boolean success = main.getBoolean("success");
                    if (success) {
                        String action = main.getString("action");
                        if (action.equals("create")) {
                            db.setLikedRestaurant(STR_RESTAURANT_ID);
                            setUpLikeButton();
                        } else {
                            db.deleteLikedRestaurant(STR_RESTAURANT_ID);
                            setUpLikeButton();
                        }
                    } else {
                        helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_fav));
                    }
                } catch (JSONException e) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                    helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_fav));

                } catch (Exception e) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                    helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_fav));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());
                helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_fav));
            }
        });
    }


    // POST REVIEW  ASYNC FUNCTION =================================================================
    private void asyncGetReview(final Boolean startAfresh) {
        if (!STR_MESSAGE_BODY.equals(STR_RESTAURANT_ID + String.valueOf(CURRENT_PAGE))) {
            STR_MESSAGE_BODY = STR_RESTAURANT_ID + String.valueOf(CURRENT_PAGE);

            if (startAfresh) {
                CURRENT_PAGE = 0;
//                reviewList.clear();
            }

            review_swiperefresh.setRefreshing(true);

            RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
            final Call<JsonObject> call = restaurantService.getOneRestaurantsReviews(
                    STR_RESTAURANT_ID,
                    String.valueOf(CURRENT_PAGE)
            );

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    review_swiperefresh.setRefreshing(false);
                    helper.progressDialog(false);
                    try {
                        JSONObject main = new JSONObject(String.valueOf(response.body()));
                        Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
                        if (!main.getBoolean("error")) {

                            Helpers.LogThis(TAG_LOG, "PAGE NUMBER Internal: " + CURRENT_PAGE);
                            Helpers.LogThis(TAG_LOG, "PAGE NUMBER Server: " + main.getInt("last_page"));

                            LAST_PAGE = main.getInt("last_page");

                            if (CURRENT_PAGE >= LAST_PAGE) {
                                bool_loading = false;
                            }

                            JSONArray jArray = main.getJSONArray("result");
                            int result_length = jArray.length();

                            Helpers.LogThis(TAG_LOG, getString(R.string.log_response_length) + result_length);

                            if (result_length <= 0 && CURRENT_PAGE < 1) {
                                noReviews();
                            } else {
                                CURRENT_PAGE++;

                                for (int i = 0; i < result_length; i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);
//                                    ReviewModel reviewModel = new ReviewModel();

//                                    reviewModel.user_display_name = json_data.getString("user_display_name");
//                                    reviewModel.user_thumbnail = json_data.getString("user_thumbnail");
//                                    reviewModel.review_text = json_data.getString("review_text");
//                                    reviewModel.rating = json_data.getString("rating");

                                    JSONObject created_at = json_data.getJSONObject("created_at");
                                    if (!created_at.isNull("date")) {
                                        String[] splited = created_at.getString("date").split(" ");
//                                        reviewModel.created_at_date = splited[0];
                                    }

//                                    reviewList.add(reviewModel);

                                }
                            }
                            review_recyclerView.getRecycledViewPool().clear();
//                            review_adapter.notifyDataSetChanged();
                        } else {
                            noReviews();
                        }

                    } catch (JSONException e) {
                        Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                        noReviews();

                    } catch (Exception e) {
                        noReviews();
                        Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());

                    helper.progressDialog(false);
                    review_swiperefresh.setRefreshing(false);
                    noReviews();

                }

            });

        } else {
            review_swiperefresh.setRefreshing(false);
        }

    }

    private void asyncSetReview(
            int rating_food,
            int rating_service,
            int rating_ambiance,
            int rating_value,
            int rating_average,
            String review_text) {

        helper.progressDialog(true);
        helper.setProgressDialogMessage(getString(R.string.progress_loading_review));

        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.postRestaurantsReviews(
                STR_RESTAURANT_ID,
                Database.userModel.user_id,
                rating_food,
                rating_service,
                rating_ambiance,
                rating_value,
                rating_average,
                review_text
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
                    Boolean success = main.getBoolean("success");
                    if (success) {
                        helper.myDialog(RestaurantDetailsActivity.this,
                                "EatOut", "Thank you, Your post has been submitted and will be reviewed for publishing");
                    } else {
                        helper.myDialog(RestaurantDetailsActivity.this,
                                getString(R.string.app_name), getString(R.string.error_unknown));
                    }
                } catch (JSONException e) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                    helper.myDialog(RestaurantDetailsActivity.this,
                            getString(R.string.app_name), getString(R.string.error_unknown));

                } catch (Exception e) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                    helper.myDialog(RestaurantDetailsActivity.this,
                            getString(R.string.app_name), getString(R.string.error_unknown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());
                helper.progressDialog(false);
                helper.ToastMessage(RestaurantDetailsActivity.this,
                        getString(R.string.error_connection));

            }
        });
    }


    // EVENTS HANDLED HERE =========================================================================
    private void handleEventsJSonData(JSONArray eventsArray) throws JSONException {
        LayoutInflater linf;
        linf = LayoutInflater.from(RestaurantDetailsActivity.this);

        for (int v = 0; v < eventsArray.length(); v++) {
            if (!eventsArray.isNull(v)) {
                JSONObject eventsObject = eventsArray.getJSONObject(v);

                // FIND ALL THE VIEWS ON THE CARD
                @SuppressLint("InflateParams")
                View child = linf.inflate(R.layout.list_item_events, null);
                final TextView event_id = child.findViewById(R.id.event_id);
                final TextView event_title = child.findViewById(R.id.event_title);
                final TextView event_timing_description = child.findViewById(R.id.event_timing_description);
                final TextView event_price = child.findViewById(R.id.event_price);
                final ImageView event_icon = child.findViewById(R.id.event_icon);
                final TextView show_more = child.findViewById(R.id.show_more);
                final TextView event_short_description = child.findViewById(R.id.event_short_description);
                final LinearLayout more_layout = child.findViewById(R.id.more_layout);
                more_layout.setVisibility(View.GONE);

                // CREATE A MODEL FOR THE CARD
//                final EventModel eventModel = new EventModel();
//                eventModel.id = eventsObject.getInt("id");
//                eventModel.title = eventsObject.getString("title");
//                eventModel.timing_description = eventsObject.getString("timing_description");
//                eventModel.price = eventsObject.getInt("price");
//                eventModel.full_image_url = eventsObject.getString("full_image_url");
//                eventModel.short_description = eventsObject.getString("short_description");
//                eventModel.venue = eventsObject.getString("venue");


                // SET THE DATA FROM THE THE JSON
//                event_id.setText(String.valueOf(eventModel.id));
//                event_title.setText(eventModel.title);
//                event_timing_description.setText(eventModel.timing_description);
//                event_short_description.setText(eventModel.short_description);
//                if (eventModel.price == 0) {
//                    event_price.setText(R.string.txt_entry_free);
//                } else {
//                    event_price.setText(getString(R.string.txt_entry_fee).concat(String.valueOf(eventModel.price)));
//                }
//                Glide.with(RestaurantDetailsActivity.this).load(eventModel.full_image_url).into(event_icon);


                // CREATE THE SHOW MORE BUTTON CLICKABLE AND VISIBLE IF SHORT DESCRIPTION != EMPTY
//                if (eventModel.short_description.equals("")) {
//                    show_more.setVisibility(View.GONE);
//                } else {
//                    show_more.setVisibility(View.VISIBLE);
//                }

                // ON CLICK LISTENERS ON THE EVENT ITEM
                show_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (more_layout.getVisibility() == View.GONE) {
                            more_layout.setVisibility(View.VISIBLE);
                            helper.animate_fade_in_up(more_layout);
                            show_more.setText(R.string.txt_show_less);
                        } else {
                            more_layout.setVisibility(View.GONE);
                            show_more.setText(R.string.txt_show_more);
                        }
                    }
                });

                event_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> image_urls = new ArrayList<>();
//                        image_urls.add(eventModel.full_image_url);
                        Activity activity = RestaurantDetailsActivity.this;
                        activity.startActivity(new Intent(RestaurantDetailsActivity.this, GalleryViewActivity.class)
                                .putExtra("image_urls", image_urls)
                                .putExtra("selected_position", 1)
                                .putExtra("rest_id", STR_RESTAURANT_ID)
                                .putExtra("delivery_id", "")
                                .putExtra("delivery_name", "")
                                .putExtra("delivery_phone", "")
                        );
                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });

                LL_events.addView(child);

            }
        }

        if (eventsArray.length() < 1) {
            LL_events.setVisibility(View.GONE);
        }
    }


    // ACTIVE OFFERS AND GENERATE CARDS ============================================================
    private void handleDisplayOfActiveOffers(JSONArray active_offersArray) throws JSONException {
        LayoutInflater linf;
        linf = LayoutInflater.from(RestaurantDetailsActivity.this);
        String[] TERMS_AND_CONDITIONS = new String[active_offersArray.length()];

        for (int v = 0; v < active_offersArray.length(); v++) {
            if (!active_offersArray.isNull(v)) {
                JSONObject active_offers = active_offersArray.getJSONObject(v);

                // FIND ALL THE VIEWS ON THE CARD ==================================================
                @SuppressLint("InflateParams")
                View child = linf.inflate(R.layout.list_item_offers, null);
                final TextView offer_id = child.findViewById(R.id.offer_id);
                final TextView offer_name = child.findViewById(R.id.offer_name);
                final ImageView offer_icon = child.findViewById(R.id.offer_icon);
                final RelativeLayout button_panel = child.findViewById(R.id.offer_background);

                // CREATE THE VARIABLES AND FETCH FROM JSON ========================================
                JSONObject offer_category = active_offers.getJSONObject("offer_category");
                final Boolean OFFER_REDEEMABLE = active_offers.getInt("redeemable") == 1;
                final String OFFER_ICON = active_offers.getString("full_icon_image_url");
                final int OFFER_ID = active_offers.getInt("id");
                final int DISCOUNT_AMOUNT = active_offers.getInt("discount_amount");
                final String CARD_EXCERPT = active_offers.getString("excerpt");
                final int OFFER_CATEGORY_ID = offer_category.getInt("id");
                final String CARD_TITLE = offer_category.getString("card_title");
                final String CARD_DESC = offer_category.getString("card_description");
                final String CARD_BCKGRD_IMAGE = offer_category.getString("card_background_image");
                final String CARD_BCKGRD_COLOR = offer_category.getString("card_background_color_hex");
                final String CARD_BUTTON_TEXT = offer_category.getString("card_button_text");
                final String CARD_TEXT_COLOR = offer_category.getString("card_font_color_hex");
                final Boolean CARD_SHOW_CONFIRM_CODE = offer_category.getInt("show_confirmation_code_input") == 1;
                final Boolean CARD_ALLOW_REVIEW = offer_category.getInt("allow_reviews") == 1;

                // SET THE DATA FROM THE THE JSON ==================================================
                offer_id.setText(String.valueOf(OFFER_ID));
                offer_name.setText(active_offers.getString("name"));
                int bgColor = Color.parseColor(CARD_BCKGRD_COLOR);
                int textColor = Color.parseColor(CARD_TEXT_COLOR);
                button_panel.setBackgroundColor(bgColor);
                offer_name.setTextColor(textColor);
                Glide.with(RestaurantDetailsActivity.this).load(OFFER_ICON).into(offer_icon);
                TERMS_AND_CONDITIONS[v] = active_offers.getString("description");

                // CREATE A MODEL FOR THE CARD =====================================================
//                final DynamicCardModel dynamicCardModel = new DynamicCardModel();
//                dynamicCardModel.CARD_DISCOUNT_AMOUNT = DISCOUNT_AMOUNT;
//                dynamicCardModel.CARD_OFFER_CATEGORY_ID = OFFER_CATEGORY_ID;
//                dynamicCardModel.CARD_OFFER_ID = OFFER_ID;
//                dynamicCardModel.CARD_OFFER_ICON = OFFER_ICON;
//                dynamicCardModel.CARD_TITLE = CARD_TITLE;
//                dynamicCardModel.CARD_BCKGRD_IMAGE = CARD_BCKGRD_IMAGE;
//                dynamicCardModel.CARD_BCKGRD_COLOR = CARD_BCKGRD_COLOR;
//                dynamicCardModel.CARD_BUTTON_TEXT = CARD_BUTTON_TEXT;
//                dynamicCardModel.CARD_DESC = CARD_DESC;
//                dynamicCardModel.CARD_TEXT_COLOR = CARD_TEXT_COLOR;
//                dynamicCardModel.CARD_EXCERPT = CARD_EXCERPT;
//                dynamicCardModel.CARD_ALLOW_REVIEW = CARD_ALLOW_REVIEW;
//                dynamicCardModel.CARD_SHOW_CONFIRMATION_CODE = CARD_SHOW_CONFIRM_CODE;
//                dynamicCardModel.CARD_CONFIRMATION_CODE = "";
//                dynamicCardModel.CARD_TNC = TERMS_AND_CONDITIONS[v];

                // IF THE OFFER IS REDEEMABLE THEN SHOW THE BUTTON =================================
                if (OFFER_REDEEMABLE) {
                    child.setVisibility(View.VISIBLE);
                    child.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            globalDynamicCardModel = dynamicCardModel;
                            fetchLocationAndClaimOffer();

                            if (BuildConfig.DEBUG) {
//                                helper.ToastMessage(RestaurantDetailsActivity.this, "OFFER ID: " + globalDynamicCardModel.CARD_OFFER_ID);
                            }

                        }
                    });

                    // SHOW THE CARD IF THE ICON CLICKED ON THE LIST BEFORE MATCHES THE OFFER ID
//                    if (INT_OFFER_ID != 0 && INT_OFFER_ID == dynamicCardModel.CARD_OFFER_ID) {
//                        globalDynamicCardModel = dynamicCardModel;
//                        fetchLocationAndClaimOffer();
//                    }

                    LL_offers_children.addView(child);
                }
            }
        }

        if (active_offersArray.length() < 1) {
            LL_offers.setVisibility(View.GONE);
        }
    }

//    private void generateConfirmOffer(final DynamicCardModel dynamicCardModel) {
//        final Dialog dialog = new Dialog(RestaurantDetailsActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_confirm_offer);
//        final TextView txtTitle = dialog.findViewById(R.id.txtTitle);
//        final TextView txtMessage = dialog.findViewById(R.id.txtMessage);
//        final TextView txtOk = dialog.findViewById(R.id.txtOk);
//        final TextView txtCancel = dialog.findViewById(R.id.txtCancel);
//        final TextView digital_card_confirmation_code = dialog.findViewById(R.id.digital_card_confirmation_code);
//        final RoundedImageView digital_card_background_color = dialog.findViewById(R.id.digital_card_background_color);
//        final RoundedImageView digital_card_icon = dialog.findViewById(R.id.digital_card_icon);
//
//        int bgColor = Color.parseColor(dynamicCardModel.CARD_BCKGRD_COLOR);
//        digital_card_background_color.setBackgroundColor(bgColor);
//
//        Glide.with(RestaurantDetailsActivity.this).load(dynamicCardModel.CARD_OFFER_ICON).into(digital_card_icon);
//
//        txtTitle.setText(dynamicCardModel.CARD_TITLE);
//
//        txtOk.setText(R.string.txt_proceed);
////        txtCancel.setText(R.string.txt_cancel);
//        txtCancel.setVisibility(View.VISIBLE);
//
//        txtMessage.setText(dynamicCardModel.CARD_EXCERPT);
//
//        if (dynamicCardModel.CARD_SHOW_CONFIRMATION_CODE) {
//            digital_card_confirmation_code.setVisibility(View.VISIBLE);
//        } else {
//            digital_card_confirmation_code.setVisibility(View.GONE);
//        }
//
//        txtCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.cancel();
//            }
//        });
//
//        txtOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (dynamicCardModel.CARD_SHOW_CONFIRMATION_CODE) {
//                    if (digital_card_confirmation_code.getText().toString().length() < 1) {
//                        digital_card_confirmation_code.setError("Incorrect");
//                    } else {
//                        dynamicCardModel.CARD_CONFIRMATION_CODE = digital_card_confirmation_code.getText().toString();
//                        asyncSetClaimOffer(dynamicCardModel);
//                        dialog.cancel();
//                    }
//                } else {
//                    asyncSetClaimOffer(dynamicCardModel);
//                    dialog.cancel();
//                }
//            }
//        });
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        dialog.show();
//        dialog.getWindow().setAttributes(lp);
//    }

//    private void generateDigitalCard(final DynamicCardModel dynamicCardModel, final String ref_code) {
//        final Dialog dialog = new Dialog(RestaurantDetailsActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_digital_card);
//        dialog.setCancelable(false);
//
//        final TextView digital_card_title = dialog.findViewById(R.id.digital_card_title);
//        final TextView digital_card_user_name = dialog.findViewById(R.id.digital_card_user_name);
//        final TextView digital_card_ref_code_txt = dialog.findViewById(R.id.digital_card_ref_code_txt);
//        final TextView digital_card_ref_code = dialog.findViewById(R.id.digital_card_ref_code);
//        final TextView digital_card_time_label = dialog.findViewById(R.id.digital_card_time_label);
//        final TextView digital_card_time = dialog.findViewById(R.id.digital_card_time);
//        final TextView digital_card_date_label = dialog.findViewById(R.id.digital_card_date_label);
//        final TextView digital_card_date = dialog.findViewById(R.id.digital_card_date);
//        final TextView digital_card_support_number = dialog.findViewById(R.id.digital_card_support_number);
//        final TextView digital_card_desc = dialog.findViewById(R.id.digital_card_desc);
//        final TextView txtOk = dialog.findViewById(R.id.txtOk);
//        final TextView txtCancel = dialog.findViewById(R.id.txtCancel);
//        final ImageView btn_separator = dialog.findViewById(R.id.btn_separator);
//        RoundedImageView digital_card_icon = dialog.findViewById(R.id.digital_card_icon);
//        RoundedImageView digital_card_background_image = dialog.findViewById(R.id.digital_card_background_image);
//        RoundedImageView digital_card_background_color = dialog.findViewById(R.id.digital_card_background_color);
//
//
//        digital_card_ref_code.setText(ref_code);
//
////      "EEE, d MMM yyyy HH:mm:ss Z"------- Wed, 4 Jul 2001 12:08:56 -0700
//        @SuppressLint("SimpleDateFormat")
//        SimpleDateFormat sdfDATE = new SimpleDateFormat("d.MMM.yyyy");
//        @SuppressLint("SimpleDateFormat")
//        SimpleDateFormat sdfTIME = new SimpleDateFormat("h:mm a");
//
////        UserModel userModel = db.getUser();
//        digital_card_user_name.setText(Database.userModel.first_name.concat(" ").concat(Database.userModel.last_name));
//        digital_card_date.setText(sdfDATE.format(new Date()));
//        digital_card_time.setText(sdfTIME.format(new Date()));
//
//
//        digital_card_title.setText(dynamicCardModel.CARD_TITLE);
//        Glide.with(RestaurantDetailsActivity.this).load(dynamicCardModel.CARD_BCKGRD_IMAGE).into(digital_card_background_image);
//        Glide.with(RestaurantDetailsActivity.this).load(dynamicCardModel.CARD_OFFER_ICON).into(digital_card_icon);
//        txtOk.setText(dynamicCardModel.CARD_BUTTON_TEXT);
//        digital_card_desc.setText(dynamicCardModel.CARD_DESC);
//        if (SharedPrefs.getSupportNumber().equals("")) {
//            SharedPrefs.setSupportNumber("254711222222");
//        }
//        digital_card_support_number.setText(SharedPrefs.getSupportNumber());
//        digital_card_support_number.setPaintFlags(digital_card_support_number.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        digital_card_support_number.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogMakeCall(getString(R.string.txt_calling), SharedPrefs.getSupportNumber());
//            }
//        });
//
//        int bckgrdColor = Color.parseColor(dynamicCardModel.CARD_BCKGRD_COLOR);
//        digital_card_background_color.setBackgroundColor(bckgrdColor);
//
//        int textColor = Color.parseColor(dynamicCardModel.CARD_TEXT_COLOR);
//        digital_card_user_name.setTextColor(textColor);
//        digital_card_time_label.setTextColor(textColor);
//        digital_card_time.setTextColor(textColor);
//        digital_card_date_label.setTextColor(textColor);
//        digital_card_date.setTextColor(textColor);
//        digital_card_ref_code.setTextColor(textColor);
//        digital_card_ref_code_txt.setTextColor(textColor);
//
//        if (MPESA_CHECKOUT_ENABLED && dynamicCardModel.CARD_OFFER_CATEGORY_ID == YUMMY_CARD) {
//            txtCancel.setVisibility(View.VISIBLE);
//            btn_separator.setVisibility(View.VISIBLE);
//        } else {
//            txtCancel.setVisibility(View.GONE);
//            btn_separator.setVisibility(View.GONE);
//        }
//
//        txtCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.cancel();
//            }
//        });
//
//        txtOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (MPESA_CHECKOUT_ENABLED && dynamicCardModel.CARD_OFFER_CATEGORY_ID == YUMMY_CARD) {
//                    startActivity(new Intent(RestaurantDetailsActivity.this, PaymentActivity.class)
//                            .putExtra("restaurant_id", STR_RESTAURANT_ID)
//                            .putExtra("restaurant_name", STR_RESTAURANT_NAME)
//                            .putExtra("offer_id", dynamicCardModel.CARD_OFFER_ID)
//                            .putExtra("offer_discount", String.valueOf(dynamicCardModel.CARD_DISCOUNT_AMOUNT))
//                            .putExtra("offer_icon", dynamicCardModel.CARD_OFFER_ICON)
//                            .putExtra("tnc", dynamicCardModel.CARD_TNC)
//                    );
//                } else {
//                    if (dynamicCardModel.CARD_ALLOW_REVIEW) {
//                        capturePartialReview();
//                    } else {
//                        helper.myDialog(RestaurantDetailsActivity.this,
//                                getString(R.string.app_name),
//                                getString(R.string.txt_redeem_offer_success));
//                    }
//                }
//                dialog.cancel();
//            }
//        });
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        dialog.show();
//        dialog.getWindow().setAttributes(lp);
//    }


    // CLAIM OFFER ASYNC TASKS =====================================================================
//    private void asyncGetClaimOfferCount(final DynamicCardModel dynamicCardModel) {
//        helper.progressDialog(true);
//        helper.setProgressDialogMessage(getString(R.string.progress_loading_claim_offer_status));
//
//        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
//        final Call<JsonObject> call = restaurantService.claimOffer(
//                dynamicCardModel.CARD_OFFER_ID,
//                Database.userModel.user_id,
//                dynamicCardModel.CARD_CONFIRMATION_CODE,
//                CLAIM_OFFER_CHECKING
//        );
//
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
//                helper.progressDialog(false);
//                try {
//                    JSONObject main = new JSONObject(String.valueOf(response.body()));
//                    Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
//                    Boolean success = main.getBoolean("success");
//                    if (success) {
//                        generateConfirmOffer(dynamicCardModel);
//                    } else {
//                        helper.myDialog(RestaurantDetailsActivity.this,
//                                getString(R.string.app_name), main.getString("failure_message"));
//                    }
//                } catch (JSONException e) {
//                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
//                    helper.myDialog(RestaurantDetailsActivity.this,
//                            getString(R.string.app_name), getString(R.string.error_unknown));
//
//                } catch (Exception e) {
//                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
//                    helper.myDialog(RestaurantDetailsActivity.this,
//                            getString(R.string.app_name), getString(R.string.error_unknown));
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
//                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
//                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());
//                helper.progressDialog(false);
//                helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_connection));
//
//            }
//        });
//    }

//    private void asyncSetClaimOffer(final DynamicCardModel dynamicCardModel) {
//        helper.progressDialog(true);
//        helper.setProgressDialogMessage(getString(R.string.progress_loading_claim_offer));
//
//        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
//        final Call<JsonObject> call = restaurantService.claimOffer(
//                dynamicCardModel.CARD_OFFER_ID,
//                Database.userModel.user_id,
//                dynamicCardModel.CARD_CONFIRMATION_CODE,
//                CLAIM_OFFER_CLAIMING
//        );
//
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
//                helper.progressDialog(false);
//                try {
//                    JSONObject main = new JSONObject(String.valueOf(response.body()));
//                    Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
//                    Boolean success = main.getBoolean("success");
//                    if (success) {
//                        generateDigitalCard(dynamicCardModel, main.getString("ref_code"));
//                    } else {
//                        helper.myDialog(RestaurantDetailsActivity.this,
//                                getString(R.string.app_name), main.getString("failure_message"));
//                    }
//                } catch (JSONException e) {
//                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
//                    helper.myDialog(RestaurantDetailsActivity.this,
//                            getString(R.string.app_name), getString(R.string.error_unknown));
//
//                } catch (Exception e) {
//                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
//                    helper.myDialog(RestaurantDetailsActivity.this,
//                            getString(R.string.app_name), getString(R.string.error_unknown));
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
//                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
//                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());
//                helper.progressDialog(false);
//                helper.ToastMessage(RestaurantDetailsActivity.this, getString(R.string.error_connection));
//
//            }
//        });
//    }


    // FETCH RESTAURANTS IN CASE THIS RESTAURANT IS A WHITE RESTAURANT =============================
//    private void asyncGetRestaurants() {
//        restaurantsList.clear();
//
//        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
//        final Call<JsonObject> call = restaurantService.searchRestaurant(
//                "",
//                "",
//                Database.userModel.city_id,
//                "",
//                "",
//                "",
//                String.valueOf(RESTAURANT_LATITUDE),
//                String.valueOf(RESTAURANT_LONGITUDE),
//                SORT_NEARBY,
//                "",
//                FLAG_TRUE,
//                FLAG_FALSE
//        );
//
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
//                try {
//                    JSONObject main = new JSONObject(String.valueOf(response.body()));
//                    Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
//                    if (!main.getBoolean("error")) {
//
//                        JSONArray jArray = main.getJSONArray("result");
//                        int result_length = jArray.length();
//
//                        Helpers.LogThis(TAG_LOG, getString(R.string.log_response_length) + result_length);
//
//                        if (result_length <= 0) {
//                            noRestaurants();
//                        } else {
//
//                            for (int i = 0; i < 10; i++) {
//                                RestaurantModel restaurantModel = db.setRestaurants(jArray.getJSONObject(i + 1));
//                                restaurantsList.add(restaurantModel);
//                            }
//                        }
//                        restaurant_adapter.notifyDataSetChanged();
//
//                    } else {
//                        noRestaurants();
//                    }
//                } catch (JSONException e) {
//                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
//                    noRestaurants();
//
//                } catch (Exception e) {
//                    noRestaurants();
//                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
//                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
//                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());
//
//                if (helper.validateInternetConnection()) {
//                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.drawer_layout),
//                            getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
//                    snackBar.setAction("Retry", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            snackBar.dismiss();
//                            asyncGetRestaurants();
//                        }
//                    });
//                    snackBar.show();
//                } else {
//                    noRestaurants();
//                }
//            }
//        });
//
//    }

}