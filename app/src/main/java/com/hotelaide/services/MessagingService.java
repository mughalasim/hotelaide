package com.hotelaide.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.hotelaide.BuildConfig;
import com.hotelaide.main.models.MessageModel;
import com.hotelaide.main.models.NotificationModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.MyApplication;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.hotelaide.utils.StaticVariables.ALLOW_PUSH_MESSAGES;
import static com.hotelaide.utils.StaticVariables.APP_IS_RUNNING;
import static com.hotelaide.utils.StaticVariables.CHANNEL_DESC;
import static com.hotelaide.utils.StaticVariables.CHANNEL_ID;
import static com.hotelaide.utils.StaticVariables.CHANNEL_NAME;
import static com.hotelaide.utils.StaticVariables.USER_ID;

public class MessagingService extends Service {

    private static final String TAG_LOG = "MESSAGES";

    private static DatabaseReference child_ref;

    private static ChildEventListener childEventListener;

    Helpers helpers;


    // OVERRIDE METHODS ============================================================================
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
        Helpers.logThis(TAG_LOG, "ON_CREATE");
        MyApplication.initFireBase();
        helpers = new Helpers(MessagingService.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Get the reference to the DB
        child_ref = database.getReference().child(BuildConfig.USERS_URL + SharedPrefs.getInt(USER_ID) + BuildConfig.MESSAGE_URL);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Helpers.logThis(TAG_LOG, "ALLOW PUSH: " + SharedPrefs.getBool(ALLOW_PUSH_MESSAGES));
                Helpers.logThis(TAG_LOG, "APP RUNNING: " + SharedPrefs.getBool(APP_IS_RUNNING));

                if (SharedPrefs.getBool(ALLOW_PUSH_MESSAGES) && !SharedPrefs.getBool(APP_IS_RUNNING)) {
                    Helpers.logThis(TAG_LOG, "FB DB CHILD ADDED");
                    setDataSnapshotFromObject(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Helpers.logThis(TAG_LOG, "ALLOW PUSH: " + SharedPrefs.getBool(ALLOW_PUSH_MESSAGES));
                Helpers.logThis(TAG_LOG, "APP RUNNING: " + SharedPrefs.getBool(APP_IS_RUNNING));

                if (SharedPrefs.getBool(ALLOW_PUSH_MESSAGES) && !SharedPrefs.getBool(APP_IS_RUNNING)) {
                    Helpers.logThis(TAG_LOG, "FB DB CHILD CHANGED");
                    setDataSnapshotFromObject(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        child_ref.addChildEventListener(childEventListener);

    }

    public static void stopListeningForMessages() {
        if (childEventListener != null)
            child_ref.removeEventListener(childEventListener);
    }

    @Override
    public void onDestroy() {
        Helpers.logThis(TAG_LOG, "ON_DESTROY");
        super.onDestroy();
    }


    // BASIC METHODS ===============================================================================
    private void setDataSnapshotFromObject(DataSnapshot dataSnapshot) {
        try {
            Gson gson = new Gson();
            JSONObject message_object = new JSONObject(gson.toJson(dataSnapshot.getValue()));

            Helpers.logThis(TAG_LOG, message_object.toString());

            if (!message_object.isNull("unread_messages") && message_object.getInt("unread_messages") > 0) {
                Helpers.logThis(TAG_LOG, "UNREAD COUNT: " + message_object.getInt("unread_messages"));
                MessageModel messageModel = new MessageModel();
                messageModel.last_message = message_object.getString("last_message");
                messageModel.unread_messages = message_object.getInt("unread_messages");
                JSONArray user_array = message_object.getJSONArray("users");
                int length = user_array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject user_object = user_array.getJSONObject(i);
                    if (user_object.getInt("id") != SharedPrefs.getInt(USER_ID)) {
                        messageModel.from_id = user_object.getInt("id");
                        messageModel.from_name = user_object.getString("name");
                        messageModel.from_pic_url = user_object.getString("pic_url");
                    }
                }
                if (SharedPrefs.getBool(ALLOW_PUSH_MESSAGES)) {

                    Helpers.logThis(TAG_LOG, "CREATE NOTIFICATION: " + messageModel.from_name + " : " + messageModel.last_message);

                    CHANNEL_ID = String.valueOf(messageModel.from_id);
                    CHANNEL_NAME = messageModel.from_name;
                    CHANNEL_DESC = messageModel.last_message;

                    NotificationModel notification_model = new NotificationModel();
                    notification_model.table_id = messageModel.from_id;
                    notification_model.job_id = messageModel.from_id;
                    notification_model.title = messageModel.from_name;
                    notification_model.preview = messageModel.last_message;

                    Helpers.createNotification(MessagingService.this, notification_model);
                }
            }

        } catch (JSONException e) {
            Helpers.logThis(TAG_LOG, e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Helpers.logThis(TAG_LOG, e.toString());
            e.printStackTrace();
        }

    }

}
