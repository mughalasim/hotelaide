package com.hotelaide.startup;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.interfaces.UserInterface;
import com.hotelaide.main.activities.DashboardActivity;
import com.hotelaide.services.MessagingService;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.MyApplication;
import com.hotelaide.utils.SharedPrefs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.StaticVariables.ACCESS_TOKEN;
import static com.hotelaide.utils.StaticVariables.DATABASE_VERSION;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_FIRST_TIME;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_LAUNCH;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_DASH;
import static com.hotelaide.utils.StaticVariables.INT_ANIMATION_TIME;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_ID;

public class SplashScreenActivity extends AppCompatActivity {
    private Database db;
    private final String TAG_LOG = "SPLASH";
    private Helpers helpers;

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.initFireBase();

        db = new Database();

        helpers = new Helpers(SplashScreenActivity.this);

        setDataBaseVersion();

        setContentView(R.layout.activity_splash_screen);

        // Uncomment Only when SHA Cert needed for Facebook API
//        helpers.getShaCertificate();

        startUp();

    }


    // BASIC FUNCTIONS =============================================================================
    private void setDataBaseVersion() {
        if (SharedPrefs.getInt(DATABASE_VERSION) < BuildConfig.DATABASE_VERSION) {
            Helpers.logThis(TAG_LOG, "DATABASE UPDATED");
            db.deleteAllTables();
            SharedPrefs.deleteAllSharedPrefs();
            SharedPrefs.setInt(DATABASE_VERSION, BuildConfig.DATABASE_VERSION);
        }
    }

    private void startUp() {

        // Clear all notification
        NotificationManager notification_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notification_manager != null)
            notification_manager.cancelAll();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Helpers.logThis(TAG_LOG, "Start Up");
                if (SharedPrefs.getString(ACCESS_TOKEN).equals("")) {
                    if (SharedPrefs.getGlobalBool(FIRST_LAUNCH)) {
                        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    } else {
                        SharedPrefs.setGlobalBool(FIRST_LAUNCH, true);
                        startActivity(new Intent(SplashScreenActivity.this, IntroActivity.class));
                    }
                } else {
                    if (helpers.validateServiceRunning(MessagingService.class)) {
                        startService(new Intent(SplashScreenActivity.this, MessagingService.class));
                    }
                    if (SharedPrefs.getString(USER_F_NAME).equals("")) {
                        startActivity(new Intent(SplashScreenActivity.this, DashboardActivity.class)
                                .putExtra(EXTRA_START_FIRST_TIME, EXTRA_START_FIRST_TIME));
                    } else {
                        startActivity(new Intent(SplashScreenActivity.this, DashboardActivity.class)
                                .putExtra(EXTRA_START_LAUNCH, EXTRA_START_LAUNCH));
                    }
                }
                finish();
            }
        }, INT_ANIMATION_TIME);
    }

}
