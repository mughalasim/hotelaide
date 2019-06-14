package com.hotelaide.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.UserInterface;
import com.hotelaide.main.fragments.DashboardFragment;
import com.hotelaide.main.fragments.NewsFeedFragment;
import com.hotelaide.main.models.NotificationModel;
import com.hotelaide.services.BackgroundFetchService;
import com.hotelaide.services.ReminderService;
import com.hotelaide.services.UserIsOnlineService;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.MyApplication;
import com.hotelaide.utils.SharedPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.ALLOW_PUSH_REMINDERS;
import static com.hotelaide.utils.StaticVariables.EXTRA_CONVERSATIONS;
import static com.hotelaide.utils.StaticVariables.EXTRA_NOTIFICATIONS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_BASIC;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_FIRST_TIME;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_RETURN;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_APPLIED;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_INTERVIEWS;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_SAVED;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_SHORTLISTED;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_ID;
import static com.hotelaide.utils.StaticVariables.db;

public class DashboardActivity extends ParentActivity {

    private final String[] fragment_extras = {
            "",
            "https://www.hotelmanagement.net/rss/xml",
            "https://www.hotelmanagement.net/rss/tech/xml",
//            "https://www.hotelmanagement.net/rss/design/xml",
            "https://www.hotelmanagement.net/rss/operate/xml"
    };

    private int[] fragment_title_list = {
            R.string.nav_home,
            R.string.nav_news_feed_latest,
            R.string.nav_news_feed_tech,
//            R.string.nav_news_feed_design,
            R.string.nav_news_feed_operations
    };

    private Fragment[] fragment_list = {
            new DashboardFragment(),
            new NewsFeedFragment(),
            new NewsFeedFragment(),
//            new NewsFeedFragment(),
            new NewsFeedFragment()
    };


    private final String TAG_LOG = "DASH";

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_pager);

        initialize(R.id.drawer_dashboard, getString(R.string.txt_welcome));

        handleExtraBundles();

        setupViewPager(fragment_list, fragment_title_list, fragment_extras);

        if (helpers.validateServiceRunning(BackgroundFetchService.class)) {
            startService(new Intent(DashboardActivity.this, BackgroundFetchService.class));
        }

        if (helpers.validateServiceRunning(ReminderService.class)) {
            if (SharedPrefs.getBool(ALLOW_PUSH_REMINDERS)) {
                startService(new Intent(DashboardActivity.this, ReminderService.class));
            } else {
                stopService(new Intent(DashboardActivity.this, ReminderService.class));
            }
        }

        handleFireBase();

        setUpHomeSearch();

    }

    @Override
    protected void onStart() {
        if (helpers.validateServiceRunning(UserIsOnlineService.class)) {
            startService(new Intent(DashboardActivity.this, UserIsOnlineService.class));
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (!helpers.validateServiceRunning(UserIsOnlineService.class)) {
            stopService(new Intent(DashboardActivity.this, UserIsOnlineService.class));
        }
        super.onDestroy();
    }

    // BASIC FUNCTIONS =============================================================================
    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString(EXTRA_START_FIRST_TIME) != null) {
            if (SharedPrefs.getString(USER_F_NAME).equals("")) {
                startActivity(new Intent(DashboardActivity.this, ProfileEditActivity.class)
                        .putExtra(EXTRA_PROFILE_BASIC, EXTRA_PROFILE_BASIC));

                NotificationModel notificationModel = new NotificationModel();
                notificationModel.title = "Welcome";
                notificationModel.body = "Thank you for joining HotelAide. Complete your profile to help increase your chances of being seen by yur future employer";
                notificationModel.date = "By Management";
                notificationModel.read = 0;
                db.setNotification(notificationModel);

            } else {
                NotificationModel notificationModel = new NotificationModel();
                notificationModel.title = "Welcome";
                notificationModel.body = "Thank you " + SharedPrefs.getString(USER_F_NAME) + ", for joining Hotelaide!";
                notificationModel.date = "By Management";
                notificationModel.read = 0;
                db.setNotification(notificationModel);
            }

            MyApplication.setFirstTimeTutorial(true);

//            setCountOnDrawerItem(menu_profile, "*");
//            setCountOnDrawerItem(menu_find_jobs, "*");
//            setCountOnDrawerItem(menu_my_messages, "*");

        } else if (extras != null && extras.getString(EXTRA_START_RETURN) != null) {
            helpers.toastMessage("Welcome back " + SharedPrefs.getString(USER_F_NAME));

            MyApplication.setFirstTimeTutorial(false);

//            setCountOnDrawerItem(menu_find_jobs, "*");

        }
    }

    public void openMyJobs(View view) {
        if (view.getId() == R.id.ll_applied_jobs) {
            startActivity(new Intent(DashboardActivity.this, MyJobsActivity.class)
                    .putExtra(FILTER_TYPE_APPLIED, FILTER_TYPE_APPLIED));
        } else if (view.getId() == R.id.ll_shortlisted) {
            startActivity(new Intent(DashboardActivity.this, MyJobsActivity.class)
                    .putExtra(FILTER_TYPE_SHORTLISTED, FILTER_TYPE_SHORTLISTED));
        } else if (view.getId() == R.id.ll_saved_jobs) {
            startActivity(new Intent(DashboardActivity.this, MyJobsActivity.class)
                    .putExtra(FILTER_TYPE_SAVED, FILTER_TYPE_SAVED));
        } else if (view.getId() == R.id.ll_interviews) {
            startActivity(new Intent(DashboardActivity.this, MyJobsActivity.class)
                    .putExtra(FILTER_TYPE_INTERVIEWS, FILTER_TYPE_INTERVIEWS));
        } else if (view.getId() == R.id.ll_unread_messages) {
            startActivity(new Intent(DashboardActivity.this, ConversationActivity.class)
                    .putExtra(EXTRA_CONVERSATIONS, EXTRA_CONVERSATIONS));
        }

    }

    public void openProfile(View view) {
        startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
    }

    public void openNotifications(View view) {
        startActivity(new Intent(DashboardActivity.this, ConversationActivity.class)
                .putExtra(EXTRA_NOTIFICATIONS, EXTRA_NOTIFICATIONS));
    }

    private void handleFireBase() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(DashboardActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String refreshedToken = instanceIdResult.getToken();
                        Helpers.logThis(TAG_LOG, refreshedToken);
                        AppEventsLogger.newLogger(DashboardActivity.this, refreshedToken);
                        AppEventsLogger.setPushNotificationsRegistrationId(refreshedToken);

                        UserInterface.retrofit.create(UserInterface.class)
                                .setUserToken(SharedPrefs.getInt(USER_ID), refreshedToken).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull
                                    Response<JsonObject> response) {
                                Helpers.logThis(TAG_LOG, "Successfully updated token");
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                Helpers.logThis(TAG_LOG, "Failed to update token");
                            }

                        });

                    }
                });
    }

}
