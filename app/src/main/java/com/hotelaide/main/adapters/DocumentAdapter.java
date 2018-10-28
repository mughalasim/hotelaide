package com.hotelaide.main.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hotelaide.R;
import com.hotelaide.main.models.DocumentModel;
import com.hotelaide.utils.Helpers;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {
    private final ArrayList<DocumentModel> document_models;
    private final String TAG_LOG = "DOCUMENT ADAPTER";
    private Context context;
    private Helpers helpers;
    private int final_position = 0;

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView
                btn_download;

        private final RoundedImageView
                img_image;

        private final TextView
                txt_name,
                txt_date_uploaded;

        private final CardView
                list_item;

        ViewHolder(View v) {
            super(v);
            list_item = v.findViewById(R.id.list_item);
            img_image = v.findViewById(R.id.img_image);
            btn_download = v.findViewById(R.id.btn_download);
            txt_name = v.findViewById(R.id.txt_name);
            txt_date_uploaded = v.findViewById(R.id.txt_date_uploaded);
        }

    }

    public DocumentAdapter(ArrayList<DocumentModel> document_models) {
        this.document_models = document_models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_document, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        context = holder.itemView.getContext();
        helpers = new Helpers(context);

        final DocumentModel documentModel = document_models.get(position);

        if (documentModel.id == 0) {
            holder.list_item.setAlpha(0.5f);

            holder.txt_date_uploaded.setText(documentModel.date_uploaded);
            holder.txt_name.setText(documentModel.name);

            Glide.with(context).load(documentModel.image).into(holder.img_image);
            holder.btn_download.setVisibility(View.GONE);


        } else {
            holder.list_item.setAlpha(1.0f);

            final_position = document_models.size();

            holder.txt_date_uploaded.setText(documentModel.date_uploaded);
            holder.txt_name.setText(documentModel.name);

            Glide.with(context).load(documentModel.image).into(holder.img_image);

            holder.btn_download.setVisibility(View.VISIBLE);
        }
    }
    public void updateData(ArrayList<DocumentModel> view_model) {
        document_models.clear();
        document_models.addAll(view_model);
        notifyDataSetChanged();
    }

    public void removeLoadingItem(){
        document_models.remove(final_position);
        updateData(document_models);
    }

    @Override
    public int getItemCount() {
        return document_models.size();
    }

}