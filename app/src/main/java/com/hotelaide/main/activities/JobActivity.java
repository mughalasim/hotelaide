package com.hotelaide.main.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.EstablishmentInterface;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_APPLIED;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_SAVED;
import static com.hotelaide.utils.StaticVariables.INT_JOB_ID;
import static com.hotelaide.utils.StaticVariables.STR_SHARE_LINK;
import static com.hotelaide.utils.StaticVariables.USER_ID;

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
            txt_establishment_name;

    private ImageView
            img_banner;

    private MaterialButton
            btn_apply;

    private AppBarLayout app_bar_layout;

    private String
            STR_PAGE_TITLE = "",
            STR_BANNER_URL = "";

    private int
            INT_ESTABLISHMENT_ID = 0;

    private final String
            TAG_LOG = "JOB VACANCY";
    private Database db;

    private LinearLayout
            ll_main_view;

    private JSONObject
            globalJobObject;

    private SwipeRefreshLayout
            swipe_refresh;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_job);

        helpers = new Helpers(JobActivity.this);

        db = new Database();

        setUpToolBarAndTabs();

        findAllViews();

        setListeners();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (getStaticVariable()) {
            asyncGetJob();
        }
    }

    // BASIC FUNCTIONS =============================================================================
    private Boolean getStaticVariable() {
        if (INT_JOB_ID != 0) {
            Helpers.logThis(TAG_LOG, "JOB ID: " + INT_JOB_ID);
            return true;
        } else {
            return false;
        }
    }

    private void findAllViews() {
        app_bar_layout = findViewById(R.id.app_bar_layout);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        ll_main_view = findViewById(R.id.ll_main_view);

        btn_apply = findViewById(R.id.btn_apply);

        img_banner = findViewById(R.id.img_banner);
        txt_establishment_name = findViewById(R.id.txt_establishment_name);

        txt_job_name = findViewById(R.id.txt_job_name);
        txt_job_description = findViewById(R.id.txt_job_description);
        txt_job_requirements = findViewById(R.id.txt_job_requirements);
        txt_job_location = findViewById(R.id.txt_job_location);
        txt_job_post_date = findViewById(R.id.txt_job_post_date);
        txt_job_end_date = findViewById(R.id.txt_job_end_date);

    }

    private void hideAllViews() {
        ll_main_view.setVisibility(View.GONE);
    }

    private void setUpToolBarAndTabs() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_text = toolbar.findViewById(R.id.toolbar_text);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(JobActivity.this, R.drawable.ic_back));
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
                    toolbar.setBackground(ContextCompat.getDrawable(JobActivity.this, R.drawable.back_toolbar));
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

        img_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpers.openImageViewer(JobActivity.this, STR_BANNER_URL);
            }
        });

        helpers.animateSwipeRefresh(swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                asyncGetJob();
            }
        });

    }

    public void applyJob(View view) {
        if (!db.isFilteredJob(INT_JOB_ID, FILTER_TYPE_APPLIED)) {
            if (helpers.validateJobApplication()) {
                asyncApplyJob();
            }
        }
    }

    public void viewEstablishment(View view) {
        if (INT_ESTABLISHMENT_ID != 0)
            startActivity(new Intent(JobActivity.this, EstablishmentActivity.class)
                    .putExtra("ESTABLISHMENT_ID", INT_ESTABLISHMENT_ID)
            );
    }

    private void checkJobApplied() {
        if (db.isFilteredJob(INT_JOB_ID, FILTER_TYPE_APPLIED)) {
            btn_apply.setText(getString(R.string.txt_applied));
            btn_apply.setTextAppearance(JobActivity.this, R.style.Material_Text);
            btn_apply.setBackground(null);
            btn_apply.setClickable(false);
        }
    }


    // GET ESTABLISHMENT ASYNC FUNCTION ============================================================
    private void asyncGetJob() {
        hideAllViews();
        swipe_refresh.setRefreshing(true);
        EstablishmentInterface establishmentInterface = EstablishmentInterface.retrofit.create(EstablishmentInterface.class);
        Call<JsonObject> call = establishmentInterface.getJob(INT_JOB_ID);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    swipe_refresh.setRefreshing(false);
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {

                        JSONObject job_object = main.getJSONObject("data");

                        globalJobObject = job_object;

                        STR_PAGE_TITLE = job_object.getString("title");
                        txt_job_name.setText(STR_PAGE_TITLE);
                        helpers.animateFadeIn(txt_job_name);

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

                        txt_job_post_date.setText(getString(R.string.txt_posted_on).concat(job_object.getString("posted_on")));
                        txt_job_end_date.setText(getString(R.string.txt_posted_till).concat(job_object.getString("end_date")));
                        STR_SHARE_LINK = "Please take a look at this Job Vacancy on HotelAide ".concat(job_object.getString("url"));

                        // ESTABLISHMENT OBJECT
                        JSONObject establishment_object = job_object.getJSONObject("establishment");
                        if (establishment_object != null) {
                            INT_ESTABLISHMENT_ID = establishment_object.getInt("id");
                            txt_establishment_name.setText(establishment_object.getString("name"));
                            STR_BANNER_URL = establishment_object.getString("image");
                            Glide.with(JobActivity.this).load(STR_BANNER_URL).into(img_banner);
                            txt_job_location.setText(establishment_object.getString("full_address"));
                        }

                        checkJobApplied();

                        ll_main_view.setVisibility(View.VISIBLE);
                        helpers.animateFadeIn(ll_main_view);

                    } else {
                        db.deleteFilteredJobByJobId(INT_JOB_ID, FILTER_TYPE_APPLIED);
                        helpers.handleErrorMessage(JobActivity.this, main.getJSONObject("data"));
                    }

                } catch (JSONException e) {
                    helpers.toastMessage(getString(R.string.error_server));
                    e.printStackTrace();
                    onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                try {
                    Helpers.logThis(TAG_LOG, t.toString());
                    hideAllViews();
                    if (helpers.validateInternetConnection()) {
                        helpers.toastMessage(getString(R.string.error_server));
                        onBackPressed();
                    } else {
                        helpers.toastMessage(getString(R.string.error_connection));
                        onBackPressed();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void asyncApplyJob() {

        EstablishmentInterface establishmentInterface = EstablishmentInterface.retrofit.create(EstablishmentInterface.class);
        Call<JsonObject> call = establishmentInterface.applyForJob(SharedPrefs.getInt(USER_ID), INT_JOB_ID);
        helpers.setProgressDialog("Applying for this position");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.dismissProgressDialog();
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.logThis(TAG_LOG, main.toString());

                    db.setJobFromJson(globalJobObject, FILTER_TYPE_APPLIED);

                    db.deleteFilteredJobByJobId(INT_JOB_ID, FILTER_TYPE_SAVED);

                    helpers.toastMessage(main.getString("message"));

                    checkJobApplied();

                } catch (JSONException e) {
                    helpers.toastMessage(getString(R.string.error_server));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.dismissProgressDialog();
                Helpers.logThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.toastMessage(getString(R.string.error_server));
                } else {
                    helpers.toastMessage(getString(R.string.error_connection));
                }

            }
        });

    }

}
