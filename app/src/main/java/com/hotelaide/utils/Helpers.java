package com.hotelaide.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.JsonObject;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.interfaces.GeneralInterface;
import com.hotelaide.interfaces.UserInterface;
import com.hotelaide.main.activities.AboutUsActivity;
import com.hotelaide.main.activities.DashboardActivity;
import com.hotelaide.main.activities.FindJobsActivity;
import com.hotelaide.main.activities.GalleryViewActivity;
import com.hotelaide.main.activities.MyJobsActivity;
import com.hotelaide.main.activities.MyMessages;
import com.hotelaide.main.activities.ProfileActivity;
import com.hotelaide.main.activities.ProfileEditActivity;
import com.hotelaide.main.activities.SettingsActivity;
import com.hotelaide.main.models.SearchFilterModel;
import com.hotelaide.services.BackgroundFetchService;
import com.hotelaide.services.FileUploadService;
import com.hotelaide.services.MessagingService;
import com.hotelaide.startup.SplashScreenActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.pm.PackageManager.GET_ACTIVITIES;
import static android.content.pm.PackageManager.GET_SIGNATURES;
import static android.content.pm.PackageManager.NameNotFoundException;
import static com.hotelaide.utils.StaticVariables.APP_IS_RUNNING;
import static com.hotelaide.utils.StaticVariables.BROADCAST_LOG_OUT;
import static com.hotelaide.utils.StaticVariables.BROADCAST_SET_USER_COMPLETE;
import static com.hotelaide.utils.StaticVariables.BROADCAST_UPLOAD_COMPLETE;
import static com.hotelaide.utils.StaticVariables.CATEGORIES_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.CHANNEL_DESC;
import static com.hotelaide.utils.StaticVariables.CHANNEL_ID;
import static com.hotelaide.utils.StaticVariables.CHANNEL_NAME;
import static com.hotelaide.utils.StaticVariables.COUNTY_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EDUCATION_LEVEL_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXPERIENCE_TYPE_WORK;
import static com.hotelaide.utils.StaticVariables.EXTRA_FAILED;
import static com.hotelaide.utils.StaticVariables.EXTRA_PASSED;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_ADDRESS;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_BASIC;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_EDUCATION;
import static com.hotelaide.utils.StaticVariables.EXTRA_PROFILE_WORK;
import static com.hotelaide.utils.StaticVariables.INT_ANIMATION_TIME;
import static com.hotelaide.utils.StaticVariables.JOB_TYPE_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.USER_AVAILABILITY;
import static com.hotelaide.utils.StaticVariables.USER_COUNTY;
import static com.hotelaide.utils.StaticVariables.USER_DOB;
import static com.hotelaide.utils.StaticVariables.USER_FULL_ADDRESS;
import static com.hotelaide.utils.StaticVariables.USER_F_NAME;
import static com.hotelaide.utils.StaticVariables.USER_ID;
import static com.hotelaide.utils.StaticVariables.USER_L_NAME;
import static com.hotelaide.utils.StaticVariables.USER_PHONE;

public class Helpers {

    public final static String TAG_LOG = "HELPER CLASS";
    private final Context context;
    private static Toast toast;
    private final TextView txt_loading_message;
    private final Dialog dialog;


    private Database db;

    public Helpers(Context context) {
        this.context = context;
        db = new Database();
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        txt_loading_message = dialog.findViewById(R.id.txt_loading_message);
    }


    // DRAWER CLICKS ===============================================================================
    public void drawerItemClicked(Context context, int id) {
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
                ToastMessage(context, "Under development ;)");
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
        SharedPrefs.deleteAllSharedPrefs();

        AccessToken.setCurrentAccessToken(null);
        LoginManager.getInstance().logOut();

        Database db = new Database();
        db.deleteAllTables();

        MessagingService.stopListeningForMessages();
        context.stopService(new Intent(context, MessagingService.class));
        context.stopService(new Intent(context, BackgroundFetchService.class));
        context.stopService(new Intent(context, FileUploadService.class));

        context.sendBroadcast(new Intent().setAction(BROADCAST_LOG_OUT));
        context.startActivity(new Intent(context, SplashScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }


    // LOGS ========================================================================================
    public static void logThis(String page_name, String data) {
        if (BuildConfig.LOGGING) {
            Log.e(page_name, data);
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    public void getShaCertificate() {
        PackageInfo info;
        try {

            info = context.getPackageManager().getPackageInfo(
                    "com.hotelaide", GET_SIGNATURES);

            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
                System.out.println("Hash key" + something);
            }

        } catch (NameNotFoundException e) {
            logThis(TAG_LOG, "name not found " + e.toString());
        } catch (NoSuchAlgorithmException e) {
            logThis(TAG_LOG, "no such algorithm " + e.toString());
        } catch (Exception e) {
            logThis(TAG_LOG, "exception " + e.toString());
        }
    }


    // DIALOGS AND DISPLAYS ========================================================================
    public void setProgressDialog(String message) {
        if (!dialog.isShowing()) {
            dialog.show();
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

    public void myDialog(Context DialogContext, String title, String message) {
        final Dialog dialog = new Dialog(DialogContext);
        dialog.setContentView(R.layout.dialog_cancel);
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

    public void myPermissionsDialog(final Context context, int[] grant_results) {
        for (int result : grant_results) {
            if (result == -1) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_confirm);
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

    public void dialogNoGPS(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
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

    public void dialogShare(final Activity context, final String share_link_url) {
        final Dialog dialog = new Dialog(context);
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
                if (validateAppIsInstalled("com.facebook.katana")) {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(share_link_url))
                            .build();
                    ShareDialog.show(context, content);
                    dialog.cancel();
                } else {
                    ToastMessage(context, context.getString(R.string.error_app_not_installed));
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
                    ToastMessage(context, context.getString(R.string.error_app_not_installed));
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
                    ToastMessage(context, context.getString(R.string.error_app_not_installed));
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
                    ToastMessage(context, context.getString(R.string.error_app_not_installed));
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
                    ToastMessage(context, context.getString(R.string.error_app_not_installed));
                }
            }
        });

        dialog.setCancelable(true);
        dialog.show();
    }

    public void dialogMakeCall(final Context context, final String phone_number) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        final TextView txt_message = dialog.findViewById(R.id.txt_message);
        final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final TextView txt_title = dialog.findViewById(R.id.txt_title);
        txt_title.setText(R.string.txt_call_title);
        txt_message.setText(R.string.txt_call);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone_number));
                    context.startActivity(intent);

                } else {
                    myDialog(context,
                            context.getString(R.string.app_name),
                            context.getString(R.string.error_call_permissions));
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

    private void dialogEditProfile(final Context dialogContext, String message, final String bundle_extra) {
        final Dialog dialog = new Dialog(dialogContext);
        dialog.setContentView(R.layout.dialog_confirm);
        final TextView txt_message = dialog.findViewById(R.id.txt_message);
        final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final TextView txt_title = dialog.findViewById(R.id.txt_title);
        txt_title.setText(R.string.txt_update);
        txt_message.setText("You cannot apply for this role just yet, It seems you have not setup your " + message + ", Set this now?");
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
                dialogContext.startActivity(new Intent(dialogContext, ProfileEditActivity.class)
                        .putExtra(bundle_extra, bundle_extra));
                dialog.cancel();
            }
        });
        dialog.show();

    }

    public void ToastMessage(Context context, String message) {
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
            ToastMessage(activity, "Image not set");
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
        if (activity != null && !SharedPrefs.getBool(bool_value)) {
            ArrayList<TapTarget> tapTargets = new ArrayList<>();
            int length = titles.length;
            for (int i = 0; i < length; i++) {
                tapTargets.add(TapTarget.forView(views[i], titles[i], messages[i])
                        .dimColor(R.color.dimmer)
                        .outerCircleColor(R.color.colorAccent)
                        .targetCircleColor(R.color.colorPrimary)
                        .cancelable(false)
                        .descriptionTextColor(R.color.black)
                        .titleTextColor(R.color.dark_grey)
                        .drawShadow(true)
                        .transparentTarget(true));
            }
            new TapTargetSequence(activity)
                    .targets(tapTargets).start();
            SharedPrefs.setBool(bool_value, true);
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
            ToastMessage(context, "This app has not been installed");
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
            ToastMessage(context, errorMessage);
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

    public boolean validateJobApplication(Context context) {

        if (SharedPrefs.getInt(USER_AVAILABILITY) == 0) {
            dialogEditProfile(context, "availability", EXTRA_PROFILE_BASIC);
            return false;

        } else if (SharedPrefs.getString(USER_FULL_ADDRESS).equals("")) {
            dialogEditProfile(context, "full address", EXTRA_PROFILE_ADDRESS);
            return false;

        } else if (SharedPrefs.getInt(USER_PHONE) == 0) {
            dialogEditProfile(context, "phone", EXTRA_PROFILE_BASIC);
            return false;

        } else if (SharedPrefs.getInt(USER_COUNTY) == 0) {
            dialogEditProfile(context, "county", EXTRA_PROFILE_ADDRESS);
            return false;

        } else if (db.getAllExperience(EXPERIENCE_TYPE_EDUCATION).size() < 1) {
            dialogEditProfile(context, "education history", EXTRA_PROFILE_EDUCATION);
            return false;

        } else if (db.getAllExperience(EXPERIENCE_TYPE_WORK).size() < 1) {
            dialogEditProfile(context, "employment history", EXTRA_PROFILE_WORK);
            return false;

        } else {
            return true;
        }
    }

    public boolean validateProfileCompletion(Context context) {

        if (SharedPrefs.getString(USER_F_NAME).equals("")) {
            ToastMessage(context, "You have not set your first name");
            return false;

        } else if (SharedPrefs.getString(USER_L_NAME).equals("")) {
            ToastMessage(context, "You have not set your last name");
            return false;

        }
        if (SharedPrefs.getString(USER_DOB).equals("")) {
            ToastMessage(context, "You have not set your date of birth");
            return false;

        } else if (SharedPrefs.getString(USER_FULL_ADDRESS).equals("")) {
            ToastMessage(context, "You have not set your address");
            return false;

        } else if (SharedPrefs.getInt(USER_PHONE) == 0) {
            ToastMessage(context, "You have not set your phone number");
            return false;

        } else if (SharedPrefs.getInt(USER_COUNTY) == 0) {
            ToastMessage(context, "You have not set your county");
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
                    myDialog(context, context.getString(R.string.txt_errors), display_message);

                    Helpers.logThis(TAG_LOG, display_message);

                } catch (JSONException e) {
                    e.printStackTrace();
                    logThis(TAG_LOG, e.toString());
                    myDialog(context, context.getString(R.string.app_name), context.getString(R.string.error_unknown));
                }
            }


        } catch (Exception j) {
            j.printStackTrace();
            logThis(TAG_LOG, j.toString());
            myDialog(context, context.getString(R.string.app_name), context.getString(R.string.error_unknown));
        }

    }


    // ANIMATIONS ==================================================================================
    public void animateWobble(View v) {
        YoYo.with(Techniques.Wobble)
                .duration(INT_ANIMATION_TIME).delay(20)
                .playOn(v);
    }

    public void animateFlash(View v) {
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


    // NOTIFICATION CREATOR ========================================================================
    public static void createNotification(Context context, String message_title, String message_body) {
        Intent intent = new Intent(context, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("notification_title", message_title);
        intent.putExtra("notification_body", message_body);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        createNotificationChannel(context);

        NotificationCompat.Builder mBuilder;

        if (SharedPrefs.getBool(APP_IS_RUNNING)) {
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(message_title)
                    .setContentText(message_body)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setSound(null)
                    .setVibrate(null)
                    .setLights(ContextCompat.getColor(context, R.color.colorPrimary), 1000, 1000)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
        } else {
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(message_title)
                    .setContentText(message_body)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setLights(ContextCompat.getColor(context, R.color.colorPrimary), 1000, 1000)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());
        ShortcutBadger.applyCount(context, 1);


    }

    private static int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ?
                R.drawable.ic_logo :
                R.mipmap.ic_launcher;
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


    // COMMON ASYNC TASKS ==========================================================================
    // GET USER ====================================================================================
    public void asyncGetUser() {
        UserInterface userInterface = UserInterface.retrofit.create(UserInterface.class);
        final Call<JsonObject> call = userInterface.getUser();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

//                    logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        SharedPrefs.setUser(main.getJSONObject("data"));
                    }

                } catch (JSONException e) {
                    logThis(TAG_LOG, e.toString());
                    context.sendBroadcast(new Intent().setAction(BROADCAST_SET_USER_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));

                } catch (Exception e) {
                    logThis(TAG_LOG, e.toString());
                    context.sendBroadcast(new Intent().setAction(BROADCAST_SET_USER_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                logThis(TAG_LOG, t.toString());
                logThis(TAG_LOG, call.toString());
                context.sendBroadcast(new Intent().setAction(BROADCAST_SET_USER_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
            }

        });
    }

    // GET COUNTIES ================================================================================
    public void asyncGetCounties() {
        GeneralInterface generalInterface = GeneralInterface.retrofit.create(GeneralInterface.class);
        final Call<JsonObject> call = generalInterface.getCounties();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

//                    logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        JSONArray main_array = main.getJSONArray("data");
                        int length = main_array.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject object = main_array.getJSONObject(i);
                            SearchFilterModel searchFilterModel = new SearchFilterModel();
                            searchFilterModel.id = object.getInt("id");
                            searchFilterModel.name = object.getString("county_name");
                            db.setFilter(COUNTY_TABLE_NAME, searchFilterModel);
                        }
                    }

                } catch (JSONException e) {
                    logThis(TAG_LOG, e.toString());

                } catch (Exception e) {
                    logThis(TAG_LOG, e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                logThis(TAG_LOG, t.toString());
                logThis(TAG_LOG, call.toString());
            }

        });
    }

    // GET EDUCATIONAL LEVEL =======================================================================
    public void asyncGetEducationalLevels() {
        GeneralInterface generalInterface = GeneralInterface.retrofit.create(GeneralInterface.class);
        final Call<JsonObject> call = generalInterface.getEducationalLevels();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

//                    logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        JSONArray main_array = main.getJSONArray("data");
                        int length = main_array.length();
                        db.deleteEducationLevelsTable();
                        for (int i = 0; i < length; i++) {
                            JSONObject object = main_array.getJSONObject(i);
                            SearchFilterModel searchFilterModel = new SearchFilterModel();
                            searchFilterModel.id = object.getInt("id");
                            searchFilterModel.name = object.getString("name");
                            db.setFilter(EDUCATION_LEVEL_TABLE_NAME, searchFilterModel);
                        }
                    }

                } catch (JSONException e) {
                    logThis(TAG_LOG, e.toString());

                } catch (Exception e) {
                    logThis(TAG_LOG, e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                logThis(TAG_LOG, t.toString());
                logThis(TAG_LOG, call.toString());
            }

        });
    }

    // GET JOB TYPES ===============================================================================
    public void asyncGetJobTypes() {
        GeneralInterface generalInterface = GeneralInterface.retrofit.create(GeneralInterface.class);
        final Call<JsonObject> call = generalInterface.getJobTypes();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

//                    logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        JSONArray main_array = main.getJSONArray("data");
                        int length = main_array.length();
                        db.deleteJobTypeTable();
                        for (int i = 0; i < length; i++) {
                            JSONObject object = main_array.getJSONObject(i);
                            SearchFilterModel searchFilterModel = new SearchFilterModel();
                            searchFilterModel.id = object.getInt("id");
                            searchFilterModel.name = object.getString("name");
                            db.setFilter(JOB_TYPE_TABLE_NAME, searchFilterModel);
                        }
                    }

                } catch (JSONException e) {
                    logThis(TAG_LOG, e.toString());

                } catch (Exception e) {
                    logThis(TAG_LOG, e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                logThis(TAG_LOG, t.toString());
                logThis(TAG_LOG, call.toString());
            }

        });
    }

    // GET CATEGORIES ==============================================================================
    public void asyncGetCategories() {
        GeneralInterface generalInterface = GeneralInterface.retrofit.create(GeneralInterface.class);
        final Call<JsonObject> call = generalInterface.getCategories();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

//                    logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        JSONArray main_array = main.getJSONArray("data");
                        db.deleteCategoriesTable();
                        int length = main_array.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject object = main_array.getJSONObject(i);
                            SearchFilterModel searchFilterModel = new SearchFilterModel();
                            searchFilterModel.id = object.getInt("id");
                            searchFilterModel.name = object.getString("name");
                            db.setFilter(CATEGORIES_TABLE_NAME, searchFilterModel);
                        }
                    }

                } catch (JSONException e) {
                    logThis(TAG_LOG, e.toString());

                } catch (Exception e) {
                    logThis(TAG_LOG, e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                logThis(TAG_LOG, t.toString());
                logThis(TAG_LOG, call.toString());
            }

        });
    }

    // GET DOCUMENTS ===============================================================================
    public void asyncGetAllDocuments() {
        UserInterface userInterface = UserInterface.retrofit.create(UserInterface.class);
        final Call<JsonObject> call = userInterface.getAllDocuments(SharedPrefs.getInt(USER_ID));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    logThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        JSONObject data_object = main.getJSONObject("data");
                        JSONArray document_array = data_object.getJSONArray("documents");

                        db.deleteDocumentsTable();

                        if (document_array != null && document_array.length() > 0) {
                            int length = document_array.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject document_object = document_array.getJSONObject(i);
                                db.setDocumentFromJson(document_object);
                            }
                        }
                    }
                    context.sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE));

                } catch (JSONException e) {
                    logThis(TAG_LOG, e.toString());

                } catch (Exception e) {
                    logThis(TAG_LOG, e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                logThis(TAG_LOG, t.toString());
                logThis(TAG_LOG, call.toString());
                context.sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE));
            }

        });
    }

    // UPLOAD DOCUMENTS ============================================================================
    public void asyncUploadDocument(final MultipartBody.Part partFile) {
        UserInterface userInterface = UserInterface.retrofit.create(UserInterface.class);
        Call<JsonObject> call = userInterface.setUserDocument(
                SharedPrefs.getInt(USER_ID),
                partFile
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    db.deleteDirtyDocuments();
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    Helpers.logThis(TAG_LOG, main.toString());
                    if (main.getBoolean("success")) {
                        db.deleteDirtyDocuments();
                        JSONObject data_object = main.getJSONObject("data");
                        db.setDocumentFromJson(data_object.getJSONObject("document"));

                        context.sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_PASSED, EXTRA_PASSED));
                    } else {
                        createNotification(context, context.getString(R.string.txt_upload_failed), context.getString(R.string.error_unknown));
                        context.sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
                    }
                } catch (JSONException e) {
                    createNotification(context, context.getString(R.string.txt_upload_failed), context.getString(R.string.error_server));
                    context.sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Helpers.logThis(TAG_LOG, t.toString());
                db.deleteDirtyDocuments();
                if (validateInternetConnection()) {
                    createNotification(context, context.getString(R.string.txt_upload_failed), context.getString(R.string.error_server));
                } else {
                    createNotification(context, context.getString(R.string.txt_upload_failed), context.getString(R.string.error_connection));
                }
                context.sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
            }
        });

    }

    // GET DOCUMENTS ===============================================================================
    public void asyncDeleteDocument(final int id) {
        UserInterface userInterface = UserInterface.retrofit.create(UserInterface.class);
        final Call<JsonObject> call = userInterface.deleteDocument(
                SharedPrefs.getInt(USER_ID),
                id
        );
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    logThis(TAG_LOG, main.toString());
                    if (main.getBoolean("success")) {
                        db.deleteDocumentByID(String.valueOf(id));
                        context.sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_PASSED, EXTRA_PASSED));
                    } else {
                        context.sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
                    }
                } catch (JSONException e) {
                    context.sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
                    logThis(TAG_LOG, e.toString());

                } catch (Exception e) {
                    context.sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
                    logThis(TAG_LOG, e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                logThis(TAG_LOG, t.toString());
                logThis(TAG_LOG, call.toString());
                context.sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
            }

        });
    }

}
