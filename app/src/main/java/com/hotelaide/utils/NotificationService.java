package com.hotelaide.utils;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hotelaide.main.models.NotificationModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.hotelaide.utils.StaticVariables.NOTIFICATION_BODY;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_JOB_ID;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_PREVIEW;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_TITLE;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_TYPE_CODE;

public class NotificationService extends FirebaseMessagingService {

    private final String TAG_LOG = "MESSAGING";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Helpers.logThis("NEW_TOKEN", token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            Helpers.logThis(TAG_LOG, "Data: " + remoteMessage.getData());
            Bundle data = new Bundle();
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                data.putString(entry.getKey(), entry.getValue());
            }
            if (SharedPrefs.getBool(StaticVariables.ALLOW_PUSH_NOTIFICATIONS)) {

                NotificationModel notification_model = new NotificationModel();

                notification_model.title = data.getString(NOTIFICATION_TITLE);
                notification_model.preview = data.getString(NOTIFICATION_PREVIEW);
                notification_model.body = data.getString(NOTIFICATION_BODY);
                notification_model.type_code = data.getInt(NOTIFICATION_TYPE_CODE);
                notification_model.job_id = data.getInt(NOTIFICATION_JOB_ID);
                notification_model.read = 0;

                Date date = new Date();
                @SuppressLint("SimpleDateFormat")
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy (hh:mm a)");
                notification_model.date = dateFormat.format(date);

                Database db = new Database();
                db.setNotification(notification_model);

                Helpers.createNotification(NotificationService.this, notification_model);

            }
        }

    }
}
