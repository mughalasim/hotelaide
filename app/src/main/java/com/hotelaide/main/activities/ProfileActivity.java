package com.hotelaide.main.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.UserInterface;
import com.hotelaide.main.fragments.DocumentsFragment;
import com.hotelaide.main.fragments.ExperienceViewFragment;
import com.hotelaide.main.fragments.ProfileUpdateFragment;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;
import com.hotelaide.utils.SharedPrefs;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.BROADCAST_SET_USER;
import static com.hotelaide.utils.StaticVariables.COUNTY_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.StaticVariables.EXTRA_FAILED;
import static com.hotelaide.utils.StaticVariables.EXTRA_PASSED;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_ADDRESS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_BASIC;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_DOCUMENTS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_PASS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_WORK;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_PROFILE;
import static com.hotelaide.utils.StaticVariables.INT_PERMISSIONS_CAMERA;
import static com.hotelaide.utils.StaticVariables.STR_SHARE_LINK;
import static com.hotelaide.utils.StaticVariables.USER_ABOUT;
import static com.hotelaide.utils.StaticVariables.USER_AVAILABILITY;
import static com.hotelaide.utils.StaticVariables.USER_COUNTRY_CODE;
import static com.hotelaide.utils.StaticVariables.USER_COUNTY;
import static com.hotelaide.utils.StaticVariables.USER_DOB;
import static com.hotelaide.utils.StaticVariables.USER_EMAIL;
import static com.hotelaide.utils.StaticVariables.USER_FULL_ADDRESS;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_GENDER;
import static com.hotelaide.utils.StaticVariables.USER_ID;
import static com.hotelaide.utils.StaticVariables.USER_IMG_AVATAR;
import static com.hotelaide.utils.StaticVariables.USER_IMG_BANNER;
import static com.hotelaide.utils.StaticVariables.USER_L_NAME;
import static com.hotelaide.utils.StaticVariables.USER_PHONE;
import static com.hotelaide.utils.StaticVariables.USER_SKILLS;
import static com.hotelaide.utils.StaticVariables.USER_URL;
import static com.hotelaide.utils.StaticVariables.db;

public class ProfileActivity extends ParentActivity {

    private final String TAG_LOG = "PROFILE VIEW";

    // BANNER AND PROFILE PIC -------------------------
    private ImageView
            img_banner,
            img_avatar;
    private final int
            RESULT_BANNER = 222,
            RESULT_AVATAR = 333;
    private int
            RESULT_EXPECTED = 0;
    final String[] perms = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    // INFO AND CONTACT ------------------------------
    private TextView
            txt_user_f_name,
            txt_user_l_name,
            txt_user_about,
            txt_user_skills,
            txt_user_gender,
            txt_user_age,
            txt_user_dob,
            txt_user_full_address,
            txt_user_county_name,
            txt_user_email,
            txt_user_phone,
            txt_user_availability;
    private ChipGroup chip_group_user_skills;

    private BroadcastReceiver receiver;

    // BACKGROUND ------------------------------------
    private SwipeRefreshLayout swipe_refresh;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        String TAG_LOG = "MY PROFILE";

        initialize(R.id.drawer_profile, getString(R.string.drawer_my_profile_view));

        findAllViews();

        setListeners();

        HelpersAsync.setTrackerPage(TAG_LOG);

    }

    @Override
    public void onResume() {
        super.onResume();
        setTextAndImages();
        setupEducation();
        setupWork();
        setUpDocuments();
        if (receiver == null) {
            listenSetUserBroadcast();
        }
    }

    @Override
    protected void onPause() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        helpers.dialogShare(ProfileActivity.this, STR_SHARE_LINK);
        return super.onOptionsItemSelected(item);
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
                        helpers.toastMessage(getResources().getString(R.string.error_unknown));
                    }
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        helpers.myPermissionsDialog(grantResults);
    }

    // BASIC METHODS ===============================================================================
    private void findAllViews() {
        STR_SHARE_LINK = "Hey! Kindly check out my CV on HotelAide by following this link: " + SharedPrefs.getString(USER_URL);

        swipe_refresh = findViewById(R.id.swipe_refresh);

        // BANNER
        img_avatar = findViewById(R.id.img_avatar);
        img_banner = findViewById(R.id.img_banner);

        // USER DETAILS
        txt_user_f_name = findViewById(R.id.txt_user_f_name);
        txt_user_l_name = findViewById(R.id.txt_user_l_name);
        txt_user_about = findViewById(R.id.txt_user_about);
        txt_user_skills = findViewById(R.id.txt_user_skills);
        chip_group_user_skills = findViewById(R.id.chip_group_user_skills);
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

        helpers.animateSwipeRefresh(swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(false);
                HelpersAsync.asyncGetUser();
            }
        });
    }

    private void setupEducation() {
        Fragment myFrag = new ExperienceViewFragment(null, EXPERIENCE_TYPE_EDUCATION);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.ll_fragment_education, myFrag)
                .commit();
    }

    private void setupWork() {
        Fragment myFrag = new ExperienceViewFragment(null, EXPERIENCE_TYPE_WORK);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.ll_fragment_work, myFrag)
                .commit();
    }

    private void setUpDocuments() {
        Fragment myFrag = new DocumentsFragment(false, null);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.ll_fragment_documents, myFrag)
                .commit();
    }

    private void setTextAndImages() {
        // BANNER IMAGES
        if (SharedPrefs.getString(USER_IMG_AVATAR).equals("")) {
            helpers.setTarget(ProfileActivity.this,
                    findViewById(R.id.img_avatar),
                    "Profile Picture",
                    "Help your employer find you be uploading a profile picture here");
        }
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_AVATAR)).into(img_avatar);
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_BANNER)).into(img_banner);

        // INFO AND CONTACT DETAILS
        txt_user_f_name.setText(SharedPrefs.getString(USER_F_NAME).concat(" "));
        txt_user_l_name.setText(SharedPrefs.getString(USER_L_NAME).concat(" "));

        // ABOUT USER
        if (SharedPrefs.getString(USER_ABOUT).equals("")) {
            txt_user_about.setText("...tell your employer something about yourself...");
            txt_user_about.setTextColor(getResources().getColor(R.color.colorAccent));
            helpers.setTarget(ProfileActivity.this,
                    findViewById(R.id.txt_user_about),
                    "About you",
                    "Your employer needs to know something about you that makes you stand out from the rest");
        } else {
            txt_user_about.setText(SharedPrefs.getString(USER_ABOUT));
            txt_user_about.setTextColor(getResources().getColor(R.color.dark_grey));
        }

        // SKILLS
        if (SharedPrefs.getArrayList(USER_SKILLS).isEmpty()) {
            txt_user_skills.setVisibility(View.VISIBLE);
            chip_group_user_skills.setVisibility(View.GONE);
        } else {
            txt_user_skills.setVisibility(View.GONE);
            chip_group_user_skills.setVisibility(View.VISIBLE);
            chip_group_user_skills.removeAllViews();
            ArrayList<String> list = SharedPrefs.getArrayList(USER_SKILLS);
            int length = list.size();
            for (int i = 0; i < length; i++) {
                Chip chip = new Chip(ProfileActivity.this);
                chip.setText(list.get(i));
                //chip.setCloseIconEnabled(true);
                //chip.setCloseIconResource(R.drawable.your_icon);
                //chip.setChipIconResource(R.drawable.your_icon);
                chip.setChipBackgroundColorResource(R.color.light_grey);
                chip.setTextAppearanceResource(R.style.Text_Small);
                //chip.setElevation(15);
                chip_group_user_skills.addView(chip);
            }
        }

        // GENDER
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

        // DOB
        String user_age = helpers.calculateAge(SharedPrefs.getString(USER_DOB));
        if (user_age.equals("")) {
            txt_user_age.setVisibility(View.GONE);
        } else {
            txt_user_age.setVisibility(View.VISIBLE);
            txt_user_age.setText(user_age);
        }

        txt_user_dob.setText(helpers.formatDate(SharedPrefs.getString(USER_DOB)));

        // FULL ADDRESS
        if (SharedPrefs.getString(USER_FULL_ADDRESS).equals("")) {
            txt_user_full_address.setVisibility(View.GONE);
        } else {
            txt_user_full_address.setText(SharedPrefs.getString(USER_FULL_ADDRESS));
            txt_user_full_address.setVisibility(View.VISIBLE);
        }

        // COUNTRY
        if (SharedPrefs.getInt(USER_COUNTY) > 0) {
            txt_user_county_name.setText(db.getFilterNameByID(COUNTY_TABLE_NAME, SharedPrefs.getInt(USER_COUNTY)));
            txt_user_county_name.setVisibility(View.VISIBLE);
        } else {
            txt_user_county_name.setVisibility(View.GONE);
        }

        // EMAIL
        txt_user_email.setText(SharedPrefs.getString(USER_EMAIL));

        // COUNTRY
        String user_phone = String.valueOf(SharedPrefs.getInt(USER_COUNTRY_CODE)) + " "
                + SharedPrefs.getString(USER_PHONE);

        // PHONE
        txt_user_phone.setText(user_phone);

        // AVAILABILITY
        if (SharedPrefs.getInt(USER_AVAILABILITY) == 1) {
            txt_user_availability.setText("Available");
            txt_user_availability.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.colorPrimary));

        } else {
            txt_user_availability.setText("Hidden");
            txt_user_availability.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.red));
            helpers.setTarget(ProfileActivity.this,
                    findViewById(R.id.txt_user_availability),
                    "Hidden account",
                    "Your employer and other members cannot find you, Inorder to let employers know you are available, kindly update this field");
        }

    }

    public void editProfile(View view) {
        if (view.getId() == R.id.txt_about_me) {
            startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class)
                    .putExtra(EXTRA_PROFILE_BASIC, EXTRA_PROFILE_BASIC));

        } else if (view.getId() == R.id.txt_skills) {
            startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class)
                    .putExtra(EXTRA_PROFILE_BASIC, EXTRA_PROFILE_BASIC));

        } else if (view.getId() == R.id.txt_basic_info) {
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

        } else if (view.getId() == R.id.txt_change_password) {
            startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class)
                    .putExtra(EXTRA_PROFILE_PASS, EXTRA_PROFILE_PASS));

        } else if (view.getId() == R.id.rl_view_as_member) {
            startActivity(new Intent(ProfileActivity.this, MemberProfileActivity.class)
                    .putExtra("MEMBER_ID", SharedPrefs.getInt(USER_ID))
            );

        } else if (view.getId() == R.id.rl_edit_profile_banner) {
            if (EasyPermissions.hasPermissions(ProfileActivity.this, perms)) {
                RESULT_EXPECTED = RESULT_BANNER;
                startImageActivity();
            } else {
                EasyPermissions.requestPermissions(ProfileActivity.this, getString(R.string.rationale_image),
                        INT_PERMISSIONS_CAMERA, perms);
            }

        } else if (view.getId() == R.id.rl_edit_profile_pic) {
            if (EasyPermissions.hasPermissions(ProfileActivity.this, perms)) {
                RESULT_EXPECTED = RESULT_AVATAR;
                startImageActivity();
            } else {
                EasyPermissions.requestPermissions(ProfileActivity.this, getString(R.string.rationale_image),
                        INT_PERMISSIONS_CAMERA, perms);
            }
        }
    }

    private void listenSetUserBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_SET_USER);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    if (intent.getExtras().getString(EXTRA_PASSED) != null) {
                        Helpers.logThis(TAG_LOG, "PASSED");
                        helpers.toastMessage("Update successful");
                        setTextAndImages();
                    } else if (intent.getExtras().getString(EXTRA_FAILED) != null) {
                        Helpers.logThis(TAG_LOG, "FAILED");
                        helpers.toastMessage("Update failed, please try again later");
                    }
                }
            }
        };
        registerReceiver(receiver, filter);
    }

    @AfterPermissionGranted(INT_PERMISSIONS_CAMERA)
    private void startImageActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(ProfileActivity.this);
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
                            helpers.toastMessage("Image updated");

                        } else {
                            helpers.toastMessage(getString(R.string.error_server));
                        }
                    } else {
                        helpers.handleErrorMessage(ProfileActivity.this, main.getJSONObject("data"));
                    }

                    setFromSharedPrefs();

                } catch (JSONException e) {
                    helpers.toastMessage(getString(R.string.error_server));
                    e.printStackTrace();
                    setFromSharedPrefs();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                helpers.dismissProgressDialog();
                Helpers.logThis(TAG_LOG, t.toString());
                if (helpers.validateInternetConnection()) {
                    helpers.toastMessage(getString(R.string.error_server));
                } else {
                    helpers.toastMessage(getString(R.string.error_connection));
                }

            }
        });

    }

    private void setFromSharedPrefs() {
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_AVATAR)).into(img_avatar);
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_BANNER)).into(img_banner);
    }
}
