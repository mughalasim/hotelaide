package com.hotelaide.interfaces;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hotelaide.BuildConfig;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.hotelaide.utils.StaticVariables.ACCESS_TOKEN;

//import com.facebook.stetho.okhttp3.StethoInterceptor;

public interface UserInterface {
    String TAG_LOG = "CALL: USER";

    OkHttpClient okClient = new OkHttpClient.Builder()
//            .addNetworkInterceptor(new StethoInterceptor())
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .addHeader("Authorization", "Bearer " + SharedPrefs.getString(ACCESS_TOKEN))
                            .method(original.method(), original.body())
                            .build();

                    Response response = chain.proceed(request);

                    Helpers.logThis(TAG_LOG, "URL: " + request.url());
                    Helpers.logThis(TAG_LOG, "CODE:" + response.code());
                    if (response.code() == 401) {
                        Helpers.logThis(TAG_LOG, "MESSAGE: " + response.message());
                        Helpers.sessionExpiryBroadcast();
                    } else if (response.code() > 300) {
                        Helpers.logThis(TAG_LOG, "MESSAGE: " + response.message());
                    }

                    return response;
                }
            })
            .connectTimeout(BuildConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(BuildConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .build();

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.MAIN_URL)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();


    // GET USER ====================================================================================
    @GET("user")
    Call<JsonObject> getUser();


    // UPDATE USER =================================================================================
    @FormUrlEncoded
    @PUT("user/{user_id}")
    Call<JsonObject> setUserDetails(
            @Path("user_id") int user_id,
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("about_me") String about_me,
            @Field("country_code") int country_code,
            @Field("phone_number") String phone_number,
            @Field("email") String email,
            @Field("geo_lat") double geo_lat,
            @Field("geo_lng") double geo_lng,
            @Field("dob") String dob,
            @Field("fb_id") String fb_id,
            @Field("google_id") String google_id,
            @Field("gender") int gender
    );

    // UPDATE USER TOKEN ===========================================================================
    @FormUrlEncoded
    @PUT("user/{user_id}")
    Call<JsonObject> setUserToken(
            @Path("user_id") int user_id,
            @Field("firebase_token") String token
    );

    // IMAGE UPDATE ================================================================================
    @Multipart
    @POST("user/update-images/{user_id}")
    Call<JsonObject> setUserImages(
            @Path("user_id") int user_id,
            @Part MultipartBody.Part avatar,
            @Part MultipartBody.Part banner
    );

    // AVAILABILITY UPDATE =========================================================================
    @FormUrlEncoded
    @POST("user/{user_id}/availability")
    Call<JsonObject> setUserAvailability(
            @Path("user_id") int user_id,
            @Field("availability") int availability
    );

    // ADDRESS UPDATE ==============================================================================
    @FormUrlEncoded
    @POST("user/update-address/{user_id}")
    Call<JsonObject> setUserAddress(
            @Path("user_id") int user_id,
            @Field("county_id") int county_id,
            @Field("postal_code") String postal_code,
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("full_address") String full_address
    );

    // UPDATE PASSWORD =============================================================================
    @FormUrlEncoded
    @POST("user/{user_id}/password")
    Call<JsonObject> updateUserPassword(
            @Path("user_id") int user_id,
            @Field("current_password") String current_password,
            @Field("password") String password,
            @Field("confirm_password") String confirm_password
    );


    // DELETE USER =================================================================================
    @DELETE("user/delete/{user_id}")
    Call<JsonObject> deleteUser(
            @Path("user_id") int user_id
    );

    // GET ALL USERS ===============================================================================
    @GET("users")
    Call<JsonObject> getAllUsers(
            @Query("page") int page_number
    );

    // GET ONE MEMBER BY ID ========================================================================
    @GET("user/{member_id}")
    Call<JsonObject> getMemberByID(
            @Path("member_id") int user_id
    );


    // GET ALL DOCUMENTS ===========================================================================
    @GET("user/{user_id}/documents")
    Call<JsonObject> getAllDocuments(
            @Path("user_id") int user_id
    );

    // DOCUMENT UPDATE =============================================================================
    @Multipart
    @POST("user/update-documents/{user_id}")
    Call<JsonObject> setUserDocument(
            @Path("user_id") int user_id,
            @Part MultipartBody.Part file
    );

    // DELETE DOCUMENT =============================================================================
    @DELETE("user/update-documents/{user_id}/delete/{document_id}")
    Call<JsonObject> deleteDocument(
            @Path("user_id") int user_id,
            @Path("document_id") int document_id
    );


}