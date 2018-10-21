package com.hotelaide.main.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hotelaide.R;
import com.hotelaide.main.fragments.DocumentsFragment;
import com.hotelaide.main.fragments.ExperienceViewFragment;
import com.hotelaide.utils.SharedPrefs;

import static com.hotelaide.utils.StaticVariables.COUNTY_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_ADDRESS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_BASIC;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_DOCUMENTS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_PASS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_WORK;
import static com.hotelaide.utils.StaticVariables.STR_SHARE_LINK;
import static com.hotelaide.utils.StaticVariables.USER_AVAILABILITY;
import static com.hotelaide.utils.StaticVariables.USER_COUNTRY_CODE;
import static com.hotelaide.utils.StaticVariables.USER_COUNTY;
import static com.hotelaide.utils.StaticVariables.USER_DOB;
import static com.hotelaide.utils.StaticVariables.USER_EMAIL;
import static com.hotelaide.utils.StaticVariables.USER_FULL_ADDRESS;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_GENDER;
import static com.hotelaide.utils.StaticVariables.USER_IMG_AVATAR;
import static com.hotelaide.utils.StaticVariables.USER_IMG_BANNER;
import static com.hotelaide.utils.StaticVariables.USER_L_NAME;
import static com.hotelaide.utils.StaticVariables.USER_PHONE;
import static com.hotelaide.utils.StaticVariables.USER_PROFILE_COMPLETION;
import static com.hotelaide.utils.StaticVariables.USER_URL;

public class ProfileActivity extends ParentActivity {

    // PROGRESS --------------------------------------
    private RelativeLayout rl_progress;
    private SeekBar seek_bar_progress;
    private TextView txt_progress;

    // BANNER ----------------------------------------
    private ImageView
            img_banner,
            img_avatar;


    // INFO AND CONTACT ------------------------------
    private TextView
            txt_user_f_name,
            txt_user_l_name,
            txt_user_gender,
            txt_user_age,
            txt_user_dob,
            txt_user_full_address,
            txt_user_county_name,
            txt_user_email,
            txt_user_phone,
            txt_user_availability;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        String TAG_LOG = "MY PROFILE";

        initialize(R.id.drawer_profile, TAG_LOG);

        findAllViews();

        setListeners();

    }

    @Override
    public void onResume() {
        super.onResume();
        setTextAndImages();
        setupEducation();
        setupWork();
        setUpDocuments();
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
                helpers.dialogShare(ProfileActivity.this, STR_SHARE_LINK);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // BASIC METHODS ===============================================================================
    private void findAllViews() {
        STR_SHARE_LINK = "Hey! Kindly check out my CV on HotelAide by following this link: " + SharedPrefs.getString(USER_URL);

        rl_progress = findViewById(R.id.rl_progress);
        seek_bar_progress = findViewById(R.id.seek_bar_progress);
        txt_progress = findViewById(R.id.txt_progress);

        // BANNER
        img_avatar = findViewById(R.id.img_avatar);
        img_banner = findViewById(R.id.img_banner);

        // INFO AND CONTACT DETAILS
        txt_user_f_name = findViewById(R.id.txt_user_f_name);
        txt_user_l_name = findViewById(R.id.txt_user_l_name);
        txt_user_gender = findViewById(R.id.txt_user_gender);
        txt_user_age = findViewById(R.id.txt_user_age);
        txt_user_dob = findViewById(R.id.txt_user_dob);
        txt_user_full_address = findViewById(R.id.txt_user_full_address);
        txt_user_county_name = findViewById(R.id.txt_user_county_name);
        txt_user_email = findViewById(R.id.txt_user_email);
        txt_user_phone = findViewById(R.id.txt_user_phone);
        txt_user_availability = findViewById(R.id.txt_user_availability);


    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        seek_bar_progress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpers.openImageViewer(ProfileActivity.this, SharedPrefs.getString(USER_IMG_AVATAR));
            }
        });

        img_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpers.openImageViewer(ProfileActivity.this, SharedPrefs.getString(USER_IMG_BANNER));
            }
        });
    }

    private void setupEducation() {
        Fragment myFrag = new ExperienceViewFragment();

        Bundle bundle = new Bundle();
        bundle.putString("EXPERIENCE_TYPE", EXPERIENCE_TYPE_EDUCATION);
        myFrag.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.ll_fragment_education, myFrag)
                .commit();
    }

    private void setupWork() {
        Fragment myFrag = new ExperienceViewFragment();

        Bundle bundle = new Bundle();
        bundle.putString("EXPERIENCE_TYPE", EXPERIENCE_TYPE_WORK);
        myFrag.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.ll_fragment_work, myFrag)
                .commit();
    }

    private void setUpDocuments() {
        Fragment myFrag = new DocumentsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("VIEW", "VIEW");
        myFrag.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.ll_fragment_documents, myFrag)
                .commit();
    }

    private void setTextAndImages() {
        // BANNER IMAGES
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_AVATAR)).into(img_avatar);
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_BANNER)).into(img_banner);

        // INFO AND CONTACT DETAILS
        txt_user_f_name.setText(SharedPrefs.getString(USER_F_NAME).concat(" "));
        txt_user_l_name.setText(SharedPrefs.getString(USER_L_NAME).concat(" "));

        if (SharedPrefs.getInt(USER_GENDER) == 0) {
            txt_user_gender.setText("Not set");
            txt_user_gender.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.red));
        } else if (SharedPrefs.getInt(USER_GENDER) == 1) {
            txt_user_gender.setText("Male");
            txt_user_gender.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.dark_grey));
        } else {
            txt_user_gender.setText("Female");
            txt_user_gender.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.dark_grey));
        }


        String user_age = helpers.calculateAge(SharedPrefs.getString(USER_DOB));
        if (user_age.equals("")) {
            txt_user_age.setVisibility(View.GONE);
        } else {
            txt_user_age.setVisibility(View.VISIBLE);
            txt_user_age.setText(user_age);
        }

        txt_user_dob.setText(helpers.formatDate(SharedPrefs.getString(USER_DOB)));

        if (SharedPrefs.getString(USER_FULL_ADDRESS).equals("")) {
            txt_user_full_address.setVisibility(View.GONE);
        } else {
            txt_user_full_address.setText(SharedPrefs.getString(USER_FULL_ADDRESS));
            txt_user_full_address.setVisibility(View.VISIBLE);
        }

        if (SharedPrefs.getInt(USER_COUNTY) > 0) {
            txt_user_county_name.setText(db.getFilterNameByID(COUNTY_TABLE_NAME, SharedPrefs.getInt(USER_COUNTY)));
            txt_user_county_name.setVisibility(View.VISIBLE);
        } else {
            txt_user_county_name.setVisibility(View.GONE);
        }

        txt_user_email.setText(SharedPrefs.getString(USER_EMAIL));

        String user_phone = String.valueOf(SharedPrefs.getInt(USER_COUNTRY_CODE)) + " "
                + String.valueOf(SharedPrefs.getInt(USER_PHONE));

        txt_user_phone.setText(user_phone);

        if (SharedPrefs.getInt(USER_AVAILABILITY) == 1) {
            txt_user_availability.setText("Available");
            txt_user_availability.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.colorPrimary));
        } else {
            txt_user_availability.setText("Hidden");
            txt_user_availability.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.red));
        }

        updateProfileSeekBar(SharedPrefs.getInt(USER_PROFILE_COMPLETION));

        updateProfileSeekBar(55);
    }

    private void updateProfileSeekBar(int completion) {
        if (completion == 100) {
            rl_progress.setVisibility(View.GONE);
        } else {
            rl_progress.setVisibility(View.GONE);
            seek_bar_progress.setProgress(completion);
            txt_progress.setText(String.valueOf(completion).concat("%"));
        }
    }

    public void editProfile(View view) {
        if (view.getId() == R.id.txt_basic_info) {
            startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class)
                    .putExtra(EXTRA_PROFILE_BASIC, EXTRA_PROFILE_BASIC));
        } else if (view.getId() == R.id.txt_contact_info) {
            startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class)
                    .putExtra(EXTRA_PROFILE_ADDRESS, EXTRA_PROFILE_ADDRESS));
        } else if (view.getId() == R.id.txt_education_edit) {
            startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class)
                    .putExtra(EXTRA_PROFILE_EDUCATION, EXTRA_PROFILE_EDUCATION));
        } else if (view.getId() == R.id.txt_work_edit) {
            startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class)
                    .putExtra(EXTRA_PROFILE_WORK, EXTRA_PROFILE_WORK));
        } else if (view.getId() == R.id.txt_documents_edit) {
            startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class)
                    .putExtra(EXTRA_PROFILE_DOCUMENTS, EXTRA_PROFILE_DOCUMENTS));
        } else if (view.getId() == R.id.btn_change_password) {
            startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class)
                    .putExtra(EXTRA_PROFILE_PASS, EXTRA_PROFILE_PASS));
        } else if (view.getId() == R.id.btn_update_image) {
            startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class));
        }
    }
}
