package com.hotelaide.main.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotelaide.R;
import com.hotelaide.main.fragments.GalleryViewFragment;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static com.hotelaide.main.activities.EstablishmentActivity.STR_PAGE_TITLE;
import static com.hotelaide.main.activities.EstablishmentActivity.STR_SHARE_LINK;

public class GalleryViewActivity extends AppCompatActivity {
    private Helpers helpers;

    private Toolbar toolbar;

    private ViewPager viewPager;

    private RelativeLayout no_list_items;

    private final String TAG_LOG = "GALLERY VIEW";


    // OVERRIDE FUNCTIONS ==========================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);

        helpers = new Helpers(GalleryViewActivity.this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        findAllViews();

        setUpToolBar();

        handleExtraBundles();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        helpers.dialogShare(GalleryViewActivity.this, STR_SHARE_LINK);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        helpers.myPermissionsDialog(GalleryViewActivity.this, grantResults);
    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        toolbar = findViewById(R.id.toolbar);
        TextView txt_no_results = findViewById(R.id.txt_no_results);
        txt_no_results.setText(getString(R.string.error_unknown));

        no_list_items = findViewById(R.id.rl_no_list_items);
        no_list_items.setVisibility(View.GONE);

        viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager, true);

    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getStringArrayList("image_urls") != null) {
            int selected_position, data_size;

            ArrayList<String> image_urls = extras.getStringArrayList("image_urls");
            selected_position = extras.getInt("selected_position");

            ViewPagerAdapter viewPageradapter = new ViewPagerAdapter(getSupportFragmentManager());

            assert image_urls != null;
            data_size = image_urls.size();

            if (data_size > 0) {
                for (int i = 0; i < data_size; i++) {
                    Bundle bundle = new Bundle();
                    bundle.putString("image_urls", image_urls.get(i));
                    Fragment fragment = new GalleryViewFragment();
                    fragment.setArguments(bundle);
                    viewPageradapter.addFragment(fragment);
                }
                viewPager.setAdapter(viewPageradapter);
                viewPager.setCurrentItem(selected_position);

            } else {
                noImages();
            }

        } else {
            finish();
            helpers.ToastMessage(GalleryViewActivity.this,
                    getString(R.string.error_unknown));
        }
    }

    private void setUpToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setTitle(STR_PAGE_TITLE);
        }
        toolbar.setTitle(STR_PAGE_TITLE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    // VIEWPAGER ADAPTER ===========================================================================
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }


    // NO COLLECTIONS ==============================================================================
    private void noImages() {
        viewPager.removeAllViews();
        no_list_items.setVisibility(View.VISIBLE);
    }

}
