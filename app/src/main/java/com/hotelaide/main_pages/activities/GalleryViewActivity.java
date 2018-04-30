package com.hotelaide.main_pages.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import com.hotelaide.R;
import com.hotelaide.main_pages.fragments.GalleryViewFragment;
import com.hotelaide.services.TrackingService;
import com.hotelaide.utils.Helpers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.main_pages.activities.RestaurantDetailsActivity.STR_SHARE_LINK;
import static com.hotelaide.main_pages.eo_activities.RestaurantDetailsActivity.STR_SHARE_LINK;

public class GalleryViewActivity extends AppCompatActivity {
    private Helpers helper;

    private Toolbar toolbar;

    private TextView delivery_phone;

    private ViewPager viewPager;

    private RelativeLayout no_list_items;

    private String
            STR_RESTAURANT_ID = "",
            STR_DELIVERY_PARTNER_ID = "",
            STR_DELIVERY_PARTNER_NAME = "",
            STR_DELIVERY_PARTNER_PHONE = "",
            TAG_LOG = "GALLERY VIEW";


    // OVERRIDE FUNCTIONS ==========================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);

        helper = new Helpers(GalleryViewActivity.this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        findAllViews();

        setUpToolBar();

        handleExtraBundles();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                final Dialog dialog = new Dialog(GalleryViewActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_share);
                final ImageView share_facebook = dialog.findViewById(R.id.share_facebook);
                final ImageView share_email = dialog.findViewById(R.id.share_email);
                final ImageView share_messenger = dialog.findViewById(R.id.share_messenger);
                final ImageView share_sms = dialog.findViewById(R.id.share_sms);
                final ImageView share_whatsapp = dialog.findViewById(R.id.share_whatsapp);

                share_facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (helper.validateAppIsInstalled("com.facebook.katana")) {
                            ShareLinkContent content = new ShareLinkContent.Builder()
                                    .setContentUrl(Uri.parse(STR_SHARE_LINK))
                                    .build();
                            ShareDialog.show(GalleryViewActivity.this, content);
                            dialog.cancel();
                        } else {
                            helper.ToastMessage(GalleryViewActivity.this, getString(R.string.error_app_not_installed));
                        }
                    }
                });

                share_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setType("text/html");
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                            emailIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARE_LINK);
                            startActivity(Intent.createChooser(emailIntent, "Send Email"));
                            dialog.cancel();
                        } catch (Exception e) {
                            helper.ToastMessage(GalleryViewActivity.this, getString(R.string.error_app_not_installed));
                        }
                    }
                });

                share_messenger.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (helper.validateAppIsInstalled("com.facebook.orca")) {
                            Intent messengerIntent = new Intent();
                            messengerIntent.setAction(Intent.ACTION_SEND);
                            messengerIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARE_LINK);
                            messengerIntent.setType("text/plain");
                            messengerIntent.setPackage("com.facebook.orca");
                            startActivity(messengerIntent);
                        } else {
                            helper.ToastMessage(GalleryViewActivity.this, getString(R.string.error_app_not_installed));
                        }
                    }
                });

                share_sms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.putExtra("sms_body", STR_SHARE_LINK);
                            smsIntent.setType("vnd.android-dir/mms-sms");
                            startActivity(smsIntent);
                            dialog.cancel();
                        } catch (Exception e) {
                            helper.ToastMessage(GalleryViewActivity.this, getString(R.string.error_app_not_installed));
                        }
                    }
                });

                share_whatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (helper.validateAppIsInstalled("com.whatsapp")) {
                            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                            whatsappIntent.setType("text/plain");
                            whatsappIntent.setPackage("com.whatsapp");
                            whatsappIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARE_LINK);
                            startActivity(whatsappIntent);
                            dialog.cancel();
                        } else {
                            helper.ToastMessage(GalleryViewActivity.this, getString(R.string.error_app_not_installed));
                        }
                    }
                });

                dialog.setCancelable(true);
                dialog.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    // TRACKING CALLS ==============================================================================
    // TRACK CALLS MADE
    private void TrackCallsMade() {
        Helpers.LogThis(TAG_LOG, "TRACK REST ID: " + STR_RESTAURANT_ID);
        Helpers.LogThis(TAG_LOG, "TRACK PARTNER ID: " + STR_DELIVERY_PARTNER_ID);
        TrackingService trackingService = TrackingService.retrofit.create(TrackingService.class);
        final Call<JsonObject> call = trackingService.trackRestaurantCallToOrder(STR_RESTAURANT_ID, STR_DELIVERY_PARTNER_ID);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_response) + String.valueOf(response.body()));
                } catch (Exception e) {
                    Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + t.toString());
                Helpers.LogThis(TAG_LOG, getString(R.string.log_exception) + call.toString());
            }
        });
    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        toolbar = findViewById(R.id.toolbar);
        TextView txt_no_results = findViewById(R.id.txt_no_results);
        txt_no_results.setText(getString(R.string.error_unknown));

        delivery_phone = findViewById(R.id.delivery_phone);

        no_list_items = findViewById(R.id.no_list_items);
        no_list_items.setVisibility(View.GONE);

        viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager, true);

    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getStringArrayList("image_urls") != null) {
            int selected_position, data_size;

            ArrayList<String> image_urls = extras.getStringArrayList("image_urls");
            selected_position = extras.getInt("selected_position");

            STR_RESTAURANT_ID = extras.getString("rest_id");
            STR_DELIVERY_PARTNER_ID = extras.getString("delivery_id");
            STR_DELIVERY_PARTNER_NAME = extras.getString("delivery_name");
            STR_DELIVERY_PARTNER_PHONE = extras.getString("delivery_phone");

            Helpers.LogThis(TAG_LOG,
                    STR_DELIVERY_PARTNER_ID + " - " +
                    STR_DELIVERY_PARTNER_NAME + " - "+
                    STR_DELIVERY_PARTNER_PHONE + " - "
            );

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

                helper.setTracker(TAG_LOG);

                if (!STR_DELIVERY_PARTNER_ID.equals("") && !STR_DELIVERY_PARTNER_PHONE.equals("")) {
                    showCallButton();
                } else{
                    delivery_phone.setVisibility(View.GONE);
                }

            } else {
                noImages();
            }

        } else {
            finish();
            helper.ToastMessage(GalleryViewActivity.this,
                    getString(R.string.error_unknown));
        }
    }

    private void showCallButton() {
        delivery_phone.setVisibility(View.VISIBLE);
        delivery_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMakeCall("You are about to call " + STR_DELIVERY_PARTNER_NAME +
                                " for your order, Are you sure you wish to proceed?",
                        STR_DELIVERY_PARTNER_PHONE);
            }
        });
    }

    // DIALOG HANDLING =============================================================================
    private void dialogMakeCall(final String Message, final String TelephoneNumber) {
        final Dialog dialog = new Dialog(GalleryViewActivity.this);
        dialog.setContentView(R.layout.dialog_confirm);
        final TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        final TextView txtOk = dialog.findViewById(R.id.txtOk);
        final TextView txtCancel = dialog.findViewById(R.id.txtCancel);
        final TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        txtTitle.setText(getString(R.string.txt_call_to_order));
        txtMessage.setText(Message);
        txtCancel.setVisibility(View.VISIBLE);

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission
                        (GalleryViewActivity.this, Manifest.permission.CALL_PHONE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + TelephoneNumber));
                    startActivity(intent);
                    TrackCallsMade();
                    helper.setTracker(TAG_LOG + " - " + STR_RESTAURANT_ID
                            + " :CALL TO ORDER: PARTNER ID " + STR_DELIVERY_PARTNER_ID);
                } else {
                    helper.myDialog(GalleryViewActivity.this,
                            getString(R.string.txt_alert), getString(R.string.txt_call_permissions));
                }
                dialog.cancel();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void setUpToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        getSupportActionBar().setTitle("");
        toolbar.setTitle("");
        final LinearLayout toolbar_image = toolbar.findViewById(R.id.toolbar_image);
        toolbar_image.setVisibility(View.VISIBLE);
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
        delivery_phone.setVisibility(View.GONE);
    }

}
