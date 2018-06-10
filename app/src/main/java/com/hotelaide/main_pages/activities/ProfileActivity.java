package com.hotelaide.main_pages.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hotelaide.R;
import com.hotelaide.main_pages.models.WorkExperienceModel;
import com.hotelaide.utils.SharedPrefs;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import static com.hotelaide.utils.SharedPrefs.USER_IMG_AVATAR;
import static com.hotelaide.utils.SharedPrefs.USER_IMG_BANNER;

public class ProfileActivity extends ParentActivity {

    private ImageView
            img_banner;

    private TextView
            txt_user_f_name,
            txt_user_l_name,
            txt_user_email,
            txt_user_country_code,
            txt_user_phone,
            txt_user_dob,
            btn_add_work_experience,
            btn_banner_upload;

    private LinearLayout ll_work_experience;

    private RoundedImageView
            img_avatar;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile);

        String TAG_LOG = "MY PROFILE";
        initialize(R.id.drawer_my_profile, TAG_LOG);

        findAllViews();

        setFromSharedPrefs();

        populateWorkExperience();

        setListeners();

    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        img_avatar = findViewById(R.id.img_avatar);
        img_banner = findViewById(R.id.img_banner);
        txt_user_f_name = findViewById(R.id.txt_user_f_name);
        txt_user_l_name = findViewById(R.id.txt_user_l_name);
        txt_user_email = findViewById(R.id.txt_user_email);
        txt_user_country_code = findViewById(R.id.txt_user_country_code);
        txt_user_phone = findViewById(R.id.txt_user_phone);
        txt_user_dob = findViewById(R.id.txt_user_dob);

        btn_add_work_experience = findViewById(R.id.btn_add_work_experience);
        btn_banner_upload = findViewById(R.id.btn_banner_upload);

        ll_work_experience = findViewById(R.id.ll_work_experience);

    }

    private void setFromSharedPrefs() {
        txt_user_f_name.setText(SharedPrefs.getString(SharedPrefs.USER_F_NAME));
        txt_user_l_name.setText(SharedPrefs.getString(SharedPrefs.USER_L_NAME));
        txt_user_email.setText(SharedPrefs.getString(SharedPrefs.USER_EMAIL));
        txt_user_dob.setText(SharedPrefs.getString(SharedPrefs.USER_DOB));
        txt_user_phone.setText(String.valueOf(SharedPrefs.getInt(SharedPrefs.USER_PHONE)));
        txt_user_country_code.setText(String.valueOf(SharedPrefs.getInt(SharedPrefs.USER_COUNTRY_CODE)));

        Glide.with(this).load(SharedPrefs.getString(USER_IMG_AVATAR)).into(img_avatar);
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_BANNER)).into(img_banner);


    }

    private void populateWorkExperience() {
        ArrayList<WorkExperienceModel> workExperienceModelArrayList = db.getAllWorkExperience();
        LayoutInflater linf;
        linf = LayoutInflater.from(ProfileActivity.this);

        int array_size = workExperienceModelArrayList.size();

        for (int v = 0; v < array_size; v++) {
            View child = linf.inflate(R.layout.list_item_work_experience, null);

            final TextView txt_company_name = child.findViewById(R.id.txt_company_name);
            final TextView txt_position = child.findViewById(R.id.txt_position);
            final TextView txt_start_date = child.findViewById(R.id.txt_start_date);
            final TextView txt_end_date = child.findViewById(R.id.txt_end_date);
            final TextView txt_current = child.findViewById(R.id.txt_current);
            final TextView txt_responsibilities = child.findViewById(R.id.txt_responsibilities);
            final TextView txt_responsibilities_show = child.findViewById(R.id.txt_responsibilities_show);

            WorkExperienceModel workExperienceModel = workExperienceModelArrayList.get(v);

            txt_company_name.setText(workExperienceModel.company_name);
            txt_position.setText(workExperienceModel.position);
            txt_start_date.setText(workExperienceModel.start_date);

            if (workExperienceModel.current) {
                txt_current.setVisibility(View.VISIBLE);
                txt_end_date.setVisibility(View.GONE);
            } else {
                txt_current.setVisibility(View.GONE);
                txt_end_date.setVisibility(View.VISIBLE);
                txt_end_date.setText(workExperienceModel.end_date);
            }

            txt_responsibilities.setText(workExperienceModel.responsibilities);
            if (workExperienceModel.responsibilities.length() > 50) {
                txt_responsibilities_show.setVisibility(View.VISIBLE);
            } else {
                txt_responsibilities_show.setVisibility(View.GONE);
            }

            txt_responsibilities_show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(txt_responsibilities_show.getText().toString().equals(getResources().getString(R.string.txt_more))){
                        txt_responsibilities.setMaxLines(Integer.MAX_VALUE);
                        txt_responsibilities_show.setText(getResources().getString(R.string.txt_less));
                    }else{
                        txt_responsibilities.setMaxLines(3);
                        txt_responsibilities_show.setText(getResources().getString(R.string.txt_more));
                    }
                }
            });

            ll_work_experience.addView(child);

        }

    }

    private void setListeners() {

    }

}
