package com.hotelaide.main_pages.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.hotelaide.utils.SharedPrefs.ALLOW_UPDATE_APP;
import static com.hotelaide.utils.SharedPrefs.USER_ACCOUNT_TYPE;
import static com.hotelaide.utils.SharedPrefs.USER_EMAIL;
import static com.hotelaide.utils.SharedPrefs.USER_F_NAME;
import static com.hotelaide.utils.SharedPrefs.USER_IMG_AVATAR;
import static com.hotelaide.utils.SharedPrefs.USER_L_NAME;

public class ParentActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    Helpers helper;

    Database db;

    Toolbar toolbar;

    DrawerLayout drawer;

    NavigationView navigationView;

    private BroadcastReceiver receiver;

    private TextView nav_user_name, nav_user_email, toolbar_text;

    private RoundedImageView nav_img_user_pic;

    private int drawer_id;

    private String toolbarTitle;

    private final String TAG_LOG = "PARENT";


    void initialize(int drawer_id, String toolbarTitle) {
        helper = new Helpers(ParentActivity.this);
        db = new Database();
        this.drawer_id = drawer_id;
        this.toolbarTitle = toolbarTitle;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar_text = toolbar.findViewById(R.id.toolbar_text);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        listenExitBroadcast();

        setUpToolBarAndDrawer();

        updateDrawer();

    }

    private void setUpToolBarAndDrawer() {

        toolbar_text.setText(toolbarTitle);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        nav_user_name = header.findViewById(R.id.nav_user_name);
        nav_user_email = header.findViewById(R.id.nav_user_email);
        nav_img_user_pic = header.findViewById(R.id.nav_img_user_pic);

    }

    void updateDrawer() {
        nav_user_name.setText(SharedPrefs.getString(USER_F_NAME).concat(" ").concat(SharedPrefs.getString(USER_L_NAME)));
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_AVATAR)).into(nav_img_user_pic);
        nav_user_email.setText(SharedPrefs.getString(USER_EMAIL));
        nav_img_user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(ParentActivity.this, MyAccountActivity.class));
            }
        });

        navigationView.getMenu().findItem(drawer_id).setChecked(true);

        TextView app_version = findViewById(R.id.app_version);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            app_version.setText(getString(R.string.txt_version).concat(pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        // FIND THE MENU ITEMS ==================================================================
        MenuItem reservations = navigationView.getMenu().getItem(5);
        reservations.setVisible(false);

        helper.asyncGetUser();

        if(SharedPrefs.getString(USER_ACCOUNT_TYPE).equals(BuildConfig.ACCOUNT_TYPE_EMPLOYEER)){

        } else{

        }

    }

    private void listenExitBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Helpers.BroadcastValue);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(receiver, filter);
    }


    // BASIC OVERRIDE METHODS ======================================================================
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        helper.Drawer_Item_Clicked(ParentActivity.this, id);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateDrawer();
        navigationView.getMenu().findItem(drawer_id).setChecked(true);
        if (SharedPrefs.getBool(ALLOW_UPDATE_APP)) {
            getAppVersionFromFirebase();
        }
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        helper.dismissProgressDialog();
        super.onDestroy();
    }

    // FIREBASE DATABASE VERSION CONTROL NOTIFICATION CHECKER ======================================
    private void getAppVersionFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("AppVersion");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(gson.toJson(dataSnapshot.getValue()));
                    Helpers.LogThis(TAG_LOG, "AFTER PARSING: " + jsonObject.toString());

                    if (!jsonObject.isNull("version")) {
                        Long NEW_VERSION_CODE = jsonObject.getLong("version");
                        if (BuildConfig.VERSION_CODE < NEW_VERSION_CODE) {
                            final Dialog dialog = new Dialog(ParentActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_confirm);
                            final TextView txt_message = dialog.findViewById(R.id.txt_message);
                            final TextView btn_confirm = dialog.findViewById(R.id.btn_confirm);
                            final TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
                            final TextView txt_title = dialog.findViewById(R.id.txt_title);
                            btn_cancel.setVisibility(View.VISIBLE);
                            txt_title.setText(R.string.txt_old_version_title);
                            txt_message.setText(R.string.txt_old_version_desc);

                            btn_confirm.setText(R.string.txt_update_now);
                            btn_confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String appPackageName = getPackageName();
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                    dialog.cancel();
                                }
                            });

                            btn_cancel.setText(R.string.txt_later);
                            btn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPrefs.setBool(ALLOW_UPDATE_APP, false);
                                    dialog.cancel();
                                }
                            });
                            dialog.show();

                            ShortcutBadger.applyCount(ParentActivity.this, 1);

                        } else {
                            ShortcutBadger.applyCount(ParentActivity.this, 0);
                            SharedPrefs.setBool(ALLOW_UPDATE_APP, false);
                        }

                        Helpers.LogThis(TAG_LOG, "DATABASE VERSION: " + NEW_VERSION_CODE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Helpers.LogThis(TAG_LOG, "DATABASE:" + error.toString());
            }
        });

    }

}
