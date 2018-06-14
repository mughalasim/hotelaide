package com.hotelaide.main_pages.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hotelaide.R;
import com.hotelaide.main_pages.fragments.ProfileUpdateFragment;
import com.hotelaide.main_pages.fragments.WorkExperienceFragment;
import com.hotelaide.start_up.LoginActivity;
import com.hotelaide.start_up.StartUpAboutUsFragment;
import com.hotelaide.start_up.StartUpContactUsFragment;
import com.hotelaide.start_up.StartUpForgotPassFragment;
import com.hotelaide.start_up.StartUpLoginFragment;
import com.hotelaide.start_up.StartUpSignUpFragment;
import com.hotelaide.utils.SharedPrefs;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static com.hotelaide.utils.Helpers.INT_PERMISSIONS_CAMERA;
import static com.hotelaide.utils.SharedPrefs.USER_IMG_AVATAR;
import static com.hotelaide.utils.SharedPrefs.USER_IMG_BANNER;

public class ProfileActivity extends ParentActivity {

    private ImageView
            img_banner;

    private RoundedImageView
            img_avatar;

    private final String
            TAG_LOG = "PROFILE MAIN";

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private final int RESULT_BANNER = 222, RESULT_AVATAR = 333;

    private int[] navLabels = {
            R.string.nav_profile,
            R.string.nav_education,
            R.string.nav_work,
            R.string.nav_documents,
            R.string.nav_pass
    };



    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile);

        initialize(R.id.drawer_my_profile, TAG_LOG);

        findAllViews();

        setFromSharedPrefs();

        setListeners();


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case RESULT_AVATAR:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
//                    img_avatar.setImageURI(selectedImage);
                    Glide.with(this).load(selectedImage).into(img_avatar);
                }

                break;
            case RESULT_BANNER:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
//                    img_banner.setImageURI(selectedImage);
                    Glide.with(this).load(selectedImage).into(img_banner);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        helpers.myPermissionsDialog(ProfileActivity.this, grantResults);
    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        img_avatar = findViewById(R.id.img_avatar);
        img_banner = findViewById(R.id.img_banner);

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager, true);
    }

    private void setFromSharedPrefs() {
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_AVATAR)).into(img_avatar);
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_BANNER)).into(img_banner);
    }

    private void setListeners() {
        final String[] perms = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        img_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(ProfileActivity.this, perms)) {
                    dialogSetImage(RESULT_BANNER, "SET BANNER");
                } else {
                    EasyPermissions.requestPermissions(ProfileActivity.this, getString(R.string.rationale_image),
                            INT_PERMISSIONS_CAMERA, perms);
                }

            }
        });

        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(ProfileActivity.this, perms)) {
                    dialogSetImage(RESULT_AVATAR, "SET AVATAR");
                } else {
                    EasyPermissions.requestPermissions(ProfileActivity.this, getString(R.string.rationale_image),
                            INT_PERMISSIONS_CAMERA, perms);
                }
            }
        });
    }

    private void dialogSetImage(final int result_code, String title) {
        final Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.dialog_set_image);
        final TextView btn_camera = dialog.findViewById(R.id.btn_camera);
        final TextView btn_gallery = dialog.findViewById(R.id.btn_gallery);
        final TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final TextView txt_title = dialog.findViewById(R.id.txt_title);
        txt_title.setText(title);

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, result_code);
                dialog.cancel();
            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, result_code);
                dialog.cancel();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void setupViewPager(ViewPager viewPager) {
        ProfileActivity.ViewPagerAdapter adapter = new ProfileActivity.ViewPagerAdapter(getSupportFragmentManager());

        Fragment fragment1 = new ProfileUpdateFragment();
        adapter.addFragment(fragment1, getResources().getString(navLabels[0]));

        Fragment fragment2 = new WorkExperienceFragment();
        adapter.addFragment(fragment2, getResources().getString(navLabels[1]));

        Fragment fragment3 = new WorkExperienceFragment();
        adapter.addFragment(fragment3, getResources().getString(navLabels[2]));

        Fragment fragment4 = new WorkExperienceFragment();
        adapter.addFragment(fragment4, getResources().getString(navLabels[3]));

        Fragment fragment5 = new WorkExperienceFragment();
        adapter.addFragment(fragment5, getResources().getString(navLabels[4]));

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

}
