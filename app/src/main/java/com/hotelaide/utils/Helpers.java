package com.hotelaide.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.JsonObject;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main_pages.activities.AboutUsActivity;
import com.hotelaide.main_pages.activities.DashboardActivity;
import com.hotelaide.services.UserService;
import com.hotelaide.start_up.SplashScreenActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Pattern;

import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.pm.PackageManager.GET_ACTIVITIES;
import static android.content.pm.PackageManager.GET_SIGNATURES;
import static android.content.pm.PackageManager.NameNotFoundException;

public class Helpers {

    public final static String TAG_LOG = "HELPER CLASS";

    private final static int INT_ANIMATION_TIME = 800;

//    private static Tracker sTracker;
//    private static GoogleAnalytics sAnalytics;

    private final Context context;

    private static Toast mToast;

    private final TextView ProgressDialogMessage;

    private final Dialog dialog;

    public final static String BroadcastValue = "com.hotelaide.ACTIONLOGOUT";
    public final static String BroadcastValueAsyncCompleted = "com.hotelaide.COMPLETED";

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
    public void Drawer_Item_Clicked(Context context, int id) {
        switch (id) {
            case R.id.drawer_dashboard:
                context.startActivity(new Intent(context, DashboardActivity.class));
                break;

            case R.id.drawer_about_us:
                context.startActivity(new Intent(context, AboutUsActivity.class));
                break;
        }
    }


    // BROADCASTS ==================================================================================
    public static void sessionExpiryBroadcast() {
        Context context = MyApplication.getAppContext();
        SharedPrefs.deleteAllSharedPrefs();
        Database db = new Database();
        context.startActivity(new Intent(context, SplashScreenActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        context.sendBroadcast(new Intent().setAction(BroadcastValue));
    }


    // LOGS ========================================================================================
    public static void LogThis(String PageName, String data) {
        if (BuildConfig.LOGGING) {
            Log.e(PageName, data);
        }
    }

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
            LogThis(TAG_LOG, "name not found " + e.toString());
        } catch (NoSuchAlgorithmException e) {
            LogThis(TAG_LOG, "no such algorithm " + e.toString());
        } catch (Exception e) {
            LogThis(TAG_LOG, "exception " + e.toString());
        }
    }


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
        dialog.setContentView(R.layout.dialog_confirm);
        final TextView txt_message = dialog.findViewById(R.id.txt_message);
        final TextView btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final TextView txt_title = dialog.findViewById(R.id.txt_title);
        txt_title.setText(Title);
        txt_message.setText(Message);
        btn_cancel.setVisibility(View.GONE);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void dialogNoGPS(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        final TextView txt_message = dialog.findViewById(R.id.txt_message);
        final TextView btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
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

    public void dialogMakeCall(final Context context, final String phoneNumber) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        final TextView txt_message = dialog.findViewById(R.id.txt_message);
        final TextView btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
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
            animate_flash(editText);
            return false;
        } else if (!pattern.matcher(editText.getText().toString()).matches()) {
            editText.setError(context.getString(R.string.error_field_required));
            animate_wobble(editText);
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
            animate_flash(editText);
            return false;
        } else {
            editText.setError(null);
            return true;
        }

    }

    public boolean validateEmptyTextView(TextView textView, String errorMessage) {
        if (textView.getText().toString().length() < 1) {
            ToastMessage(context, errorMessage);
            animate_flash(textView);
            return false;
        } else {
            return true;
        }

    }


    // FORMAT CURRENCY ==============================================================================
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


    // ANIMATIONS ==================================================================================
    public void animate_wobble(View v) {
        YoYo.with(Techniques.Wobble)
                .duration(INT_ANIMATION_TIME).delay(20)
                .playOn(v);
    }

    public void animate_flash(View v) {
        YoYo.with(Techniques.Flash)
                .duration(INT_ANIMATION_TIME)
                .delay(0)
                .repeat(2)
                .playOn(v);
    }

    public void animate_slide_in(View v) {
        YoYo.with(Techniques.SlideInRight)
                .duration(INT_ANIMATION_TIME).delay(10)
                .playOn(v);
    }

    public void animate_fade_in(View v) {
        YoYo.with(Techniques.FadeIn)
                .duration(2000)
                .playOn(v);
    }

    public static void animate_recyclerview(View view) {
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
                .setLights(context.getResources().getColor(R.color.colorPrimary), 1000, 1000)
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
                R.drawable.main_logo_icon :
                R.drawable.main_logo_icon;
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
                    SharedPrefs.setUser(main);

                } catch (JSONException e) {
                    LogThis(TAG_LOG, context.getString(R.string.log_exception) + e.toString());

                } catch (Exception e) {
                    LogThis(TAG_LOG, context.getString(R.string.log_exception) + e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                LogThis(TAG_LOG, context.getString(R.string.log_exception) + t.toString());
                LogThis(TAG_LOG, context.getString(R.string.log_exception) + call.toString());
            }

        });
    }

    // SET USER ====================================================================================
//    public void asyncUpdateUser(UserModel userModel) {
//        UserService userService = UserService.retrofit.create(UserService.class);
//        final Call<JsonObject> call = userService.updateUser();
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
//                try {
//                    JSONObject main = new JSONObject(String.valueOf(response.body()));
//                    if (!db.setUser(main)) {
//                        ToastMessage(context, "Account Update Failed");
//                    }
//                } catch (JSONException e) {
//                    Helpers.LogThis(TAG_LOG, "JSON exception " + e.toString());
//                    ToastMessage(context, "Account Update Failed");
//                } catch (Exception e) {
//                    Helpers.LogThis(TAG_LOG, context.getString(R.string.log_exception) + e.toString());
//                    ToastMessage(context, "Account Update Failed");
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
//                Helpers.LogThis(TAG_LOG, context.getString(R.string.log_exception) + t.toString());
//                Helpers.LogThis(TAG_LOG, context.getString(R.string.log_exception) + call.toString());
//                ToastMessage(context, "Account Update Failed");
//            }
//        });
//    }


}
