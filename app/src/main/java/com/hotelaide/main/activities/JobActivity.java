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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            txt_establishment_name,
            txt_establishment_id;

    private ImageView
            img_banner;

    private MaterialButton
            btn_apply;

    private AppBarLayout app_bar_layout;

    private String
            STR_PAGE_TITLE = "",
            STR_BANNER_URL = "";

    private int INT_JOB_ID = 0;

    private final String
            TAG_LOG = "JOB VACANCY";
    private Database db;

    private JSONObject globalJobObject;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (handleExtraBundles()) {
            setContentView(R.layout.activity_job);

            helpers = new Helpers(JobActivity.this);

            db = new Database();

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
            Helpers.logThis(TAG_LOG, "JOB ID: " + INT_JOB_ID);
            return true;
        } else {
            return false;
        }
    }

    private void findAllViews() {
        app_bar_layout = findViewById(R.id.app_bar_layout);

        btn_apply = findViewById(R.id.btn_apply);

        img_banner = findViewById(R.id.img_banner);
        txt_establishment_name = findViewById(R.id.txt_establishment_name);
        txt_establishment_id = findViewById(R.id.txt_establishment_id);

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
                    toolbar.setBackground(ContextCompat.getDrawable(JobActivity.this, R.drawable.bckgrd_toolbar));
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

    }

    public void applyJob(View view) {
        if (!db.isAppliedJob(INT_JOB_ID)) {
            if (helpers.validateJobApplication(JobActivity.this)) {
                asyncApplyJob();
            }
        }
    }

    public void viewEstablishment(View view) {
        startActivity(new Intent(JobActivity.this, EstablishmentActivity.class)
                .putExtra("ESTABLISHMENT_ID", Integer.parseInt(txt_establishment_id.getText().toString()))
        );
    }

    private void checkJobApplied() {
        if (db.isAppliedJob(INT_JOB_ID)) {
            btn_apply.setText("APPLIED");
            btn_apply.setTextAppearance(JobActivity.this, R.style.Material_Text);
            btn_apply.setBackground(null);
            btn_apply.setClickable(false);
        }
    }


    // GET ESTABLISHMENT ASYNC FUNCTION ============================================================
    private void asyncFetchHotel() {

        EstablishmentInterface establishmentInterface = EstablishmentInterface.retrofit.create(EstablishmentInterface.class);
        Call<JsonObject> call = establishmentInterface.getJob(INT_JOB_ID);
        helpers.setProgressDialog("Loading Job details");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.dismissProgressDialog();
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {

                        JSONObject job_object = main.getJSONObject("data");

                        globalJobObject = job_object;

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

                        txt_job_post_date.setText(getString(R.string.txt_posted_on).concat(job_object.getString("posted_on")));
                        txt_job_end_date.setText(getString(R.string.txt_posted_till).concat(job_object.getString("end_date")));
                        STR_SHARE_LINK = "Please take a look at this Job Vacancy on HotelAide ".concat(job_object.getString("url"));

                        // ESTABLISHMENT OBJECT
                        JSONObject establishment_object = job_object.getJSONObject("establishment");
                        if (establishment_object != null) {
                            txt_establishment_id.setText(establishment_object.getString("id"));
                            txt_establishment_name.setText(establishment_object.getString("name"));
                            STR_BANNER_URL = establishment_object.getString("image");
                            Glide.with(JobActivity.this).load(STR_BANNER_URL).into(img_banner);
                            txt_job_location.setText(establishment_object.getString("full_address"));
                        }

                        checkJobApplied();

                    } else {
                        db.deleteAppliedJobByJobId(INT_JOB_ID);
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
                helpers.dismissProgressDialog();
                Helpers.logThis(TAG_LOG, t.toString());
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

                    db.setJobFromJson(globalJobObject, true);

                    helpers.ToastMessage(JobActivity.this, main.getString("message"));

                    checkJobApplied();

                } catch (JSONException e) {
                    helpers.ToastMessage(JobActivity.this, getString(R.string.error_server));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.dismissProgressDialog();
                Helpers.logThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(JobActivity.this, getString(R.string.error_server));
                } else {
                    helpers.ToastMessage(JobActivity.this, getString(R.string.error_connection));
                }

            }
        });

    }

}
