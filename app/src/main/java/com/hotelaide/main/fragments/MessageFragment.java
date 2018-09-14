package com.hotelaide.main.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
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

import static com.hotelaide.utils.SharedPrefs.USER_ID;

public class MessageFragment extends Fragment {

    private View root_view;
    private Helpers helpers;
    private final String
            TAG_LOG = "MESSAGES";
    private SwipeRefreshLayout swipe_refresh;

    // FIREBASE DB
    private FirebaseDatabase database;
    private DatabaseReference parent_ref, child_ref;

    // MESSAGE ADAPTER ITEMS -----------------------------------------------------------------------
    private LinearLayoutManager layoutManager;
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

                helpers = new Helpers(getActivity());

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
        layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);
        if (getActivity()!=null) {
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                    layoutManager.getOrientation());
            recycler_view.addItemDecoration(dividerItemDecoration);
        }

    }

    private void setListeners() {
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMessageList();
            }
        });

        child_ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                setFromDataSnapShotObject(dataSnapshot);
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


    // FIRE BASE METHODS ===========================================================================
    private void setupFireBase() {
        FirebaseApp.initializeApp(getActivity());
        database = FirebaseDatabase.getInstance();
        parent_ref = database.getReference();
        child_ref = parent_ref.child(BuildConfig.MESSAGE_URL + SharedPrefs.getInt(USER_ID) + "/message_list");
        Helpers.LogThis(TAG_LOG, "FB URL: " + BuildConfig.MESSAGE_URL + SharedPrefs.getInt(USER_ID) + "/message_list");

    }

    private void fetchMessageList() {
        swipe_refresh.setRefreshing(true);
        child_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                swipe_refresh.setRefreshing(false);
                setFromDataSnapShotArray(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipe_refresh.setRefreshing(false);
                Helpers.LogThis(TAG_LOG, "DATABASE ERROR:" + databaseError.toString());
                noListItems();
            }
        });
    }

    // PARSING METHODS =============================================================================
    private void setFromDataSnapShotArray(DataSnapshot dataSnapshot) {
        try {
            Gson gson = new Gson();
            JSONArray message_array = new JSONArray(gson.toJson(dataSnapshot.getValue()));

            Helpers.LogThis(TAG_LOG, "MESSAGE LENGTH: " + message_array.length());

            if (!message_array.isNull(0)) {
                model_list.clear();
                int length = message_array.length();
                for (int i = 0; i < length; i++) {
                    Object object = message_array.get(i);
                    if (object instanceof JSONObject) {
                        JSONObject message_object = (JSONObject) object;
                        MessageModel messageModel = new MessageModel();
                        messageModel.from_id = message_object.getInt("from_id");
                        messageModel.from_name = message_object.getString("from_name");
                        messageModel.from_pic_url = message_object.getString("from_pic_url");
                        messageModel.last_message = message_object.getString("last_message");
                        messageModel.unread_messages = message_object.getInt("unread_messages");
                        messageModel.pos = message_object.getInt("pos");

                        Helpers.LogThis(TAG_LOG, "MESSAGE FROM: " + messageModel.from_name);

                        model_list.add(messageModel);
                    }
                }

                if (model_list.size() <= 0) {
                    noListItems();
                }

                adapter.notifyDataSetChanged();

            } else {
                noListItems();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            noListItems();
        } catch (Exception e) {
            e.printStackTrace();
            noListItems();
        }
    }

    private void setFromDataSnapShotObject(DataSnapshot dataSnapshot) {
        try {
            Gson gson = new Gson();
            JSONObject message_object = new JSONObject(gson.toJson(dataSnapshot.getValue()));
            MessageModel messageModel = new MessageModel();
            messageModel.from_id = message_object.getInt("from_id");
            messageModel.from_name = message_object.getString("from_name");
            messageModel.from_pic_url = message_object.getString("from_pic_url");
            messageModel.last_message = message_object.getString("last_message");
            messageModel.unread_messages = message_object.getInt("unread_messages");
            messageModel.pos = message_object.getInt("pos");

            Helpers.LogThis(TAG_LOG, "MESSAGE FROM: " + messageModel.from_name);

            adapter.replaceMessage(model_list, messageModel, messageModel.pos);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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