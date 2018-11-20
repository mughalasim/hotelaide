package com.hotelaide.utils;

import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class NotificationService extends FirebaseMessagingService {

    private final String TAG_LOG = "MESSAGING";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Helpers.LogThis("NEW_TOKEN",s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String notification_title = "", notification_message = "", notification_content = "";

        if (remoteMessage.getData().size() > 0) {
            Helpers.LogThis(TAG_LOG, "Data: " + remoteMessage.getData());
            Bundle data = new Bundle();
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                data.putString(entry.getKey(), entry.getValue());
            }
            if (SharedPrefs.getBool(StaticVariables.ALLOW_NOTIFICATIONS)) {

                notification_title = data.getString("title");
                notification_message = data.getString("message");
                notification_content = data.getString("content");
                String target_id;
//                if (data.getString(STR_NAVIGATION_COLLECTION) != null) {
//                    target_id = data.getString(STR_NAVIGATION_COLLECTION);
//                    Helpers.setAppNavigation(STR_NAVIGATION_COLLECTION, target_id, notification_title, notification_message);
//                } else if (data.getString(STR_NAVIGATION_REST) != null) {
//                    target_id = data.getString(STR_NAVIGATION_REST);
//                    Helpers.setAppNavigation(STR_NAVIGATION_REST, target_id, notification_title, notification_message);
//                }

//                Helpers.createNotification(
//                        NotificationService.this,
//                        notification_title,
//                        notification_message,
//                        notification_content);
            }
        }

    }
}
