package com.hotelaide.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main.activities.AboutUsActivity;
import com.hotelaide.main.activities.DashboardActivity;
import com.hotelaide.main.activities.FindJobsActivity;
import com.hotelaide.main.activities.FindMembersActivity;
import com.hotelaide.main.activities.GalleryViewActivity;
import com.hotelaide.main.activities.MyJobsActivity;
import com.hotelaide.main.activities.MyMessages;
import com.hotelaide.main.activities.ProfileActivity;
import com.hotelaide.main.activities.ProfileEditActivity;
import com.hotelaide.main.activities.SettingsActivity;
import com.hotelaide.main.models.NotificationModel;
import com.hotelaide.services.BackgroundFetchService;
import com.hotelaide.services.MessagingService;
import com.hotelaide.services.ReminderService;
import com.hotelaide.startup.SplashScreenActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import me.leolin.shortcutbadger.ShortcutBadger;

import static android.content.pm.PackageManager.GET_ACTIVITIES;
import static android.content.pm.PackageManager.NameNotFoundException;
import static com.hotelaide.utils.StaticVariables.APP_IS_RUNNING;
import static com.hotelaide.utils.StaticVariables.BROADCAST_LOG_OUT;
import static com.hotelaide.utils.StaticVariables.CHANNEL_DESC;
import static com.hotelaide.utils.StaticVariables.CHANNEL_ID;
import static com.hotelaide.utils.StaticVariables.CHANNEL_NAME;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_ADDRESS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_BASIC;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_WORK;
import static com.hotelaide.utils.StaticVariables.FIRST_LAUNCH;
import static com.hotelaide.utils.StaticVariables.INT_ANIMATION_TIME;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_PREVIEW;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_TITLE;
import static com.hotelaide.utils.StaticVariables.USER_AVAILABILITY;
import static com.hotelaide.utils.StaticVariables.USER_COUNTY;
import static com.hotelaide.utils.StaticVariables.USER_DOB;
import static com.hotelaide.utils.StaticVariables.USER_FULL_ADDRESS;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_ID;
import static com.hotelaide.utils.StaticVariables.USER_L_NAME;
import static com.hotelaide.utils.StaticVariables.USER_PHONE;
import static com.hotelaide.utils.StaticVariables.db;

//import static android.content.pm.PackageManager.GET_SIGNATURES;

public class Helpers {

    public final static String TAG_LOG = "HELPER";
    private final Context context;
    private static Toast toast;
    private final TextView txt_loading_message;
    private final Dialog dialog;

    public Helpers(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        txt_loading_message = dialog.findViewById(R.id.txt_loading_message);
    }


    // DRAWER CLICKS ===============================================================================
    public void drawerItemClicked(int id) {
        switch (id) {
            case R.id.drawer_dashboard:
                context.startActivity(new Intent(context, DashboardActivity.class));
                break;

            case R.id.drawer_find_jobs:
                context.startActivity(new Intent(context, FindJobsActivity.class));
                break;

            case R.id.drawer_my_jobs:
                context.startActivity(new Intent(context, MyJobsActivity.class));
                break;

            case R.id.drawer_my_messages:
                context.startActivity(new Intent(context, MyMessages.class));
                break;

            case R.id.drawer_find_members:
                context.startActivity(new Intent(context, FindMembersActivity.class));
                break;

            case R.id.drawer_profile:
                context.startActivity(new Intent(context, ProfileActivity.class));
                break;

            case R.id.drawer_about_us:
                context.startActivity(new Intent(context, AboutUsActivity.class));
                break;

            case R.id.drawer_settings:
                context.startActivity(new Intent(context, SettingsActivity.class));
                break;

            case R.id.drawer_log_out:
                sessionExpiryBroadcast();
                break;
        }
    }


    // BROADCASTS ==================================================================================
    public static void sessionExpiryBroadcast() {
        Context context = MyApplication.getAppContext();

        updateUserOnlineStatus(Calendar.getInstance().getTimeInMillis());

        MessagingService.stopListeningForMessages();
        context.stopService(new Intent(context, MessagingService.class));
        context.stopService(new Intent(context, BackgroundFetchService.class));
        context.stopService(new Intent(context, ReminderService.class));

        SharedPrefs.deleteAllSharedPrefs();

        AccessToken.setCurrentAccessToken(null);
        LoginManager.getInstance().logOut();

//        Database db = new Database();
        db.deleteAllTables();

        context.sendBroadcast(new Intent().setAction(BROADCAST_LOG_OUT));
        context.startActivity(new Intent(context, SplashScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void updateUserOnlineStatus(Object status) {
        if (SharedPrefs.getInt(USER_ID) != 0) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference parent_ref = database.getReference(BuildConfig.USERS_URL + SharedPrefs.getInt(USER_ID) + BuildConfig.USERS_STATUS_URL);
            parent_ref.setValue(status);
        }
    }

    // LOGS ========================================================================================
    public static void logThis(String page_name, String data) {
        if (BuildConfig.DEBUG) {
            Log.e(page_name, data);
        }
    }

//    @SuppressLint("PackageManagerGetSignatures")
//    public void getShaCertificate() {
//        PackageInfo info;
//        try {
//
//            info = context.getPackageManager().getPackageInfo(
//                    "com.hotelaide", GET_SIGNATURES);
//
//            for (android.content.pm.Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                Log.e("Hash key", something);
//                System.out.println("Hash key" + something);
//            }
//
//        } catch (NameNotFoundException e) {
//            logThis(TAG_LOG, "name not found " + e.toString());
//        } catch (NoSuchAlgorithmException e) {
//            logThis(TAG_LOG, "no such algorithm " + e.toString());
//        } catch (Exception e) {
//            logThis(TAG_LOG, "exception " + e.toString());
//        }
//    }


    // DIALOGS AND DISPLAYS ========================================================================
    public void setProgressDialog(String message) {
        if (!dialog.isShowing()) {
            dialog.show();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            txt_loading_message.setText(message);
        } else {
            dialog.cancel();
            dialog.show();
            txt_loading_message.setText(message);
        }
    }

    public void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void myDialog(String title, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_cancel);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView txt_message = dialog.findViewById(R.id.txt_message);
        final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final TextView txt_title = dialog.findViewById(R.id.txt_title);
        txt_title.setText(title);
        txt_message.setText(message);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void myPermissionsDialog(int[] grant_results) {
        for (int result : grant_results) {
            if (result == -1) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_confirm);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final TextView txt_message = dialog.findViewById(R.id.txt_message);
                final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
                final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                final TextView txt_title = dialog.findViewById(R.id.txt_title);
                txt_title.setText(R.string.txt_alert);
                txt_message.setText(R.string.error_permissions);
                btn_confirm.setText(R.string.txt_take_me_there);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                        dialog.cancel();
                    }
                });
                dialog.show();
                break;
            }
        }
    }

    public void dialogNoGPS() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView txt_message = dialog.findViewById(R.id.txt_message);
        final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final TextView txt_title = dialog.findViewById(R.id.txt_title);
        txt_title.setText(R.string.txt_location_title);
        txt_message.setText(R.string.rationale_locations);
        btn_confirm.setText(context.getString(R.string.txt_enable));
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.cancel();
            }
        });
        btn_cancel.setVisibility(View.VISIBLE);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void dialogPrivacyPolicy(final Activity activity) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_policy);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView txt_link = dialog.findViewById(R.id.txt_link);
        final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final CheckBox checkBox = dialog.findViewById(R.id.checkbox);

        txt_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.TERMS_URL));
                    activity.startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    toastMessage(context.getString(R.string.error_app_not_installed));
                    e.printStackTrace();
                } catch (Exception e) {
                    toastMessage(context.getString(R.string.error_unknown));
                    e.printStackTrace();
                }
            }
        });

        btn_confirm.setText(context.getString(R.string.txt_accept));
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    SharedPrefs.setGlobalBool(FIRST_LAUNCH, true);
                    dialog.cancel();
                } else {
                    toastMessage("Please scroll down to accept the terms and conditions");
                }
            }
        });

        btn_cancel.setText(context.getString(R.string.txt_decline));
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                SharedPrefs.setGlobalBool(FIRST_LAUNCH, false);
                activity.finish();
            }
        });
        dialog.show();
    }

    public void dialogShare(final Activity context, final String share_link_url) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_share);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ImageView share_facebook = dialog.findViewById(R.id.share_facebook);
        final ImageView share_email = dialog.findViewById(R.id.share_email);
        final ImageView share_messenger = dialog.findViewById(R.id.share_messenger);
        final ImageView share_sms = dialog.findViewById(R.id.share_sms);
        final ImageView share_whatsapp = dialog.findViewById(R.id.share_whatsapp);

        share_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAppIsInstalled("com.facebook.katana")) {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(share_link_url))
                            .build();
                    ShareDialog.show(context, content);
                    dialog.cancel();
                } else {
                    toastMessage(context.getString(R.string.error_app_not_installed));
                }
            }
        });

        share_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("text/html");
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
                    emailIntent.putExtra(Intent.EXTRA_TEXT, share_link_url);
                    context.startActivity(Intent.createChooser(emailIntent, "Send Email"));
                    dialog.cancel();
                } catch (Exception e) {
                    toastMessage(context.getString(R.string.error_app_not_installed));
                }
            }
        });

        share_messenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAppIsInstalled("com.facebook.orca")) {
                    Intent messengerIntent = new Intent();
                    messengerIntent.setAction(Intent.ACTION_SEND);
                    messengerIntent.putExtra(Intent.EXTRA_TEXT, share_link_url);
                    messengerIntent.setType("text/plain");
                    messengerIntent.setPackage("com.facebook.orca");
                    context.startActivity(messengerIntent);
                } else {
                    toastMessage(context.getString(R.string.error_app_not_installed));
                }
            }
        });

        share_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.putExtra("sms_body", share_link_url);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    context.startActivity(smsIntent);
                    dialog.cancel();
                } catch (Exception e) {
                    toastMessage(context.getString(R.string.error_app_not_installed));
                }
            }
        });

        share_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAppIsInstalled("com.whatsapp")) {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, share_link_url);
                    context.startActivity(whatsappIntent);
                    dialog.cancel();
                } else {
                    toastMessage(context.getString(R.string.error_app_not_installed));
                }
            }
        });

        dialog.setCancelable(true);
        dialog.show();
    }

    public void dialogMakeCall(final String phone_number) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView txt_message = dialog.findViewById(R.id.txt_message);
        final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final TextView txt_title = dialog.findViewById(R.id.txt_title);
        txt_title.setText(R.string.txt_call_title);
        txt_message.setText(R.string.txt_call);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone_number));
                    context.startActivity(intent);
                } catch (Exception e) {
                    myDialog(
                            context.getString(R.string.app_name),
                            context.getString(R.string.error_app_not_installed));
                }
                dialog.cancel();
            }
        });
        btn_cancel.setVisibility(View.VISIBLE);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void dialogEditProfile(String message, final String bundle_extra) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_confirm);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView txt_message = dialog.findViewById(R.id.txt_message);
        final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final TextView txt_title = dialog.findViewById(R.id.txt_title);
        txt_title.setText(R.string.txt_update);
        txt_message.setText(
                context.getString(R.string.txt_not_setup1)
                        .concat(message)
                        .concat(context.getString(R.string.txt_not_setup2))
        );
        btn_confirm.setText(R.string.txt_take_me_there);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProfileEditActivity.class)
                        .putExtra(bundle_extra, bundle_extra));
                dialog.cancel();
            }
        });
        dialog.show();

    }

    public void toastMessage(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void openImageViewer(final Activity activity, final String image_url) {
        if (!image_url.equals("")) {
            ArrayList<String> image_urls = new ArrayList<>();
            image_urls.add(image_url);
            activity.startActivity(new Intent(activity, GalleryViewActivity.class)
                    .putExtra("image_urls", image_urls)
                    .putExtra("selected_position", 1)
            );
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            toastMessage("Image not set");
        }
    }

    public void setWelcomeMessage(TextView txt_welcome) {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String greeting = "";
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Good Morning ";

        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greeting = "Good Afternoon ";

        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            greeting = "Good Evening ";

        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            greeting = "Good Night ";
        }

        txt_welcome.setText(greeting.concat(SharedPrefs.getString(USER_F_NAME)));
    }

    public void setTarget(Activity activity, String bool_value, View[] views, String[] titles, String[] messages) {
        if (activity != null && SharedPrefs.getBool(bool_value)) {
            ArrayList<TapTarget> tapTargets = new ArrayList<>();
            int length = titles.length;
            for (int i = 0; i < length; i++) {
                tapTargets.add(TapTarget.forView(views[i], titles[i], messages[i])
                        .dimColor(R.color.dimmer)
                        .outerCircleColor(R.color.colorPrimary)
                        .cancelable(true)
                        .descriptionTextColor(R.color.white)
                        .titleTextColor(R.color.black)
                        .drawShadow(true)
                        .transparentTarget(true));
            }
            new TapTargetSequence(activity)
                    .targets(tapTargets).start();
            SharedPrefs.setBool(bool_value, true);
        }
    }

    public void setTarget(Activity activity, View view, String title, String message) {
        if (activity != null) {
            ArrayList<TapTarget> tapTargets = new ArrayList<>();
            tapTargets.add(TapTarget.forView(view, title, message)
                    .dimColor(R.color.dimmer)
                    .outerCircleColor(R.color.colorPrimary)
                    .cancelable(true)
                    .descriptionTextColor(R.color.white)
                    .titleTextColor(R.color.black)
                    .drawShadow(true)
                    .transparentTarget(true));
            new TapTargetSequence(activity)
                    .targets(tapTargets).start();
        }
    }


    // VALIDATIONS =================================================================================
    public boolean validateEmail(EditText edit_text) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (edit_text.getText().toString().length() < 1) {
            edit_text.setError(context.getString(R.string.error_field_required));
            animateFlash(edit_text);
            return false;
        } else if (!pattern.matcher(edit_text.getText().toString()).matches()) {
            edit_text.setError(context.getString(R.string.error_field_required));
            animateWobble(edit_text);
            return false;
        } else {
            edit_text.setError(null);
            return true;
        }
    }

    private boolean validateAppIsInstalled(String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, GET_ACTIVITIES);
            app_installed = true;
        } catch (NameNotFoundException e) {
            app_installed = false;
            toastMessage("This app has not been installed");
        }
        return app_installed;
    }

    public boolean validateInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return (netInfo != null && netInfo.isConnected());

    }

    public boolean validateGooglePlayServices(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    public boolean validateEmptyEditText(EditText editText) {
        if (editText.getText().toString().length() < 1) {
            editText.setError(context.getString(R.string.error_field_required));
            animateFlash(editText);
            return false;
        } else {
            editText.setError(null);
            return true;
        }

    }

    public boolean validateEmptyTextView(TextView textView, String errorMessage) {
        if (textView.getText().toString().length() < 1) {
            toastMessage(errorMessage);
            animateFlash(textView);
            return false;
        } else {
            return true;
        }

    }

    public boolean validateServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return false;
            }
        }
        return true;
    }

    public boolean validateJobApplication() {

        if (SharedPrefs.getInt(USER_AVAILABILITY) == 0) {
            dialogEditProfile("availability", EXTRA_PROFILE_BASIC);
            return false;

        } else if (SharedPrefs.getString(USER_FULL_ADDRESS).equals("")) {
            dialogEditProfile("full address", EXTRA_PROFILE_ADDRESS);
            return false;

        } else if (SharedPrefs.getString(USER_PHONE).equals("")) {
            dialogEditProfile("phone", EXTRA_PROFILE_BASIC);
            return false;

        } else if (SharedPrefs.getInt(USER_COUNTY) == 0) {
            dialogEditProfile("county", EXTRA_PROFILE_ADDRESS);
            return false;

        } else if (db.getAllExperience(EXPERIENCE_TYPE_EDUCATION).size() < 1) {
            dialogEditProfile("education history", EXTRA_PROFILE_EDUCATION);
            return false;

        } else if (db.getAllExperience(EXPERIENCE_TYPE_WORK).size() < 1) {
            dialogEditProfile("employment history", EXTRA_PROFILE_WORK);
            return false;

        } else {
            return true;
        }
    }

    public boolean validateProfileCompletion(Context context) {

        if (SharedPrefs.getString(USER_F_NAME).equals("")) {
            toastMessage("You have not set your first name");
            return false;

        } else if (SharedPrefs.getString(USER_L_NAME).equals("")) {
            toastMessage("You have not set your last name");
            return false;

        }
        if (SharedPrefs.getString(USER_DOB).equals("")) {
            toastMessage("You have not set your date of birth");
            return false;

        } else if (SharedPrefs.getString(USER_FULL_ADDRESS).equals("")) {
            toastMessage("You have not set your address");
            return false;

        } else if (SharedPrefs.getString(USER_PHONE).equals("")) {
            toastMessage("You have not set your phone number");
            return false;

        } else if (SharedPrefs.getInt(USER_COUNTY) == 0) {
            toastMessage("You have not set your county");
            return false;

        } else {
            return true;
        }
    }


    // FORMAT ======================================================================================
    public String formatNumbersCurrency(String amount) {
        String[] amount_array = amount.split("\\.");
        String[] savingarray = amount_array[0].split("");
        int counter_savings = 0;
        StringBuilder newalloc = new StringBuilder();
        for (int j = savingarray.length; j > 1; j--) {
            if (j < savingarray.length - 3) {
                if (counter_savings == 2) {
                    counter_savings = 0;
                    newalloc.insert(0, savingarray[j - 1] + ",");
                } else {
                    newalloc.insert(0, savingarray[j - 1]);
                    counter_savings = counter_savings + 1;
                }
            } else {
                if (counter_savings == 3) {
                    counter_savings = 0;
                    newalloc.insert(0, savingarray[j - 1] + ",");
                } else {
                    newalloc.insert(0, savingarray[j - 1]);
                    counter_savings = counter_savings + 1;
                }
            }

        }
        if (amount.contains(".")) {
            return newalloc + "." + amount_array[1];
        } else {
            return newalloc.toString();
        }

    }

    public String formatDate(String string_date) {

        Date date;

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try {
            date = sdf.parse(string_date);

            Calendar calendarDate = Calendar.getInstance();

            Locale locale = Locale.getDefault();

            calendarDate.setTime(date);

            String month = calendarDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, locale).concat(" ");
            String day = String.valueOf(calendarDate.get(Calendar.DATE)).concat(", ");
            String year = String.valueOf(calendarDate.get(Calendar.YEAR));

            return month + day + year;

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String calculateAge(String dob) {

        Date dateDOB;

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try {
            dateDOB = sdf.parse(dob);

            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();

            startDate.setTime(dateDOB);
            endDate.getTime();

            int years = endDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR);
            int months = endDate.get(Calendar.MONTH) - startDate.get(Calendar.MONTH);

            String str_years = "", str_months = "";

            if (years > 0) {
                str_years = String.valueOf(years) + " yrs ";
            }

            if (months > 0) {
                str_months = String.valueOf(months) + " mths";
            }

            return str_years + str_months;

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String calculateDateInterval(String start_date, String end_date) {

        Date starting_date;
        Date ending_date;

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try {
            starting_date = sdf.parse(start_date);
            ending_date = sdf.parse(end_date);

            Calendar calendarStartDate = Calendar.getInstance();
            Calendar calendarEndDate = Calendar.getInstance();

            calendarStartDate.setTime(starting_date);
            calendarEndDate.setTime(ending_date);

            int years = calendarEndDate.get(Calendar.YEAR) - calendarStartDate.get(Calendar.YEAR);
            int months = calendarEndDate.get(Calendar.MONTH) - calendarStartDate.get(Calendar.MONTH);

            String str_years = "", str_months = "";

            if (years > 0) {
                str_years = String.valueOf(years) + "yrs ";
            }

            if (months > 0) {
                str_months = String.valueOf(months) + "mth";
            }

            return str_years + str_months;

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void handleErrorMessage(final Context context, JSONObject data) {
        try {
            Iterator<String> i = data.keys();
            while (i.hasNext()) {
                String key = i.next();
                try {
                    JSONArray message_array = new JSONArray(String.valueOf(data.get(key)));
                    String display_message = "";
                    for (int y = 0; y < message_array.length(); y++) {
                        display_message = display_message.concat(message_array.getString(y)).concat("\n");
                    }
                    myDialog(context.getString(R.string.txt_errors), display_message);

                    Helpers.logThis(TAG_LOG, display_message);

                    HelpersAsync.setTrackerEvent(TAG_LOG, false);

                } catch (JSONException e) {
                    e.printStackTrace();
                    logThis(TAG_LOG, e.toString());
                    HelpersAsync.setTrackerEvent(TAG_LOG, false);
                    myDialog(context.getString(R.string.app_name), context.getString(R.string.error_unknown));
                }
            }


        } catch (Exception j) {
            j.printStackTrace();
            logThis(TAG_LOG, j.toString());
            myDialog(context.getString(R.string.app_name), context.getString(R.string.error_unknown));
            HelpersAsync.setTrackerEvent(TAG_LOG, false);
        }

    }

    public String fetchFromEditText(EditText editText) {
        String data = "";
        if (editText.getText().toString().length() > 1) {
            data = editText.getText().toString();
        }
        return data;
    }

    // ANIMATIONS ==================================================================================
    private void animateWobble(View v) {
        YoYo.with(Techniques.Wobble)
                .duration(INT_ANIMATION_TIME).delay(20)
                .playOn(v);
    }

    private void animateFlash(View v) {
        YoYo.with(Techniques.Flash)
                .duration(INT_ANIMATION_TIME)
                .delay(0)
                .repeat(2)
                .playOn(v);
    }

    public void animateSlide_in(View v) {
        YoYo.with(Techniques.SlideInRight)
                .duration(INT_ANIMATION_TIME).delay(0)
                .playOn(v);
    }

    public void animateFadeIn(View v) {
        YoYo.with(Techniques.FadeIn)
                .duration(INT_ANIMATION_TIME)
                .playOn(v);
    }

    public static void animateRecyclerView(View view) {
        ScaleAnimation anim = new ScaleAnimation(
                0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(INT_ANIMATION_TIME);
        view.startAnimation(anim);
    }

    public void animateSwipeRefresh(SwipeRefreshLayout swipe_refresh) {
        swipe_refresh.setSize(0);
        swipe_refresh.setDistanceToTriggerSync(500);
        swipe_refresh.setColorSchemeResources(
                R.color.colorAccentLight,
                R.color.colorAccent,
                R.color.colorAccentDark,
                R.color.dark_grey,
                R.color.colorPrimaryDark,
                R.color.colorPrimary,
                R.color.colorPrimaryLight,
                R.color.light_grey
        );
    }


    // NOTIFICATION CREATOR ========================================================================
    public static void createNotification(Context context, NotificationModel notification_model) {

        Intent intent = new Intent(context, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(NOTIFICATION_TITLE, notification_model.title);
        intent.putExtra(NOTIFICATION_PREVIEW, notification_model.body);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notification_model.job_id, intent,
                PendingIntent.FLAG_ONE_SHOT);

        createNotificationChannel(context);

        NotificationCompat.Builder mBuilder;

        if (APP_IS_RUNNING) {
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(notification_model.title)
                    .setContentText(notification_model.preview)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setSound(null)
                    .setVibrate(null)
                    .setLights(ContextCompat.getColor(context, R.color.colorPrimary), 1000, 1000)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
        } else {
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(notification_model.title)
                    .setContentText(notification_model.preview)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setLights(ContextCompat.getColor(context, R.color.colorPrimary), 1000, 1000)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notification_model.type_code, mBuilder.build());
        ShortcutBadger.applyCount(context, 1);

    }

    private static int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ?
                R.drawable.ic_logo :
                R.mipmap.ic_logo;
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

}
