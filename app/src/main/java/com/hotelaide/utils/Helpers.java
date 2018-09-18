package com.hotelaide.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.JsonObject;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main.activities.AboutUsActivity;
import com.hotelaide.main.activities.DashboardActivity;
import com.hotelaide.main.activities.FindJobsActivity;
import com.hotelaide.main.activities.ProfileActivity;
import com.hotelaide.main.activities.SettingsActivity;
import com.hotelaide.main.models.SearchFilterModel;
import com.hotelaide.services.BackgroundFetchService;
import com.hotelaide.services.MessagingService;
import com.hotelaide.services.UserService;
import com.hotelaide.startup.LoginActivity;
import com.hotelaide.startup.SplashScreenActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.pm.PackageManager.GET_ACTIVITIES;
import static android.content.pm.PackageManager.NameNotFoundException;
import static com.hotelaide.utils.Database.CATEGORIES_TABLE_NAME;
import static com.hotelaide.utils.Database.COUNTY_TABLE_NAME;
import static com.hotelaide.utils.Database.JOB_TYPE_TABLE_NAME;

public class Helpers {

    public final static String TAG_LOG = "HELPER CLASS";
    public final static int INT_ANIMATION_TIME = 800;
    //    private static Tracker sTracker;
//    private static GoogleAnalytics sAnalytics;
    private final Context context;
    private static Toast mToast;
    private final TextView ProgressDialogMessage;
    private final Dialog dialog;

    public final static int INT_PERMISSIONS_CAMERA = 601;

    public final static String BroadcastValue = "com.hotelaide.ACTIONLOGOUT";

    public final static String START_FIRST_TIME = "FIRSTTIMER";
    public final static String START_RETURN = "RETURN";
    public final static String START_LAUNCH = "LAUNCH";

    private Database db;

    public Helpers(Context context) {
        this.context = context;
        db = new Database();
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        ProgressDialogMessage = dialog.findViewById(R.id.message_text);
//        sAnalytics = GoogleAnalytics.getInstance(context);
//        getDefaultTracker();
    }


    // GOOGLE ANALYTICS TRACKING ===================================================================
//    synchronized private void getDefaultTracker() {
//        if (sTracker == null) {
//            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
//        }
//    }

//    public void setTracker(String TAG_LOG) {
//        sTracker.setScreenName("ANDROID - " + TAG_LOG);
//        sTracker.setAppVersion(BuildConfig.VERSION_NAME);
//        sTracker.send(new HitBuilders.ScreenViewBuilder().build());
//        Helpers.LogThis(TAG_LOG, "TRACKED");
//    }


    // DRAWER CLICKS ===============================================================================
    public void drawerItemClicked(Context context, int id) {
        switch (id) {
            case R.id.drawer_dashboard:
                context.startActivity(new Intent(context, DashboardActivity.class));
                break;

            case R.id.drawer_find_jobs:
                context.startActivity(new Intent(context, FindJobsActivity.class));
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
                SharedPrefs.deleteAllSharedPrefs();
                AccessToken.setCurrentAccessToken(null);
                LoginManager.getInstance().logOut();
                context.startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                context.sendBroadcast(new Intent().setAction(BroadcastValue));
                break;
        }
    }


    // BROADCASTS ==================================================================================
    public static void sessionExpiryBroadcast() {
        Context context = MyApplication.getAppContext();
        SharedPrefs.deleteAllSharedPrefs();
        Database db = new Database();
        db.deleteAllTables();

        context.stopService(new Intent(context, MessagingService.class));
        context.stopService(new Intent(context, BackgroundFetchService.class));

        context.startActivity(new Intent(context, SplashScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        context.sendBroadcast(new Intent().setAction(BroadcastValue));
    }


    // LOGS ========================================================================================
    public static void LogThis(String PageName, String data) {
        if (BuildConfig.LOGGING) {
            Log.e(PageName, data);
        }
    }

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
//            LogThis(TAG_LOG, "name not found " + e.toString());
//        } catch (NoSuchAlgorithmException e) {
//            LogThis(TAG_LOG, "no such algorithm " + e.toString());
//        } catch (Exception e) {
//            LogThis(TAG_LOG, "exception " + e.toString());
//        }
//    }


    // DIALOGS AND DISPLAYS ========================================================================
    public void progressDialog(Boolean status) {
        if (status) {
            dialog.show();
        } else {
            dialog.cancel();
        }
    }

    public void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setProgressDialogMessage(String message) {
        ProgressDialogMessage.setText(message);
    }

    public void myDialog(Context DialogContext, String Title, String Message) {
        final Dialog dialog = new Dialog(DialogContext);
        dialog.setContentView(R.layout.dialog_cancel);
        final TextView txt_message = dialog.findViewById(R.id.txt_message);
        final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final TextView txt_title = dialog.findViewById(R.id.txt_title);
        txt_title.setText(Title);
        txt_message.setText(Message);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void myPermissionsDialog(final Context dialogContext, int[] grantResults) {
        for (int result : grantResults) {
            if (result == -1) {
                final Dialog dialog = new Dialog(dialogContext);
                dialog.setContentView(R.layout.dialog_confirm);
                final TextView txt_message = dialog.findViewById(R.id.txt_message);
                final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
                final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                final TextView txt_title = dialog.findViewById(R.id.txt_title);
                txt_title.setText(R.string.txt_alert);
                txt_message.setText(R.string.error_permissions);
                btn_confirm.setText(R.string.txt_take_me_there);
                btn_cancel.setVisibility(View.GONE);
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", dialogContext.getPackageName(), null);
                        intent.setData(uri);
                        dialogContext.startActivity(intent);
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
        txt_message.setText(R.string.txt_locations_permission);
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

    public void dialogShare(final Activity context, final String STR_SHARE_LINK) {
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
                            .setContentUrl(Uri.parse(STR_SHARE_LINK))
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
                    emailIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARE_LINK);
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
                    messengerIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARE_LINK);
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
                    smsIntent.putExtra("sms_body", STR_SHARE_LINK);
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
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARE_LINK);
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

    public void dialogMakeCall(final Context context, final String phoneNumber) {
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
                    intent.setData(Uri.parse("tel:" + phoneNumber));
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

    public void ToastMessage(Context MessageContext, String Message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(MessageContext, Message, Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }


    // VALIDATIONS =================================================================================
    public boolean validateEmail(EditText editText) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (editText.getText().toString().length() < 1) {
            editText.setError(context.getString(R.string.error_field_required));
            animateFlash(editText);
            return false;
        } else if (!pattern.matcher(editText.getText().toString()).matches()) {
            editText.setError(context.getString(R.string.error_field_required));
            animateWobble(editText);
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    public boolean validateAppIsInstalled(String uri) {
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
            Iterator<String> iter = data.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONArray message_array = new JSONArray(String.valueOf(data.get(key)));
                    String display_message = "";
                    for (int i = 0; i < message_array.length(); i++) {
                        display_message = display_message.concat(message_array.getString(i)).concat("\n");
                    }
                    myDialog(context, context.getString(R.string.txt_errors), display_message);

                    Helpers.LogThis(TAG_LOG, display_message);

                } catch (JSONException e) {
                    e.printStackTrace();
                    LogThis(TAG_LOG, e.toString());
                    myDialog(context, context.getString(R.string.app_name), context.getString(R.string.error_unknown));
                }
            }


        } catch (Exception j) {
            j.printStackTrace();
            LogThis(TAG_LOG, j.toString());
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
                .duration(300)
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


    // NOTIFICATION CREATER ========================================================================
    public void createNotification(Context context, String MessageTitle, String messageBody, Bundle data) {
        Intent intent = new Intent(context, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("push", data);
        intent.putExtra("notification_title", MessageTitle);
        intent.putExtra("notification_body", messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, MessageTitle)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(MessageTitle)
                .setContentText(messageBody)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(ContextCompat.getColor(context, R.color.colorPrimary), 1000, 1000)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
            ShortcutBadger.applyCount(context, 1);
        }

    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ?
                R.mipmap.ic_launcher :
                R.mipmap.ic_launcher;
    }


    // COMMON ASYNC TASKS ==========================================================================
    // GET USER ====================================================================================
    public void asyncGetUser() {
        UserService userService = UserService.retrofit.create(UserService.class);
        final Call<JsonObject> call = userService.getUser();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        SharedPrefs.setUser(main.getJSONObject("data"));
                    }

                } catch (JSONException e) {
                    LogThis(TAG_LOG, e.toString());

                } catch (Exception e) {
                    LogThis(TAG_LOG, e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                LogThis(TAG_LOG, t.toString());
                LogThis(TAG_LOG, call.toString());
            }

        });
    }

    // GET COUNTIES ================================================================================
    public void asyncGetCounties() {
        UserService userService = UserService.retrofit.create(UserService.class);
        final Call<JsonObject> call = userService.getCounties();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    LogThis(TAG_LOG, main.toString());

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
                    LogThis(TAG_LOG, e.toString());

                } catch (Exception e) {
                    LogThis(TAG_LOG, e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                LogThis(TAG_LOG, t.toString());
                LogThis(TAG_LOG, call.toString());
            }

        });
    }


    // GET COUNTIES ================================================================================
    public void asyncGetJobTypes() {
        UserService userService = UserService.retrofit.create(UserService.class);
        final Call<JsonObject> call = userService.getJobTypes();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        JSONArray main_array = main.getJSONArray("data");
                        int length = main_array.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject object = main_array.getJSONObject(i);
                            SearchFilterModel searchFilterModel = new SearchFilterModel();
                            searchFilterModel.id = object.getInt("id");
                            searchFilterModel.name = object.getString("name");
                            db.setFilter(JOB_TYPE_TABLE_NAME, searchFilterModel);
                        }
                    }

                } catch (JSONException e) {
                    LogThis(TAG_LOG, e.toString());

                } catch (Exception e) {
                    LogThis(TAG_LOG, e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                LogThis(TAG_LOG, t.toString());
                LogThis(TAG_LOG, call.toString());
            }

        });
    }

    // GET CATEGORIES ==============================================================================
    public void asyncGetCategories() {
        UserService userService = UserService.retrofit.create(UserService.class);
        final Call<JsonObject> call = userService.getCategories();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

                    LogThis(TAG_LOG, main.toString());

                    if (main.getBoolean("success")) {
                        JSONArray main_array = main.getJSONArray("data");
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
                    LogThis(TAG_LOG, e.toString());

                } catch (Exception e) {
                    LogThis(TAG_LOG, e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                LogThis(TAG_LOG, t.toString());
                LogThis(TAG_LOG, call.toString());
            }

        });
    }

}
