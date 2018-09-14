package com.hotelaide.main.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main.activities.ConversationActivity;
import com.hotelaide.main.models.MessageModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import java.util.ArrayList;

import static com.hotelaide.utils.SharedPrefs.USER_ID;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private final ArrayList<MessageModel> messageModels;
    private Context context;
    private Helpers helpers;

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout
                no_list_item,
                list_item;
        final TextView
                txt_no_results,
                txt_from_name,
                txt_last_message;
        final ImageView
                img_from_pic;
        final Chip txt_message_counter;

        ViewHolder(View v) {
            super(v);
            img_from_pic = v.findViewById(R.id.img_from_pic);
            txt_no_results = v.findViewById(R.id.txt_no_results);
            txt_from_name = v.findViewById(R.id.txt_from_name);
            txt_last_message = v.findViewById(R.id.txt_last_message);
            txt_message_counter = v.findViewById(R.id.txt_message_counter);
            no_list_item = v.findViewById(R.id.no_list_items);
            list_item = v.findViewById(R.id.list_item);
        }

    }

    public MessageAdapter(ArrayList<MessageModel> messageModels) {
        this.messageModels = messageModels;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_messages, parent, false);
        return new MessageAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {
        context = holder.itemView.getContext();
        helpers = new Helpers(context);

        final MessageModel messageModel = messageModels.get(position);

        if (messageModel.from_id == 0) {
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item.setVisibility(View.GONE);
            if (helpers.validateInternetConnection()) {
                holder.txt_no_results.setText("No Messages received");
            } else {
                holder.txt_no_results.setText(R.string.error_connection);
            }

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            holder.txt_from_name.setText(messageModel.from_name);
            holder.txt_last_message.setText(messageModel.last_message);

            if (messageModel.unread_messages == 0) {
                holder.txt_message_counter.setVisibility(View.GONE);
            } else {
                holder.txt_message_counter.setVisibility(View.VISIBLE);
                holder.txt_message_counter.setText(String.valueOf(messageModel.unread_messages));
            }

            Glide.with(context).load(messageModel.from_pic_url).into(holder.img_from_pic);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (messageModel.from_id != 0) {

                        if (messageModel.unread_messages > 0) {
                            FirebaseApp.initializeApp(context);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            Helpers.LogThis("MESSAGE ADAPTER", messageModel.from_name + "  " + holder.getAdapterPosition());
                            messageModel.unread_messages = 0;
                            database.getReference()
                                    .child(BuildConfig.MESSAGE_URL + SharedPrefs.getInt(USER_ID) + "/message_list/" + holder.getAdapterPosition() + "/unread_messages")
                                    .setValue(0);
                        }

                        context.startActivity(new Intent(context, ConversationActivity.class)
                                .putExtra("FROM_NAME", messageModel.from_name)
                                .putExtra("FROM_ID", messageModel.from_id)
                        );
                    }
                }
            });

//            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    if (messageModel.from_id != 0 && messageModel.pos > -1) {
//                        final Dialog dialog = new Dialog(context);
//                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                        dialog.setContentView(R.layout.dialog_confirm);
//                        final TextView txt_message = dialog.findViewById(R.id.txt_message);
//                        final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
//                        final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
//                        final TextView txt_title = dialog.findViewById(R.id.txt_title);
//                        txt_title.setText(context.getString(R.string.txt_delete));
//                        txt_message.setText(context.getString(R.string.txt_delete_desc));
//                        btn_confirm.setText(context.getString(R.string.txt_yes));
//                        btn_confirm.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                FirebaseApp.initializeApp(context);
//                                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                                Helpers.LogThis("MESSAGE ADAPTER", messageModel.from_name + "  " + holder.getAdapterPosition());
//                                database.getReference()
//                                        .child(BuildConfig.MESSAGE_URL + SharedPrefs.getInt(USER_ID) + "/message_list/" + messageModel.pos)
//                                        .setValue(null);
//                                messageModels.remove(holder.getAdapterPosition());
//                                notifyItemRemoved(holder.getAdapterPosition());
//                                notifyDataSetChanged();
//                                dialog.cancel();
//                            }
//                        });
//                        btn_cancel.setVisibility(View.VISIBLE);
//                        btn_cancel.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.cancel();
//                            }
//                        });
//                        dialog.show();
//
//                    }
//                    return false;
//
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public void replaceMessage(ArrayList<MessageModel> messageModels, MessageModel messageModel, int position) {
        messageModels.remove(position);
        messageModels.add(position, messageModel);
        notifyItemChanged(position);
    }

}
