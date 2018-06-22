package com.hotelaide.main_pages.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.main_pages.fragments.ChangePasswordFragment;
import com.hotelaide.main_pages.fragments.DocumentsFragment;
import com.hotelaide.main_pages.fragments.EducationFragment;
import com.hotelaide.main_pages.fragments.ProfileUpdateFragment;
import com.hotelaide.main_pages.fragments.WorkExperienceFragment;
import com.hotelaide.main_pages.models.UserModel;
import com.hotelaide.services.UserService;
import com.hotelaide.start_up.LoginActivity;
import com.hotelaide.start_up.StartUpAboutUsFragment;
import com.hotelaide.start_up.StartUpContactUsFragment;
import com.hotelaide.start_up.StartUpForgotPassFragment;
import com.hotelaide.start_up.StartUpLoginFragment;
import com.hotelaide.start_up.StartUpSignUpFragment;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static com.hotelaide.utils.Helpers.INT_PERMISSIONS_CAMERA;
import static com.hotelaide.utils.Helpers.START_RETURN;
import static com.hotelaide.utils.SharedPrefs.USER_IMG_AVATAR;
import static com.hotelaide.utils.SharedPrefs.USER_IMG_BANNER;

public class ProfileActivity extends ParentActivity {

    private ImageView
            img_banner;

    private AppBarLayout app_bar_layout;

    private Boolean isCollapsedToolbar = false;

    private RoundedImageView
            img_avatar;

    private final String
            TAG_LOG = "MY PROFILE";

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private final int RESULT_BANNER = 222, RESULT_AVATAR = 333;


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
        app_bar_layout = findViewById(R.id.app_bar_layout);

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
        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    isCollapsedToolbar = true;
                } else if (verticalOffset == 0) {
                    isCollapsedToolbar = false;
                } else {
                    isCollapsedToolbar = false;

                }
            }
        });

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
        adapter.addFragment(fragment1, getResources().getString(R.string.nav_profile));

        Fragment fragment2 = new EducationFragment();
        adapter.addFragment(fragment2, getResources().getString(R.string.nav_education));

        Fragment fragment3 = new WorkExperienceFragment();
        adapter.addFragment(fragment3, getResources().getString(R.string.nav_work));

        Fragment fragment4 = new DocumentsFragment();
        adapter.addFragment(fragment4, getResources().getString(R.string.nav_documents));

        Fragment fragment5 = new ChangePasswordFragment();
        adapter.addFragment(fragment5, getResources().getString(R.string.nav_pass));

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (isCollapsedToolbar) {
                    switch (position) {
                        case 1:
                            toolbar.setTitle(R.string.nav_profile);
                            break;

                        case 2:
                            toolbar.setTitle(R.string.nav_education);
                            break;

                        case 3:
                            toolbar.setTitle(R.string.nav_work);
                            break;

                        case 4:
                            toolbar.setTitle(R.string.nav_documents);
                            break;

                        case 5:
                            toolbar.setTitle(R.string.nav_pass);
                            break;
                    }
                } else {
                    toolbar.setTitle(TAG_LOG);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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


    // LOGIN ASYNC FUNCTIONS =======================================================================
    private void asyncUpdate(final UserModel userModel, final MultipartBody.Part avatar, final MultipartBody.Part banner) {

        helpers.setProgressDialogMessage("Updating profile, please wait...");
        helpers.progressDialog(true);

        // TODO - image uploading
//        File avatar_file = new File("");
//        MultipartBody.Part avatar = MultipartBody.Part.createFormData("file", avatar_file.getName(), RequestBody.create(MediaType.parse("image/*"), avatar_file));
//
//        File baner_file = new File("");
//        MultipartBody.Part banner = MultipartBody.Part.createFormData("file", baner_file.getName(), RequestBody.create(MediaType.parse("image/*"), baner_file));


        UserService userService = UserService.retrofit.create(UserService.class);
        final Call<JsonObject> call = userService.setUser(
                userModel.first_name,
                userModel.last_name,
                userModel.country_code,
                userModel.phone,
                userModel.email,
                userModel.password,
                userModel.geo_lat,
                userModel.geo_lng,
                userModel.dob,
                userModel.fb_id,
                userModel.google_id,
                avatar,
                banner
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        JSONObject data = main.getJSONObject("data");
                        if (SharedPrefs.setUser(data.getJSONObject("user"))) {
                            SharedPrefs.setString(SharedPrefs.ACCESS_TOKEN, data.getString("token"));
//                            startActivity(new Intent(ProfileActivity.this, DashboardActivity.class).putExtra(START_RETURN, START_RETURN));
//                            finish();
                            helpers.ToastMessage(ProfileActivity.this, "SUCCESSFULLY UPDATED");

                        } else {
                            helpers.ToastMessage(ProfileActivity.this, getString(R.string.error_server));
                        }
                    } else {
                        helpers.handleErrorMessage(ProfileActivity.this, main.getJSONObject("data"));
                    }

                } catch (JSONException e) {
                    helpers.ToastMessage(ProfileActivity.this, e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.progressDialog(false);
                Helpers.LogThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(ProfileActivity.this, getString(R.string.error_server));
                } else {
                    helpers.ToastMessage(ProfileActivity.this, getString(R.string.error_connection));
                }

            }
        });

    }

}
