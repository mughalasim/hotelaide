package com.hotelaide.main.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main.activities.ConversationActivity;
import com.hotelaide.main.adapters.MessageAdapter;
import com.hotelaide.main.models.MessageModel;
import com.hotelaide.main.models.UserModel;
import com.hotelaide.services.UserService;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.SharedPrefs.USER_ID;

public class MessageFragment extends Fragment {

    private View root_view;
    private final String
            TAG_LOG = "MESSAGES";
    private Helpers helpers;

    // TOP PANEL ITEMS ------------------------------
    private SwipeRefreshLayout swipe_refresh;
    private DatabaseReference child_ref;
    private RecyclerView recycler_view;
    private ArrayList<MessageModel> model_list = new ArrayList<>();
    private MessageAdapter adapter;
    private FloatingActionButton btn_add_message;
    private SlidingUpPanelLayout sliding_panel;

    // BOTTOM PANEL ITEMS ---------------------------
    MaterialButton btn_cancel;
    RecyclerView recycler_view_contacts;
    private ArrayList<UserModel> model_list_contacts = new ArrayList<>();
    private ContactAdapter adapter_contacts;


    public MessageFragment() {
    }


    // OVERRIDE FUNCTIONS ==========================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_messages, container, false);

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
        // TOP PANEL FUNCTIONS ----------------------------------------------------------
        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
        recycler_view = root_view.findViewById(R.id.recycler_view);
        adapter = new MessageAdapter(model_list);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);
        btn_add_message = root_view.findViewById(R.id.btn_add_message);
        sliding_panel = root_view.findViewById(R.id.sliding_panel);


        // BOTTOM PANEL ITEMS ----------------------------------------
        btn_cancel = root_view.findViewById(R.id.btn_cancel);
        recycler_view_contacts = root_view.findViewById(R.id.recycler_view_contacts);
        adapter_contacts = new ContactAdapter(model_list_contacts);
        recycler_view_contacts.setAdapter(adapter_contacts);
        recycler_view.setHasFixedSize(false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        recycler_view_contacts.setLayoutManager(layoutManager2);

    }

    private void setListeners() {
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMessageList();
            }
        });
        btn_add_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                if (model_list_contacts.size() < 1) {
                    model_list_contacts.clear();
                    asyncFetchContacts(1);
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliding_panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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


    // CONTACT ASYNC FUNCTIONS =====================================================================
    private void asyncFetchContacts(final int page_number) {
        helpers.ToastMessage(getActivity(), "Loading... please wait...");
        UserService userService = UserService.retrofit.create(UserService.class);
        final Call<JsonObject> call = userService.getAllUsers(page_number);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (getActivity() != null) {
                    try {
                        JSONObject main = new JSONObject(String.valueOf(response.body()));

                        Helpers.LogThis(TAG_LOG, main.toString());

                        JSONArray data = main.getJSONArray("data");
                        int length = data.length();

                        for (int i = 0; i < length; i++) {
                            JSONObject user_object = data.getJSONObject(i);
                            UserModel userModel = new UserModel();
                            userModel.img_avatar = user_object.getString("avatar");
                            userModel.id = user_object.getInt("id");
                            userModel.first_name = user_object.getString("first_name");
                            userModel.last_name = user_object.getString("last_name");
                            if (userModel.id != SharedPrefs.getInt(USER_ID)) {
                                model_list_contacts.add(userModel);
                            }
                        }

                        adapter_contacts.notifyDataSetChanged();

                        JSONObject metadata = main.getJSONObject("meta");
                        int last_page = metadata.getInt("last_page");
                        if (last_page > page_number) {
                            asyncFetchContacts(page_number + 1);
                        }

                    } catch (JSONException e) {
                        Helpers.LogThis(TAG_LOG, e.toString());

                    } catch (Exception e) {
                        Helpers.LogThis(TAG_LOG, e.toString());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (getActivity() != null) {
                    Helpers.LogThis(TAG_LOG, t.toString());
                    Helpers.LogThis(TAG_LOG, call.toString());
                }
            }
        });
    }

    // CONTACT ADAPTER CLASS =======================================================================
    private class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
        private final ArrayList<UserModel> userModels;
        private Context context;
        private Helpers helpers;

        class ViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout
                    no_list_item,
                    list_item;
            final TextView
                    txt_no_results,
                    txt_name;

            final ImageView
                    img_user;

            ViewHolder(View v) {
                super(v);
                img_user = v.findViewById(R.id.img_user);
                txt_no_results = v.findViewById(R.id.txt_no_results);
                txt_name = v.findViewById(R.id.txt_name);
                no_list_item = v.findViewById(R.id.no_list_items);
                list_item = v.findViewById(R.id.list_item);
            }

        }

        ContactAdapter(ArrayList<UserModel> userModels) {
            this.userModels = userModels;
        }

        @NonNull
        @Override
        public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_contacts, parent, false);
            return new ContactAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ContactAdapter.ViewHolder holder, final int position) {
            context = holder.itemView.getContext();
            helpers = new Helpers(context);

            final UserModel userModel = userModels.get(position);

            if (userModel.id == 0) {
                holder.no_list_item.setVisibility(View.VISIBLE);
                holder.list_item.setVisibility(View.GONE);
                if (helpers.validateInternetConnection()) {
                    holder.txt_no_results.setText("No contacts followed");
                } else {
                    holder.txt_no_results.setText(R.string.error_connection);
                }

            } else {
                holder.no_list_item.setVisibility(View.GONE);
                holder.list_item.setVisibility(View.VISIBLE);

                final String name = userModel.first_name + " " + userModel.last_name;

                holder.txt_name.setText(name);
                Glide.with(context).load(userModel.img_avatar).into(holder.img_user);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userModel.id != 0) {

                            context.startActivity(new Intent(context, ConversationActivity.class)
                                    .putExtra("FROM_NAME", name)
                                    .putExtra("FROM_ID", userModel.id)
                                    .putExtra("FROM_PIC_URL", userModel.img_avatar)
                            );
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }

    }

}