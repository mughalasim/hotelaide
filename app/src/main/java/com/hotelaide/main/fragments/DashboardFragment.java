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

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.hotelaide.R;
import com.hotelaide.main.activities.ProfileActivity;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_APPLIED;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_SAVED;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_DASH;
import static com.hotelaide.utils.StaticVariables.USER_PROFILE_COMPLETION;

public class DashboardFragment extends Fragment {
    private View root_view;
    private Helpers helpers;
    private Database db;
    private String TAG_LOG = "DASH FRAG";

    private TextView
            txt_welcome,
            txt_applied_jobs,
            txt_shortlisted,
            txt_interviews,
            txt_saved_jobs,
            txt_profile_views,
            txt_unread_messages,
            txt_progress,
            txt_unread_notifications;

    private SeekBar
            seek_bar_progress;

    private RelativeLayout
            rl_progress;

    private RelativeLayout
            rl_notifications;


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
                                    root_view.findViewById(R.id.txt_shortlisted),
                                    root_view.findViewById(R.id.txt_interviews),
                                    root_view.findViewById(R.id.txt_saved_jobs),
                                    root_view.findViewById(R.id.txt_profile_views),
                                    root_view.findViewById(R.id.txt_unread_messages)
                            },
                            new String[]{
                                    getString(R.string.nav_applied),
                                    getString(R.string.nav_shortlisted),
                                    getString(R.string.nav_interviews),
                                    getString(R.string.nav_saved),
                                    getString(R.string.nav_profile_views),
                                    getString(R.string.nav_messages),
                            },
                            new String[]{
                                    "This will show you how many jobs you have applied for",
                                    "This will show you how many jobs you have been short listed for",
                                    "This will show you how many job interviews you have",
                                    "Here you can save a job listing for later application",
                                    "How many users have visited your profile",
                                    "And lastly....See who sent you a message"
                            }
                    );

                }
            });

//            fetchFacebookPosts();

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
        // WELCOME GREETING
        txt_welcome = root_view.findViewById(R.id.txt_welcome);

        // STATS BAR
        txt_applied_jobs = root_view.findViewById(R.id.txt_applied_jobs);
        txt_saved_jobs = root_view.findViewById(R.id.txt_saved_jobs);
        txt_shortlisted = root_view.findViewById(R.id.txt_shortlisted);
        txt_interviews = root_view.findViewById(R.id.txt_interviews);
        txt_profile_views = root_view.findViewById(R.id.txt_profile_views);
        txt_unread_messages = root_view.findViewById(R.id.txt_unread_messages);

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

        // NEW NOTIFICATIONS
        rl_notifications = root_view.findViewById(R.id.rl_notifications);
        txt_unread_notifications = root_view.findViewById(R.id.txt_unread_notifications);
    }

    private void updateDashboard() {
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
        int message_size = db.getAllUnreadNotifications();
        if (message_size > 0) {
            txt_unread_messages.setText(String.valueOf(message_size));
        } else{
            txt_unread_messages.setText(R.string.txt_zero);
        }

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