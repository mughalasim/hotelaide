package com.hotelaide.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.hotelaide.main.models.ConversationModel;
import com.hotelaide.utils.FBDatabase;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.MyApplication;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import static com.hotelaide.BuildConfig.URL_LAST;
import static com.hotelaide.BuildConfig.URL_UNREAD;
import static com.hotelaide.utils.StaticVariables.ALLOW_PUSH_MESSAGES;

public class ConversationService extends Service {

    private static final String TAG_LOG = "MESSAGES";

    private static DatabaseReference child_ref;

    private static ChildEventListener childEventListener;



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
        // Get the reference to the DB
        child_ref = FBDatabase.getURLConversation();

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Helpers.logThis(TAG_LOG, "ALLOW PUSH: " + SharedPrefs.getBool(ALLOW_PUSH_MESSAGES));
//                Helpers.logThis(TAG_LOG, "CONVERSATION IS RUNNING: " + CONVERSATION_IS_RUNNING);

                if (SharedPrefs.getBool(ALLOW_PUSH_MESSAGES)) {
                    Helpers.logThis(TAG_LOG, "FB DB CHILD ADDED");
                    setDataSnapshotFromObject(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Helpers.logThis(TAG_LOG, "ALLOW PUSH: " + SharedPrefs.getBool(ALLOW_PUSH_MESSAGES));
//                Helpers.logThis(TAG_LOG, "CONVERSATION IS RUNNING: " + CONVERSATION_IS_RUNNING);

                if (SharedPrefs.getBool(ALLOW_PUSH_MESSAGES)) {
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

            if (!message_object.isNull(URL_UNREAD) && message_object.getInt(URL_UNREAD) > 0) {
                Helpers.logThis(TAG_LOG, "UNREAD COUNT: " + message_object.getInt(URL_UNREAD));
                ConversationModel conversationModel = new ConversationModel();
                conversationModel.last_message = message_object.getString(URL_LAST);
                conversationModel.unread_messages = message_object.getInt(URL_UNREAD);
//                JSONArray user_array = message_object.getJSONArray("users");
//                int length = user_array.length();
//                for (int i = 0; i < length; i++) {
//                    JSONObject user_object = user_array.getJSONObject(i);
//                    if (user_object.getInt("id") != SharedPrefs.getInt(USER_ID)) {
//                        conversationModel.from_id = user_object.getInt("id");
//                        conversationModel.from_name = user_object.getString("name");
//                        conversationModel.from_pic_url = user_object.getString("pic_url");
//                    }
//                }

//                if (SharedPrefs.getBool(ALLOW_PUSH_MESSAGES)) {
//
//                    Helpers.logThis(TAG_LOG, "CREATE NOTIFICATION: " + conversationModel.from_name + " : " + conversationModel.last_message);
//
//                    CHANNEL_ID = String.valueOf(conversationModel.from_id);
//                    CHANNEL_NAME = conversationModel.from_name;
//                    CHANNEL_DESC = conversationModel.last_message;
//
//                    NotificationModel notification_model = new NotificationModel();
//                    notification_model.table_id = conversationModel.from_id;
//                    notification_model.job_id = conversationModel.from_id;
//                    notification_model.title = conversationModel.from_name;
//                    notification_model.preview = conversationModel.last_message;
//
//                    Helpers.createNotification(ConversationService.this, notification_model);
//                }
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
