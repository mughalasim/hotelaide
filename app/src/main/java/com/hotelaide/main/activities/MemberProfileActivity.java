package com.hotelaide.main.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.STR_SHARE_LINK;

public class MemberProfileActivity extends AppCompatActivity {
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
    private int INT_MEMBER_ID = 0;
    private final String
            TAG_LOG = "MEMBER PROFILE";

    private LinearLayout ll_gallery;
    private RecyclerView gallery_recycler;
    private LinearLayoutManager gallery_layout_manager;
    private ArrayList<GalleryModel> gallery_list = new ArrayList<>();
    private GalleryAdapter gallery_adapter;

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helpers = new Helpers(MemberProfileActivity.this);

        if (handleExtraBundles()) {
            setContentView(R.layout.activity_member_profile);

            setUpToolBarAndTabs();

            findAllViews();

            setListeners();

            asyncFetchHotel();

        } else {
            helpers.ToastMessage(MemberProfileActivity.this, getString(R.string.error_unknown));
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
                helpers.dialogShare(MemberProfileActivity.this, STR_SHARE_LINK);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // BASIC FUNCTIONS =============================================================================
    private Boolean handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getInt("ESTABLISHMENT_ID") != 0) {
            INT_MEMBER_ID = extras.getInt("MEMBER_ID");
            Helpers.logThis(TAG_LOG, "MEMBER ID: " + INT_MEMBER_ID);
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
        txt_establishment_description = findViewById(R.id.txt_establishment_description);


        // GALLERY ITEMS
        ll_gallery = findViewById(R.id.ll_gallery);
        gallery_recycler = findViewById(R.id.gallery_recycler_small);
        gallery_adapter = new GalleryAdapter(gallery_list);
        gallery_recycler.setAdapter(gallery_adapter);
        gallery_recycler.setHasFixedSize(true);
        gallery_layout_manager = new LinearLayoutManager(MemberProfileActivity.this);
        gallery_layout_manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        gallery_recycler.setLayoutManager(gallery_layout_manager);

    }

    private void setUpToolBarAndTabs() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_text = toolbar.findViewById(R.id.toolbar_text);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(MemberProfileActivity.this, R.drawable.ic_back));
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
                    toolbar.setBackground(ContextCompat.getDrawable(MemberProfileActivity.this, R.drawable.bckgrd_toolbar));
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
                helpers.openImageViewer(MemberProfileActivity.this, STR_BANNER_URL);
            }
        });

    }

    // GET ESTABLISHMENT ASYNC FUNCTION ============================================================
    private void asyncFetchHotel() {

        EstablishmentInterface service = EstablishmentInterface.retrofit.create(EstablishmentInterface.class);
        Call<JsonObject> call = service.getEstablishment(INT_MEMBER_ID);
        helpers.setProgressDialog("Loading Establishment details");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.dismissProgressDialog();
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.logThis(TAG_LOG, main.toString());

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
                        STR_SHARE_LINK = "Please have a look at this establishment on HotelAide ".concat(object.getString("establishment_url"));
                        STR_BANNER_URL = object.getString("banner");
                        Glide.with(MemberProfileActivity.this).load(STR_BANNER_URL).into(img_banner);

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
                                GalleryModel galleryModel = new GalleryModel();
                                galleryModel.id = v+1;
                                galleryModel.image = galleryImageArrays.getString(v);
                                gallery_list.add(galleryModel);
                            }
                            gallery_adapter.notifyDataSetChanged();
                        } else {
                            ll_gallery.setVisibility(View.GONE);
                        }

                    } else {
                        helpers.handleErrorMessage(MemberProfileActivity.this, main.getJSONObject("data"));
                    }

                } catch (JSONException e) {
                    helpers.ToastMessage(MemberProfileActivity.this, getString(R.string.error_server));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.dismissProgressDialog();
                Helpers.logThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(MemberProfileActivity.this, getString(R.string.error_server));
                    onBackPressed();
                } else {
                    helpers.ToastMessage(MemberProfileActivity.this, getString(R.string.error_connection));
                    onBackPressed();
                }

            }
        });

    }


}
