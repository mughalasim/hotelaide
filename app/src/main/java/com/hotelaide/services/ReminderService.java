package com.hotelaide.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hotelaide.main.models.NotificationModel;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.MyApplication;
import com.hotelaide.utils.SharedPrefs;
import com.hotelaide.utils.StaticVariables;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.hotelaide.utils.StaticVariables.NOTIFICATION_TYPE_CODE_MESSAGE;
import static com.hotelaide.utils.StaticVariables.USER_ABOUT;
import static com.hotelaide.utils.StaticVariables.USER_FULL_ADDRESS;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_IMG_AVATAR;
import static com.hotelaide.utils.StaticVariables.USER_PHONE;

public class ReminderService extends Service {
    private static final String TAG_LOG = "REMINDER SERVICE";
//    private static final int TIME =  48 * 60 * 60 * 1000;
    private static final int TIME =  30 * 1000;


    // BASIC OVERRIDE METHODS ======================================================================
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Helpers.logThis(TAG_LOG, "ON_START");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (SharedPrefs.getBool(StaticVariables.ALLOW_PUSH_REMINDERS)) {

                    NotificationModel notification_model = new NotificationModel();

                    notification_model.job_id = 0;
                    notification_model.read = 0;
                    notification_model.type_code = NOTIFICATION_TYPE_CODE_MESSAGE;
                    notification_model.title = SharedPrefs.getString(USER_F_NAME);
                    notification_model.preview = "Your profile seems incomplete";

                    if (SharedPrefs.getString(USER_IMG_AVATAR).equals("")) {
                        notification_model.body = "Add a profile picture so that your employer can easily find you";
                    } else if (SharedPrefs.getString(USER_FULL_ADDRESS).equals("")) {
                        notification_model.body = "Add an address so that your employer knows where you are located";
                    } else if (SharedPrefs.getString(USER_ABOUT).equals("")) {
                        notification_model.body = "Add a small description about yourself so that you stand out from the rest";
                    } else if (SharedPrefs.getString(USER_PHONE).equals("")) {
                        notification_model.body = "Add a contact number so that your employer knows how to get in touch with you";
                    }

                    Date date = new Date();
                    @SuppressLint("SimpleDateFormat")
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy (hh:mm a)");
                    notification_model.date = dateFormat.format(date);

                    if (!notification_model.body.equals("")) {
                        Database db = new Database();
                        db.setNotification(notification_model);
                        Helpers.createNotification(MyApplication.getAppContext(), notification_model);
                    }
                }
            }
        }, TIME, TIME);
    }

    @Override
    public void onDestroy() {
        Helpers.logThis(TAG_LOG, "ON_DESTROY");
        super.onDestroy();
    }


}
