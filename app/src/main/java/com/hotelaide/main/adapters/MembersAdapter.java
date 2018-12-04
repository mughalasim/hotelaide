package com.hotelaide.main.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hotelaide.R;
import com.hotelaide.main.activities.MemberProfileActivity;
import com.hotelaide.main.models.MemberModel;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
    private final ArrayList<MemberModel> member_models;
    private Context context;
    private Helpers helpers;

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout
                no_list_item;
        CardView
                list_item;
        final TextView
                txt_no_results,
                txt_first_name,
                txt_last_name,
                txt_about_me;
        final ImageView
                img_avatar;

        ViewHolder(View v) {
            super(v);
            txt_first_name = v.findViewById(R.id.txt_first_name);
            txt_last_name = v.findViewById(R.id.txt_last_name);
            txt_about_me = v.findViewById(R.id.txt_about_me);
            img_avatar = v.findViewById(R.id.img_avatar);

            txt_no_results = v.findViewById(R.id.txt_no_results);
            no_list_item = v.findViewById(R.id.rl_no_list_items);
            list_item = v.findViewById(R.id.list_item);
        }

    }

    public MembersAdapter(ArrayList<MemberModel> jobModels) {
        this.member_models = jobModels;
    }

    @NonNull
    @Override
    public MembersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_find_members, parent, false);
        return new MembersAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MembersAdapter.ViewHolder holder, final int position) {
        context = holder.itemView.getContext();
        helpers = new Helpers(context);

        final MemberModel member_model = member_models.get(position);

        if (member_model.id == 0) {
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item.setVisibility(View.GONE);
            if (helpers.validateInternetConnection()) {
                holder.txt_no_results.setText(R.string.error_no_members_found);
            } else {
                holder.txt_no_results.setText(R.string.error_connection);
            }

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            holder.txt_first_name.setText(member_model.first_name);

            holder.txt_last_name.setText(member_model.last_name);

            if (!member_model.about_me.equals("") && !member_model.about_me.equals("null")){
                holder.txt_about_me.setText(member_model.about_me);
            } else{
                holder.txt_about_me.setText("");
            }

            if (member_model.avatar != null && !member_model.avatar.equals("")) {
                Glide.with(context).load(member_model.avatar).into(holder.img_avatar);
            } else {
                Glide.with(context).load("https://live-hotelaide.nyc3.digitaloceanspaces.com/12/300x300.png").into(holder.img_avatar);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (member_model.id != 0) {
                        context.startActivity(new Intent(context, MemberProfileActivity.class)
                                .putExtra("MEMBER_ID", member_model.id)
                        );
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return member_models.size();
    }

    public void updateData(ArrayList<MemberModel> view_model) {
        member_models.clear();
        member_models.addAll(view_model);
        notifyDataSetChanged();
    }

}
