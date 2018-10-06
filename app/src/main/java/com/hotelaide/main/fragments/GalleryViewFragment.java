package com.hotelaide.main.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hotelaide.R;
import com.hotelaide.utils.Helpers;

import java.io.InputStream;
import java.net.URL;


public class GalleryViewFragment extends Fragment {
    private View root_view;

    private String STRimage = "";

    public GalleryViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            STRimage = getArguments().getString("image_urls");

            Helpers.LogThis("GALLERY VIEW FRAGMENT: ", getArguments().toString());

        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            root_view = inflater.inflate(R.layout.fragment_gallery_view, null);

            SubsamplingScaleImageView image = root_view.findViewById(R.id.image);
            image.setMaxScale(20);

            String IMAGE_NOT_LOADED = "0";
            if (image.getTag().toString().equals(IMAGE_NOT_LOADED)) {
                new DownLoadImageTask(image).execute(STRimage);
                String IMAGE_LOADED = "1";
                image.setTag(IMAGE_LOADED);
            }

        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }

        return root_view;
    }

    @SuppressLint("StaticFieldLeak")
    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        final SubsamplingScaleImageView imageView;

        DownLoadImageTask(SubsamplingScaleImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                imageView.setImage(ImageSource.bitmap(result));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}