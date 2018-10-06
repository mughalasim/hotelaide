package com.hotelaide.main.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hotelaide.R;
import com.hotelaide.main.activities.JobActivity;
import com.hotelaide.main.models.JobModel;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;

public class FindJobsAdapter extends RecyclerView.Adapter<FindJobsAdapter.ViewHolder> {
    private final ArrayList<JobModel> jobModels;
    //        private final String TAG_LOG = "FIND JOB ADAPTER";
    private Context context;
    private Helpers helpers;

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout
                no_list_item;
        CardView
                list_item;
        final TextView
                txt_no_results,
                txt_name,
                txt_location,
                txt_posted_on;
        final ImageView
                img_image;

        ViewHolder(View v) {
            super(v);
            txt_no_results = v.findViewById(R.id.txt_no_results);
            txt_name = v.findViewById(R.id.txt_name);
            txt_location = v.findViewById(R.id.txt_location);
            txt_posted_on = v.findViewById(R.id.txt_posted_on);
            img_image = v.findViewById(R.id.img_image);
            no_list_item = v.findViewById(R.id.rl_no_list_items);
            list_item = v.findViewById(R.id.list_item);
        }

    }

    public FindJobsAdapter(ArrayList<JobModel> jobModels) {
        this.jobModels = jobModels;
    }

    @NonNull
    @Override
    public FindJobsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_find_jobs, parent, false);
        return new FindJobsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FindJobsAdapter.ViewHolder holder, final int position) {
        context = holder.itemView.getContext();
        helpers = new Helpers(context);

        final JobModel jobModel = jobModels.get(position);

        if (jobModel.id == 0) {
            holder.no_list_item.setVisibility(View.VISIBLE);
            holder.list_item.setVisibility(View.GONE);
            if (helpers.validateInternetConnection()) {
                holder.txt_no_results.setText(R.string.error_no_jobs);
            } else {
                holder.txt_no_results.setText(R.string.error_connection);
            }

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            holder.txt_name.setText(jobModel.name);
            holder.txt_posted_on.setText(jobModel.posted_on);
            holder.txt_location.setText(jobModel.hotel_location);
            Glide.with(context).load(jobModel.hotel_image).into(holder.img_image);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(jobModel.id!=0){
                        context.startActivity(new Intent(context, JobActivity.class)
                                .putExtra("JOB_ID", jobModel.id)
                        );
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return jobModels.size();
    }

    public void updateData(ArrayList<JobModel> view_model) {
        jobModels.clear();
        jobModels.addAll(view_model);
        notifyDataSetChanged();
    }

}
