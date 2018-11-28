package com.hotelaide.main.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.hotelaide.R;
import com.hotelaide.main.activities.GalleryViewActivity;
import com.hotelaide.main.models.GalleryModel;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private final ArrayList<GalleryModel> gallery_models;
    private final String TAG_LOG = "GALLERY ADAPTER";
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

    public GalleryAdapter(ArrayList<GalleryModel> gallery_models) {
        this.gallery_models = gallery_models;
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

        final GalleryModel gallery_model = gallery_models.get(position);

        if (gallery_model.id == 0) {
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item.setVisibility(View.GONE);

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            final_position = gallery_models.size();

            Glide.with(context)
                    .load(gallery_model.image)
                    .into(holder.image);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> image_urls = new ArrayList<>();
                    for (int i = 0; i < final_position; i++) {
                        GalleryModel galleryModel = gallery_models.get(i);
                        image_urls.add(galleryModel.image);
                        Helpers.logThis(TAG_LOG, image_urls.get(i));
                    }

                    if (helpers.validateInternetConnection()) {
                        Activity activity = (Activity) context;
                        activity.startActivity(new Intent(context, GalleryViewActivity.class)
                                .putExtra("image_urls", image_urls)
                                .putExtra("selected_position", holder.getAdapterPosition())
                        );
                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        helpers.ToastMessage(context, context.getString(R.string.error_connection));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return gallery_models.size();
    }

}