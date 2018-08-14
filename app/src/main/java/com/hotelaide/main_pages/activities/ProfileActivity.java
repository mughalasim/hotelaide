package com.hotelaide.main_pages.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.main_pages.fragments.AddressFragment;
import com.hotelaide.main_pages.fragments.ChangePasswordFragment;
import com.hotelaide.main_pages.fragments.DocumentsFragment;
import com.hotelaide.main_pages.fragments.ExperienceFragment;
import com.hotelaide.main_pages.fragments.ProfileUpdateFragment;
import com.hotelaide.services.UserService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.Helpers.INT_PERMISSIONS_CAMERA;
import static com.hotelaide.utils.SharedPrefs.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.SharedPrefs.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.SharedPrefs.USER_ID;
import static com.hotelaide.utils.SharedPrefs.USER_IMG_AVATAR;
import static com.hotelaide.utils.SharedPrefs.USER_IMG_BANNER;

public class ProfileActivity extends AppCompatActivity {
   private Helpers helpers;

   private Database db;

   private Toolbar toolbar;
   private TextView toolbar_text;

    private ImageView
            img_banner;

    private AppBarLayout app_bar_layout;

    private Boolean isCollapsedToolbar = false;

    private RoundedImageView
            img_avatar;

    private final String
            TAG_LOG = "EDIT PROFILE";

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private final int
            RESULT_BANNER = 222,
            RESULT_AVATAR = 333;
    private int
            RESULT_EXPECTED = 0;

    private int[] jobSeekerTitleList = {
            R.string.nav_profile,
            R.string.nav_address,
            R.string.nav_education,
            R.string.nav_work,
            R.string.nav_documents,
            R.string.nav_pass
    };

    private Fragment[] jobSeekerFragments = {
            new ProfileUpdateFragment(),
            new AddressFragment(),
            new ExperienceFragment(),
            new ExperienceFragment(),
            new DocumentsFragment(),
            new ChangePasswordFragment()
    };

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile);

        helpers = new Helpers(ProfileActivity.this);
        db = new Database();

        setUpToolBarAndTabs();

        findAllViews();

        setFromSharedPrefs();

        setListeners();

        helpers.asyncGetUser();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(imageReturnedIntent);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    File file = new File(resultUri.getPath());

                    if (RESULT_EXPECTED == RESULT_AVATAR) {
                        Glide.with(this).load(resultUri).into(img_avatar);
                        MultipartBody.Part partFile = MultipartBody.Part.createFormData("avatar",
                                file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                        asyncUpdateImages(partFile, RESULT_EXPECTED);
                    } else {
                        Glide.with(this).load(resultUri).into(img_banner);
                        MultipartBody.Part partFile = MultipartBody.Part.createFormData("banner",
                                file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                        asyncUpdateImages(partFile, RESULT_EXPECTED);
                    }


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    helpers.ToastMessage(ProfileActivity.this,
                            getResources().getString(R.string.error_unknown));
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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

    private void setUpToolBarAndTabs() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_text = toolbar.findViewById(R.id.toolbar_text);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
                    toolbar_text.setText(jobSeekerTitleList[viewPager.getCurrentItem()]);
                } else if (verticalOffset == 0) {
                    isCollapsedToolbar = false;
                    toolbar_text.setText(TAG_LOG);
                } else {
                    isCollapsedToolbar = false;
                    toolbar_text.setText(TAG_LOG);

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
                    RESULT_EXPECTED = RESULT_BANNER;
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(ProfileActivity.this);

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
                    RESULT_EXPECTED = RESULT_AVATAR;
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(ProfileActivity.this);
                } else {
                    EasyPermissions.requestPermissions(ProfileActivity.this, getString(R.string.rationale_image),
                            INT_PERMISSIONS_CAMERA, perms);
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ProfileActivity.ViewPagerAdapter adapter = new ProfileActivity.ViewPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i <= jobSeekerTitleList.length - 1; i++) {
            Fragment fragment = jobSeekerFragments[i];
            if (i == 2) {
                Bundle bundle = new Bundle();
                bundle.putString("EXPERIENCE_TYPE", EXPERIENCE_TYPE_EDUCATION);
                fragment.setArguments(bundle);
            } else if (i == 3) {
                Bundle bundle = new Bundle();
                bundle.putString("EXPERIENCE_TYPE", EXPERIENCE_TYPE_WORK);
                fragment.setArguments(bundle);
            }
            adapter.addFragment(fragment, getResources().getString(jobSeekerTitleList[i]));
        }

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (isCollapsedToolbar) {
                    toolbar_text.setText(jobSeekerTitleList[position]);
                } else {
                    toolbar_text.setText(TAG_LOG);
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
    private void asyncUpdateImages(final MultipartBody.Part partFile, final int type) {

        UserService userService = UserService.retrofit.create(UserService.class);

        Call<JsonObject> call;

        if (type == RESULT_AVATAR) {
            call = userService.setUserImages(
                    SharedPrefs.getInt(USER_ID),
                    partFile,
                    null
            );
        } else {
            call = userService.setUserImages(
                    SharedPrefs.getInt(USER_ID),
                    null,
                    partFile
            );
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        if (SharedPrefs.setUser(main.getJSONObject("user"))) {
                            helpers.ToastMessage(ProfileActivity.this, "Image updated");

                        } else {
                            helpers.ToastMessage(ProfileActivity.this, getString(R.string.error_server));
                        }
                    } else {
                        helpers.handleErrorMessage(ProfileActivity.this, main.getJSONObject("data"));
                    }

                    setFromSharedPrefs();

                } catch (JSONException e) {
                    helpers.ToastMessage(ProfileActivity.this, getString(R.string.error_server));
                    e.printStackTrace();
                    setFromSharedPrefs();
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
