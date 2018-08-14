package com.hotelaide.main_pages.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hotelaide.R;
import com.hotelaide.main_pages.fragments.ExperienceViewFragment;
import com.hotelaide.utils.SharedPrefs;

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

public class ProfileViewActivity extends ParentActivity {

    // BANNER ------------------------------
    private ImageView
            btn_share,
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


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile_view);

        String TAG_LOG = "PROFILE VIEW";

        initialize(R.id.drawer_my_profile, TAG_LOG);

        findAllViews();

    }

    @Override
    public void onResume() {
        super.onResume();
        setTextAndImages();
        setupEducation();
        setupWork();
    }


    // BASIC METHODS ===============================================================================
    public void profileEdit(View view) {
        startActivity(new Intent(ProfileViewActivity.this, ProfileActivity.class));
    }

    private void findAllViews() {
        // BANNER
        img_avatar = findViewById(R.id.img_avatar);
        img_banner = findViewById(R.id.img_banner);
        btn_share = findViewById(R.id.btn_share);

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

        txt_user_full_address.setText(SharedPrefs.getString(USER_FULL_ADDRESS));

        if (SharedPrefs.getInt(USER_COUNTY) > 0) {
            txt_user_county_name.setText(db.getCountyNameByID(SharedPrefs.getInt(USER_COUNTY)));
        } else {
            txt_user_county_name.setVisibility(View.GONE);
        }

        txt_user_email.setText(SharedPrefs.getString(USER_EMAIL));

        String user_phone = String.valueOf(SharedPrefs.getInt(USER_COUNTRY_CODE)) + " " + String.valueOf(SharedPrefs.getInt(USER_PHONE));
        txt_user_phone.setText(user_phone);

        txt_user_availability.setText("Immediate");

    }

}
