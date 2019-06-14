package com.hotelaide.main.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hotelaide.R;
import com.hotelaide.main.activities.MemberProfileActivity;
import com.hotelaide.main.activities.MessagingActivity;
import com.hotelaide.main.models.ConversationModel;
import com.hotelaide.main.models.UserModel;
import com.hotelaide.utils.FBDatabase;
import com.hotelaide.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.hotelaide.BuildConfig.URL_USER_IMG;
import static com.hotelaide.BuildConfig.URL_USER_NAME;
import static com.hotelaide.BuildConfig.URL_USER_STATUS;
import static com.hotelaide.utils.StaticVariables.EXTRA_INT;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private final ArrayList<ConversationModel> conversationModels;
    private Context context;
    private Helpers helpers;

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout
                no_list_item;
        CardView
                list_item;
        final TextView
                txt_no_results,
                txt_from_name,
                txt_last_message;
        final ImageView
                img_from_pic;
        final Chip
                chip_message_count,
                chip_user_status;

        ViewHolder(View v) {
            super(v);
            img_from_pic = v.findViewById(R.id.img_pic);
            txt_no_results = v.findViewById(R.id.txt_no_results);
            txt_from_name = v.findViewById(R.id.txt_from_name);
            txt_last_message = v.findViewById(R.id.txt_last_message);
            chip_message_count = v.findViewById(R.id.chip_message_count);
            chip_user_status = v.findViewById(R.id.chip_user_status);
            no_list_item = v.findViewById(R.id.rl_no_list_items);
            list_item = v.findViewById(R.id.list_item);
        }

    }

    public ConversationAdapter(ArrayList<ConversationModel> conversationModels) {
        this.conversationModels = conversationModels;
    }

    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_conversations, parent, false);
        return new ConversationAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ConversationAdapter.ViewHolder holder, final int position) {
        context = holder.itemView.getContext();
        helpers = new Helpers(context);

        final ConversationModel conversationModel = conversationModels.get(position);
        final UserModel userModel = new UserModel();

        if (conversationModel.from_id == 0) {
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item.setVisibility(View.GONE);
            if (helpers.validateInternetConnection()) {
                holder.txt_no_results.setText("No Messages");
            } else {
                holder.txt_no_results.setText(R.string.error_connection);
            }

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            DatabaseReference child_ref = FBDatabase.getURLMember(conversationModel.from_id);
            child_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        Gson gson = new Gson();
                        JSONObject user_object = new JSONObject(gson.toJson(dataSnapshot.getValue()));

                        userModel.img_avatar = user_object.getString(URL_USER_IMG);
                        userModel.first_name = user_object.getString(URL_USER_NAME);

                        holder.txt_from_name.setText(user_object.getString(URL_USER_NAME));

                        Glide.with(context)
                                .load(user_object.getString(URL_USER_IMG))
                                .placeholder(R.drawable.ic_profile)
                                .into(holder.img_from_pic);

                        if (!user_object.isNull(URL_USER_STATUS) && user_object.get(URL_USER_STATUS) instanceof String) {
                            holder.chip_user_status.setText("Online");
                            holder.chip_user_status.setChipBackgroundColorResource(R.color.green);

                        } else if (!user_object.isNull(URL_USER_STATUS) && user_object.get(URL_USER_STATUS) instanceof Long) {
                            holder.chip_user_status.setText("Offline");
                            holder.chip_user_status.setChipBackgroundColorResource(R.color.grey);
                        } else {
                            holder.chip_user_status.setText("Offline");
                            holder.chip_user_status.setChipBackgroundColorResource(R.color.grey);
                        }
                    } catch (JSONException e) {
                        holder.txt_from_name.setText("Unknown user");
                        holder.chip_user_status.setText("Offline");
                        holder.chip_user_status.setChipBackgroundColorResource(R.color.grey);

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.txt_last_message.setText(conversationModel.last_message);

            if (conversationModel.unread_messages == 0) {
                holder.chip_message_count.setVisibility(View.GONE);
            } else {
                holder.chip_message_count.setVisibility(View.VISIBLE);
                holder.chip_message_count.setText(String.valueOf(conversationModel.unread_messages));
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (conversationModel.from_id != 0) {

                        if (conversationModel.unread_messages > 0) {
                            conversationModel.unread_messages = 0;
                            FBDatabase.setMessageRead(conversationModel.from_id);
                        }

                        context.startActivity(new Intent(context, MessagingActivity.class)
                                .putExtra("FROM_NAME", userModel.first_name)
                                .putExtra("FROM_ID", conversationModel.from_id)
                                .putExtra("FROM_PIC_URL", userModel.img_avatar)
                        );
                    }
                }
            });

            holder.img_from_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (conversationModel.from_id != 0) {
                        context.startActivity(new Intent(context, MemberProfileActivity.class)
                                .putExtra(EXTRA_INT, conversationModel.from_id
                                ));
                    }
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (conversationModel.from_id != 0) {
                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_confirm);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        final TextView txt_message = dialog.findViewById(R.id.txt_message);
                        final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
                        final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                        final TextView txt_title = dialog.findViewById(R.id.txt_title);
                        txt_title.setText(context.getString(R.string.txt_delete));
                        txt_message.setText(context.getString(R.string.txt_delete_desc));
                        btn_confirm.setText(context.getString(R.string.txt_yes));
                        btn_confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FBDatabase.deleteConversation(conversationModel.from_id);
                                conversationModels.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyDataSetChanged();
                                Helpers.logThis(Helpers.TAG_LOG, "MESSAGE DELETED " + holder.getAdapterPosition());
                                dialog.cancel();
                            }
                        });
                        btn_cancel.setVisibility(View.VISIBLE);
                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });
                        dialog.show();

                    }
                    return false;

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return conversationModels.size();
    }

}
