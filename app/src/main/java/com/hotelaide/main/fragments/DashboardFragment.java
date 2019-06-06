package com.hotelaide.main.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;
import com.hotelaide.utils.SharedPrefs;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.hotelaide.utils.StaticVariables.BROADCAST_SET_USER;
import static com.hotelaide.utils.StaticVariables.BROADCAST_SET_USER_STATS;
import static com.hotelaide.utils.StaticVariables.EXTRA_FAILED;
import static com.hotelaide.utils.StaticVariables.EXTRA_PASSED;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_APPLIED;
import static com.hotelaide.utils.StaticVariables.FILTER_TYPE_SAVED;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH_DASH;
import static com.hotelaide.utils.StaticVariables.USER_PROFILE_COMPLETION;
import static com.hotelaide.utils.StaticVariables.db;

public class DashboardFragment extends Fragment {
    private View root_view;
    private Helpers helpers;

    private String TAG_LOG = "DASH FRAG";

    private TickerView
            txt_applied_jobs,
            txt_shortlisted,
            txt_interviews,
            txt_saved_jobs,
            txt_profile_views,
            txt_unread_messages;

    private TextView
            txt_welcome,
            txt_progress,
            txt_unread_notifications;

    private SeekBar
            seek_bar_progress;

    private RelativeLayout
            rl_progress;

    private RelativeLayout
            rl_notifications;

    private SwipeRefreshLayout swipe_refresh;

    private BroadcastReceiver receiver;


    public DashboardFragment() {

    }

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            root_view = inflater.inflate(R.layout.frag_dashboard, container, false);

            helpers = new Helpers(getActivity());

            findAllViews();

            updateDashboard(null);

            setListeners();

            swipe_refresh.setRefreshing(true);

            helpers.setTarget(
                    getActivity(),
                    FIRST_LAUNCH_DASH,
                    new View[]{
                            root_view.findViewById(R.id.ll_applied_jobs),
                            root_view.findViewById(R.id.ll_shortlisted),
                            root_view.findViewById(R.id.ll_interviews),
                            root_view.findViewById(R.id.ll_saved_jobs),
                            root_view.findViewById(R.id.ll_profile_views),
                            root_view.findViewById(R.id.ll_unread_messages)
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

        } else {
            container.removeView(root_view);
        }

        return root_view;
    }

    @Override
    public void onResume() {
        helpers.setWelcomeMessage(txt_welcome);
        HelpersAsync.asyncGetUserStats();
        if (receiver == null) {
            listenSetUserBroadcast();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (receiver != null && getActivity() != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
        super.onPause();
    }

    // BASIC METHODS ===============================================================================
    @SuppressLint("ClickableViewAccessibility")
    private void findAllViews() {
        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
        helpers.animateSwipeRefresh(swipe_refresh);

        // WELCOME GREETING
        txt_welcome = root_view.findViewById(R.id.txt_welcome);

        // STATS BAR
        txt_applied_jobs = root_view.findViewById(R.id.txt_applied_jobs);
        txt_saved_jobs = root_view.findViewById(R.id.txt_saved_jobs);
        txt_shortlisted = root_view.findViewById(R.id.txt_shortlisted);
        txt_interviews = root_view.findViewById(R.id.txt_interviews);
        txt_profile_views = root_view.findViewById(R.id.txt_profile_views);
        txt_unread_messages = root_view.findViewById(R.id.txt_unread_messages);

        txt_applied_jobs.setCharacterLists(TickerUtils.provideNumberList());
        txt_saved_jobs.setCharacterLists(TickerUtils.provideNumberList());
        txt_shortlisted.setCharacterLists(TickerUtils.provideNumberList());
        txt_interviews.setCharacterLists(TickerUtils.provideNumberList());
        txt_profile_views.setCharacterLists(TickerUtils.provideNumberList());
        txt_unread_messages.setCharacterLists(TickerUtils.provideNumberList());

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
        seek_bar_progress.setPadding(0, 0, 0, 0);

        // NEW NOTIFICATIONS
        rl_notifications = root_view.findViewById(R.id.rl_notifications);
        txt_unread_notifications = root_view.findViewById(R.id.txt_unread_notifications);
    }

    private void setListeners() {
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HelpersAsync.asyncGetUserStats();
                swipe_refresh.setRefreshing(true);
                txt_applied_jobs.setText(getString(R.string.txt_zero));
                txt_saved_jobs.setText(getString(R.string.txt_zero));
                txt_shortlisted.setText(getString(R.string.txt_zero));
                txt_interviews.setText(getString(R.string.txt_zero));
                txt_profile_views.setText(getString(R.string.txt_zero));
                txt_unread_messages.setText(getString(R.string.txt_zero));
            }
        });
    }

    private void updateDashboard(JSONObject data) {
        if (data == null) {
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
//        } else {
//            txt_unread_messages.setText(R.string.txt_zero);
//        }
        } else {
            try {
                txt_shortlisted.setText(data.getString("shortlisted_jobs"));
                txt_interviews.setText(data.getString("interview_invites"));
                txt_applied_jobs.setText(data.getString("applications"));

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

            } catch (JSONException e) {
                e.printStackTrace();
                helpers.toastMessage("Update failed, please try again later");
            }
        }

    }

    private void listenSetUserBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_SET_USER_STATS);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    swipe_refresh.setRefreshing(false);
                    if (intent.getExtras().getString(EXTRA_PASSED) != null) {
                        try {
                            Helpers.logThis(TAG_LOG, "PASSED");
                            updateDashboard(new JSONObject(intent.getStringExtra("data")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            helpers.toastMessage("Update failed, please try again later");
                        }
                    } else if (intent.getExtras().getString(EXTRA_FAILED) != null) {
                        Helpers.logThis(TAG_LOG, "FAILED");
                        helpers.toastMessage("Update failed, please try again later");
                    }
                }
            }
        };
        if (getActivity() != null)
            getActivity().registerReceiver(receiver, filter);
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
            if (completion < 10) {
                seek_bar_progress.setProgress(10);
                txt_progress.setText("10%");
            } else {
                seek_bar_progress.setProgress(completion);
                txt_progress.setText(String.valueOf(completion).concat("%"));
            }
        }
    }

}