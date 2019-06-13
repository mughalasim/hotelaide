package com.hotelaide.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static com.hotelaide.BuildConfig.URL_CONVERSATIONS;
import static com.hotelaide.BuildConfig.URL_LAST;
import static com.hotelaide.BuildConfig.URL_MESSAGES;
import static com.hotelaide.BuildConfig.URL_SLASH;
import static com.hotelaide.BuildConfig.URL_UNREAD;
import static com.hotelaide.BuildConfig.URL_USER;
import static com.hotelaide.BuildConfig.URL_USER_IMG;
import static com.hotelaide.BuildConfig.URL_USER_NAME;
import static com.hotelaide.BuildConfig.URL_USER_STATUS;
import static com.hotelaide.utils.MyApplication.fb_parent_ref;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_ID;
import static com.hotelaide.utils.StaticVariables.USER_IMG_AVATAR;
import static com.hotelaide.utils.StaticVariables.USER_L_NAME;

public class FBDatabase {

    public FBDatabase() {
    }

    public static void updateUserDetails() {
        DatabaseReference child_ref = fb_parent_ref.child(URL_USER + URL_SLASH + SharedPrefs.getInt(USER_ID) + URL_SLASH);

        DatabaseReference avatar_ref = child_ref.child(URL_USER_IMG);
        avatar_ref.setValue(SharedPrefs.getString(USER_IMG_AVATAR));

        DatabaseReference name_ref = child_ref.child(URL_USER_NAME);
        name_ref.setValue(SharedPrefs.getString(USER_F_NAME) + " " + SharedPrefs.getString(USER_L_NAME));
    }

    public static void updateMemberDetails(int member_id, String name, String img_url) {
        DatabaseReference child_ref = fb_parent_ref.child(URL_USER + URL_SLASH + member_id + URL_SLASH);

        DatabaseReference avatar_ref = child_ref.child(URL_USER_IMG);
        avatar_ref.setValue(img_url);

        DatabaseReference name_ref = child_ref.child(URL_USER_NAME);
        name_ref.setValue(name);
    }

    private static void setUserMessageCount(final int user_id) {
        final DatabaseReference child_ref = fb_parent_ref.child(URL_USER + URL_SLASH + user_id+ URL_SLASH + URL_CONVERSATIONS + URL_SLASH
                + SharedPrefs.getInt(USER_ID) + URL_SLASH+ URL_UNREAD);

        child_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() instanceof Long) {
                    Long count = (Long) dataSnapshot.getValue();
                    count++;
                    child_ref.setValue(count);
                } else {
                    child_ref.setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static DatabaseReference getURLMember(int member_id) {
        return fb_parent_ref.child(URL_USER + URL_SLASH + member_id);
    }

    public static DatabaseReference getURLConversation() {
        return fb_parent_ref.child(URL_USER + URL_SLASH + SharedPrefs.getInt(USER_ID) + URL_SLASH + URL_CONVERSATIONS);
    }

    public static DatabaseReference getURLMessages(int member_id) {
        return fb_parent_ref.child(URL_USER + URL_SLASH + SharedPrefs.getInt(USER_ID) + URL_SLASH + URL_MESSAGES + URL_SLASH + member_id);
    }

    public static DatabaseReference getURLMemberStatus(int member_id) {
        return fb_parent_ref.child(URL_USER + URL_SLASH + member_id + URL_SLASH + URL_USER_STATUS);
    }

    public static void sendMessage(final int INT_USER_ID, final int INT_MEMBER_ID, final String message, final long time) {

        HashMap<String, Object> hash_data = new HashMap<>();
        hash_data.put("from_id", INT_USER_ID);
        hash_data.put("text", message);
        hash_data.put("time", time);

        // TO YOUR MESSAGE LIST AND UPDATE LAST MESSAGE
        fb_parent_ref
                .child(URL_USER + URL_SLASH + INT_USER_ID + URL_SLASH + URL_MESSAGES + URL_SLASH + INT_MEMBER_ID + URL_SLASH + time)
//                .push()
                .setValue(hash_data);
        fb_parent_ref
                .child(URL_USER + URL_SLASH + INT_USER_ID + URL_SLASH + URL_CONVERSATIONS + URL_SLASH + INT_MEMBER_ID + URL_SLASH + URL_LAST)
                .setValue(message);

        // TO YOUR SENDERS MESSAGE LIST, LAST MESSAGE AND MESSAGE COUNTER
        fb_parent_ref
                .child(URL_USER + URL_SLASH + INT_MEMBER_ID + URL_SLASH + URL_MESSAGES + URL_SLASH + INT_USER_ID + URL_SLASH + time)
//                .push()
                .setValue(hash_data);
        fb_parent_ref
                .child(URL_USER + URL_SLASH + INT_MEMBER_ID + URL_SLASH + URL_CONVERSATIONS+ URL_SLASH + INT_USER_ID + URL_SLASH + URL_LAST)
                .setValue(message);

        setUserMessageCount(INT_MEMBER_ID);

    }

    public static void setMessageRead(int member_id) {
        fb_parent_ref
                .child(URL_USER + URL_SLASH + SharedPrefs.getInt(USER_ID) + URL_SLASH+ URL_CONVERSATIONS + URL_SLASH + member_id + URL_SLASH + URL_UNREAD)
                .setValue(0);
    }

    public static void setUserStatus(Object status) {
        if (SharedPrefs.getInt(USER_ID) != 0) {
            DatabaseReference child_ref = fb_parent_ref.child(URL_USER + URL_SLASH+ SharedPrefs.getInt(USER_ID) + URL_SLASH+ URL_USER_STATUS);
            child_ref.setValue(status);
        }
    }

    public static void deleteConversation(int member_id) {
        // Deletes the conversation
        fb_parent_ref.child(URL_USER +URL_SLASH+ SharedPrefs.getInt(USER_ID)+URL_SLASH + URL_CONVERSATIONS + URL_SLASH + member_id )
                .setValue(null);
        // Deletes the message
        fb_parent_ref.child(URL_USER +URL_SLASH+ SharedPrefs.getInt(USER_ID)+URL_SLASH + URL_MESSAGES + URL_SLASH + member_id )
                .setValue(null);
    }
}
