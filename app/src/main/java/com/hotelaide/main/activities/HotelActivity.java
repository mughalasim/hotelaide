package com.hotelaide.main.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.services.HotelService;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.SharedPrefs.USER_IMG_BANNER;

public class HotelActivity extends AppCompatActivity {
    private Helpers helpers;
    private Toolbar toolbar;
    private TextView toolbar_text, txt_hotel_name;
    private ImageView
            img_banner;
    private AppBarLayout app_bar_layout;
    private Boolean isCollapsedToolbar = false;
    private String
            STR_PAGE_TITLE = "",
            STR_SHARE_LINK = "Please check out this Hotel on HotelAide ";
    private int INT_HOTEL_ID = 0;
    private final String
            TAG_LOG = "HOTEL";


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (handleExtraBundles()) {
            setContentView(R.layout.activity_my_profile);

            helpers = new Helpers(HotelActivity.this);

            setUpToolBarAndTabs();

            findAllViews();

            setFromSharedPrefs();

            setListeners();

        } else {
            helpers.ToastMessage(HotelActivity.this, getString(R.string.error_unknown));
            onBackPressed();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);
        MenuItem menu_edit = menu.findItem(R.id.edit);
        menu_edit.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                helpers.dialogShare(HotelActivity.this, STR_SHARE_LINK);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // BASIC FUNCTIONS =============================================================================
    private Boolean handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getInt("HOTEL_ID") != 0) {
            INT_HOTEL_ID = extras.getInt("HOTEL_ID");
            Helpers.LogThis(TAG_LOG, "HOTEL ID: " + INT_HOTEL_ID);
            return true;
        } else {
            return false;
        }
    }

    private void findAllViews() {
        app_bar_layout = findViewById(R.id.app_bar_layout);

        img_banner = findViewById(R.id.img_banner);
        txt_hotel_name = findViewById(R.id.txt_hotel_name);

    }

    private void setUpToolBarAndTabs() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_text = toolbar.findViewById(R.id.toolbar_text);
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

    private void setFromSharedPrefs() {
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_BANNER)).into(img_banner);
    }

    private void setListeners() {
        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    isCollapsedToolbar = true;
                    toolbar_text.setText(STR_PAGE_TITLE);
                } else if (verticalOffset == 0) {
                    isCollapsedToolbar = false;
                    toolbar_text.setText(TAG_LOG);
                    txt_hotel_name.setText(STR_PAGE_TITLE);
                } else {
                    isCollapsedToolbar = false;
                    toolbar_text.setText(TAG_LOG);
                    txt_hotel_name.setText(STR_PAGE_TITLE);
                }
            }
        });

        final String[] perms = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    // GET HOTEL ASYNC FUNCTION ====================================================================
    private void asyncFetchHotel(final MultipartBody.Part partFile, final int type) {

        HotelService userService = HotelService.retrofit.create(HotelService.class);

        Call<JsonObject> call = userService.getHotel(INT_HOTEL_ID);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        if (SharedPrefs.setUser(main.getJSONObject("user"))) {
                            helpers.ToastMessage(HotelActivity.this, "Image updated");

                        } else {
                            helpers.ToastMessage(HotelActivity.this, getString(R.string.error_server));
                        }
                    } else {
                        helpers.handleErrorMessage(HotelActivity.this, main.getJSONObject("data"));
                    }

                    setFromSharedPrefs();

                } catch (JSONException e) {
                    helpers.ToastMessage(HotelActivity.this, getString(R.string.error_server));
                    e.printStackTrace();
                    setFromSharedPrefs();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.progressDialog(false);
                Helpers.LogThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(HotelActivity.this, getString(R.string.error_server));
                } else {
                    helpers.ToastMessage(HotelActivity.this, getString(R.string.error_connection));
                }

            }
        });

    }


}
