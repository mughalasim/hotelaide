package com.hotelaide.main.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.hotelaide.main.adapters.MessageAdapter;
import com.hotelaide.main.models.MessageModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static com.hotelaide.utils.SharedPrefs.USER_ID;

public class MessageFragment extends Fragment {

    private View root_view;
    private final String
            TAG_LOG = "MESSAGES";
    private SwipeRefreshLayout swipe_refresh;

    private DatabaseReference child_ref;

    private RecyclerView recycler_view;
    private ArrayList<MessageModel> model_list = new ArrayList<>();
    private MessageAdapter adapter;

    public MessageFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_recycler_view, container, false);

                findAllViews();

                setupFireBase();

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
        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
        // MESSAGE DISPLAY  FUNCTIONALITY ----------------------------------------------------------
        recycler_view = root_view.findViewById(R.id.recycler_view);
        adapter = new MessageAdapter(model_list);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);

    }

    private void setListeners() {
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMessageList();
            }
        });
    }


    // FIRE BASE METHODS ===========================================================================
    private void setupFireBase() {
        FirebaseApp.initializeApp(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference parent_ref = database.getReference();
        child_ref = parent_ref.child(BuildConfig.USERS_URL + SharedPrefs.getInt(USER_ID) + BuildConfig.MESSAGE_URL);
        Helpers.LogThis(TAG_LOG, "FB URL: " + BuildConfig.USERS_URL + SharedPrefs.getInt(USER_ID) + BuildConfig.MESSAGE_URL);

    }

    private void fetchMessageList() {
        swipe_refresh.setRefreshing(true);
        child_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                swipe_refresh.setRefreshing(false);
                setFromDataSnapShotArray(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // PARSING METHODS =============================================================================
    private void setFromDataSnapShotArray(DataSnapshot dataSnapshot) {
        try {
            Gson gson = new Gson();
            JSONObject messages_object = new JSONObject(gson.toJson(dataSnapshot.getValue()));
            Iterator<String> keys = messages_object.keys();

            model_list.clear();

            while (keys.hasNext()) {
                String key = keys.next();
                Helpers.LogThis(TAG_LOG, key);

                JSONObject message_object = messages_object.getJSONObject(key);
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

                model_list.add(messageModel);

            }

            if (model_list.size() <= 0) {
                noListItems();
            }

            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
            noListItems();
        } catch (Exception e) {
            e.printStackTrace();
            noListItems();
        }
    }

    private void noListItems() {
        recycler_view.invalidate();
        model_list.clear();
        MessageModel messageModel = new MessageModel();
        model_list.add(messageModel);
        adapter.notifyDataSetChanged();
    }


}