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

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main.adapters.ConversationAdapter;
import com.hotelaide.main.models.ConversationModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_ID;
import static com.hotelaide.utils.StaticVariables.USER_IMG_AVATAR;
import static com.hotelaide.utils.StaticVariables.USER_L_NAME;

public class ConversationActivity extends AppCompatActivity {
    private String
            STR_PAGE_TITLE = "",
            STR_FROM_PIC_URL = "";

    private int INT_FROM_ID = 0;

    private final String
            TAG_LOG = "CONVERSATION";

    private DatabaseReference parent_ref, child_ref;

    private RecyclerView recycler_view;
    private ArrayList<ConversationModel> model_list = new ArrayList<>();
    private ConversationAdapter adapter;
    private Helpers helpers;

    private ImageView btn_send;
    private EditText et_message;

    private boolean show_my_texts = true;


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
            STR_FROM_PIC_URL = extras.getString("FROM_PIC_URL");

            Helpers.LogThis(TAG_LOG, "FROM ID: " + INT_FROM_ID);

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(ConversationActivity.this);
        recycler_view.setLayoutManager(layoutManager);

        btn_send = findViewById(R.id.btn_send);
        et_message = findViewById(R.id.et_message);

    }

    private void setUpToolBarAndTabs() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView img_from_pic = findViewById(R.id.img_from_pic);
        Glide.with(this).load(STR_FROM_PIC_URL).into(img_from_pic);
        img_from_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpers.openImageViewer(ConversationActivity.this, STR_FROM_PIC_URL);
            }
        });

        TextView toolbar_text = toolbar.findViewById(R.id.toolbar_text);
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

    private void sendMessage(final String message) {

        if (model_list.size() < 1) {
            HashMap<String, Object> user1 = new HashMap<>();
            user1.put("id", String.valueOf(SharedPrefs.getInt(USER_ID)));
            user1.put("name", SharedPrefs.getString(USER_F_NAME) + " " + SharedPrefs.getString(USER_L_NAME));
            user1.put("pic_url", SharedPrefs.getString(USER_IMG_AVATAR));

            HashMap<String, Object> user2 = new HashMap<>();
            user2.put("id", String.valueOf(INT_FROM_ID));
            user2.put("name", STR_PAGE_TITLE);
            user2.put("pic_url", STR_FROM_PIC_URL);

            HashMap< String ,Object> users = new HashMap<>();
            users.put("0", user1);
            users.put("1", user2);

            HashMap<String, Object> main_hash = new HashMap<>();
            main_hash.put("last_message", message);
            main_hash.put("unread_messages", "0");
            main_hash.put("users", users);

            Helpers.LogThis(TAG_LOG, main_hash.toString());

            parent_ref
                    .child(BuildConfig.USERS_URL + SharedPrefs.getInt(USER_ID) + BuildConfig.MESSAGE_URL + "/" + INT_FROM_ID + "/")
                    .setValue(main_hash);

            main_hash.put("unread_messages", "1");

            parent_ref
                    .child(BuildConfig.USERS_URL + INT_FROM_ID + BuildConfig.MESSAGE_URL + "/" + SharedPrefs.getInt(USER_ID) + "/")
                    .setValue(main_hash);
        }

        ConversationModel conversationModel = new ConversationModel();
        conversationModel.from_id = SharedPrefs.getInt(USER_ID);
        conversationModel.text = message;

        model_list.add(conversationModel);
        adapter.notifyDataSetChanged();
        show_my_texts = false;


        HashMap<String, Object> hash_data = new HashMap<>();
        hash_data.put("from_id", conversationModel.from_id);
        hash_data.put("text", message);

        // TO YOUR CONVERSATION LIST AND UPDATE LAST MESSAGE
        parent_ref
                .child(BuildConfig.USERS_URL + SharedPrefs.getInt(USER_ID) + BuildConfig.CONVERSATION_URL + INT_FROM_ID + "/")
                .push()
                .setValue(hash_data);
        parent_ref
                .child(BuildConfig.USERS_URL + SharedPrefs.getInt(USER_ID) + BuildConfig.MESSAGE_URL + "/" + INT_FROM_ID + "/last_message")
                .setValue(message);

        // TO YOUR SENDERS CONVERSATION LIST, LAST MESSAGE AND MESSAGE COUNTER
        parent_ref
                .child(BuildConfig.USERS_URL + INT_FROM_ID + BuildConfig.CONVERSATION_URL + SharedPrefs.getInt(USER_ID) + "/")
                .push()
                .setValue(hash_data);
        parent_ref
                .child(BuildConfig.USERS_URL + INT_FROM_ID + BuildConfig.MESSAGE_URL + "/" + SharedPrefs.getInt(USER_ID) + "/last_message")
                .setValue(message);
        parent_ref
                .child(BuildConfig.USERS_URL + INT_FROM_ID + BuildConfig.MESSAGE_URL + "/" + SharedPrefs.getInt(USER_ID) + "/unread_messages")
                .setValue(1);

    }

    // FIRE BASE METHODS ===========================================================================
    private void setupFireBase() {
        FirebaseApp.initializeApp(ConversationActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        parent_ref = database.getReference();
        child_ref = parent_ref.child(BuildConfig.USERS_URL + SharedPrefs.getInt(USER_ID) + BuildConfig.CONVERSATION_URL + INT_FROM_ID);
        Helpers.LogThis(TAG_LOG, "FB URL: " + BuildConfig.USERS_URL + SharedPrefs.getInt(USER_ID) + BuildConfig.CONVERSATION_URL + INT_FROM_ID);

    }

//    private void setFromDataSnapShotArray(DataSnapshot dataSnapshot) {
//        try {
//            Gson gson = new Gson();
//            JSONArray message_array = new JSONArray(gson.toJson(dataSnapshot.getValue()));
//
//            Helpers.LogThis(TAG_LOG, "LENGTH: " + message_array.length());
//
//            if (!message_array.isNull(0)) {
//                model_list.clear();
//                int length = message_array.length();
//                for (int i = 0; i < length; i++) {
//                    Object object = message_array.get(i);
//                    if (object instanceof JSONObject) {
//                        JSONObject conversation_object = (JSONObject) object;
//                        ConversationModel conversationModel = new ConversationModel();
//                        conversationModel.from_id = conversation_object.getInt("from_id");
//                        conversationModel.text = conversation_object.getString("text");
//                        conversationModel.is_empty = false;
//
//                        Helpers.LogThis(TAG_LOG, "TEXT: " + conversationModel.text);
//
//                        model_list.add(conversationModel);
//                    }
//                }
//
//                if (model_list.size() <= 0) {
//                    noListItems();
//                }
//
//                adapter.notifyDataSetChanged();
//
//            } else {
//                noListItems();
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            noListItems();
//        } catch (Exception e) {
//            e.printStackTrace();
//            noListItems();
//        }
//    }


    // PARSING METHODS =============================================================================
    private void setFromDataSnapShotObject(DataSnapshot dataSnapshot) {
        try {
            Gson gson = new Gson();
            JSONObject object = new JSONObject(gson.toJson(dataSnapshot.getValue()));
            ConversationModel conversationModel = new ConversationModel();
            conversationModel.from_id = object.getInt("from_id");
            conversationModel.text = object.getString("text");

            Helpers.LogThis(TAG_LOG, "TEXT: " + conversationModel.text);

            if (conversationModel.from_id != SharedPrefs.getInt(USER_ID)) {
                model_list.add(conversationModel);
            } else if (show_my_texts) {
                model_list.add(conversationModel);
            }

            adapter.notifyDataSetChanged();


//            Helpers.LogThis(TAG_LOG, STR_MESSAGE_URL);
//            parent_ref.child(STR_MESSAGE_URL).setValue(conversationModel.text);

            recycler_view.scrollToPosition(model_list.size());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void noListItems() {
//        recycler_view.invalidate();
//        model_list.clear();
//        ConversationModel conversationModel = new ConversationModel();
//        conversationModel.is_empty = true;
//        model_list.add(conversationModel);
//        adapter.notifyDataSetChanged();
//    }

}
