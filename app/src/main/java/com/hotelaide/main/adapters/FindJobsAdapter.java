package com.hotelaide.main.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hotelaide.R;
import com.hotelaide.main.activities.FindJobsActivity;
import com.hotelaide.main.activities.JobActivity;
import com.hotelaide.main.models.JobModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.StaticVariables;

import java.util.ArrayList;

import static com.hotelaide.main.activities.FindJobsActivity.CURRENT_PAGE;
import static com.hotelaide.main.activities.FindJobsActivity.LAST_PAGE;

public class FindJobsAdapter extends RecyclerView.Adapter<FindJobsAdapter.ViewHolder> {
    private final ArrayList<JobModel> jobModels;
    private Context context;
    private Helpers helpers;
    private String error_message = "";

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

    public FindJobsAdapter(ArrayList<JobModel> jobModels, String error_message) {
        this.jobModels = jobModels;
        this.error_message = error_message;
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
                holder.txt_no_results.setText(error_message);
            } else {
                holder.txt_no_results.setText(R.string.error_connection);
            }

        } else {
            holder.no_list_item.setVisibility(View.GONE);
            holder.list_item.setVisibility(View.VISIBLE);

            holder.txt_name.setText(jobModel.name);
            holder.txt_posted_on.setText(context.getString(R.string.txt_posted_on).concat(jobModel.posted_on));
            holder.txt_location.setText(jobModel.establishment_location);
            Glide.with(context).load(jobModel.establishment_image).into(holder.img_image);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (jobModel.id != 0 && !jobModel.name.equals("")) {
                        StaticVariables.INT_JOB_ID = jobModel.id;
                        context.startActivity(new Intent(context, JobActivity.class));
                    }
                }
            });

            if (context instanceof FindJobsActivity && position == (getItemCount() - 1)) {
                if (CURRENT_PAGE < LAST_PAGE) {
                    Helpers.logThis("MEMBERS ADAPTER", "LAST PAGE REACHED");
                    ((FindJobsActivity)context).loadMoreResults();
                }
            }
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
