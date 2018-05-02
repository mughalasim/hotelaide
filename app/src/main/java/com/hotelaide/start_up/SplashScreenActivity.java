package com.hotelaide.start_up;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;

import bolts.AppLinks;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main_pages.activities.HomeActivity;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.hotelaide.utils_external.FirebaseService;
import io.fabric.sdk.android.Fabric;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.hotelaide.utils.Helpers.STR_NAVIGATION_COLLECTION;
import static com.hotelaide.utils.Helpers.STR_NAVIGATION_REST;
import static com.hotelaide.utils.Helpers.STR_NAVIGATION_SEARCH;

public class SplashScreenActivity extends AppCompatActivity {
    private Database db;
    private final String TAG_LOG = "SPLASH";

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        db = new Database();

        setDataBaseVersion();

        db.getUser();

        SharedPrefs.setToken(Database.userModel.user_token);

        setContentView(R.layout.activity_splash_screen);

        SharedPrefs.setAsyncCallHomePage(true);
        SharedPrefs.setAsyncCallUserDetails(true);

        ShortcutBadger.applyCount(SplashScreenActivity.this, 0);

        handleFireBase();

        // Uncomment Only when SHA Cert needed for Facebook API
        // helper.getShaCertificate();

        handleExtraBundles();

        logFacebookPushNotificationReceived();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDynamicLinks();
    }


    // BASIC FUNCTIONS =============================================================================
    private void logFacebookPushNotificationReceived() {
        Intent startingIntent = this.getIntent();
        Bundle pushData = startingIntent.getBundleExtra("push");
        if (pushData != null) {
            final AppEventsLogger logger = AppEventsLogger.newLogger(this);
            logger.logPushNotificationOpen(pushData, startingIntent.getAction());
        }
    }

    private void setDataBaseVersion() {
        if (SharedPrefs.getOldDataBaseVersion() < BuildConfig.DATABASE_VERSION) {
            Helpers.LogThis(TAG_LOG, "DATABASE UPDATED");
            db.deleteAllTables();
            SharedPrefs.deleteAllSharedPrefs();
            SharedPrefs.setNewDataBaseVersion(BuildConfig.DATABASE_VERSION);
        }
        SharedPrefs.setAllowUpdateApp(true);
    }

    private void handleFireBase() {
        FirebaseService firebaseService = new FirebaseService();
        firebaseService.onTokenRefresh();

        if (FirebaseInstanceId.getInstance().getToken() != null) {
            Helpers.LogThis(TAG_LOG, FirebaseInstanceId.getInstance().getToken());
        }
    }

    private void handleExtraBundles() {
        Helpers.LogThis(TAG_LOG, "Handle Extra Bundles");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String STR_NOTIF_TITLE = extras.getString("notification_title");
            if (STR_NOTIF_TITLE != null && !STR_NOTIF_TITLE.equals("")) {
                String STR_NOTIF_BODY = extras.getString("notification_body");

                final Dialog dialog = new Dialog(SplashScreenActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_confirm);
                dialog.setCancelable(false);
                final TextView txtMessage = dialog.findViewById(R.id.txtMessage);
                final TextView txtOk = dialog.findViewById(R.id.txtOk);
                final TextView txtTitle = dialog.findViewById(R.id.txtTitle);
                txtTitle.setText(STR_NOTIF_TITLE);
                txtMessage.setText(STR_NOTIF_BODY);
                txtOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPrefs.setNavigationPushCLicked(true);
                        startUp();
                        dialog.cancel();
                    }
                });
                dialog.show();

            } else {
                startUp();
            }

        } else {
            startUp();
        }
    }

    private void handleDynamicLinks() {
        try {
            Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
            if (targetUrl != null) {
                Helpers.LogThis(TAG_LOG, "FB DEEP URL: " + targetUrl.toString());
                String[] splits = targetUrl.toString().split("/");

                // fbeatout://collections/restaurant/320
                // fbeatout://collections/collection/125

                switch (splits[2]) {
                    case "restaurant":
                        Helpers.setAppNavigation(STR_NAVIGATION_REST, splits[3], splits[3], splits[3]);
                        break;

                    case "collection":
                        Helpers.setAppNavigation(STR_NAVIGATION_COLLECTION, splits[3], splits[3], splits[3]);
                        break;

                    default:
                        Helpers.setAppNavigation(STR_NAVIGATION_SEARCH, splits[3], splits[3], splits[3]);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Helpers.LogThis(TAG_LOG, e.toString());
        }


        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();
        if (appLinkData != null) {
            String slug = appLinkData.getLastPathSegment();
            String[] path = appLinkData.getPath().split("/");
            Helpers.LogThis(TAG_LOG, "APP LINK: " + slug);

            if (path[1].equals("nairobi")) {
                String STR_REST_ID = db.getRestaurantIDFromSlug(slug);
                Helpers.LogThis(TAG_LOG, STR_REST_ID);
                if (!STR_REST_ID.equals("")) {
                    Helpers.setAppNavigation(STR_NAVIGATION_REST, STR_REST_ID, STR_REST_ID, STR_REST_ID);
                }

            }
        }


        AppLinkData.fetchDeferredAppLinkData(this,
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        // Process app link data
                        if (appLinkData != null) {
                            try {
                                Uri targetUrl = appLinkData.getTargetUri();
                                if (targetUrl != null) {
                                    Helpers.LogThis(TAG_LOG, "FB DEEP URL: " + targetUrl.toString());
                                    String[] splits = targetUrl.toString().split("/");

                                    // fbeatout://collections/restaurant/320
                                    // fbeatout://collections/collection/125

                                    switch (splits[2]) {
                                        case "restaurant":
                                            Helpers.setAppNavigation(STR_NAVIGATION_REST, splits[3], splits[3], splits[3]);
                                            break;

                                        case "collection":
                                            Helpers.setAppNavigation(STR_NAVIGATION_COLLECTION, splits[3], splits[3], splits[3]);
                                            break;

                                        default:
                                            Helpers.setAppNavigation(STR_NAVIGATION_SEARCH, splits[3], splits[3], splits[3]);
                                            break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Helpers.LogThis(TAG_LOG, e.toString());
                            }
                        }
                    }
                }
        );

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(SplashScreenActivity.this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink;

                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            //deep Link has data
                            Helpers.LogThis(TAG_LOG, "DEEP LINK: success, Data: " + deepLink);
                            String URL = deepLink.toString();
                            String DATA = URL.substring(URL.lastIndexOf("/") + 1);
                            Helpers.LogThis(TAG_LOG, "DEEP LINK Where To:" + DATA);

                            if (!db.getRestaurantIDFromSlug(DATA).equals("")) {
                                Helpers.setAppNavigation(STR_NAVIGATION_REST, DATA, DATA, DATA);

                            } else {
                                Helpers.setAppNavigation(STR_NAVIGATION_SEARCH, db.getRestaurantIDFromSlug(DATA), DATA, DATA);
                            }

                        }

                    }
                })
                .addOnFailureListener(SplashScreenActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Helpers.LogThis(TAG_LOG, "DEEP LINK " + e.toString());

                    }
                });
    }

    private void startUp() {
        handleDynamicLinks();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Helpers.LogThis(TAG_LOG, "Start Up");
                if (Database.userModel.user_token.equals("")) {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                } else if (db.validateUserName()) {
                    startActivity(new Intent(SplashScreenActivity.this, SetAccount.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                }
                finish();
            }
        }, 500);
    }

}
