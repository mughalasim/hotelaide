/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hotelaide.utils_external;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.notifications.NotificationsManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import com.hotelaide.main_pages.activities.DashboardActivity;
import com.hotelaide.utils.Helpers;

import static com.hotelaide.utils.Helpers.STR_NAVIGATION_COLLECTION;
import static com.hotelaide.utils.Helpers.STR_NAVIGATION_REST;

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = "MESSAGING ";
    private Bundle data;
    private Helpers helper;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        helper = new Helpers(NotificationService.this);
        if (remoteMessage.getData().size() > 0) {

            Helpers.LogThis(TAG, "Message From: " + remoteMessage.getFrom());
            Helpers.LogThis(TAG, "Message Data: " + remoteMessage.getData());
            Helpers.LogThis(TAG, "Message Type: " + remoteMessage.getMessageType());
            Helpers.LogThis(TAG, "Message Notification: " + remoteMessage.getNotification());

            data = new Bundle();
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                data.putString(entry.getKey(), entry.getValue());
            }

            if (NotificationsManager.canPresentCard(data)) {
                Helpers.LogThis(TAG, "IN-APP Notification");
                NotificationsManager.presentNotification(
                        this,
                        data,
                        new Intent(getApplicationContext(), DashboardActivity.class)
                );

            } else {
                Helpers.LogThis(TAG, "PUSH Notification");
                String title = data.getString("title");
                String body = data.getString("body");
                String collection_id;
                String rest_id;
                if (data.getString(STR_NAVIGATION_COLLECTION) != null) {
                    collection_id = data.getString(STR_NAVIGATION_COLLECTION);
                    Helpers.setAppNavigation(STR_NAVIGATION_COLLECTION, collection_id, title, body);

                } else if (data.getString(STR_NAVIGATION_REST) != null) {
                    rest_id = data.getString(STR_NAVIGATION_REST);
                    Helpers.setAppNavigation(STR_NAVIGATION_REST, rest_id, title, body);
                }
                helper.createNotification(NotificationService.this, title, body, data);
            }
        }
    }

}
