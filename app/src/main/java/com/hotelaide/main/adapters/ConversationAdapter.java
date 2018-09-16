package com.hotelaide.main.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotelaide.R;
import com.hotelaide.main.models.ConversationModel;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private final ArrayList<ConversationModel> conversationModels;
    private int FROM_ID;
    private Context context;
    private Helpers helpers;

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout
                no_list_item,
                list_item;
        final TextView
                txt_no_results,
                txt_text_from,
                txt_text_to;

        ViewHolder(View v) {
            super(v);
            txt_no_results = v.findViewById(R.id.txt_no_results);
            txt_text_from = v.findViewById(R.id.txt_text_from);
            txt_text_to = v.findViewById(R.id.txt_text_to);
            no_list_item = v.findViewById(R.id.no_list_items);
            list_item = v.findViewById(R.id.list_item);
        }

    }

    public ConversationAdapter(ArrayList<ConversationModel> conversationModels, int FROM_ID) {
        this.conversationModels = conversationModels;
        this.FROM_ID = FROM_ID;
    }

    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_conversation, parent, false);
        return new ConversationAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ConversationAdapter.ViewHolder holder, final int position) {
        context = holder.itemView.getContext();
        helpers = new Helpers(context);

        final ConversationModel conversationModel = conversationModels.get(position);

        if (conversationModel.is_empty) {
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item.setVisibility(View.GONE);
            if (helpers.validateInternetConnection()) {
                holder.txt_no_results.setText("Start a conversation");
            } else {
                holder.txt_no_results.setText(R.string.error_connection);
            }

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            if (conversationModel.from_id == FROM_ID) {
                holder.txt_text_from.setText(conversationModel.text);
                holder.txt_text_to.setVisibility(View.GONE);
            } else {
                holder.txt_text_to.setText(conversationModel.text);
                holder.txt_text_from.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return conversationModels.size();
    }
}
