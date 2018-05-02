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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import me.leolin.shortcutbadger.ShortcutBadger;

@SuppressWarnings("unchecked")
public class ParentActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    Helpers helper;

    Database db;

    Toolbar toolbar;

    DrawerLayout drawer;

    NavigationView navigationView;

    private BroadcastReceiver receiver;

    private TextView userName, userEmail, toolbar_text;

    private RoundedImageView userPic;

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
        if(getSupportActionBar()!=null){
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
        final LinearLayout toolbar_image = toolbar.findViewById(R.id.toolbar_image);
        if (toolbarTitle.equals("")) {
            toolbar_image.setVisibility(View.VISIBLE);
            toolbar_text.setVisibility(View.GONE);
        } else {
            toolbar_image.setVisibility(View.GONE);
            toolbar_text.setVisibility(View.VISIBLE);
            toolbar_text.setText(toolbarTitle);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        userName = header.findViewById(R.id.username);
        userEmail = header.findViewById(R.id.userEmail);
        userPic = header.findViewById(R.id.userPic);

    }

    void updateDrawer() {
        MenuItem reservations = navigationView.getMenu().getItem(5);
        reservations.setVisible(false);

        TextView app_version = findViewById(R.id.app_version);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            app_version.setText(getString(R.string.txt_version).concat(pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        userName.setText(Database.userModel.first_name.concat(" ").concat(Database.userModel.last_name));
        Glide.with(this).load(Database.userModel.profile_pic).into(userPic);
        userEmail.setText(Database.userModel.email);
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ParentActivity.this, MyAccountActivity.class));
            }
        });


        navigationView.getMenu().findItem(drawer_id).setChecked(true);
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
        if (SharedPrefs.getAllowUpdateApp()) {
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
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                            final TextView txtMessage = dialog.findViewById(R.id.txtMessage);
                            final TextView txtOk = dialog.findViewById(R.id.txtOk);
                            final TextView txtCancel = dialog.findViewById(R.id.txtCancel);
                            final TextView txtTitle = dialog.findViewById(R.id.txtTitle);
                            txtCancel.setVisibility(View.VISIBLE);
                            txtTitle.setText(R.string.txt_old_version_title);
                            txtMessage.setText(R.string.txt_old_version_desc);

                            txtOk.setText(R.string.txt_update_now);
                            txtOk.setOnClickListener(new View.OnClickListener() {
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

                            txtCancel.setText(R.string.txt_later);
                            txtCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPrefs.setAllowUpdateApp(false);
                                    dialog.cancel();
                                }
                            });
                            dialog.show();

                            ShortcutBadger.applyCount(ParentActivity.this, 1);

                        } else {
                            ShortcutBadger.applyCount(ParentActivity.this, 0);
                            SharedPrefs.setAllowUpdateApp(false);
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
            public void onCancelled(DatabaseError error) {
                Helpers.LogThis(TAG_LOG, "DATABASE:" + error.toString());
            }
        });

    }


    // FIREBASE DATABASE RESTAURANT QUICK LOAD =====================================================
    public static void asyncGetAllRestaurants() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Database");
        final Database db = new Database();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(gson.toJson(dataSnapshot.getValue()));

                    JSONArray jArray = jsonObject.getJSONArray("result");
                    int result_length = jArray.length();

                    Helpers.LogThis("PARENT", "FETCH REST FROM FIREBASE");
                    if (result_length > 0) {
                        for (int i = 0; i < result_length; i++) {
                            // Helpers.LogThis("PARENT", "ONE REST OBJECT: " + jArray.getJSONObject(i).toString());
                            db.setRestaurants(jArray.getJSONObject(i));
                        }
                    }
                    database.goOffline();
                    Helpers.LogThis("PARENT", "OFFLINE FIREBASE DATABASE");


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Helpers.LogThis("PARENT", "DATABASE ERROR: " + error.toString());
            }
        });
    }

}
