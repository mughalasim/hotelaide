package com.hotelaide.main.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.hotelaide.main.fragments.ExperienceViewFragment;
import com.hotelaide.utils.SharedPrefs;

import static com.hotelaide.utils.Database.COUNTY_TABLE_NAME;
import static com.hotelaide.utils.SharedPrefs.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.SharedPrefs.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.SharedPrefs.USER_COUNTRY_CODE;
import static com.hotelaide.utils.SharedPrefs.USER_COUNTY;
import static com.hotelaide.utils.SharedPrefs.USER_DOB;
import static com.hotelaide.utils.SharedPrefs.USER_EMAIL;
import static com.hotelaide.utils.SharedPrefs.USER_FULL_ADDRESS;
import static com.hotelaide.utils.SharedPrefs.USER_F_NAME;
import static com.hotelaide.utils.SharedPrefs.USER_IMG_AVATAR;
import static com.hotelaide.utils.SharedPrefs.USER_IMG_BANNER;
import static com.hotelaide.utils.SharedPrefs.USER_L_NAME;
import static com.hotelaide.utils.SharedPrefs.USER_PHONE;
import static com.hotelaide.utils.SharedPrefs.USER_PROFILE_COMPLETION;
import static com.hotelaide.utils.SharedPrefs.USER_URL;

public class ProfileViewActivity extends ParentActivity {

    // PROGRESS --------------------------------------
    private RelativeLayout rl_progress;
    private SeekBar seek_bar_progress;
    private TextView txt_progress;

    // MENU OPTIONS -----------------------------------
    private MenuItem menu_edit;
    private Boolean bool_edit_mode;

    // BANNER ----------------------------------------
    private ImageView
            img_banner,
            img_avatar;


    // INFO AND CONTACT ------------------------------
    private TextView
            txt_user_f_name,
            txt_user_l_name,
            txt_user_age,
            txt_user_dob,
            txt_user_full_address,
            txt_user_county_name,
            txt_user_email,
            txt_user_phone,
            txt_user_availability;

    private String STR_SHARE_LINK =
            "Hey! Kindly check out my CV on HotelAide by following this link: ";


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile_view);

        String TAG_LOG = "MY PROFILE";

        initialize(R.id.drawer_my_profile, TAG_LOG);

        findAllViews();

        setListeners();

        handleExtraBundles();

    }

    @Override
    public void onResume() {
        super.onResume();
        setTextAndImages();
        setupEducation();
        setupWork();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);
        menu_edit = menu.findItem(R.id.edit);

        setViewAccordingToEditMode(bool_edit_mode);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                helpers.dialogShare(ProfileViewActivity.this, STR_SHARE_LINK);
                break;

            case R.id.edit:
                startActivity(new Intent(ProfileViewActivity.this, ProfileActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // BASIC METHODS ===============================================================================
    private void findAllViews() {
        STR_SHARE_LINK = STR_SHARE_LINK + SharedPrefs.getString(USER_URL);

        rl_progress = findViewById(R.id.rl_progress);
        seek_bar_progress = findViewById(R.id.seek_bar_progress);
        txt_progress = findViewById(R.id.txt_progress);

        // BANNER
        img_avatar = findViewById(R.id.img_avatar);
        img_banner = findViewById(R.id.img_banner);

        // INFO AND CONTACT DETAILS
        txt_user_f_name = findViewById(R.id.txt_user_f_name);
        txt_user_l_name = findViewById(R.id.txt_user_l_name);
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
        seek_bar_progress.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        bool_edit_mode = extras != null && extras.getString("EDIT_MODE") != null;
    }

    private void setViewAccordingToEditMode(Boolean bool_edit_mode) {
        menu_edit.setVisible(bool_edit_mode);
        if (bool_edit_mode) {
            rl_progress.setVisibility(View.VISIBLE);
        } else {
            rl_progress.setVisibility(View.GONE);
        }
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

    private void setTextAndImages() {
        // BANNER IMAGES
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_AVATAR)).into(img_avatar);
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_BANNER)).into(img_banner);

        // INFO AND CONTACT DETAILS
        txt_user_f_name.setText(SharedPrefs.getString(USER_F_NAME).concat(" "));
        txt_user_l_name.setText(SharedPrefs.getString(USER_L_NAME).concat(" "));

        txt_user_age.setText(helpers.formatAge(SharedPrefs.getString(USER_DOB)));

        txt_user_dob.setText(SharedPrefs.getString(USER_DOB));

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

        txt_user_availability.setText("Immediate");

        updateProfileSeekBar(SharedPrefs.getInt(USER_PROFILE_COMPLETION));

        updateProfileSeekBar(55);
    }

    private void updateProfileSeekBar(int completion) {
        if (completion == 100) {
            rl_progress.setVisibility(View.GONE);
        } else if(bool_edit_mode){
            rl_progress.setVisibility(View.VISIBLE);
            seek_bar_progress.setProgress(completion);
            txt_progress.setText(String.valueOf(completion).concat("%"));
        }
    }

}
