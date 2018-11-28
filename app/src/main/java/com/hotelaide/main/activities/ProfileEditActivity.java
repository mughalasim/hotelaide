package com.hotelaide.main.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.UserInterface;
import com.hotelaide.main.fragments.AddressFragment;
import com.hotelaide.main.fragments.ChangePasswordFragment;
import com.hotelaide.main.fragments.DocumentsFragment;
import com.hotelaide.main.fragments.ExperienceEditFragment;
import com.hotelaide.main.fragments.ProfileUpdateFragment;
import com.hotelaide.services.BackgroundFetchService;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.main.activities.MapActivity.MAP_ACTIVITY_LATITUDE;
import static com.hotelaide.main.activities.MapActivity.MAP_ACTIVITY_LONGITUDE;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_ADDRESS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_BASIC;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_DOCUMENTS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_PASS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_WORK;
import static com.hotelaide.utils.StaticVariables.INT_PERMISSIONS_CAMERA;
import static com.hotelaide.utils.StaticVariables.USER_ID;
import static com.hotelaide.utils.StaticVariables.USER_IMG_AVATAR;
import static com.hotelaide.utils.StaticVariables.USER_IMG_BANNER;

public class ProfileEditActivity extends FragmentActivity {
    private Helpers helpers;

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

    private TabLayout tab_layout;

    private ViewPager view_pager;

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
            new ExperienceEditFragment(),
            new ExperienceEditFragment(),
            new DocumentsFragment(),
            new ChangePasswordFragment()
    };

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_edit);

        helpers = new Helpers(ProfileEditActivity.this);

        setUpToolBarAndTabs();

        findAllViews();

        setFromSharedPrefs();

        setListeners();

        if (helpers.validateServiceRunning(BackgroundFetchService.class)) {
            startService(new Intent(ProfileEditActivity.this, BackgroundFetchService.class));
        }

        handleExtraBundles();

    }

    @Override
    protected void onDestroy() {
        MAP_ACTIVITY_LATITUDE = 0.0;
        MAP_ACTIVITY_LONGITUDE = 0.0;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (helpers.validateProfileCompletion(ProfileEditActivity.this)) {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED)
            switch (requestCode) {
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
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
                        helpers.ToastMessage(ProfileEditActivity.this,
                                getResources().getString(R.string.error_unknown));
                    }
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        helpers.myPermissionsDialog(ProfileEditActivity.this, grantResults);
    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        app_bar_layout = findViewById(R.id.app_bar_layout);

        img_avatar = findViewById(R.id.img_avatar);
        img_banner = findViewById(R.id.img_banner);

        view_pager = findViewById(R.id.view_pager);
        tab_layout = findViewById(R.id.tabs);

        setupViewPager(view_pager);
        tab_layout.setupWithViewPager(view_pager, true);
    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString(EXTRA_PROFILE_BASIC) != null) {
            view_pager.setCurrentItem(0);
            app_bar_layout.setExpanded(false);
        } else if (extras != null && extras.getString(EXTRA_PROFILE_ADDRESS) != null) {
            view_pager.setCurrentItem(1);
            app_bar_layout.setExpanded(false);
        } else if (extras != null && extras.getString(EXTRA_PROFILE_EDUCATION) != null) {
            view_pager.setCurrentItem(2);
            app_bar_layout.setExpanded(false);
        } else if (extras != null && extras.getString(EXTRA_PROFILE_WORK) != null) {
            view_pager.setCurrentItem(3);
            app_bar_layout.setExpanded(false);
        } else if (extras != null && extras.getString(EXTRA_PROFILE_DOCUMENTS) != null) {
            view_pager.setCurrentItem(4);
            app_bar_layout.setExpanded(false);
        } else if (extras != null && extras.getString(EXTRA_PROFILE_PASS) != null) {
            view_pager.setCurrentItem(5);
            app_bar_layout.setExpanded(false);
        } else {
            app_bar_layout.setExpanded(true);
        }
    }

    private void setUpToolBarAndTabs() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_text = toolbar.findViewById(R.id.toolbar_text);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(ProfileEditActivity.this, R.drawable.ic_back));
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
                    toolbar_text.setText(jobSeekerTitleList[view_pager.getCurrentItem()]);
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
                if (EasyPermissions.hasPermissions(ProfileEditActivity.this, perms)) {
                    RESULT_EXPECTED = RESULT_BANNER;
                    startImageActivity();
                } else {
                    EasyPermissions.requestPermissions(ProfileEditActivity.this, getString(R.string.rationale_image),
                            INT_PERMISSIONS_CAMERA, perms);
                }

            }
        });

        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(ProfileEditActivity.this, perms)) {
                    RESULT_EXPECTED = RESULT_AVATAR;
                    startImageActivity();
                } else {
                    EasyPermissions.requestPermissions(ProfileEditActivity.this, getString(R.string.rationale_image),
                            INT_PERMISSIONS_CAMERA, perms);
                }
            }
        });
    }

    @AfterPermissionGranted(INT_PERMISSIONS_CAMERA)
    private void startImageActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(ProfileEditActivity.this);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

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
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragment_titles = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragment_titles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragment_titles.get(position);
        }

    }

    public void moveViewPagerNext() {
        view_pager.setCurrentItem(view_pager.getCurrentItem() + 1);
    }

    // UPDATE AVATAR AND BANNER ASYNC FUNCTIONS ====================================================
    private void asyncUpdateImages(final MultipartBody.Part partFile, final int type) {

        UserInterface userInterface = UserInterface.retrofit.create(UserInterface.class);

        Call<JsonObject> call;

        if (type == RESULT_AVATAR) {
            call = userInterface.setUserImages(
                    SharedPrefs.getInt(USER_ID),
                    partFile,
                    null
            );
        } else {
            call = userInterface.setUserImages(
                    SharedPrefs.getInt(USER_ID),
                    null,
                    partFile
            );
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                helpers.dismissProgressDialog();
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    Helpers.logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        if (SharedPrefs.setUser(main.getJSONObject("user"))) {
                            helpers.ToastMessage(ProfileEditActivity.this, "Image updated");

                        } else {
                            helpers.ToastMessage(ProfileEditActivity.this, getString(R.string.error_server));
                        }
                    } else {
                        helpers.handleErrorMessage(ProfileEditActivity.this, main.getJSONObject("data"));
                    }

                    setFromSharedPrefs();

                } catch (JSONException e) {
                    helpers.ToastMessage(ProfileEditActivity.this, getString(R.string.error_server));
                    e.printStackTrace();
                    setFromSharedPrefs();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.dismissProgressDialog();
                Helpers.logThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.ToastMessage(ProfileEditActivity.this, getString(R.string.error_server));
                } else {
                    helpers.ToastMessage(ProfileEditActivity.this, getString(R.string.error_connection));
                }

            }
        });

    }


}
