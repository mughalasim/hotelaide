package com.hotelaide.main.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.hotelaide.R;
import com.hotelaide.main.models.DocumentModel;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;


public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {
    private final ArrayList<DocumentModel> document_models;
    private final String TAG_LOG = "DOCUMENT ADAPTER";
    private Context context;
    private Helpers helpers;
    private int final_position = 0;

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView
                image;

        private final RelativeLayout
                no_list_item,
                list_item;

        ViewHolder(View v) {
            super(v);
            no_list_item = v.findViewById(R.id.rl_no_list_items);
            list_item = v.findViewById(R.id.list_items);
            image = v.findViewById(R.id.image);
        }

    }

    public DocumentAdapter(ArrayList<DocumentModel> document_models) {
        this.document_models = document_models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_gallery, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        context = holder.itemView.getContext();
        helpers = new Helpers(context);

        final DocumentModel gallery_model = document_models.get(position);

        if (gallery_model.id == 0) {
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item.setVisibility(View.GONE);

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            final_position = document_models.size();

            Glide.with(context).load(gallery_model.image).into(holder.image);

        }
    }

    @Override
    public int getItemCount() {
        return document_models.size();
    }

}