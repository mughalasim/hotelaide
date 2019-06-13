package com.hotelaide.main.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.hotelaide.R;
import com.hotelaide.main.fragments.ConversationFragment;
import com.hotelaide.main.fragments.NotificationFragment;

import static com.hotelaide.utils.StaticVariables.EXTRA_CONVERSATIONS;
import static com.hotelaide.utils.StaticVariables.EXTRA_NOTIFICATIONS;

public class ConversationActivity extends ParentActivity {
    
    private int[] fragment_title_list = {
            R.string.nav_conversations,
            R.string.nav_notifications
    };

    private final String[] fragment_extras = {
            "",
            ""
    };

    private Fragment[] fragment_list = {
            new ConversationFragment(),
            new NotificationFragment(),
    };

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_pager);

        initialize(R.id.drawer_conversations, getString(R.string.drawer_conversations));

        setupViewPager(fragment_list, fragment_title_list, fragment_extras);

        setUpHomeSearch();

    }

    @Override
    public void onResume() {
        handleExtraBundles();
        super.onResume();
    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString(EXTRA_CONVERSATIONS) != null) {
            view_pager.setCurrentItem(0);
        } else if (extras != null && extras.getString(EXTRA_NOTIFICATIONS) != null) {
            view_pager.setCurrentItem(1);
        }
    }

}
