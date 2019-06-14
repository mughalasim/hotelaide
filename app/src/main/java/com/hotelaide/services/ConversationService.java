package com.hotelaide.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hotelaide.main.models.NotificationModel;
import com.hotelaide.utils.FBDatabase;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.MyApplication;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import static com.hotelaide.BuildConfig.URL_LAST;
import static com.hotelaide.BuildConfig.URL_UNREAD;
import static com.hotelaide.utils.StaticVariables.ALLOW_PUSH_MESSAGES;
import static com.hotelaide.utils.StaticVariables.CHANNEL_DESC;
import static com.hotelaide.utils.StaticVariables.CHANNEL_ID;
import static com.hotelaide.utils.StaticVariables.CHANNEL_NAME;

public class ConversationService extends Service {

    private static final String TAG_LOG = "CONVERSATION_SERVICE";

    private static DatabaseReference child_ref;

//    private static ChildEventListener childEventListener;
    private static ValueEventListener valueEventListener;


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

//        childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Helpers.logThis(TAG_LOG, "ALLOW PUSH: " + SharedPrefs.getBool(ALLOW_PUSH_MESSAGES));
//                Helpers.logThis(TAG_LOG, "FB DB CHILD ADDED");
//                setDataSnapshotFromObject(dataSnapshot);
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Helpers.logThis(TAG_LOG, "ALLOW PUSH: " + SharedPrefs.getBool(ALLOW_PUSH_MESSAGES));
//                Helpers.logThis(TAG_LOG, "FB DB CHILD CHANGED");
//                setDataSnapshotFromObject(dataSnapshot);
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };

//        child_ref.addChildEventListener(childEventListener);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Helpers.logThis(TAG_LOG, "ALLOW PUSH: " + SharedPrefs.getBool(ALLOW_PUSH_MESSAGES));
                setDataSnapshotFromObject(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        child_ref.addValueEventListener(valueEventListener);

    }

    public static void stopListeningForMessages() {
//        if (childEventListener != null)
//            child_ref.removeEventListener(childEventListener);
        if (valueEventListener != null)
            child_ref.removeEventListener(valueEventListener);
    }

    @Override
    public void onDestroy() {
        Helpers.logThis(TAG_LOG, "ON_DESTROY");
        stopListeningForMessages();
        super.onDestroy();
    }


    // BASIC METHODS ===============================================================================
    private void setDataSnapshotFromObject(DataSnapshot dataSnapshot) {
        try {
            Gson gson = new Gson();
            JSONObject messages_object = new JSONObject(gson.toJson(dataSnapshot.getValue()));
            Iterator<String> keys = messages_object.keys();

            Helpers.logThis(TAG_LOG, messages_object.toString());

            String key = "";
            if (keys.hasNext()){
                while (keys.hasNext()) {
                    key = keys.next();

                    Helpers.logThis(TAG_LOG, key);

                    JSONObject object = messages_object.getJSONObject(key);

                    if (!object.isNull(URL_UNREAD) && object.getInt(URL_UNREAD) > 0) {
                        Helpers.logThis(TAG_LOG, "UNREAD COUNT: " + object.getInt(URL_UNREAD));

                        if (SharedPrefs.getBool(ALLOW_PUSH_MESSAGES)) {
                            CHANNEL_ID = key;
                            CHANNEL_NAME = "New Message";
                            CHANNEL_DESC = object.getString(URL_LAST);

                            NotificationModel notification_model = new NotificationModel();
                            notification_model.table_id = Integer.parseInt(key);
                            notification_model.job_id = Integer.parseInt(key);
                            notification_model.title = object.getInt(URL_UNREAD) + " new message(s)";
                            notification_model.preview = "Tap to read in app";
                            notification_model.count = object.getInt(URL_UNREAD);

                            Helpers.createNotification(ConversationService.this, notification_model);
                        }
                    }

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
