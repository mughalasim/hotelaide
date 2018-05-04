package com.hotelaide.main_pages.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import com.hotelaide.R;
import com.hotelaide.main_pages.models.RestaurantModel;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;

import static com.hotelaide.utils.Helpers.ADAPTER_DEFAULT;
import static com.hotelaide.utils.Helpers.ADAPTER_DISTANCE;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<RestaurantModel> mDataset;
    private final Helpers helper;
    private final Database db;
    private final String ADAPTER_TYPE;

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView
                txtId,
                txtName,
                txtArea,
                txtCuisine,
                txt_no_results;

        private final RoundedImageView
                image,
                offer_icon,
                sponsor_icon;

        private final ImageView
                image_like;

        private final RelativeLayout
                no_list_item,
                list_item;

        private final RatingBar
                rating;

        ViewHolder(View v) {
            super(v);
            txtId = v.findViewById(R.id.id);
            txtName = v.findViewById(R.id.name);
            txtArea = v.findViewById(R.id.area);
            txtCuisine = v.findViewById(R.id.cuisine);
            txt_no_results = v.findViewById(R.id.txt_no_results);

            no_list_item = v.findViewById(R.id.no_list_items);
            list_item = v.findViewById(R.id.list_items);

            image = v.findViewById(R.id.image);
            image_like = v.findViewById(R.id.image_like);
            rating = v.findViewById(R.id.rating);

            offer_icon = v.findViewById(R.id.offer_icon);
            sponsor_icon = v.findViewById(R.id.sponsor_icon);
        }


    }

    public RestaurantAdapter(Context context, ArrayList<RestaurantModel> myDataset, String ADAPTER_TYPE) {
        this.context = context;
        this.mDataset = myDataset;
        this.ADAPTER_TYPE = ADAPTER_TYPE;
        helper = new Helpers(context);
        db = new Database();

    }

    @NonNull
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_restaurant, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final RestaurantModel restaurantModel = mDataset.get(position);
        if (restaurantModel.id == null) {
            holder.txt_no_results.setText(holder.itemView.getContext().getString(R.string.txt_no_restaurants));
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item.setVisibility(View.GONE);

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            if (restaurantModel.premium_level == 0) {
                holder.list_item.setAlpha(0.6f);
            } else {
                holder.list_item.setAlpha(1f);
            }


            holder.txtId.setText(restaurantModel.id);
            holder.txtName.setText(restaurantModel.restaurant_name);

            if (ADAPTER_TYPE.equals(ADAPTER_DEFAULT)) {
                holder.txtArea.setText(restaurantModel.area_name);
            } else if (ADAPTER_TYPE.equals(ADAPTER_DISTANCE)) {
                holder.txtArea.setText(restaurantModel.distance);
            }

            if (!restaurantModel.cuisine_name.equals(""))
                holder.txtCuisine.setText(restaurantModel.cuisine_name.substring(0, restaurantModel.cuisine_name.length() - 2));

            if (restaurantModel.offer_icon.equals("")) {
                holder.offer_icon.setVisibility(View.GONE);
            } else {
                holder.offer_icon.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(restaurantModel.offer_icon)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Glide.with(context).load(R.drawable.ic_no_wifi).into(holder.image);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                helper.animate_wobble(holder.offer_icon);
                                return false;
                            }
                        })
                        .into(holder.offer_icon);
                holder.offer_icon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }

            if (restaurantModel.sponsor_icon.equals("")) {
                holder.sponsor_icon.setVisibility(View.GONE);
            } else {
                holder.sponsor_icon.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(restaurantModel.sponsor_icon)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                helper.animate_wobble(holder.sponsor_icon);
                                return false;
                            }
                        })
                        .into(holder.sponsor_icon);
            }

            if (restaurantModel.average_rating.equals("")) {
                holder.rating.setRating(1);
            } else {
                holder.rating.setRating(Float.valueOf(restaurantModel.average_rating) / 2);
            }

            Glide.with(context).load(restaurantModel.image).into(holder.image);


            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void removeItem(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
    }

    public void updateData(ArrayList<RestaurantModel> viewModels) {
        mDataset.clear();
        mDataset.addAll(viewModels);
        notifyDataSetChanged();
    }

}