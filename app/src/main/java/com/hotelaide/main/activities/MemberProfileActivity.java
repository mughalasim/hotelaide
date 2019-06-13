package com.hotelaide.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.UserInterface;
import com.hotelaide.main.fragments.DocumentsFragment;
import com.hotelaide.main.fragments.ExperienceViewFragment;
import com.hotelaide.main.models.UserModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.StaticVariables.EXTRA_INT;
import static com.hotelaide.utils.StaticVariables.USER_ID;

public class MemberProfileActivity extends AppCompatActivity {
    private Helpers helpers;
    private Toolbar toolbar;
    private final String TAG_LOG = "PROFILE VIEW";

    private int INT_USER_ID = 0;

    private UserModel model = new UserModel();

    // BANNER AND PROFILE PIC -------------------------
    private ImageView
            img_banner,
            img_avatar;

    // INFO AND CONTACT ------------------------------
    private TextView
            txt_no_results,
            txt_user_f_name,
            txt_user_l_name,
            txt_user_about,
            txt_user_skills,
            txt_user_gender,
            txt_user_age,
            txt_user_dob;

    private AppCompatImageView
            btn_share;

    // LAYOUT ITEMS
    private TextView
            txt_documents_edit,
            txt_work_edit,
            txt_education_edit;
    private LinearLayout
            ll_fragment_education,
            ll_fragment_work,
            ll_fragment_documents;


    private ChipGroup chip_group_user_skills;

    // BACKGROUND ------------------------------------
    private SwipeRefreshLayout swipe_refresh;
    private RelativeLayout rl_no_list_items;
    private LinearLayout ll_profile;

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

    // BASIC FUNCTIONS =============================================================================
    private Boolean handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getInt(EXTRA_INT) != 0) {
            INT_USER_ID = extras.getInt(EXTRA_INT);
            Helpers.logThis(TAG_LOG, "USER ID: " + INT_USER_ID);
            return true;
        } else {
            return false;
        }
    }

    private void findAllViews() {
        swipe_refresh = findViewById(R.id.swipe_refresh);

        // BANNER
        img_avatar = findViewById(R.id.img_avatar);
        img_banner = findViewById(R.id.img_banner);

        // USER DETAILS
        btn_share = findViewById(R.id.btn_share);

        txt_user_f_name = findViewById(R.id.txt_user_f_name);
        txt_user_l_name = findViewById(R.id.txt_user_l_name);
        txt_user_about = findViewById(R.id.txt_user_about);
        txt_user_skills = findViewById(R.id.txt_user_skills);
        chip_group_user_skills = findViewById(R.id.chip_group_user_skills);
        txt_user_gender = findViewById(R.id.txt_user_gender);
        txt_user_age = findViewById(R.id.txt_user_age);
        txt_user_dob = findViewById(R.id.txt_user_dob);

        // LAYOUTS
        txt_documents_edit = findViewById(R.id.txt_documents_edit);
        txt_work_edit = findViewById(R.id.txt_work_edit);
        txt_education_edit = findViewById(R.id.txt_education_edit);
        ll_fragment_education = findViewById(R.id.ll_fragment_education);
        ll_fragment_work = findViewById(R.id.ll_fragment_work);
        ll_fragment_documents = findViewById(R.id.ll_fragment_documents);

        ll_profile = findViewById(R.id.ll_profile);
        rl_no_list_items = findViewById(R.id.rl_no_list_items);
        rl_no_list_items.setVisibility(View.GONE);
        txt_no_results = findViewById(R.id.txt_no_results);

    }

    private void setUpToolBarAndTabs() {
        toolbar = findViewById(R.id.toolbar);
        TextView toolbar_text = findViewById(R.id.toolbar_text);
        toolbar_text.setText("MEMBER VIEW");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(MemberProfileActivity.this, R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setListeners() {
        img_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpers.openImageViewer(MemberProfileActivity.this, model.img_banner);
            }
        });

        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpers.openImageViewer(MemberProfileActivity.this, model.img_avatar);
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpers.dialogShare(MemberProfileActivity.this, model.share_link);
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
        if (model.availability == 0) {
            helpers.myDialog(getString(R.string.txt_alert), "Members that are unavailable cannot be messaged");
        } else if (INT_USER_ID == 0) {
            helpers.toastMessage(getString(R.string.error_server));
        } else if (INT_USER_ID == SharedPrefs.getInt(USER_ID)) {
            helpers.toastMessage("LOL! You cant talk to yourself!");
        } else if (!helpers.validateInternetConnection()) {
            helpers.toastMessage(getString(R.string.error_connection));
        } else {
            startActivity(new Intent(MemberProfileActivity.this, MessagingActivity.class)
                    .putExtra("FROM_NAME", model.first_name.concat(" ").concat(model.last_name))
                    .putExtra("FROM_ID", INT_USER_ID)
                    .putExtra("FROM_PIC_URL", model.img_avatar)
            );
        }
    }

    private void setTextAndImages() {
        // BANNER IMAGES
        Glide.with(this)
                .load(model.img_avatar)
                .placeholder(R.drawable.ic_profile)
                .into(img_avatar);
        Glide.with(this)
                .load(model.img_banner)
                .into(img_banner);

        // INFO AND CONTACT DETAILS
        txt_user_f_name.setText(model.first_name);
        txt_user_l_name.setText(model.last_name);

        // ABOUT USER
        if (model.about.equals("")) {
            txt_user_about.setText("Not set...");
            txt_user_about.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            txt_user_about.setText(model.about);
            txt_user_about.setTextColor(getResources().getColor(R.color.dark_grey));
        }

        // SKILLS
        if (model.skills == null || model.skills.isEmpty()) {
            txt_user_skills.setVisibility(View.VISIBLE);
            chip_group_user_skills.setVisibility(View.GONE);
        } else {
            txt_user_skills.setVisibility(View.GONE);
            chip_group_user_skills.setVisibility(View.VISIBLE);
            chip_group_user_skills.removeAllViews();
            ArrayList<String> list = model.skills;
            int length = list.size();
            for (int i = 0; i < length; i++) {
                Chip chip = new Chip(MemberProfileActivity.this);
                chip.setText(list.get(i));
                chip.setChipBackgroundColorResource(R.color.light_grey);
                chip.setTextAppearanceResource(R.style.Text_Small);
                chip_group_user_skills.addView(chip);
            }
        }

        // GENDER
        if (model.gender == 0) {
            txt_user_gender.setText("Not set");
        } else if (model.gender == 1) {
            txt_user_gender.setText("Male");
        } else {
            txt_user_gender.setText("Female");
        }

        // DOB
        String user_age = helpers.calculateAge(model.dob);
        if (user_age.equals("")) {
            txt_user_age.setVisibility(View.GONE);
        } else {
            txt_user_age.setVisibility(View.VISIBLE);
            txt_user_age.setText(user_age);
        }

        txt_user_dob.setText(helpers.formatDate(model.dob));

        if (model.educational_experience == null || model.educational_experience.length() < 1) {
            txt_education_edit.setVisibility(View.GONE);
            ll_fragment_education.setVisibility(View.GONE);
        } else {
            txt_education_edit.setVisibility(View.VISIBLE);
            ll_fragment_education.setVisibility(View.VISIBLE);
            setupEducation();
        }

        if (model.work_experience == null || model.work_experience.length() < 1) {
            txt_work_edit.setVisibility(View.GONE);
            ll_fragment_work.setVisibility(View.GONE);
        } else {
            txt_work_edit.setVisibility(View.VISIBLE);
            ll_fragment_work.setVisibility(View.VISIBLE);
            setupWork();
        }

        if (model.documents == null || model.documents.length() < 1) {
            txt_documents_edit.setVisibility(View.GONE);
            ll_fragment_documents.setVisibility(View.GONE);
        } else {
            ll_fragment_documents.setVisibility(View.VISIBLE);
            txt_documents_edit.setVisibility(View.VISIBLE);
            setupEducation();
        }

        setUpDocuments();
    }

    private void setupEducation() {
        Fragment myFrag = new ExperienceViewFragment(model.educational_experience, EXPERIENCE_TYPE_EDUCATION);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.ll_fragment_education, myFrag)
                .commit();
    }

    private void setupWork() {
        Fragment myFrag = new ExperienceViewFragment(model.work_experience, EXPERIENCE_TYPE_WORK);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.ll_fragment_work, myFrag)
                .commit();
    }

    private void setUpDocuments() {
        Fragment myFrag = new DocumentsFragment(false, model.documents);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.ll_fragment_documents, myFrag)
                .commit();
    }

    private void hideAllViews(boolean hide, String message) {
        if (hide) {
            ll_profile.setVisibility(View.GONE);
            rl_no_list_items.setVisibility(View.VISIBLE);
            txt_no_results.setText(message);
        } else {
            ll_profile.setVisibility(View.VISIBLE);
            rl_no_list_items.setVisibility(View.GONE);
        }
    }

    // GET MEMBER ASYNC FUNCTION ===================================================================
    private void asyncFetchMember() {

        swipe_refresh.setRefreshing(true);

        UserInterface.retrofit.create(UserInterface.class)
                .getMemberByID(INT_USER_ID).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                swipe_refresh.setRefreshing(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        JSONObject user = main.getJSONObject("data");

                        model.availability = user.getInt("availability");

                        if (model.availability == 0) {
                            hideAllViews(true, "Member is unavailable");
                        } else {

                            model.id = user.getInt("id");
                            model.first_name = user.getString("first_name");
                            model.last_name = user.getString("last_name");
                            model.about = user.getString("about_me");
                            model.email = user.getString("email");
                            model.img_avatar = user.getString("avatar");
                            model.img_banner = user.getString("banner");
                            model.share_link = "Hey! Kindly check out my CV on HotelAide by following this link: " + user.getString("profile_url");
                            model.phone = user.getString("phone_number");
                            model.country_code = user.getInt("country_code");
                            model.dob = user.getString("dob");

                            if (!user.isNull("gender")) {
                                model.gender = user.getInt("gender");
                            } else {
                                model.gender = 0;
                            }

                            JSONArray skills_array = user.getJSONArray("skills");
                            if (skills_array != null && skills_array.length() > 0) {
                                int array_length = skills_array.length();
                                ArrayList<String> list = new ArrayList<>();
                                for (int i = 0; i < array_length; i++) {
                                    JSONObject object = skills_array.getJSONObject(i);
                                    list.add(object.getString("name"));
                                }
                                model.skills = list;
                            }

                            if (!user.isNull("work_experience")) {
                                model.work_experience = user.getJSONArray("work_experience");
                            }

                            if (!user.isNull("education_experience")) {
                                model.educational_experience = user.getJSONArray("education_experience");
                            }

                            if (!user.isNull("documents")) {
                                model.documents = user.getJSONArray("documents");
                            }

                            setTextAndImages();

                            hideAllViews(false, "");
                        }

                    } else {
                        helpers.handleErrorMessage(MemberProfileActivity.this, main.getJSONObject("data"));
                        hideAllViews(true, getString(R.string.error_server));
                    }

                } catch (JSONException e) {
                    hideAllViews(true, getString(R.string.error_server));
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    hideAllViews(true, getString(R.string.error_server));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                try {
                    swipe_refresh.setRefreshing(false);
                    Helpers.logThis(TAG_LOG, t.toString());
                    if (helpers.validateInternetConnection()) {
                        hideAllViews(true, getString(R.string.error_server));
                    } else {
                        hideAllViews(true, getString(R.string.error_connection));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


}
