/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hotelaide.utils_external;

import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.MyApplication;

public class FirebaseService extends FirebaseInstanceIdService {

    private static final String TAG = "FIREBASE";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Helpers.LogThis(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }


    private void sendRegistrationToServer(String token) {
        AppEventsLogger.newLogger(MyApplication.getAppContext(), token);
        AppEventsLogger.setPushNotificationsRegistrationId(token);
    }
}
