package com.hotelaide.start_up;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main_pages.activities.DashboardActivity;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.hotelaide.utils_external.FirebaseService;

import io.fabric.sdk.android.Fabric;

import static com.hotelaide.utils.Helpers.START_LAUNCH;
import static com.hotelaide.utils.SharedPrefs.ACCESS_TOKEN;
import static com.hotelaide.utils.SharedPrefs.ALLOW_UPDATE_APP;
import static com.hotelaide.utils.SharedPrefs.DATABASE_VERSION;


public class SplashScreenActivity extends AppCompatActivity {
    private Database db;
    private final String TAG_LOG = "SPLASH";
    private Helpers helpers;

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        db = new Database();
        helpers = new Helpers(SplashScreenActivity.this);

        setDataBaseVersion();

        setContentView(R.layout.activity_splash_screen);

        handleFireBase();

        // Uncomment Only when SHA Cert needed for Facebook API
         helpers.getShaCertificate();

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
        SharedPrefs.setBool(ALLOW_UPDATE_APP, true);
    }

    private void handleFireBase() {
        FirebaseService firebaseService = new FirebaseService();
        firebaseService.onTokenRefresh();

        if (FirebaseInstanceId.getInstance().getToken() != null) {
            Helpers.LogThis(TAG_LOG, FirebaseInstanceId.getInstance().getToken());
        }
    }

    private void startUp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Helpers.LogThis(TAG_LOG, "Start Up");
                if (SharedPrefs.getString(ACCESS_TOKEN).equals("")) {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, DashboardActivity.class).putExtra(START_LAUNCH, START_LAUNCH));
                }
                finish();
            }
        }, 2000);
    }

}
