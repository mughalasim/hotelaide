package com.hotelaide.main.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.hotelaide.R;
import com.hotelaide.main.activities.ConversationActivity;
import com.hotelaide.main.adapters.FindJobsAdapter;
import com.hotelaide.main.models.NotificationModel;
import com.hotelaide.main.models.UserModel;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class NotificationFragment extends Fragment {

    private View root_view;
    private final String
            TAG_LOG = "NOTIFICATIONS";
    private Helpers helpers;
    private Database db;

    // TOP PANEL ITEMS ------------------------------
    private SwipeRefreshLayout swipe_refresh;
    private RecyclerView recycler_view;
    private ArrayList<NotificationModel> model_list = new ArrayList<>();
    private NotificationAdapter adapter;


    public NotificationFragment() {
    }


    // OVERRIDE FUNCTIONS ==========================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_recycler_view, container, false);

                helpers = new Helpers(getActivity());

                db = new Database();

                findAllViews();

                setListeners();

                NotificationModel notificationModel = new NotificationModel();
                notificationModel.title = "Welcome!";
                notificationModel.message = "About Thyme offers an eclectic menu with imaginative, well-prepared and beautifully presented dishes from around the world. Intimate dining in a leafy, secluded area of Westlands where good food and drinks can be enjoyed in a tranquil setting. There is something to cater for all tastes with a wide range of vegetarian dishes & desserts. Here are special treats that have become famous over the years.";
                notificationModel.date = "12-12-2018";
                notificationModel.read = 0;
                db.setNotification(notificationModel);

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
        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
        recycler_view = root_view.findViewById(R.id.recycler_view);
        adapter = new NotificationAdapter(model_list);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);
    }

    private void setListeners() {
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(true);
                populateFromDB();
            }
        });
    }

    private void populateFromDB() {
        model_list.clear();
        model_list = db.getAllNotifications();
        recycler_view.invalidate();
        adapter.updateData(model_list);
        if (model_list.size() <= 0) {
            noListItems();
        }
        swipe_refresh.setRefreshing(false);
    }

    private void noListItems() {
        recycler_view.invalidate();
        model_list.clear();
        model_list.add(new NotificationModel());
        adapter.updateData(model_list);
    }


    // NOTIFICATIONS ADAPTER CLASS =======================================================================
    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
        private final ArrayList<NotificationModel> notificationModels;
        private Context context;
        private Helpers helpers;

        class ViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout
                    no_list_item;

            CardView list_item;

            final TextView
                    txt_title,
                    txt_message,
                    txt_date,
                    txt_no_results;

            ViewHolder(View v) {
                super(v);
                txt_no_results = v.findViewById(R.id.txt_no_results);
                txt_title = v.findViewById(R.id.txt_title);
                txt_message = v.findViewById(R.id.txt_message);
                txt_date = v.findViewById(R.id.txt_date);
                no_list_item = v.findViewById(R.id.rl_no_list_items);
                list_item = v.findViewById(R.id.list_item);
            }

        }

        NotificationAdapter(ArrayList<NotificationModel> notificationModels) {
            this.notificationModels = notificationModels;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_notification, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            context = holder.itemView.getContext();
            helpers = new Helpers(context);

            final NotificationModel notificationModel = notificationModels.get(position);

            if (notificationModel.id == 0) {
                holder.no_list_item.setVisibility(View.VISIBLE);
                holder.list_item.setVisibility(View.GONE);
                holder.itemView.setAlpha(1f);
                if (helpers.validateInternetConnection()) {
                    holder.txt_no_results.setText("No notifications");
                } else {
                    holder.txt_no_results.setText(R.string.error_connection);
                }

            } else {
                holder.no_list_item.setVisibility(View.GONE);
                holder.list_item.setVisibility(View.VISIBLE);

                holder.txt_title.setText(notificationModel.title);
                holder.txt_message.setText(notificationModel.message);
                holder.txt_date.setText(notificationModel.date);

                if (notificationModel.read == 1) {
                    holder.itemView.setAlpha(0.5f);
                } else{
                    holder.itemView.setAlpha(1f);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (notificationModel.id != 0) {
                            final Dialog dialog = new Dialog(context);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_confirm);
                            final TextView txt_message = dialog.findViewById(R.id.txt_message);
                            final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
                            final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                            final TextView txt_title = dialog.findViewById(R.id.txt_title);
                            txt_title.setText(notificationModel.title);
                            txt_message.setText(notificationModel.message);
                            btn_confirm.setText("READ");
                            btn_cancel.setText("DELETE");
                            btn_confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    db.updateNotificationRead(notificationModel.id);
                                    populateFromDB();
                                    dialog.cancel();
                                }
                            });
                            btn_cancel.setVisibility(View.VISIBLE);
                            btn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (notificationModel.id != 0)
                                        db.deleteNotificationByID(String.valueOf(notificationModel.id));
                                    populateFromDB();
                                    dialog.cancel();
                                }
                            });
                            dialog.show();
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return notificationModels.size();
        }

        void updateData(ArrayList<NotificationModel> view_model) {
            notificationModels.clear();
            notificationModels.addAll(view_model);
            notifyDataSetChanged();
        }
    }

}