package com.hotelaide.startup;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main.activities.DashboardActivity;
import com.hotelaide.services.ConversationService;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.MyApplication;
import com.hotelaide.utils.SharedPrefs;

import static com.hotelaide.utils.StaticVariables.ACCESS_TOKEN;
import static com.hotelaide.utils.StaticVariables.DATABASE_VERSION;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_FIRST_TIME;
import static com.hotelaide.utils.StaticVariables.EXTRA_START_LAUNCH;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH;
import static com.hotelaide.utils.StaticVariables.INT_ANIMATION_TIME;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.db;

public class SplashScreenActivity extends AppCompatActivity {

    private final String TAG_LOG = "SPLASH";
    private Helpers helpers;

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.initFireBase();

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
                        startActivity(new Intent(SplashScreenActivity.this, IntroActivity.class));
                    }
                } else {
                    if (helpers.validateServiceRunning(ConversationService.class)) {
                        startService(new Intent(SplashScreenActivity.this, ConversationService.class));
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
