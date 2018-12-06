package com.hotelaide.main.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.EstablishmentInterface;
import com.hotelaide.main.adapters.GalleryAdapter;
import com.hotelaide.main.models.GalleryModel;
import com.hotelaide.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.EXTRA_JOB_ID;
import static com.hotelaide.utils.StaticVariables.STR_SHARE_LINK;

public class EstablishmentActivity extends AppCompatActivity {
    private Helpers helpers;
    private Toolbar toolbar;
    private TextView
            toolbar_text,
            txt_establishment_name,
            txt_establishment_description,
            txt_establishment_location,
            txt_establishment_type;
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

    private SwipeRefreshLayout
            swipe_refresh;

    private LinearLayout
            ll_main_view,
            ll_vacancies,
            ll_vacancies_child,
            ll_gallery;


    private RecyclerView gallery_recycler;
    private LinearLayoutManager gallery_layout_manager;
    private ArrayList<GalleryModel> gallery_list = new ArrayList<>();
    private GalleryAdapter gallery_adapter;

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

            asyncGetEstablishment();

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
            Helpers.logThis(TAG_LOG, "ESTABLISHMENT ID: " + INT_ESTABLISHMENT_ID);
            return true;
        } else {
            return false;
        }
    }

    private void findAllViews() {
        app_bar_layout = findViewById(R.id.app_bar_layout);
        ll_main_view = findViewById(R.id.ll_main_view);
        swipe_refresh = findViewById(R.id.swipe_refresh);

        img_banner = findViewById(R.id.img_banner);
        txt_establishment_name = findViewById(R.id.txt_establishment_name);
        txt_establishment_location = findViewById(R.id.txt_establishment_location);
        txt_establishment_type = findViewById(R.id.txt_establishment_type);
        txt_establishment_description = findViewById(R.id.txt_establishment_description);


        // GALLERY ITEMS
        ll_gallery = findViewById(R.id.ll_gallery);
        gallery_recycler = findViewById(R.id.recycler_gallery);
        gallery_adapter = new GalleryAdapter(gallery_list);
        gallery_recycler.setAdapter(gallery_adapter);
        gallery_recycler.setHasFixedSize(true);
        gallery_layout_manager = new LinearLayoutManager(EstablishmentActivity.this);
        gallery_layout_manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        gallery_recycler.setLayoutManager(gallery_layout_manager);


        // JOB VACANCIES
        ll_vacancies = findViewById(R.id.ll_vacancies);
        ll_vacancies_child = findViewById(R.id.ll_vacancies_child);

        hideAllViews();

    }

    private void hideAllViews() {
        ll_main_view.setVisibility(View.GONE);
    }

    private void setUpToolBarAndTabs() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_text = toolbar.findViewById(R.id.toolbar_text);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(EstablishmentActivity.this, R.drawable.ic_back));
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
                    toolbar.setBackground(ContextCompat.getDrawable(EstablishmentActivity.this, R.drawable.back_toolbar));
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

        helpers.animateSwipeRefresh(swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                asyncGetEstablishment();
            }
        });

    }

    // GET ESTABLISHMENT ASYNC FUNCTION ============================================================
    private void asyncGetEstablishment() {
        swipe_refresh.setRefreshing(true);
        EstablishmentInterface service = EstablishmentInterface.retrofit.create(EstablishmentInterface.class);
        Call<JsonObject> call = service.getEstablishment(INT_ESTABLISHMENT_ID);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    swipe_refresh.setRefreshing(false);
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {

                        JSONObject establishment_object = main.getJSONObject("data");
                        STR_PAGE_TITLE = establishment_object.getString("establishment_name");
                        txt_establishment_name.setText(STR_PAGE_TITLE);
                        helpers.animateFadeIn(txt_establishment_name);
                        txt_establishment_location.setText(establishment_object.getString("full_address"));

                        txt_establishment_description.setText(establishment_object.getString("establishment_description"));
                        STR_SHARE_LINK = "Please have a look at this establishment on HotelAide ".concat(establishment_object.getString("establishment_url"));
                        STR_BANNER_URL = establishment_object.getString("banner");
                        Glide.with(EstablishmentActivity.this).load(STR_BANNER_URL).into(img_banner);

                        JSONObject establishment_type = establishment_object.getJSONObject("establishment_type");
                        txt_establishment_type.setText(establishment_type.getString("name"));


                        // GALLERY
                        gallery_list.clear();
                        JSONArray galleryImageArrays = establishment_object.getJSONArray("gallery");
                        if (!galleryImageArrays.isNull(0)) {
                            ll_gallery.setVisibility(View.VISIBLE);
                            for (int v = 0; v < galleryImageArrays.length(); v++) {
                                GalleryModel galleryModel = new GalleryModel();
                                galleryModel.id = v + 1;
                                galleryModel.image = galleryImageArrays.getString(v);
                                gallery_list.add(galleryModel);
                            }
                            gallery_adapter.notifyDataSetChanged();
                        } else {
                            ll_gallery.setVisibility(View.GONE);
                        }

                        // JOB VACANCIES
                        JSONArray job_vacancies = establishment_object.getJSONArray("job_vacancies");
                        if (job_vacancies != null && job_vacancies.length() > 0) {
                            ll_vacancies.setVisibility(View.VISIBLE);
                            ll_vacancies_child.removeAllViews();
                            LayoutInflater layout_inflater = LayoutInflater.from(EstablishmentActivity.this);
                            int array_length = job_vacancies.length();
                            for (int i = 0; i < array_length; i++) {
                                JSONObject vacancy_object = job_vacancies.getJSONObject(i);
                                final int id = vacancy_object.getInt("id");
                                final String title = vacancy_object.getString("title");
                                final String desc = vacancy_object.getString("description");
                                final String posted_on = vacancy_object.getString("posted_on");
                                createVacancyListing(layout_inflater, id, title, desc, posted_on);
                            }
                        } else {
                            ll_vacancies.setVisibility(View.GONE);
                        }

                        ll_main_view.setVisibility(View.VISIBLE);
                        helpers.animateFadeIn(ll_main_view);

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
                try {
                    swipe_refresh.setRefreshing(false);
                    Helpers.logThis(TAG_LOG, t.toString());
                    if (helpers.validateInternetConnection()) {
                        helpers.ToastMessage(EstablishmentActivity.this, getString(R.string.error_server));
                        onBackPressed();
                    } else {
                        helpers.ToastMessage(EstablishmentActivity.this, getString(R.string.error_connection));
                        onBackPressed();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void createVacancyListing(
            LayoutInflater layout_inflater,
            final int id,
            String title,
            String desc,
            String posted_on) {

        @SuppressLint("InflateParams") final View v = layout_inflater.inflate(R.layout.list_item_notification, null);
        final TextView txt_title = v.findViewById(R.id.txt_title);
        final TextView txt_desc = v.findViewById(R.id.txt_message);
        final TextView txt_posted_on = v.findViewById(R.id.txt_date);
        final RelativeLayout rl_no_list_items = v.findViewById(R.id.rl_no_list_items);
        rl_no_list_items.setVisibility(View.GONE);

        txt_title.setText(title);
        txt_posted_on.setText(posted_on);
        txt_desc.setText(desc);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(EstablishmentActivity.this, JobActivity.class)
                        .putExtra(EXTRA_JOB_ID, id)
                );
            }
        });

        ll_vacancies_child.addView(v);

    }


}
