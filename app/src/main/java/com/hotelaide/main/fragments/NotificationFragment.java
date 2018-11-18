package com.hotelaide.main.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hotelaide.R;
import com.hotelaide.main.activities.ConversationActivity;
import com.hotelaide.main.models.MessageModel;
import com.hotelaide.main.models.UserModel;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class NotificationFragment extends Fragment {

    private View root_view;
    private final String
            TAG_LOG = "NOTIFICATIONS";
    private Helpers helpers;

    // TOP PANEL ITEMS ------------------------------
    private SwipeRefreshLayout swipe_refresh;
    private RecyclerView recycler_view;
    private ArrayList<MessageModel> model_list = new ArrayList<>();
    private ContactAdapter adapter;


    public NotificationFragment() {
    }


    // OVERRIDE FUNCTIONS ==========================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_recycler_view, container, false);

                helpers = new Helpers(getActivity());

                findAllViews();

                setListeners();


            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateFromDB();
    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        // TOP PANEL FUNCTIONS ----------------------------------------------------------
        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
        recycler_view = root_view.findViewById(R.id.recycler_view);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);
    }

    private void setListeners() {
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateFromDB();
            }
        });
    }

    private void populateFromDB(){

    }

    private void noListItems() {
        recycler_view.invalidate();
        model_list.clear();
        model_list.add(new MessageModel());
        adapter.notifyDataSetChanged();
    }


    // NOTIFICATIONS ADAPTER CLASS =======================================================================
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
                no_list_item = v.findViewById(R.id.rl_no_list_items);
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