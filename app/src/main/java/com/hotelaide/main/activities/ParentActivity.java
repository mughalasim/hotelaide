package com.hotelaide.main.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.services.UserIsOnlineService;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;
import com.hotelaide.utils.SharedPrefs;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.hotelaide.utils.MyApplication.fb_parent_ref;
import static com.hotelaide.utils.StaticVariables.ALLOW_UPDATE_APP;
import static com.hotelaide.utils.StaticVariables.BROADCAST_LOG_OUT;
import static com.hotelaide.utils.StaticVariables.EXTRA_STRING;
import static com.hotelaide.utils.StaticVariables.USER_EMAIL;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_IMG_AVATAR;
import static com.hotelaide.utils.StaticVariables.USER_IMG_BANNER;
import static com.hotelaide.utils.StaticVariables.USER_L_NAME;

public class ParentActivity extends FragmentActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    Helpers helpers;

    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigation_view;
    private BroadcastReceiver receiver;
    private TextView nav_user_name, nav_user_email;
    public TextView
            menu_dashboard,
            menu_find_jobs,
            menu_my_jobs,
            menu_my_messages,
            menu_find_members,
            menu_profile,
            menu_about_us,
            menu_settings,
            menu_log_out,
            toolbar_text;
    private RoundedImageView nav_img_user_pic;
    private ImageView nav_user_banner;
    private int drawer_id;
    private String toolbar_title;
    private final String TAG_LOG = "PARENT";
    private final int INT_NAV_DRAWER_DELAY = 120;
    private int INT_NAV_DRAWER_UPDATE_COUNTER = 0;

    public AppCompatImageView img_search;
    public EditText et_search;

    public TabLayout tab_layout;
    public ViewPager view_pager;

    void initialize(int drawer_id, String toolbarTitle) {
        helpers = new Helpers(ParentActivity.this);
        this.drawer_id = drawer_id;
        this.toolbar_title = toolbarTitle;

        toolbar = findViewById(R.id.toolbar);
        toolbar_text = findViewById(R.id.toolbar_text);
        drawer = findViewById(R.id.drawer_layout);
        navigation_view = findViewById(R.id.nav_view);

        listenExitBroadcast();

        setUpToolBarAndDrawer();

        updateDrawer();

        HelpersAsync.setTrackerPage(toolbarTitle);

    }

    private void setUpToolBarAndDrawer() {
        toolbar_text.setText(toolbar_title);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigation_view.setNavigationItemSelectedListener(this);
        View header = navigation_view.getHeaderView(0);
        nav_user_name = header.findViewById(R.id.nav_user_name);
        nav_user_email = header.findViewById(R.id.nav_user_email);
        nav_img_user_pic = header.findViewById(R.id.nav_img_user_pic);
        nav_user_banner = header.findViewById(R.id.nav_user_banner);

        navigation_view.getMenu().findItem(drawer_id).setChecked(true);

        // FIND THE MENU ITEMS =====================================================================
        menu_dashboard = (TextView) navigation_view.getMenu().getItem(0).getActionView();
        menu_find_jobs = (TextView) navigation_view.getMenu().getItem(1).getActionView();
        menu_my_jobs = (TextView) navigation_view.getMenu().getItem(2).getActionView();
        menu_my_messages = (TextView) navigation_view.getMenu().getItem(3).getActionView();
        menu_find_members = (TextView) navigation_view.getMenu().getItem(4).getActionView();
        menu_profile = (TextView) navigation_view.getMenu().getItem(5).getActionView();
        menu_about_us = (TextView) navigation_view.getMenu().getItem(6).getActionView();
        menu_settings = (TextView) navigation_view.getMenu().getItem(7).getActionView();
        menu_log_out = (TextView) navigation_view.getMenu().getItem(8).getActionView();

    }

    void updateDrawer() {
        nav_user_name.setText(SharedPrefs.getString(USER_F_NAME).concat(" ").concat(SharedPrefs.getString(USER_L_NAME)));
        Glide.with(this)
                .load(SharedPrefs.getString(USER_IMG_AVATAR))
                .placeholder(R.drawable.ic_profile)
                .into(nav_img_user_pic);

        Glide.with(this)
                .load(SharedPrefs.getString(USER_IMG_BANNER))
                .into(nav_user_banner);

        nav_user_email.setText(SharedPrefs.getString(USER_EMAIL));
        nav_img_user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                drawer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        helpers.drawerItemClicked(R.id.drawer_profile);
                    }
                }, INT_NAV_DRAWER_DELAY);
            }
        });

        nav_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                drawer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        helpers.drawerItemClicked(R.id.drawer_profile);
                    }
                }, INT_NAV_DRAWER_DELAY);
            }
        });

    }

    void setCountOnDrawerItem(TextView drawer_text_view, String count) {
        drawer_text_view.setGravity(Gravity.CENTER_VERTICAL);
        drawer_text_view.setTypeface(null, Typeface.BOLD);
        drawer_text_view.setTextColor(ContextCompat.getColor(this, R.color.white));
        drawer_text_view.setText(count);
    }

    private void deleteCountOnTextView(int drawer_id) {
        switch (drawer_id) {
            case R.id.drawer_dashboard:
                menu_dashboard.setText("");
                break;

            case R.id.drawer_find_jobs:
                menu_find_jobs.setText("");
                break;

            case R.id.drawer_my_jobs:
                menu_my_jobs.setText("");
                break;

            case R.id.drawer_find_members:
                menu_find_members.setText("");
                break;

            case R.id.drawer_profile:
                menu_profile.setText("");
                break;

            case R.id.drawer_about_us:
                menu_about_us.setText("");
                break;

            case R.id.drawer_settings:
                menu_settings.setText("");
                break;

            case R.id.drawer_log_out:
                menu_log_out.setText("");
                break;
        }
    }

    private void listenExitBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_LOG_OUT);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(receiver, filter);
    }

    public void setUpHomeSearch() {
        img_search = findViewById(R.id.img_search);
        et_search = findViewById(R.id.et_search);

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_search.getText().toString().length() > 1) {
                    startActivity(new Intent(ParentActivity.this, FindJobsActivity.class)
                            .putExtra(EXTRA_STRING, et_search.getText().toString()));
                    et_search.setText("");
                } else {
                    helpers.toastMessage("Nothing typed in the search field");
                }
            }
        });
    }


    // VIEW PAGER FUNCTIONS ========================================================================
    public void setupViewPager(Fragment[] fragment_list, int[] fragment_title_list, String[] fragment_extras) {
        view_pager = findViewById(R.id.view_pager);
        tab_layout = findViewById(R.id.tabs);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < fragment_title_list.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_STRING, fragment_extras[i]);

            Fragment fragment = fragment_list[i];
            fragment.setArguments(bundle);
            adapter.addFragment(fragment, getString(fragment_title_list[i]));
        }

        view_pager.setAdapter(adapter);

        tab_layout.setupWithViewPager(view_pager, true);

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragment_list = new ArrayList<>();
        private final List<String> fragment_titles = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragment_list.get(position);
        }

        @Override
        public int getCount() {
            return fragment_list.size();
        }

        void addFragment(Fragment fragment, String title) {
            fragment_list.add(fragment);
            fragment_titles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragment_titles.get(position);
        }

    }

    // BASIC OVERRIDE METHODS ======================================================================

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();
        deleteCountOnTextView(id);
        drawer.closeDrawer(GravityCompat.START);
        drawer.postDelayed(new Runnable() {
            @Override
            public void run() {
                helpers.drawerItemClicked(id);
            }
        }, INT_NAV_DRAWER_DELAY);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (this.isTaskRoot()) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_confirm);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            final TextView txt_message = dialog.findViewById(R.id.txt_message);
            final TextView btn_confirm = dialog.findViewById(R.id.btn_confirm);
            final TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
            final TextView txt_title = dialog.findViewById(R.id.txt_title);
            txt_title.setText(getString(R.string.txt_exit).concat(new String(Character.toChars(0x1F625))));
            txt_message.setText(getString(R.string.txt_exit_desc));
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    if(!helpers.validateServiceRunning(UserIsOnlineService.class)){
                        stopService(new Intent(ParentActivity.this, UserIsOnlineService.class));
                    }
                    finish();
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            dialog.show();
        } else {
            finish();
        }
    }

    @Override
    public void onResume() {
        updateDrawer();
        navigation_view.getMenu().findItem(drawer_id).setChecked(true);
        if (SharedPrefs.getBool(ALLOW_UPDATE_APP) && INT_NAV_DRAWER_UPDATE_COUNTER == 0) {
            getAppVersionFromFireBase();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        helpers.dismissProgressDialog();
        super.onDestroy();
    }

    // FIREBASE DATABASE VERSION CONTROL NOTIFICATION CHECKER ======================================
    private void getAppVersionFromFireBase() {
        DatabaseReference parent_ref = fb_parent_ref.child("AppVersion");
        INT_NAV_DRAWER_UPDATE_COUNTER = 1;
        parent_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(gson.toJson(dataSnapshot.getValue()));
//                    Helpers.logThis(TAG_LOG, "AFTER PARSING: " + jsonObject.toString());

                    if (!jsonObject.isNull("version")) {
                        long NEW_VERSION_CODE = jsonObject.getLong("version");
                        if (BuildConfig.VERSION_CODE < NEW_VERSION_CODE) {
                            final Dialog dialog = new Dialog(ParentActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_confirm);
                            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            final TextView txt_message = dialog.findViewById(R.id.txt_message);
                            final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
                            final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
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
                                    dialog.cancel();
                                }
                            });
                            dialog.show();

                            ShortcutBadger.applyCount(ParentActivity.this, 1);

                        } else {
                            ShortcutBadger.applyCount(ParentActivity.this, 0);
                        }
//                        Helpers.logThis(TAG_LOG, "DATABASE VERSION: " + NEW_VERSION_CODE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Helpers.logThis(TAG_LOG, "DATABASE:" + databaseError.toString());

            }
        });

    }

}
