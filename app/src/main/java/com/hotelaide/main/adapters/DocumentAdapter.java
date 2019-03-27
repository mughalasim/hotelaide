package com.hotelaide.main.adapters;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.hotelaide.R;
import com.hotelaide.main.models.DocumentModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Context.DOWNLOAD_SERVICE;


public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {
    private final ArrayList<DocumentModel> document_models;
    private final String TAG_LOG = "DOCUMENT ADAPTER";
    private Context context;
    private Helpers helpers;
    private int final_position = 0;

    class ViewHolder extends RecyclerView.ViewHolder {
        private final RoundedImageView
                btn_delete,
                btn_download,
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
            btn_delete = v.findViewById(R.id.btn_delete);
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

        if (documentModel.is_dirty == 1) {
            holder.list_item.setAlpha(0.5f);

            holder.txt_date_uploaded.setText(documentModel.date_uploaded);
            holder.txt_name.setText(documentModel.name);

            Glide.with(context).load(documentModel.image).into(holder.img_image);

            holder.btn_download.setVisibility(View.GONE);
            holder.btn_delete.setVisibility(View.GONE);


        } else {
            holder.list_item.setAlpha(1.0f);

            final_position = document_models.size();

            holder.txt_date_uploaded.setText(documentModel.date_uploaded);
            holder.txt_name.setText(documentModel.name);

            Glide.with(context).load(documentModel.image).into(holder.img_image);

            holder.btn_download.setVisibility(View.VISIBLE);
            holder.btn_delete.setVisibility(View.VISIBLE);

            holder.btn_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (EasyPermissions.hasPermissions(context, perms)) {
                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_confirm);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        final TextView txt_message = dialog.findViewById(R.id.txt_message);
                        final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
                        final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                        final TextView txt_title = dialog.findViewById(R.id.txt_title);
                        txt_title.setText("Download");
                        txt_message.setText("You are about to download " + documentModel.name + " \nAre you sure you wish to proceed?");
                        btn_confirm.setText(context.getString(R.string.txt_yes));
                        btn_confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DownloadManager.Request r = new DownloadManager.Request(Uri.parse(documentModel.file_url));
                                r.setTitle(context.getString(R.string.app_name));
                                r.setDescription("Downloading " + documentModel.name);
                                r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/HotelAide/" + "/" + documentModel.name+ ".pdf");
                                r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                                dm.enqueue(r);
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
                    } else {
                        int grantResults[] = {-1, -1, -1};
                        helpers.myPermissionsDialog(grantResults);
                    }
                }
            });

            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_confirm);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    final TextView txt_message = dialog.findViewById(R.id.txt_message);
                    final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
                    final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                    final TextView txt_title = dialog.findViewById(R.id.txt_title);
                    txt_title.setText("Delete");
                    txt_message.setText("You are about to delete " + documentModel.name + " \nAre you sure you wish to proceed?");
                    btn_confirm.setText(context.getString(R.string.txt_yes));
                    btn_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            helpers.toastMessage("Deleting document, please wait...");
                            HelpersAsync.asyncDeleteDocument(documentModel.id);
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
            });

        }
    }

    public void updateData(ArrayList<DocumentModel> view_model) {
        document_models.clear();
        document_models.addAll(view_model);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return document_models.size();
    }

}