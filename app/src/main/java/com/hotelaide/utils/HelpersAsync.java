package com.hotelaide.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.GeneralInterface;
import com.hotelaide.interfaces.UserInterface;
import com.hotelaide.main.models.NotificationModel;
import com.hotelaide.main.models.SearchFilterModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hotelaide.utils.Helpers.createNotification;
import static com.hotelaide.utils.Helpers.logThis;
import static com.hotelaide.utils.StaticVariables.BROADCAST_SET_USER_COMPLETE;
import static com.hotelaide.utils.StaticVariables.BROADCAST_UPLOAD_COMPLETE;
import static com.hotelaide.utils.StaticVariables.CATEGORIES_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.COUNTY_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EDUCATION_LEVEL_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.EXTRA_FAILED;
import static com.hotelaide.utils.StaticVariables.EXTRA_PASSED;
import static com.hotelaide.utils.StaticVariables.JOB_TYPE_TABLE_NAME;
import static com.hotelaide.utils.StaticVariables.NOTIFICATION_TYPE_CODE_MESSAGE;
import static com.hotelaide.utils.StaticVariables.USER_ID;

public class HelpersAsync {

    public final static String TAG_LOG = "HELPER ASYNC CLASS";

    public HelpersAsync(){ }

    // COMMON ASYNC TASKS ==========================================================================
    // GET USER ====================================================================================
    public static void asyncGetUser() {
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
                    MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_SET_USER_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));

                } catch (Exception e) {
                    logThis(TAG_LOG, e.toString());
                    MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_SET_USER_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                logThis(TAG_LOG, t.toString());
                logThis(TAG_LOG, call.toString());
                MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_SET_USER_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
            }

        });
    }

    // GET COUNTIES ================================================================================
    public static void asyncGetCounties() {
        GeneralInterface generalInterface = GeneralInterface.retrofit.create(GeneralInterface.class);
        final Call<JsonObject> call = generalInterface.getCounties();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

//                    logThis(TAG_LOG, main.toString());

                    Database db = new Database();
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
    public static void asyncGetEducationalLevels() {
        GeneralInterface generalInterface = GeneralInterface.retrofit.create(GeneralInterface.class);
        final Call<JsonObject> call = generalInterface.getEducationalLevels();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

//                    logThis(TAG_LOG, main.toString());

                    Database db = new Database();
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
    public static void asyncGetJobTypes() {
        GeneralInterface generalInterface = GeneralInterface.retrofit.create(GeneralInterface.class);
        final Call<JsonObject> call = generalInterface.getJobTypes();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(response.body()));

//                    logThis(TAG_LOG, main.toString());

                    Database db = new Database();
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
    public static void asyncGetCategories() {
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
                        Database db = new Database();
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
    public static void asyncGetAllDocuments() {
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

                        Database db = new Database();
                        db.deleteDocumentsTable();

                        if (document_array != null && document_array.length() > 0) {
                            int length = document_array.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject document_object = document_array.getJSONObject(i);
                                db.setDocumentFromJson(document_object);
                            }
                        }
                    }
                    MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE));

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
                MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE));
            }

        });
    }

    // UPLOAD DOCUMENTS ============================================================================
    public static void asyncUploadDocument(final MultipartBody.Part partFile) {
        UserInterface userInterface = UserInterface.retrofit.create(UserInterface.class);
        Call<JsonObject> call = userInterface.setUserDocument(
                SharedPrefs.getInt(USER_ID),
                partFile
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                try {
                    Database db = new Database();
                    db.deleteDirtyDocuments();
                    JSONObject main = new JSONObject(String.valueOf(response.body()));
                    logThis(TAG_LOG, main.toString());
                    if (main.getBoolean("success")) {
                        db.deleteDirtyDocuments();
                        JSONObject data_object = main.getJSONObject("data");
                        db.setDocumentFromJson(data_object.getJSONObject("document"));

                        MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_PASSED, EXTRA_PASSED));
                    } else {
                        showErrorNotification(MyApplication.getAppContext(), MyApplication.getAppContext().getString(R.string.txt_upload_failed), MyApplication.getAppContext().getString(R.string.error_unknown));
                        MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
                    }
                } catch (JSONException e) {
                    showErrorNotification(MyApplication.getAppContext(), MyApplication.getAppContext().getString(R.string.txt_upload_failed), MyApplication.getAppContext().getString(R.string.error_server));
                    MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                logThis(TAG_LOG, t.toString());
                Database db = new Database();
                db.deleteDirtyDocuments();
                if (validateInternetConnection()) {
                    showErrorNotification(MyApplication.getAppContext(), MyApplication.getAppContext().getString(R.string.txt_upload_failed), MyApplication.getAppContext().getString(R.string.error_server));
                } else {
                    showErrorNotification(MyApplication.getAppContext(), MyApplication.getAppContext().getString(R.string.txt_upload_failed), MyApplication.getAppContext().getString(R.string.error_connection));
                }
                MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
            }
        });

    }

    private static void showErrorNotification(Context context, String title, String preview) {
        NotificationModel notification_model = new NotificationModel();
        notification_model.table_id = 0;
        notification_model.job_id = 1;
        notification_model.type_code = NOTIFICATION_TYPE_CODE_MESSAGE;
        notification_model.title = title;
        notification_model.preview = preview;
        createNotification(context, notification_model);

    }

    private static boolean validateInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return (netInfo != null && netInfo.isConnected());

    }

    // GET DOCUMENTS ===============================================================================
    public static void asyncDeleteDocument(final int id) {
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
                    Database db = new Database();
                    if (main.getBoolean("success")) {
                        db.deleteDocumentByID(String.valueOf(id));
                        MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_PASSED, EXTRA_PASSED));
                    } else {
                        MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
                    }
                } catch (JSONException e) {
                    MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
                    logThis(TAG_LOG, e.toString());

                } catch (Exception e) {
                    MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
                    logThis(TAG_LOG, e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                logThis(TAG_LOG, t.toString());
                logThis(TAG_LOG, call.toString());
                MyApplication.getAppContext().sendBroadcast(new Intent().setAction(BROADCAST_UPLOAD_COMPLETE).putExtra(EXTRA_FAILED, EXTRA_FAILED));
            }

        });
    }
}
