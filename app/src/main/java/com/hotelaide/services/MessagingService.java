package com.hotelaide.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main.models.MessageModel;
import com.hotelaide.startup.SplashScreenActivity;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.hotelaide.utils.SharedPrefs.ALLOW_MESSAGE_PUSH;
import static com.hotelaide.utils.SharedPrefs.APP_IS_RUNNING;
import static com.hotelaide.utils.SharedPrefs.USER_ID;

public class MessagingService extends Service {
    private static final String TAG_LOG = "MESSAGES";
    private String
            CHANNEL_ID = "",
            CHANNEL_NAME = "CHANNEL_NAME",
            CHANNEL_DESC = "CHANNEL_DESC";


    // BASIC OVERRIDE METHODS ======================================================================
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Helpers.LogThis(TAG_LOG, "ON_START");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Helpers.LogThis(TAG_LOG, "ON_CREATE");
        FirebaseApp.initializeApp(MessagingService.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Get the reference to the DB
        DatabaseReference child_ref = database.getReference().child(BuildConfig.USERS_URL + SharedPrefs.getInt(USER_ID) + BuildConfig.MESSAGE_URL);
        child_ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Helpers.LogThis(TAG_LOG, "ALLOW PUSH: " + SharedPrefs.getBool(ALLOW_MESSAGE_PUSH));
                Helpers.LogThis(TAG_LOG, "APP RUNNING: " + SharedPrefs.getBool(APP_IS_RUNNING));

                if (SharedPrefs.getBool(ALLOW_MESSAGE_PUSH) && !SharedPrefs.getBool(APP_IS_RUNNING)) {
                    Helpers.LogThis(TAG_LOG, "FB DB CHILD ADDED");
                    setDataSnapshotFromObject(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Helpers.LogThis(TAG_LOG, "ALLOW PUSH: " + SharedPrefs.getBool(ALLOW_MESSAGE_PUSH));
                Helpers.LogThis(TAG_LOG, "APP RUNNING: " + SharedPrefs.getBool(APP_IS_RUNNING));

                if (SharedPrefs.getBool(ALLOW_MESSAGE_PUSH) && !SharedPrefs.getBool(APP_IS_RUNNING)) {
                    Helpers.LogThis(TAG_LOG, "FB DB CHILD CHANGED");
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
        });
    }

    @Override
    public void onDestroy() {
        Helpers.LogThis(TAG_LOG, "ON_DESTROY");
        super.onDestroy();
    }


    private void setDataSnapshotFromObject(DataSnapshot dataSnapshot) {
        try {
            Gson gson = new Gson();
            JSONObject message_object = new JSONObject(gson.toJson(dataSnapshot.getValue()));

            Helpers.LogThis(TAG_LOG, message_object.toString());

            if (!message_object.isNull("unread_messages") && message_object.getInt("unread_messages") > 0) {
                Helpers.LogThis(TAG_LOG, "UNREAD COUNT: " + message_object.getInt("unread_messages"));
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

                Helpers.LogThis(TAG_LOG, "CREATE NOTIFICATION: " + messageModel.from_name + " : " + messageModel.last_message);

                CHANNEL_ID = String.valueOf(messageModel.from_id);
                CHANNEL_NAME = messageModel.from_name;
                CHANNEL_DESC = messageModel.last_message;

                createNotification(MessagingService.this, messageModel.from_name, messageModel.last_message);

            }

        } catch (JSONException e) {
            Helpers.LogThis(TAG_LOG, e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Helpers.LogThis(TAG_LOG, e.toString());
            e.printStackTrace();
        }

    }


    // NOTIFICATION CREATOR ========================================================================
    public void createNotification(Context context, String MessageTitle, String messageBody) {
        Intent intent = new Intent(context, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("notification_title", MessageTitle);
        intent.putExtra("notification_body", messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        createNotificationChannel();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(MessageTitle)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(context.getResources().getColor(R.color.colorPrimary), 1000, 1000)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());
        ShortcutBadger.applyCount(context, 1);


    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ?
                R.mipmap.ic_launcher :
                R.mipmap.ic_launcher;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }


}
