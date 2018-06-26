package com.hotelaide.services;

import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.hotelaide.BuildConfig;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserService {
    String TAG_LOG = "SERVICE: USER";

    OkHttpClient okClient = new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .addHeader("Authorization", "Bearer " + SharedPrefs.getString(SharedPrefs.ACCESS_TOKEN))
                            .method(original.method(), original.body())
                            .build();

                    Response response = chain.proceed(request);

                    Helpers.LogThis(TAG_LOG, "URL: " + request.url());
                    Helpers.LogThis(TAG_LOG, "CODE:" + response.code());
                    if (response.code() == 401) {
                        Helpers.LogThis(TAG_LOG, "MESSAGE: " + response.message());
                        Helpers.sessionExpiryBroadcast();
                    } else if (response.code() > 300) {
                        Helpers.LogThis(TAG_LOG, "MESSAGE: " + response.message());
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


    // GET USERS ===================================================================================
    @GET("user")
    Call<JsonObject> getUser();


    // UPDATE USER =================================================================================
    @FormUrlEncoded
    @POST("user/{user_id}")
    Call<JsonObject> setUserDetails(
            @Path("user_id") int user_id,
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("country_code") int country_code,
            @Field("phone_number") int phone_number,
            @Field("email") String email,
            @Field("geo_lat") double geo_lat,
            @Field("geo_lng") double geo_lng,
            @Field("dob") String dob,
            @Field("fb_id") String fb_id,
            @Field("google_id") String google_id
    );

    @Multipart
    @POST("user/update-images/{user_id}")
    Call<JsonObject> setUserImages(
            @Path("user_id") int user_id,
            @Part MultipartBody.Part avatar,
            @Part MultipartBody.Part banner
    );

    @FormUrlEncoded
    @POST("user/{user_id}/password")
    Call<JsonObject> setUserPassword(
            @Path("user_id") int user_id,
            @Field("password") String password
    );


    // DELETE USER =================================================================================
    @GET("user/delete/{user_id}")
    Call<JsonObject> deleteUser(
            @Path("user_id") int user_id
    );


}