package com.hotelaide.main.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.hotelaide.R;
import com.hotelaide.main.activities.ProfileActivity;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;
import com.makeramen.roundedimageview.RoundedImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_APPLIED;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_SAVED;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_DASH;
import static com.hotelaide.utils.StaticVariables.USER_IMG_AVATAR;
import static com.hotelaide.utils.StaticVariables.USER_PROFILE_COMPLETION;

public class DashboardFragment extends Fragment {
    private View root_view;
    private Helpers helpers;
    private Database db;
    private String TAG_LOG = "DASH FRAG";

    private TextView
            txt_welcome,
            txt_applied_jobs,
            txt_saved_jobs,
            txt_shortlisted,
            txt_interviews,
            txt_progress,
            txt_unread_notifications,
            txt_unread_messages;

    private SeekBar
            seek_bar_progress;

    private RelativeLayout
            rl_progress;

    private RelativeLayout
            rl_messages,
            rl_notifications;

    private RoundedImageView
            img_avatar;


    public DashboardFragment() {

    }

    // OVERRIDE METHODS ============================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            root_view = inflater.inflate(R.layout.frag_dashboard, container, false);

            helpers = new Helpers(getActivity());
            db = new Database();

            findAllViews();

            root_view.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                @Override
                public void onDraw() {
                    // Immediately detach the listener so it only is called once
//                    root_view.getViewTreeObserver().removeOnDrawListener(this);
                    // FIRST LAUNCH
                    helpers.setTarget(
                            getActivity(),
                            FIRST_LAUNCH_DASH,
                            new View[]{
                                    root_view.findViewById(R.id.txt_applied_jobs),
                                    root_view.findViewById(R.id.txt_saved_jobs),
                                    root_view.findViewById(R.id.txt_shortlisted),
                                    root_view.findViewById(R.id.txt_interviews),
                                    root_view.findViewById(R.id.rl_messages),
                                    root_view.findViewById(R.id.rl_progress)
                            },
                            new String[]{
                                    "Applied Jobs",
                                    "Saved Jobs",
                                    "Shortlisted",
                                    "Interviews",
                                    "Updates",
                                    "Profile Progress",
                            },
                            new String[]{
                                    "This will show you how many jobs you have applied for",
                                    "This will show you how many jobs you have saved for later review",
                                    "How many employers have shortlisted you, see that here",
                                    "How many employers have invited you for an interview",
                                    "All your updates in one place, from Messages to notifications",
                                    "And lastly....Make sure you fill out your profile completely to increase your chances of getting employed"
                            }
                    );

                }
            });

            fetchFacebookPosts();

        } else {
            container.removeView(root_view);
        }

        return root_view;
    }

    @Override
    public void onResume() {
        helpers.setWelcomeMessage(txt_welcome);
        updateDashboard();
        super.onResume();
    }

    // BASIC METHODS ===============================================================================
    @SuppressLint("ClickableViewAccessibility")
    private void findAllViews() {
        // IMAGE
        img_avatar = root_view.findViewById(R.id.img_avatar);

        // WELCOME GREETING
        txt_welcome = root_view.findViewById(R.id.txt_welcome);

        // STATS BAR
        txt_applied_jobs = root_view.findViewById(R.id.txt_applied_jobs);
        txt_saved_jobs = root_view.findViewById(R.id.txt_saved_jobs);
        txt_shortlisted = root_view.findViewById(R.id.txt_shortlisted);
        txt_interviews = root_view.findViewById(R.id.txt_interviews);

        // PROGRESS
        rl_progress = root_view.findViewById(R.id.rl_progress);
        seek_bar_progress = root_view.findViewById(R.id.seek_bar_progress);
        txt_progress = root_view.findViewById(R.id.txt_progress);
        seek_bar_progress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // NEW MESSAGES
        rl_messages = root_view.findViewById(R.id.rl_messages);
        txt_unread_messages = root_view.findViewById(R.id.txt_unread_messages);

        // NEW NOTIFICATIONS
        rl_notifications = root_view.findViewById(R.id.rl_notifications);
        txt_unread_notifications = root_view.findViewById(R.id.txt_unread_notifications);
    }

    private void updateDashboard() {
        Glide.with(this).load(SharedPrefs.getString(USER_IMG_AVATAR)).into(img_avatar);

        txt_applied_jobs.setText(db.getFilteredTableCount(FILTER_TYPE_APPLIED));
        txt_saved_jobs.setText(db.getFilteredTableCount(FILTER_TYPE_SAVED));

        // PROFILE PROGRESS
        updateProfileSeekBar(SharedPrefs.getInt(USER_PROFILE_COMPLETION));

        // NOTIFICATIONS
        int notification_size = db.getAllUnreadNotifications();
        if (notification_size > 0) {
            txt_unread_notifications.setText(String.valueOf(notification_size));
            rl_notifications.setVisibility(View.VISIBLE);
        } else {
            rl_notifications.setVisibility(View.GONE);
        }

        // MESSAGES
//        int message_size = db.getAllUnreadNotifications();
//        if (message_size > 0) {
//            txt_unread_messages.setText(String.valueOf(message_size));
//            rl_messages.setVisibility(View.VISIBLE);
//        } else {
//        }
        rl_messages.setVisibility(View.GONE);


    }

    private void fetchFacebookPosts() {
        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + getString(R.string.FACEBOOK_APP_ID) + "/feed",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        Helpers.logThis(TAG_LOG, response.toString());
                    }
                }
        ).executeAsync();
    }

    private void updateProfileSeekBar(int completion) {
        if (completion == 100) {
            rl_progress.setVisibility(View.GONE);
        } else {
            rl_progress.setVisibility(View.VISIBLE);
            seek_bar_progress.setProgress(completion);
            txt_progress.setText(String.valueOf(completion).concat("%"));
            rl_progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                }
            });
        }
    }

}