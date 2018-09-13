package com.hotelaide.main.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.hotelaide.utils.SharedPrefs.USER_ID;


public class MessagesFragment extends Fragment {

    private View root_view;
    private Helpers helpers;
    private final String
            TAG_LOG = "MESSAGES";


    public MessagesFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_recycler_view, container, false);

                helpers = new Helpers(getActivity());

                findAllViews();

                setListeners();

                fetchMessageList();


            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {


    }

    private void setListeners() {

    }

    private void fetchMessageList() {
        FirebaseApp.initializeApp(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        DatabaseReference myRef = ref.child(BuildConfig.MESSAGE_URL + SharedPrefs.getInt(USER_ID) + "/message_list");
        Helpers.LogThis(TAG_LOG, "FB URL: " + BuildConfig.MESSAGE_URL + SharedPrefs.getInt(USER_ID) + "/message_list");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Gson gson = new Gson();
                    JSONArray ticket_array = new JSONArray(gson.toJson(dataSnapshot.getValue()));

                    if (!ticket_array.isNull(0)) {
                        int length = ticket_array.length();
                        for (int i = 0; i < length; i++) {
                            Object ticket_object = ticket_array.get(i);
                            if (ticket_object instanceof JSONObject) {
//                                db.setTicketsFromJson((JSONObject) ticket_object);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Helpers.LogThis(TAG_LOG, "DATABASE ERROR:" + databaseError.toString());
            }
        });
    }


}