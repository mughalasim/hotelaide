package com.hotelaide.main.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.main.adapters.GalleryAdapter;
import com.hotelaide.main.models.GalleryModel;
import com.hotelaide.services.EstablishmentService;
import com.hotelaide.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.STR_SHARE_LINK;

public class EstablishmentActivity extends AppCompatActivity {
    private Helpers helpers;
    private Toolbar toolbar;
    private TextView
            toolbar_text,
            txt_establishment_name,
            txt_establishment_description,
            txt_establishment_location,
            txt_establishment_type,
            txt_establishment_email;
    private ImageView
            img_banner;
    private AppBarLayout app_bar_layout;
    public static String
            STR_PAGE_TITLE = "";
    private String
            STR_BANNER_URL = "";
    private int INT_ESTABLISHMENT_ID = 0;
    private final String
            TAG_LOG = "ESTABLISHMENT";

    private final ArrayList<GalleryModel>
            gallery_list = new ArrayList<>();
    private GalleryAdapter gallery_adapter;
    private final LinearLayoutManager
            gallery_layout_manager = new LinearLayoutManager(this);
    private LinearLayout
            ll_gallery;

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helpers = new Helpers(EstablishmentActivity.this);

        if (handleExtraBundles()) {
            setContentView(R.layout.activity_establishment);

            setUpToolBarAndTabs();

            findAllViews();

            setListeners();

            asyncFetchHotel();

        } else {
            helpers.ToastMessage(EstablishmentActivity.this, getString(R.string.error_unknown));
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
                helpers.dialogShare(EstablishmentActivity.this, STR_SHARE_LINK);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // BASIC FUNCTIONS =============================================================================
    private Boolean handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getInt("ESTABLISHMENT_ID") != 0) {
            INT_ESTABLISHMENT_ID = extras.getInt("ESTABLISHMENT_ID");
            Helpers.LogThis(TAG_LOG, "ESTABLISHMENT ID: " + INT_ESTABLISHMENT_ID);
            return true;
        } else {
            return false;
        }
    }

    private void findAllViews() {
        app_bar_layout = findViewById(R.id.app_bar_layout);

        img_banner = findViewById(R.id.img_banner);
        txt_establishment_name = findViewById(R.id.txt_establishment_name);
        txt_establishment_location = findViewById(R.id.txt_establishment_location);
        txt_establishment_type = findViewById(R.id.txt_establishment_type);
        txt_establishment_email = findViewById(R.id.txt_establishment_email);
        txt_establishment_description = findViewById(R.id.txt_establishment_description);


        // GALLERY ITEMS
        ll_gallery = findViewById(R.id.ll_gallery);
        RecyclerView gallery_recycler = findViewById(R.id.gallery_recycler_small);
        gallery_recycler.setHasFixedSize(true);
        gallery_layout_manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        gallery_recycler.setLayoutManager(gallery_layout_manager);
        gallery_adapter = new GalleryAdapter(gallery_list);
        gallery_recycler.setAdapter(gallery_adapter);

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

        img_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpers.openImageViewer(EstablishmentActivity.this, STR_BANNER_URL);
            }
        });

    }

    // GET ESTABLISHMENT ASYNC FUNCTION ============================================================
    private void asyncFetchHotel() {

        EstablishmentService userService = EstablishmentService.retrofit.create(EstablishmentService.class);
        Call<JsonObject> call = userService.getEstablishment(INT_ESTABLISHMENT_ID);
        helpers.setProgressDialogMessage("Loading Hotel details");
        helpers.progressDialog(true);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
//{"data":
// {
// "id":3,
// "employer_id":10,
// "establishment_url":null,
// "establishment_name":"Test Establishment",
// "establishment_email":null,
// "establishment_description":null,
// "establishment_type":{"id":1,"name":"Hotel"},
// "job_vacancies":[{"id":2,"title":"Cashier","posted":"25-09-2018","location":null,"description":"Short description","requirements":"Short requirements.","end_date":"17-06-2009","url":"https:\/\/hotelaide.com\/jobs\/cashier-test-establishment","establishment":{"id":3,"name":"Test Establishment","image":""}}],
// "gallery":[]},"success":true}

                        JSONObject object = main.getJSONObject("data");
                        STR_PAGE_TITLE = object.getString("establishment_name");
                        txt_establishment_name.setText(STR_PAGE_TITLE);
//                        txt_establishment_location.setText(object.getString("location"));
                        JSONObject establishment_type_object = object.getJSONObject("establishment_type");
                        txt_establishment_type.setText(establishment_type_object.getString("name"));
                        txt_establishment_description.setText(object.getString("establishment_description"));
                        txt_establishment_email.setText(object.getString("establishment_email"));
                        STR_SHARE_LINK = "Please have a look at this establishment on HotelAide ".concat(object.getString("establishment_url"));
                        STR_BANNER_URL = object.getString("banner");
                        Glide.with(EstablishmentActivity.this).load(STR_BANNER_URL).into(img_banner);

                        // JOB VACANCIES
                        JSONArray job_vacancies = object.getJSONArray("job_vacancies");
                        if (job_vacancies != null && job_vacancies.length() > 0) {
                            int array_length = job_vacancies.length();
                            for (int i = 0; i < array_length; i++) {
                                JSONObject vacancy_object = job_vacancies.getJSONObject(i);

                            }
                        }

                        // GALLERY
                        gallery_list.clear();
                        JSONArray galleryImageArrays = object.getJSONArray("gallery");
                        if (!galleryImageArrays.isNull(0)) {
                            ll_gallery.setVisibility(View.VISIBLE);
                            for (int v = 0; v < galleryImageArrays.length(); v++) {
                                String gallery_url = galleryImageArrays.getString(v);
                                Helpers.LogThis(TAG_LOG, "GALLERY URL: " + gallery_url);
                                GalleryModel galleryModel = new GalleryModel();
                                galleryModel.image = galleryImageArrays.getString(v);
                            }
                            gallery_adapter.notifyDataSetChanged();
                        } else {
                            ll_gallery.setVisibility(View.GONE);
                        }

                    } else {
                        helpers.handleErrorMessage(EstablishmentActivity.this, main.getJSONObject("data"));
                    }

                } catch (JSONException e) {
                    helpers.ToastMessage(EstablishmentActivity.this, getString(R.string.error_server));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.progressDialog(false);
                Helpers.LogThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(EstablishmentActivity.this, getString(R.string.error_server));
                    onBackPressed();
                } else {
                    helpers.ToastMessage(EstablishmentActivity.this, getString(R.string.error_connection));
                    onBackPressed();
                }

            }
        });

    }


}
