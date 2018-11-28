package com.hotelaide.main.activities;

import android.os.Bundle;

import com.hotelaide.R;
import com.hotelaide.main.fragments.MessageFragment;
import com.hotelaide.main.fragments.NotificationFragment;

import androidx.fragment.app.Fragment;

public class MyMessages extends ParentActivity {
    
    private int[] fragment_title_list = {
            R.string.nav_inbox,
            R.string.nav_notifications
    };

    private final String[] fragment_extras = {
            "",
            ""
    };

    private Fragment[] fragment_list = {
            new MessageFragment(),
            new NotificationFragment(),
    };

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_pager);

        initialize(R.id.drawer_my_messages, getString(R.string.drawer_my_messages));

        setupViewPager(fragment_list, fragment_title_list, fragment_extras);

    }

}
