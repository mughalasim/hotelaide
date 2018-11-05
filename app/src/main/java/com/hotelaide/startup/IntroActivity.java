package com.hotelaide.startup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.eftimoff.viewpagertransformers.ForegroundToBackgroundTransformer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.hotelaide.R;
import com.hotelaide.startup.fragments.StartUpIntroFragment;
import com.hotelaide.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class IntroActivity extends FragmentActivity {

    public final static String
            EXTRA_IMAGE = "IMAGE_ID",
            EXTRA_DESC = "DESC";

    private final String
            TAG_LOG = "INTRO";

    private MaterialButton
            btn_confirm;

    private ViewPagerAdapter
            adapter;

    private ViewPager
            view_pager;

    private TabLayout
            tab_layout;

    int[] images = {
            R.drawable.img_scroll1,
            R.drawable.img_scroll2,
            R.drawable.img_scroll3,
            R.drawable.img_scroll5,
            R.drawable.img_scroll4
    };

    String[] descriptions = {
            "Welcome to HotelAide",
            "Looking to get hired quick?",
            "The best employment App in kenya",
            "Message your future employer directly",
            "Sign up today and get started..."
    };

    private Helpers helpers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        helpers = new Helpers(IntroActivity.this);

        findAllViews();

        setListeners();

    }

    private void findAllViews() {
        btn_confirm = findViewById(R.id.btn_confirm);
        view_pager = findViewById(R.id.view_pager);
        tab_layout = findViewById(R.id.tabs);
    }

    private void setListeners() {
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
            }
        });

        setupViewPager(view_pager);
        tab_layout.setupWithViewPager(view_pager, true);

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Helpers.LogThis(TAG_LOG, "PAGE NO. " + i);
                Helpers.LogThis(TAG_LOG, "ARRAY SIZE. " + images.length);

                if (i == (images.length - 1)) {
                    btn_confirm.setVisibility(View.VISIBLE);
                    helpers.animateFadeIn(btn_confirm);
                } else {
                    btn_confirm.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        view_pager.setPageTransformer(true, new ForegroundToBackgroundTransformer());

    }

    private void setupViewPager(ViewPager view_pager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < images.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt(EXTRA_IMAGE, images[i]);
            bundle.putString(EXTRA_DESC, descriptions[i]);
            Fragment fragment = new StartUpIntroFragment();
            fragment.setArguments(bundle);
            adapter.addFragment(fragment);
        }

        view_pager.setAdapter(adapter);
        view_pager.setOffscreenPageLimit(4);
    }

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


}
