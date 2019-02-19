package com.hotelaide.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.ALLOW_PUSH_MESSAGES;
import static com.hotelaide.utils.StaticVariables.ALLOW_PUSH_NOTIFICATIONS;
import static com.hotelaide.utils.StaticVariables.ALLOW_UPDATE_APP;
import static com.hotelaide.utils.StaticVariables.EXTRA_MY_MESSAGES_INBOX;
import static com.hotelaide.utils.StaticVariables.EXTRA_MY_MESSAGES_NOTIFICATIONS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_BASIC;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_FIRST_TIME;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_RETURN;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_APPLIED;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_SAVED;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_ID;

public class DashboardActivity extends ParentActivity {

    private final String[] fragment_extras = {
            "",
            "https://www.hotelmanagement.net/rss/xml",
            "https://www.hotelmanagement.net/rss/tech/xml",
            "https://www.hotelmanagement.net/rss/design/xml",
            "https://www.hotelmanagement.net/rss/operate/xml"
    };

    private int[] fragment_title_list = {
            R.string.nav_home,
            R.string.nav_news_feed_latest,
            R.string.nav_news_feed_tech,
            R.string.nav_news_feed_design,
            R.string.nav_news_feed_operations
    };

    private Fragment[] fragment_list = {
            new DashboardFragment(),
            new NewsFeedFragment(),
            new NewsFeedFragment(),
            new NewsFeedFragment(),
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

        handleFireBase();

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
                notificationModel.body = "Thank you for joining Hotelaide!";
                notificationModel.date = "By Management";
                notificationModel.read = 0;
                db.setNotification(notificationModel);
            }

            SharedPrefs.setBool(ALLOW_UPDATE_APP, true);
            SharedPrefs.setBool(ALLOW_PUSH_MESSAGES, true);
            SharedPrefs.setBool(ALLOW_PUSH_NOTIFICATIONS, true);
            setCountOnDrawerItem(menu_profile, "*");

        } else if (extras != null && extras.getString(EXTRA_START_RETURN) != null) {
            helpers.ToastMessage(DashboardActivity.this, "Welcome back " + SharedPrefs.getString(USER_F_NAME));
            setCountOnDrawerItem(menu_find_jobs, "*");
            setCountOnDrawerItem(menu_my_messages, "*");

            NotificationModel notificationModel = new NotificationModel();
            notificationModel.title = "Welcome Back";
            notificationModel.body = SharedPrefs.getString(USER_F_NAME) + " it's great to have you back";
            notificationModel.date = "By Management";
            notificationModel.read = 0;
            db.setNotification(notificationModel);

            SharedPrefs.setBool(ALLOW_UPDATE_APP, true);
            SharedPrefs.setBool(ALLOW_PUSH_MESSAGES, true);
            SharedPrefs.setBool(ALLOW_PUSH_NOTIFICATIONS, true);

        }
    }

    public void openMyJobs(View view) {
        if (view.getId() == R.id.ll_applied) {
            startActivity(new Intent(DashboardActivity.this, MyJobsActivity.class)
                    .putExtra(FILTER_TYPE_APPLIED, FILTER_TYPE_APPLIED));
        } else if (view.getId() == R.id.ll_saved) {
            startActivity(new Intent(DashboardActivity.this, MyJobsActivity.class)
                    .putExtra(FILTER_TYPE_SAVED, FILTER_TYPE_SAVED));
        }

    }

    public void openProfile(View view) {
        startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
    }

    public void openNotifications(View view) {
        startActivity(new Intent(DashboardActivity.this, MyMessages.class)
                .putExtra(EXTRA_MY_MESSAGES_NOTIFICATIONS, EXTRA_MY_MESSAGES_NOTIFICATIONS));
    }

    public void openMessages(View view) {
        startActivity(new Intent(DashboardActivity.this, MyMessages.class)
                .putExtra(EXTRA_MY_MESSAGES_INBOX, EXTRA_MY_MESSAGES_INBOX));
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

                UserInterface userInterface = UserInterface.retrofit.create(UserInterface.class);
                final Call<JsonObject> call = userInterface.setUserToken(SharedPrefs.getInt(USER_ID),
                        refreshedToken);
                call.enqueue(new Callback<JsonObject>() {
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
