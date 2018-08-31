package com.hotelaide.main.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobActivity extends AppCompatActivity {
    private Helpers helpers;

    private Toolbar toolbar;

    private TextView
            toolbar_text,
            txt_job_name,
            txt_job_description,
            txt_job_location,
            txt_job_requirements,
            txt_job_post_date,
            txt_job_end_date,
            txt_hotel_name,
            txt_hotel_id;

    private ImageView
            img_banner;

    private AppBarLayout app_bar_layout;

    private String
            STR_PAGE_TITLE = "",
            STR_SHARE_LINK = "Please check out this Job Vacancy on HotelAide ";

    private int INT_JOB_ID = 0;

    private final String
            TAG_LOG = "JOB VACANCY";


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (handleExtraBundles()) {
            setContentView(R.layout.activity_job);

            helpers = new Helpers(JobActivity.this);

            setUpToolBarAndTabs();

            findAllViews();

            setListeners();

            asyncFetchHotel();

        } else {
            helpers.ToastMessage(JobActivity.this, getString(R.string.error_unknown));
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
                helpers.dialogShare(JobActivity.this, STR_SHARE_LINK);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // BASIC FUNCTIONS =============================================================================
    private Boolean handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getInt("JOB_ID") != 0) {
            INT_JOB_ID = extras.getInt("JOB_ID");
            Helpers.LogThis(TAG_LOG, "JOB ID: " + INT_JOB_ID);
            return true;
        } else {
            return false;
        }
    }

    private void findAllViews() {
        app_bar_layout = findViewById(R.id.app_bar_layout);

        img_banner = findViewById(R.id.img_banner);
        txt_hotel_name = findViewById(R.id.txt_hotel_name);
        txt_hotel_id = findViewById(R.id.txt_hotel_id);

        txt_job_name = findViewById(R.id.txt_job_name);
        txt_job_description = findViewById(R.id.txt_job_description);
        txt_job_requirements = findViewById(R.id.txt_job_requirements);
        txt_job_location = findViewById(R.id.txt_job_location);
        txt_job_post_date = findViewById(R.id.txt_job_post_date);
        txt_job_end_date = findViewById(R.id.txt_job_end_date);

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

    private void setListeners() {
        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    toolbar.setBackground(getResources().getDrawable(R.drawable.bckgrd_toolbar));
                    toolbar_text.setText(STR_PAGE_TITLE);
                } else if (verticalOffset == 0) {
                    toolbar_text.setText("");
                    toolbar.setBackground(null);
                } else {
                    toolbar_text.setText("");
                    toolbar.setBackground(null);
                }
            }
        });

    }

    public void applyJob(View view) {
        asyncApplyJob();
    }

    public void viewHotel(View view) {
        startActivity(new Intent(JobActivity.this, HotelActivity.class)
                .putExtra("HOTEL_ID", Integer.parseInt(txt_hotel_id.getText().toString()))
        );
    }


    // GET HOTEL ASYNC FUNCTION ====================================================================
    private void asyncFetchHotel() {

        HotelService hotelService = HotelService.retrofit.create(HotelService.class);
        Call<JsonObject> call = hotelService.getJob(INT_JOB_ID);
        helpers.setProgressDialogMessage("Loading Job details");
        helpers.progressDialog(true);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {

                        JSONObject job_object = main.getJSONObject("data");
                        STR_PAGE_TITLE = job_object.getString("title");
                        txt_job_name.setText(STR_PAGE_TITLE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txt_job_description.setText(Html.fromHtml(job_object.getString("description"), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            txt_job_description.setText(Html.fromHtml(job_object.getString("description")));
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txt_job_requirements.setText(Html.fromHtml(job_object.getString("requirements"), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            txt_job_requirements.setText(Html.fromHtml(job_object.getString("requirements")));
                        }


//                        txt_job_location.setText(job_object.getString("location"));
                        txt_job_post_date.setText(getString(R.string.txt_posted_on).concat(job_object.getString("posted")));
                        txt_job_end_date.setText(getString(R.string.txt_posted_till).concat(job_object.getString("end_date")));
//                        STR_SHARE_LINK = STR_SHARE_LINK.concat(job_object.getString("hotel_url"));

                        // HOTEL OBJECT
                        JSONObject hotel_object = job_object.getJSONObject("hotel");
                        if (hotel_object != null) {
                            txt_hotel_id.setText(hotel_object.getString("id"));
                            txt_hotel_name.setText(hotel_object.getString("name"));
                            Glide.with(JobActivity.this).load(hotel_object.getString("image")).into(img_banner);
                        }


                    } else {
                        helpers.handleErrorMessage(JobActivity.this, main.getJSONObject("data"));
                    }


                } catch (JSONException e) {
                    helpers.ToastMessage(JobActivity.this, getString(R.string.error_server));
                    e.printStackTrace();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.progressDialog(false);
                Helpers.LogThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(JobActivity.this, getString(R.string.error_server));
                    onBackPressed();
                } else {
                    helpers.ToastMessage(JobActivity.this, getString(R.string.error_connection));
                    onBackPressed();
                }

            }
        });

    }

    private void asyncApplyJob() {

        HotelService hotelService = HotelService.retrofit.create(HotelService.class);
        Call<JsonObject> call = hotelService.getJob(INT_JOB_ID);
        helpers.setProgressDialogMessage("Applying for this position");
        helpers.progressDialog(true);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        helpers.ToastMessage(JobActivity.this, main.getString("message"));
                    } else {
                        helpers.handleErrorMessage(JobActivity.this, main.getJSONObject("data"));
                    }

                } catch (JSONException e) {
                    helpers.ToastMessage(JobActivity.this, getString(R.string.error_server));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.progressDialog(false);
                Helpers.LogThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(JobActivity.this, getString(R.string.error_server));
                } else {
                    helpers.ToastMessage(JobActivity.this, getString(R.string.error_connection));
                }

            }
        });

    }

}
