package com.hotelaide.startup;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main.activities.DashboardActivity;
import com.hotelaide.services.MessagingService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.MyApplication;
import com.hotelaide.utils.SharedPrefs;

import io.fabric.sdk.android.Fabric;

import static com.hotelaide.utils.StaticVariables.ACCESS_TOKEN;
import static com.hotelaide.utils.StaticVariables.DATABASE_VERSION;
import static com.hotelaide.utils.StaticVariables.INT_ANIMATION_TIME;
import static com.hotelaide.utils.StaticVariables.START_LAUNCH;


public class SplashScreenActivity extends AppCompatActivity {
    private Database db;
    private final String TAG_LOG = "SPLASH";
    private Helpers helpers;

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.initFireBase();

        Fabric.with(this, new Crashlytics());

        db = new Database();

        helpers = new Helpers(SplashScreenActivity.this);

        setDataBaseVersion();

        setContentView(R.layout.activity_splash_screen);

        handleFireBase();

        // Uncomment Only when SHA Cert needed for Facebook API
        // helpers.getShaCertificate();

        startUp();

    }


    // BASIC FUNCTIONS =============================================================================
    private void setDataBaseVersion() {
        if (SharedPrefs.getInt(DATABASE_VERSION) < BuildConfig.DATABASE_VERSION) {
            Helpers.LogThis(TAG_LOG, "DATABASE UPDATED");
            db.deleteAllTables();
            SharedPrefs.deleteAllSharedPrefs();
            SharedPrefs.setInt(DATABASE_VERSION, BuildConfig.DATABASE_VERSION);
        }
    }

    private void handleFireBase() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashScreenActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String refreshedToken = instanceIdResult.getToken();
                        AppEventsLogger.newLogger(SplashScreenActivity.this, refreshedToken);
                        AppEventsLogger.setPushNotificationsRegistrationId(refreshedToken);
                    }
                });
    }

    private void startUp() {
        if (helpers.validateServiceRunning(MessagingService.class)) {
            startService(new Intent(SplashScreenActivity.this, MessagingService.class));
        }

        // Clear all notification
        NotificationManager notification_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notification_manager != null)
            notification_manager.cancelAll();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Helpers.LogThis(TAG_LOG, "Start Up");
                if (SharedPrefs.getString(ACCESS_TOKEN).equals("")) {
                    startActivity(new Intent(SplashScreenActivity.this, IntroActivity.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, DashboardActivity.class).putExtra(START_LAUNCH, START_LAUNCH));
                }
                finish();
            }
        }, INT_ANIMATION_TIME);
    }

}
