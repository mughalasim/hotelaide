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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.UserInterface;
import com.hotelaide.main.fragments.ExperienceViewFragment;
import com.hotelaide.main.models.ExperienceModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.EDUCATION_LEVEL_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.StaticVariables.STR_SHARE_LINK;
import static com.hotelaide.utils.StaticVariables.USER_ID;
import static com.hotelaide.utils.StaticVariables.db;

public class MemberProfileActivity extends AppCompatActivity {
    private Helpers helpers;

    private Toolbar toolbar;
    private final String
            TAG_LOG = "MEMBER PROFILE";

    // BANNER ----------------------------------------
    private ImageView
            img_banner,
            img_avatar;
    private AppBarLayout
            app_bar_layout;


    // INFO AND CONTACT ------------------------------
    private TextView
            txt_title_education,
            txt_title_work,
            txt_title_documents,
            txt_user_f_name,
            txt_user_l_name,
            txt_user_about,
            txt_user_gender,
            txt_user_age,
            txt_user_dob,
            toolbar_text;
    private CardView
            card_view_about;

    private LinearLayout
            ll_education,
            ll_work,
            ll_documents;

    public static String
            STR_PAGE_TITLE = "";
    private String
            STR_NAME = "",
            STR_AVATAR_URL = "",
            STR_BANNER_URL = "";
    private int INT_MEMBER_ID = 0;

    // BACKGROUND ------------------------------------
    private SwipeRefreshLayout swipe_refresh;

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

            asyncFetchMember();

            HelpersAsync.setTrackerPage(TAG_LOG);

        } else {
            helpers.toastMessage(getString(R.string.error_unknown));
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
        if (extras != null && extras.getInt("MEMBER_ID") != 0) {
            INT_MEMBER_ID = extras.getInt("MEMBER_ID");
            Helpers.logThis(TAG_LOG, "MEMBER ID: " + INT_MEMBER_ID);
            return true;
        } else {
            return false;
        }
    }

    private void findAllViews() {
        app_bar_layout = findViewById(R.id.app_bar_layout);
        swipe_refresh = findViewById(R.id.swipe_refresh);

        // BANNER
        img_avatar = findViewById(R.id.img_avatar);
        img_banner = findViewById(R.id.img_banner);

        // INFO AND CONTACT DETAILS
        card_view_about = findViewById(R.id.card_view_about);
        txt_user_f_name = findViewById(R.id.txt_user_f_name);
        txt_user_l_name = findViewById(R.id.txt_user_l_name);
        txt_user_about = findViewById(R.id.txt_user_about);
        txt_user_gender = findViewById(R.id.txt_user_gender);
        txt_user_age = findViewById(R.id.txt_user_age);
        txt_user_dob = findViewById(R.id.txt_user_dob);

        // EDUCATION
        txt_title_education = findViewById(R.id.txt_title_education);
        ll_education = findViewById(R.id.ll_education);

        // WORK
        txt_title_work = findViewById(R.id.txt_title_work);
        ll_work = findViewById(R.id.ll_work);

        // DOCUMENTS
        txt_title_documents = findViewById(R.id.txt_title_documents);
        ll_documents = findViewById(R.id.ll_documents);

    }

    private void hideAllViews() {
        card_view_about.setVisibility(View.GONE);

        txt_user_about.setVisibility(View.GONE);

        txt_title_education.setVisibility(View.GONE);
        ll_education.setVisibility(View.GONE);

        txt_title_work.setVisibility(View.GONE);
//        ll_work.setVisibility(View.GONE);

        txt_title_documents.setVisibility(View.GONE);
        ll_documents.setVisibility(View.GONE);

    }

    private void setUpToolBarAndTabs() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_text = findViewById(R.id.toolbar_text);
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
                    toolbar.setBackground(ContextCompat.getDrawable(MemberProfileActivity.this, R.drawable.back_toolbar));
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

        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpers.openImageViewer(MemberProfileActivity.this, STR_AVATAR_URL);
            }
        });

        helpers.animateSwipeRefresh(swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                asyncFetchMember();
            }
        });

    }

    public void startConversation(View view) {
        if (INT_MEMBER_ID == 0) {
            helpers.toastMessage(getString(R.string.error_server));
        } else if (INT_MEMBER_ID == SharedPrefs.getInt(USER_ID)) {
            helpers.toastMessage("LOL! You cant talk to yourself!");
        } else if (!helpers.validateInternetConnection()) {
            helpers.toastMessage(getString(R.string.error_connection));
        } else {
            startActivity(new Intent(MemberProfileActivity.this, ConversationActivity.class)
                    .putExtra("FROM_NAME", STR_NAME)
                    .putExtra("FROM_ID", INT_MEMBER_ID)
                    .putExtra("FROM_PIC_URL", STR_AVATAR_URL)
            );
        }
    }

    public void share(View view) {
        helpers.dialogShare(MemberProfileActivity.this, STR_SHARE_LINK);
    }

    // GET MEMBER ASYNC FUNCTION ===================================================================
    private void asyncFetchMember() {

        hideAllViews();

        swipe_refresh.setRefreshing(true);

        UserInterface.retrofit.create(UserInterface.class)
                .getMemberByID(INT_MEMBER_ID).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                swipe_refresh.setRefreshing(false);
                helpers.dismissProgressDialog();
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        JSONObject user = main.getJSONObject("data");

                        STR_PAGE_TITLE = user.getString("first_name");
                        STR_SHARE_LINK = "Please have a look at this member on HotelAide ".concat(user.getString("profile_url"));

                        STR_AVATAR_URL = user.getString("avatar");
                        STR_BANNER_URL = user.getString("banner");

                        Glide.with(MemberProfileActivity.this).load(STR_AVATAR_URL)
                                .placeholder(R.drawable.ic_profile).into(img_avatar);
                        Glide.with(MemberProfileActivity.this).load(STR_BANNER_URL).into(img_banner);

                        String f_name = user.getString("first_name");
                        String l_name = user.getString("last_name");

                        STR_NAME = f_name.concat(" ").concat(l_name);

                        txt_user_f_name.setText(f_name);
                        txt_user_l_name.setText(l_name);

                        String about_me = user.getString("about_me");
                        if (about_me.equals("")) {
                            txt_user_about.setVisibility(View.GONE);
                        } else {
                            txt_user_about.setText(about_me);
                            txt_user_about.setVisibility(View.VISIBLE);
                            helpers.animateFadeIn(txt_user_about);
                        }

                        txt_user_dob.setText(user.getString("dob"));

                        txt_user_age.setText(helpers.calculateAge(user.getString("dob")));

                        // GENDER
                        if (user.getInt("gender") == 0) {
                            txt_user_gender.setText("Not set");
                        } else if (user.getInt("gender") == 1) {
                            txt_user_gender.setText("Male");
                        } else {
                            txt_user_gender.setText("Female");
                        }

                        card_view_about.setVisibility(View.VISIBLE);
                        helpers.animateFadeIn(card_view_about);

                        if (!user.isNull("work_experience")) {
                            JSONArray work_experience = user.getJSONArray("work_experience");
                            Fragment myFrag = new ExperienceViewFragment(work_experience, EXPERIENCE_TYPE_WORK);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.ll_work, myFrag)
                                    .commit();
//                            populateExperience(work_experience, EXPERIENCE_TYPE_WORK);
                        }

                        if (!user.isNull("education_experience")) {
                            JSONArray education_experience = user.getJSONArray("education_experience");
                            populateExperience(education_experience, EXPERIENCE_TYPE_EDUCATION);
                        }

                        if (!user.isNull("documents")) {
                            JSONArray documents = user.getJSONArray("documents");
                            if (documents != null && documents.length() > 0) {
                                int array_length = documents.length();
                                for (int i = 0; i < array_length; i++) {
                                    // TODO - SHOW DOCUMENTS
                                }
//                                txt_title_documents.setVisibility(View.VISIBLE);
//                                ll_documents.setVisibility(View.VISIBLE);
                            }
                        }

                    } else {
                        helpers.handleErrorMessage(MemberProfileActivity.this, main.getJSONObject("data"));
                    }

                } catch (JSONException e) {
                    helpers.toastMessage(getString(R.string.error_server));
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                try {
                    helpers.dismissProgressDialog();
                    swipe_refresh.setRefreshing(false);
                    Helpers.logThis(TAG_LOG, t.toString());
                    hideAllViews();
                    if (helpers.validateInternetConnection()) {
                        helpers.toastMessage(getString(R.string.error_server));
                    } else {
                        helpers.toastMessage(getString(R.string.error_connection));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void populateExperience(JSONArray work_experience, final String type) throws JSONException {

        LayoutInflater layout_inflater = LayoutInflater.from(MemberProfileActivity.this);

        if (work_experience != null && work_experience.length() > 0) {
            int array_length = work_experience.length();

            if (type.equals(EXPERIENCE_TYPE_WORK)) {
                ll_work.removeAllViews();
            } else {
                ll_education.removeAllViews();
            }

            for (int i = 0; i < array_length; i++) {

                JSONObject work_object = work_experience.getJSONObject(i);

                @SuppressLint("InflateParams")
                View v = layout_inflater.inflate(R.layout.list_item_experience, null);

                final TextView txt_name = v.findViewById(R.id.txt_name);
                final TextView txt_position = v.findViewById(R.id.txt_position);
                final TextView txt_start_date = v.findViewById(R.id.txt_start_date);
                final TextView txt_end_date = v.findViewById(R.id.txt_end_date);
                final TextView txt_current = v.findViewById(R.id.txt_current);
                final TextView txt_duration = v.findViewById(R.id.txt_duration);
                final TextView txt_responsibilities_field_label = v.findViewById(R.id.txt_responsibilities_field_label);
                final TextView txt_responsibilities_field = v.findViewById(R.id.txt_responsibilities_field);
                final RelativeLayout rl_no_list_items = v.findViewById(R.id.rl_no_list_items);
                rl_no_list_items.setVisibility(View.GONE);

                ExperienceModel experienceModel = new ExperienceModel();
                if (type.equals(EXPERIENCE_TYPE_WORK)) {
                    experienceModel.experience_id = work_object.getInt("id");
                    experienceModel.name = work_object.getString("company_name");
                    experienceModel.position = work_object.getString("position");
                    experienceModel.start_date = work_object.getString("start_date");
                    experienceModel.end_date = work_object.getString("end_date");
                    experienceModel.responsibilities_field = work_object.getString("responsibilities");
                    experienceModel.current = work_object.getInt("current");
                    experienceModel.type = EXPERIENCE_TYPE_WORK;

                } else {
                    experienceModel.experience_id = work_object.getInt("id");
                    experienceModel.name = work_object.getString("institution_name");
                    experienceModel.education_level = work_object.getInt("education_level");
                    experienceModel.start_date = work_object.getString("start_date");
                    experienceModel.end_date = work_object.getString("end_date");
                    experienceModel.responsibilities_field = work_object.getString("study_field");
                    experienceModel.current = work_object.getInt("current");

                    experienceModel.type = EXPERIENCE_TYPE_EDUCATION;
                }

                txt_name.setText(experienceModel.name);
                txt_position.setText(experienceModel.position);
                txt_start_date.setText(helpers.formatDate(experienceModel.start_date));

                if (experienceModel.current == 0) {
                    txt_current.setVisibility(View.GONE);
                    txt_end_date.setVisibility(View.VISIBLE);
                    txt_end_date.setText(helpers.formatDate(experienceModel.end_date));
                    txt_duration.setText(helpers.calculateDateInterval(experienceModel.start_date, experienceModel.end_date));
                } else {
                    txt_current.setVisibility(View.VISIBLE);
                    txt_end_date.setVisibility(View.GONE);
                    txt_duration.setText(helpers.calculateAge(experienceModel.start_date));
                }

                if (txt_duration.getText().toString().length() < 1) {
                    txt_duration.setVisibility(View.GONE);
                } else {
                    txt_duration.setVisibility(View.VISIBLE);
                }

                if (experienceModel.type.equals(EXPERIENCE_TYPE_WORK)) {
                    txt_responsibilities_field_label.setText(R.string.txt_responsibilities);
                    txt_position.setText(experienceModel.position);
                } else {
                    txt_position.setText(db.getFilterNameByID(EDUCATION_LEVEL_TABLE_NAME, experienceModel.education_level));
                    txt_responsibilities_field_label.setText(R.string.txt_field_study);
                }

                txt_responsibilities_field.setText(experienceModel.responsibilities_field);

                txt_responsibilities_field.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (txt_responsibilities_field.getMaxLines() == 3) {
                            txt_responsibilities_field.setMaxLines(Integer.MAX_VALUE);
                        } else {
                            txt_responsibilities_field.setMaxLines(3);
                        }
                    }
                });

                if (type.equals(EXPERIENCE_TYPE_WORK)) {
                    ll_work.addView(v);
                } else {
                    ll_education.addView(v);
                }
            }

            if (type.equals(EXPERIENCE_TYPE_WORK)) {
                txt_title_work.setVisibility(View.VISIBLE);
                ll_work.setVisibility(View.VISIBLE);
                helpers.animateFadeIn(txt_title_work);
                helpers.animateFadeIn(ll_work);
            } else {
                txt_title_education.setVisibility(View.VISIBLE);
                ll_education.setVisibility(View.VISIBLE);
                helpers.animateFadeIn(txt_title_education);
                helpers.animateFadeIn(ll_education);
            }
        }
    }


}
