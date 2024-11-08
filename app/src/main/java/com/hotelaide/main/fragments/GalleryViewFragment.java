package com.hotelaide.main.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hotelaide.R;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;

import java.io.InputStream;
import java.net.URL;


public class GalleryViewFragment extends Fragment {
    private View root_view;

    private String STR_IMAGE_URL = "";

    public GalleryViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            STR_IMAGE_URL = getArguments().getString("image_urls");

            Helpers.logThis("GALLERY VIEW: ", getArguments().toString());

            HelpersAsync.setTrackerPage("GALLERY VIEW");

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            root_view = inflater.inflate(R.layout.frag_gallery_view, container, false);

            SubsamplingScaleImageView image = root_view.findViewById(R.id.frag_image);
            image.setMaxScale(20);

            String IMAGE_NOT_LOADED = "0";
            if (image.getTag().toString().equals(IMAGE_NOT_LOADED)) {
                new DownLoadImageTask(image).execute(STR_IMAGE_URL);
                String IMAGE_LOADED = "1";
                image.setTag(IMAGE_LOADED);
            }

        } else {
            container.removeView(root_view);
        }

        return root_view;
    }

    @SuppressLint("StaticFieldLeak")
    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        final SubsamplingScaleImageView image_view;

        DownLoadImageTask(SubsamplingScaleImageView imageView) {
            this.image_view = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                Helpers.logThis(Helpers.TAG_LOG, e.toString());
                image_view.setBackgroundResource(R.drawable.ic_image_box);
            }
            return logo;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                image_view.setImage(ImageSource.bitmap(result));
            } catch (Exception e) {
                Helpers.logThis(Helpers.TAG_LOG, e.toString());
                image_view.setBackgroundResource(R.drawable.ic_image_box);
                e.printStackTrace();
            }
        }
    }


}