package com.hotelaide.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Patterns;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.regex.Pattern;

import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main_pages.activities.AboutUsActivity;
import com.hotelaide.main_pages.activities.HomeActivity;
import com.hotelaide.main_pages.activities.MyAccountActivity;
import com.hotelaide.main_pages.activities.RestaurantSearchActivity;
import com.hotelaide.main_pages.models.UserModel;
import com.hotelaide.services.GeneralService;
import com.hotelaide.services.UserService;
import com.hotelaide.start_up.SplashScreenActivity;
import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.pm.PackageManager.GET_ACTIVITIES;
import static android.content.pm.PackageManager.NameNotFoundException;
import static com.hotelaide.utils.Database.CUISINE_NAME;
import static com.hotelaide.utils.Database.CUISINE_TABLE_NAME;
import static com.hotelaide.utils.Database.userModel;

public class Helpers {

    public final static String TAG_LOG = "HELPER CLASS";
    public final static String SORT_NEARBY = "n";
    public final static String SORT_A_Z = "az";
    public final static String SORT_FEATURED = "pre";
    public final static String SORT_RATING = "p";

    public final static String ORDER_ASCENDING = "a";
//    public final static String ORDER_DESCENDING = "d";

    public final static String FLAG_TRUE = "1";
    public final static String FLAG_FALSE = "0";

    public final static String ADAPTER_DEFAULT = "DEFAULT";
    public final static String ADAPTER_DISTANCE = "DISTANCE";

//    public final static String STR_LOGGED_OUT_EXTRA = "logged_out";
//    public final static String STR_LOGGED_OUT_TRUE = "true";
//    private final static String STR_LOGGED_OUT_FALSE = "false";

    private final static int INT_ANIMATION_TIME = 600;

    public final static String STR_NAVIGATION_REST = "restaurant_id";
    public final static String STR_NAVIGATION_COLLECTION = "collection_id";
    public final static String STR_NAVIGATION_SEARCH = "search";

    private static Tracker sTracker;
    private static GoogleAnalytics sAnalytics;

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
        ProgressDialogMessage = dialog.findViewById(R.id.messageText);
        sAnalytics = GoogleAnalytics.getInstance(context);
        getDefaultTracker();
    }

    // GOOGLE ANALYTICS TRACKING ===================================================================
    synchronized private void getDefaultTracker() {
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }
    }

    public void setTracker(String TAG_LOG) {
        sTracker.setScreenName("ANDROID - " + TAG_LOG);
        sTracker.setAppVersion(BuildConfig.VERSION_NAME);
        sTracker.setClientId(String.valueOf(userModel.user_id));
        sTracker.send(new HitBuilders.ScreenViewBuilder().build());
        Helpers.LogThis(TAG_LOG, "TRACKED");
    }


    // DRAWER CLICKS ===============================================================================
    public void Drawer_Item_Clicked(Context context, int id) {
        switch (id) {
            case R.id.home:
                context.startActivity(new Intent(context, HomeActivity.class));
                break;

            case R.id.discounts_offers:
                context.startActivity(new Intent(context, RestaurantSearchActivity.class)
                        .putExtra("extra", "offer"));
                break;

            case R.id.nearby_restaurants:
                final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (manager != null) {
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        dialogNoGPS(context);
                    } else {
                        context.startActivity(new Intent(context, RestaurantSearchActivity.class)
                                .putExtra("extra", "nearby"));
                    }
                }
                break;

            case R.id.find_restaurants:
                context.startActivity(new Intent(context, RestaurantSearchActivity.class));
                break;

            case R.id.my_account:
                context.startActivity(new Intent(context, MyAccountActivity.class));
                break;

            case R.id.about_us:
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

//    public void getShaCertificate() {
//        PackageInfo info;
//        try {
//
//            info = context.getPackageManager().getPackageInfo(
//                    "com.hotelaide", GET_SIGNATURES);
//
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                Log.e("Hash key", something);
//                System.out.println("Hash key" + something);
//            }
//
//        } catch (NameNotFoundException e) {
//            LogThis("name not found " + e.toString());
//        } catch (NoSuchAlgorithmException e) {
//            LogThis("no such algorithm " + e.toString());
//        } catch (Exception e) {
//            LogThis("exception " + e.toString());
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
        dialog.setContentView(R.layout.dialog_confirm);
        final TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        final TextView txtOk = dialog.findViewById(R.id.txtOk);
        final TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        txtTitle.setText(Title);
        txtMessage.setText(Message);
        txtOk.setOnClickListener(new View.OnClickListener() {
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
        final TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        final TextView txtOk = dialog.findViewById(R.id.txtOk);
        final TextView txtCancel = dialog.findViewById(R.id.txtCancel);
        final TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        txtTitle.setText(R.string.txt_location_title);
        txtMessage.setText(R.string.txt_locations_permission);
        txtOk.setText(context.getString(R.string.txt_enable));
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.cancel();
            }
        });
        txtCancel.setVisibility(View.VISIBLE);
        txtCancel.setOnClickListener(new View.OnClickListener() {
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
    public boolean validateEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return !pattern.matcher(email).matches();
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

    public boolean validateMobileNumber(String MobileNumber) {
        return MobileNumber.length() < 9
                || MobileNumber.contains(" ")
                || MobileNumber.contains(",")
                || MobileNumber.contains(".")
                || MobileNumber.contains("(")
                || MobileNumber.contains("#")
                || MobileNumber.contains(")");
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


    // NAVIGATION ==================================================================================
    public static void setAppNavigation(String whereTo, String id, String Title, String body) {
        SharedPrefs.setNavigationData(whereTo + "~" + id + "~" + Title + "~" + body);
        Helpers.LogThis("NAVIGATE", "NAVIGATE TO: " + whereTo + "~" + id + "~" + Title + "~" + body);
    }


    // FORMAT NUMBERS
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


    // ACTION BAR SETTINGS FOR THE EDIT TEXT =======================================================
    public void setDefaultEditTextSelectionMode(EditText editText) {
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
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

    public void animate_fade_in_up(View v) {
        YoYo.with(Techniques.FadeInUp)
                .duration(INT_ANIMATION_TIME)
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
    // GET CUISINES ================================================================================
    public void asyncGetCuisines() {
        if (db.getListItems(CUISINE_TABLE_NAME, CUISINE_NAME).size() < 2) {
            GeneralService generalService = GeneralService.retrofit.create(GeneralService.class);
            final Call<JsonObject> call = generalService.getAllCuisines();
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    try {
                        JSONObject main = new JSONObject(String.valueOf(response.body()));
                        LogThis(TAG_LOG, context.getString(R.string.log_response) + String.valueOf(response.body()));

                        if (!main.getBoolean("error")) {
                            JSONArray jArray = main.getJSONArray("result");
                            db.setCuisine(
                                    "0",
                                    "All"
                            );
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject json_data = jArray.getJSONObject(i);
                                db.setCuisine(
                                        json_data.getString("id"),
                                        json_data.getString("name")
                                );
                            }
                        }
                        context.sendBroadcast(new Intent().setAction(BroadcastValueAsyncCompleted));
                    } catch (JSONException e) {
                        context.sendBroadcast(new Intent().setAction(BroadcastValueAsyncCompleted));
                        LogThis(TAG_LOG, context.getString(R.string.log_exception) + e.toString());

                    } catch (Exception e) {
                        context.sendBroadcast(new Intent().setAction(BroadcastValueAsyncCompleted));
                        LogThis(TAG_LOG, context.getString(R.string.log_exception) + e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    LogThis(TAG_LOG, context.getString(R.string.log_exception) + t.toString());
                    LogThis(TAG_LOG, context.getString(R.string.log_exception) + call.toString());
                    call.clone();
                }
            });
        }
    }

    // GET CITIES ==================================================================================
    public void asyncGetCities() {
        GeneralService generalService = GeneralService.retrofit.create(GeneralService.class);
        final Call<JsonObject> call = generalService.getCountries();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    LogThis(TAG_LOG, context.getString(R.string.log_response) + String.valueOf(response.body()));
                    if (!main.getBoolean("error")) {

                        JSONArray jArray = main.getJSONArray("result");
                        int result_length = jArray.length();

                        for (int i = 0; i < result_length; i++) {
                            JSONObject countryObject = jArray.getJSONObject(i);

                            String CITY_NAME;

                            SharedPrefs.setCountryFlag(countryObject.getString("country_flag"));

                            JSONArray active_cities = countryObject.getJSONArray("active_cities");
                            int result_length2 = active_cities.length();
                            for (int w = 0; w < result_length2; w++) {
                                JSONObject cityObject = active_cities.getJSONObject(w);
                                final String CITY_ID = cityObject.getString("id");
                                CITY_NAME = cityObject.getString("name");
                                db.setCity(CITY_ID, CITY_NAME);

                                asyncGetAreas(CITY_ID);
                            }
                        }
                    }

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

    // GET AREAS ===================================================================================
    public void asyncGetAreas(final String city_id) {
        GeneralService generalService = GeneralService.retrofit.create(GeneralService.class);
        final Call<JsonObject> call = generalService.getAllAreas(city_id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    LogThis(TAG_LOG, context.getString(R.string.log_response) + String.valueOf(response.body()));
                    if (!main.getBoolean("error")) {
                        JSONArray jArray = main.getJSONArray("result");
                        db.setArea(
                                "0",
                                "All",
                                String.valueOf(city_id)
                        );
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);
                            db.setArea(
                                    json_data.getString("id"),
                                    json_data.getString("name"),
                                    String.valueOf(city_id)
                            );
                        }
                    }
                    context.sendBroadcast(new Intent().setAction(BroadcastValueAsyncCompleted));
                } catch (JSONException e) {
                    context.sendBroadcast(new Intent().setAction(BroadcastValueAsyncCompleted));
                    LogThis(TAG_LOG, context.getString(R.string.log_exception) + e.toString());

                } catch (Exception e) {
                    context.sendBroadcast(new Intent().setAction(BroadcastValueAsyncCompleted));
                    LogThis(TAG_LOG, context.getString(R.string.log_exception) + e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                LogThis(TAG_LOG, context.getString(R.string.log_exception) + t.toString());
                LogThis(TAG_LOG, context.getString(R.string.log_exception) + call.toString());
                context.sendBroadcast(new Intent().setAction(BroadcastValueAsyncCompleted));
            }
        });

    }

    // GET RESTAURANT TYPES ========================================================================
    public void asyncGetTypes() {
        GeneralService generalService = GeneralService.retrofit.create(GeneralService.class);
        final Call<JsonObject> call = generalService.getAllTypes();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    LogThis(TAG_LOG, context.getString(R.string.log_response) + String.valueOf(response.body()));
                    if (!main.getBoolean("error")) {
                        JSONArray jArray = main.getJSONArray("result");
                        db.setType(
                                "0",
                                "All"
                        );
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);
                            db.setType(
                                    json_data.getString("id"),
                                    json_data.getString("name")
                            );
                        }
                    }
                    context.sendBroadcast(new Intent().setAction(BroadcastValueAsyncCompleted));
                } catch (JSONException e) {
                    LogThis(TAG_LOG, context.getString(R.string.log_exception) + e.toString());
                    context.sendBroadcast(new Intent().setAction(BroadcastValueAsyncCompleted));

                } catch (Exception e) {
                    LogThis(TAG_LOG, context.getString(R.string.log_exception) + e.toString());
                    context.sendBroadcast(new Intent().setAction(BroadcastValueAsyncCompleted));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                LogThis(TAG_LOG, context.getString(R.string.log_exception) + t.toString());
                LogThis(TAG_LOG, context.getString(R.string.log_exception) + call.toString());
            }
        });

    }

    // GET USER ====================================================================================
    public void asyncGetUser() {
        UserService userService = UserService.retrofit.create(UserService.class);
        final Call<JsonObject> call = userService.getUserObject();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    db.setUser(main);
                    SharedPrefs.setAsyncCallUserDetails(false);

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
    public void asyncSetUser(UserModel userModel) {
        UserService userService = UserService.retrofit.create(UserService.class);
        final Call<JsonObject> call = userService.updateUser(
                userModel.first_name,
                userModel.first_name,
                userModel.last_name,
                userModel.email,
                userModel.profile_pic,
                userModel.banner_pic,
                userModel.user_token,
                userModel.phone,
                userModel.dob,
                userModel.fb_id
        );
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    if (!db.setUser(main)) {
                        ToastMessage(context, "Account Update Failed");
                    }
                } catch (JSONException e) {
                    Helpers.LogThis(TAG_LOG, "JSON exception " + e.toString());
                    ToastMessage(context, "Account Update Failed");
                } catch (Exception e) {
                    Helpers.LogThis(TAG_LOG, context.getString(R.string.log_exception) + e.toString());
                    ToastMessage(context, "Account Update Failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Helpers.LogThis(TAG_LOG, context.getString(R.string.log_exception) + t.toString());
                Helpers.LogThis(TAG_LOG, context.getString(R.string.log_exception) + call.toString());
                ToastMessage(context, "Account Update Failed");
            }
        });
    }


}
