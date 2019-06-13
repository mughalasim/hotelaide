package com.hotelaide.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hotelaide.R;
import com.hotelaide.main.adapters.MessagingAdapter;
import com.hotelaide.main.models.MessagingModel;
import com.hotelaide.utils.FBDatabase;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import static com.hotelaide.BuildConfig.URL_USER_IMG;
import static com.hotelaide.BuildConfig.URL_USER_NAME;
import static com.hotelaide.BuildConfig.URL_USER_STATUS;
import static com.hotelaide.utils.StaticVariables.EXTRA_INT;
import static com.hotelaide.utils.StaticVariables.USER_ID;

public class MessagingActivity extends AppCompatActivity {
    private String
            STR_PAGE_TITLE = "",
            STR_STATUS = "",
            STR_FROM_PIC_URL = "";

    private int INT_FROM_ID = 0;

    private final String TAG_LOG = "MESSAGING";

    private DatabaseReference
            db_message,
            db_user;

    private RecyclerView recycler_view;
    private ArrayList<MessagingModel> model_list = new ArrayList<>();
    private MessagingAdapter adapter;
    private Helpers helpers;

    private ImageView btn_send;
    private EditText et_message;

    private TextView
            txt_user_status,
            toolbar_text;
    private ImageView
            img_pic;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (handleExtraBundles()) {
            setContentView(R.layout.activity_messaging);

            helpers = new Helpers(MessagingActivity.this);

            db_message = FBDatabase.getURLMessages(INT_FROM_ID);
            db_user = FBDatabase.getURLMember(INT_FROM_ID);

            findAllViews();

            setUpToolBarAndTabs();

            updateToolbar();

            setListeners();

            HelpersAsync.setTrackerPage(TAG_LOG);

        } else {
            onBackPressed();
        }


    }

    // BASIC FUNCTIONS =============================================================================
    private Boolean handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getInt("FROM_ID") != 0) {
            INT_FROM_ID = extras.getInt("FROM_ID");
            if (extras.getString("FROM_NAME") != null) {
                if (!extras.getString("FROM_NAME").equals("")) {
                    STR_PAGE_TITLE = extras.getString("FROM_NAME");
                    STR_FROM_PIC_URL = extras.getString("FROM_PIC_URL");
                    FBDatabase.updateMemberDetails(INT_FROM_ID, STR_PAGE_TITLE, STR_FROM_PIC_URL);
                }
            }
            Helpers.logThis(TAG_LOG, "FROM ID: " + INT_FROM_ID);
            return true;
        } else {
            return false;
        }
    }

    private void findAllViews() {
        toolbar_text = findViewById(R.id.toolbar_text);
        txt_user_status = findViewById(R.id.txt_user_status);
        img_pic = findViewById(R.id.img_pic);

        // MESSAGE DISPLAY  FUNCTIONALITY ----------------------------------------------------------
        recycler_view = findViewById(R.id.recycler_view);
        adapter = new MessagingAdapter(model_list, INT_FROM_ID);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MessagingActivity.this);
        recycler_view.setLayoutManager(layoutManager);

        btn_send = findViewById(R.id.btn_send);
        et_message = findViewById(R.id.et_message);

    }

    private void setUpToolBarAndTabs() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessagingActivity.this, MemberProfileActivity.class)
                        .putExtra(EXTRA_INT, INT_FROM_ID
                        ));
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void updateToolbar() {
        Glide.with(this)
                .load(STR_FROM_PIC_URL)
                .placeholder(R.drawable.ic_profile)
                .into(img_pic);

        toolbar_text.setText(STR_PAGE_TITLE);
        txt_user_status.setText(STR_STATUS);
    }

    private void setListeners() {
        img_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpers.openImageViewer(MessagingActivity.this, STR_FROM_PIC_URL);
            }
        });

        db_message.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                receiveMessage(dataSnapshot);
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

        db_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Gson gson = new Gson();
                    JSONObject user_object = new JSONObject(gson.toJson(dataSnapshot.getValue()));

                    STR_PAGE_TITLE = user_object.getString(URL_USER_NAME);
                    STR_FROM_PIC_URL = user_object.getString(URL_USER_IMG);

                    if (!user_object.isNull(URL_USER_STATUS) && user_object.get(URL_USER_STATUS) instanceof String) {
                        STR_STATUS = "Online";
                    } else if (!user_object.isNull(URL_USER_STATUS) && user_object.get(URL_USER_STATUS) instanceof Long) {
                        try {
                            PrettyTime p = new PrettyTime();
                            String time = p.format(new Date(user_object.getLong(URL_USER_STATUS)));
                            STR_STATUS = "Last seen: ".concat(time);
                        } catch (Exception e) {
                            STR_STATUS = "Last seen: Unknown";
                        }
                    } else {
                        STR_STATUS = "Last seen: Unknown";
                    }
                } catch (JSONException e) {
                    STR_STATUS = "Last seen: Unknown";
                    e.printStackTrace();
                } catch (Exception e) {
                    STR_STATUS = "Last seen: Unknown";
                    e.printStackTrace();
                }
                updateToolbar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                STR_STATUS = "Last seen: Unknown";
                STR_PAGE_TITLE = "Unknown user";
                updateToolbar();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_message.getText().toString().length() > 0) {
                    FBDatabase.sendMessage(
                            SharedPrefs.getInt(USER_ID),
                            INT_FROM_ID,
                            et_message.getText().toString(),
                            Calendar.getInstance().getTimeInMillis());
                    et_message.setText("");
                }
            }
        });

    }

    private void receiveMessage(DataSnapshot dataSnapshot) {
        try {
            Gson gson = new Gson();
            JSONObject object = new JSONObject(gson.toJson(dataSnapshot.getValue()));
            MessagingModel messagingModel = new MessagingModel();
            messagingModel.from_id = object.getInt("from_id");
            messagingModel.text = object.getString("text");
            messagingModel.time = object.getLong("time");

            Helpers.logThis(TAG_LOG, "TEXT: " + messagingModel.text);

            model_list.add(messagingModel);

            adapter.notifyDataSetChanged();

            recycler_view.scrollToPosition(model_list.size());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveMessageArray(DataSnapshot dataSnapshot) {
        try {
            Gson gson = new Gson();
            JSONObject main_object = new JSONObject(gson.toJson(dataSnapshot.getValue()));
            Iterator<String> keys = main_object.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                Helpers.logThis(TAG_LOG, key);

                JSONObject object = main_object.getJSONObject(key);
                MessagingModel messagingModel = new MessagingModel();
                messagingModel.from_id = object.getInt("from_id");
                messagingModel.text = object.getString("text");
                messagingModel.time = Long.parseLong(key);

                Helpers.logThis(TAG_LOG, "TEXT: " + messagingModel.text);

                model_list.add(messagingModel);

            }

            if (model_list.size() < 1) {
                noListItems();
            } else {
                adapter.notifyDataSetChanged();
            }

            recycler_view.scrollToPosition(model_list.size());

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
        model_list.add(new MessagingModel());
        adapter.notifyDataSetChanged();
    }

}
