package com.hotelaide.main.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.bassaer.chatmessageview.model.IChatUser;
import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.MessageView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;
import com.hotelaide.R;
import com.hotelaide.services.ConversationService;
import com.hotelaide.utils.FBDatabase;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.Date;

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

    private int
            INT_MY_ID = SharedPrefs.getInt(USER_ID),
            INT_FROM_ID = 0;

    private final String TAG_LOG = "MESSAGING";

    private DatabaseReference
            db_message,
            db_member;

    private MessageView message_view;
    private User
            user,
            member;

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
            db_member = FBDatabase.getURLMember(INT_FROM_ID);

            Helpers.cancelNotification(MessagingActivity.this, INT_FROM_ID);

            findAllViews();

            setUpToolBarAndTabs();

            updateToolbar();

            setListeners();

            HelpersAsync.setTrackerPage(TAG_LOG);

            stopService(new Intent(MessagingActivity.this, ConversationService.class));

        } else {
            onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        startService(new Intent(MessagingActivity.this, ConversationService.class));
        super.onDestroy();
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
            user = new User(String.valueOf(INT_MY_ID), "Me", null);
            member = new User(String.valueOf(INT_FROM_ID), STR_PAGE_TITLE, null);

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
        message_view = findViewById(R.id.message_view);
        // RIGHT SIDE
        message_view.setRightBubbleColor(ContextCompat.getColor(this, R.color.colorPrimary));
        message_view.setRightMessageTextColor(ContextCompat.getColor(this, R.color.white));
        // LEFT SIDE
        message_view.setLeftBubbleColor(ContextCompat.getColor(this, R.color.colorAccent));
        message_view.setLeftMessageTextColor(ContextCompat.getColor(this, R.color.white));

        message_view.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        message_view.setSendTimeTextColor(ContextCompat.getColor(this, R.color.grey));
        message_view.setUsernameTextColor(ContextCompat.getColor(this, R.color.grey));

        message_view.setMessageMarginTop(5);
        message_view.setMessageMarginBottom(10);

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
        try {
            Glide.with(this)
                    .load(STR_FROM_PIC_URL)
                    .placeholder(R.drawable.ic_profile)
                    .into(img_pic);
            toolbar_text.setText(STR_PAGE_TITLE);
            txt_user_status.setText(STR_STATUS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListeners() {
        img_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpers.openImageViewer(MessagingActivity.this, STR_FROM_PIC_URL);
            }
        });

        db_member.addValueEventListener(new ValueEventListener() {
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

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_message.getText().toString().length() > 0) {
                    FBDatabase.sendMessage(
                            INT_MY_ID,
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

            String text = object.getString("text");
            boolean is_me = INT_MY_ID == object.getInt("from_id");

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(object.getLong("time"));

            Message message;
            if (is_me) {
                message = new Message.Builder()
                        .setUser(user)
                        .setRight(true)
                        .setSendTime(cal)
                        .setText(text)
                        .hideIcon(true)
                        .build();
            } else {
                message = new Message.Builder()
                        .setUser(member)
                        .setRight(false)
                        .setSendTime(cal)
                        .setText(text)
                        .hideIcon(true)
                        .build();
                FBDatabase.setMessageRead(INT_FROM_ID);
            }

            message_view.setMessage(message);
            message_view.scrollToEnd();


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // IMPLEMENTATIONS =============================================================================
    class User implements IChatUser {

        private String id;
        private String name;
        private Bitmap icon;

        public User(String id, String name, Bitmap icon) {
            this.id = id;
            this.name = name;
            this.icon = icon;
        }

        @NotNull
        @Override
        public String getId() {
            return id;
        }

        @Nullable
        @Override
        public String getName() {
            return name;
        }

        @Nullable
        @Override
        public Bitmap getIcon() {
            return icon;
        }

        @Override
        public void setIcon(Bitmap bitmap) {
            this.icon = bitmap;
        }
    }

}
