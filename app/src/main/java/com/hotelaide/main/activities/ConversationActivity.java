package com.hotelaide.main.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.hotelaide.main.adapters.ConversationAdapter;
import com.hotelaide.main.models.ConversationModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.hotelaide.utils.SharedPrefs.USER_ID;

public class ConversationActivity extends AppCompatActivity {
    private Helpers helpers;

    private Toolbar toolbar;
    private TextView toolbar_text;

    private String
            STR_PAGE_TITLE = "", STR_MESSAGE_URL = "";

    private int INT_FROM_ID = 0;

    private final String
            TAG_LOG = "CONVERSATION";

    // FIRE BASE DB
    private FirebaseDatabase database;
    private DatabaseReference parent_ref, child_ref;

    // MESSAGE ADAPTER ITEMS -----------------------------------------------------------------------
    private LinearLayoutManager layoutManager;
    private RecyclerView recycler_view;
    private ArrayList<ConversationModel> model_list = new ArrayList<>();
    private ConversationAdapter adapter;

    private ImageView btn_send;
    private EditText et_message;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (handleExtraBundles()) {
            setContentView(R.layout.activity_conversation);

            helpers = new Helpers(ConversationActivity.this);

            setUpToolBarAndTabs();

            findAllViews();

            setupFireBase();

            setListeners();

//            fetchConversationList();

        } else {
            onBackPressed();
        }


    }

    // BASIC FUNCTIONS =============================================================================
    private Boolean handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getInt("FROM_ID") != 0) {
            INT_FROM_ID = extras.getInt("FROM_ID");
            STR_PAGE_TITLE = extras.getString("FROM_NAME");
            STR_MESSAGE_URL = extras.getString("MESSAGE_URL");

            Helpers.LogThis(TAG_LOG, "FROM ID: " + INT_FROM_ID);
            Helpers.LogThis(TAG_LOG, "MESSAGE URL: " + STR_MESSAGE_URL);

            return true;
        } else {
            return false;
        }
    }

    private void findAllViews() {
        // MESSAGE DISPLAY  FUNCTIONALITY ----------------------------------------------------------
        recycler_view = findViewById(R.id.recycler_view);
        adapter = new ConversationAdapter(model_list, INT_FROM_ID);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(ConversationActivity.this);
        recycler_view.setLayoutManager(layoutManager);

        btn_send = findViewById(R.id.btn_send);
        et_message = findViewById(R.id.et_message);

    }

    private void setUpToolBarAndTabs() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_text = toolbar.findViewById(R.id.toolbar_text);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar_text.setText(STR_PAGE_TITLE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setListeners() {
        child_ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                setFromDataSnapShotObject(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
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

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_message.getText().toString().length() > 0) {
                    sendMessage(et_message.getText().toString());
                    et_message.setText("");
                }
            }
        });

    }

    // FIRE BASE METHODS ===========================================================================
    private void setupFireBase() {
        FirebaseApp.initializeApp(ConversationActivity.this);
        database = FirebaseDatabase.getInstance();
        parent_ref = database.getReference();
        child_ref = parent_ref.child(BuildConfig.CONVERSATION_URL + SharedPrefs.getInt(USER_ID) + ":" + INT_FROM_ID);
        Helpers.LogThis(TAG_LOG, "FB URL: " + BuildConfig.CONVERSATION_URL + SharedPrefs.getInt(USER_ID) + ":" + INT_FROM_ID);

    }

    private void fetchConversationList() {
        child_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setFromDataSnapShotArray(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Helpers.LogThis(TAG_LOG, "DATABASE ERROR:" + databaseError.toString());
                noListItems();
            }
        });
    }

    private void sendMessage(final String message) {
        HashMap<String, Object> hash_data = new HashMap<>();
        hash_data.put("from_id", INT_FROM_ID);
        hash_data.put("text", message);

        parent_ref
                .child(BuildConfig.CONVERSATION_URL + SharedPrefs.getInt(USER_ID) + ":" + INT_FROM_ID + "/")
                .push()
                .setValue(hash_data);

        recycler_view.scrollToPosition(model_list.size() - 1);

    }

    // PARSING METHODS =============================================================================
    private void setFromDataSnapShotArray(DataSnapshot dataSnapshot) {
        try {
            Gson gson = new Gson();
            JSONArray message_array = new JSONArray(gson.toJson(dataSnapshot.getValue()));

            Helpers.LogThis(TAG_LOG, "LENGTH: " + message_array.length());

            if (!message_array.isNull(0)) {
                model_list.clear();
                int length = message_array.length();
                for (int i = 0; i < length; i++) {
                    Object object = message_array.get(i);
                    if (object instanceof JSONObject) {
                        JSONObject conversation_object = (JSONObject) object;
                        ConversationModel conversationModel = new ConversationModel();
                        conversationModel.text = conversation_object.getString("text");
                        conversationModel.from_id = conversation_object.getInt("from_id");
                        conversationModel.is_empty = false;

                        Helpers.LogThis(TAG_LOG, "TEXT: " + conversationModel.text);

                        model_list.add(conversationModel);
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
            JSONObject object = new JSONObject(gson.toJson(dataSnapshot.getValue()));
            ConversationModel conversationModel = new ConversationModel();
            conversationModel.text = object.getString("text");
            conversationModel.from_id = object.getInt("from_id");
            conversationModel.is_empty = false;

            Helpers.LogThis(TAG_LOG, "TEXT: " + conversationModel.text);

            model_list.add(conversationModel);

            adapter.notifyDataSetChanged();


            Helpers.LogThis(TAG_LOG, STR_MESSAGE_URL);
            parent_ref.child(STR_MESSAGE_URL).setValue(conversationModel.text);

            recycler_view.scrollToPosition(model_list.size() - 1);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void noListItems() {
        recycler_view.invalidate();
        model_list.clear();
        ConversationModel conversationModel = new ConversationModel();
        conversationModel.is_empty = true;
        model_list.add(conversationModel);
        adapter.notifyDataSetChanged();
    }

}
