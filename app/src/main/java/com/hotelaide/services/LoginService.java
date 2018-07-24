package com.hotelaide.services;

import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hotelaide.BuildConfig;
import com.hotelaide.utils.Helpers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginService {
    String TAG_LOG = "SERVICE: LOGIN";

    OkHttpClient okClient = new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .addHeader("Accept", "application/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();

                    Response response = chain.proceed(request);
                    Helpers.LogThis(TAG_LOG, "URL: " + request.url());
                    Helpers.LogThis(TAG_LOG, "CODE:" + response.code());
                    if (response.code() == 401) {
                        Helpers.LogThis(TAG_LOG, "MESSAGE: " + response.message());
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


    // AUTHENTICATE FUNCTION =======================================================================
    @FormUrlEncoded
    @POST("login")
    Call<JsonObject> userLogin(
            @Field("email") String email,
            @Field("password") String password
    );


    // REGISTER USER ===============================================================================
    @FormUrlEncoded
    @POST("register")
    Call<JsonObject> userRegister(
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("country_code") int country_code,
            @Field("phone_number") int phone_number,
            @Field("email") String email,
            @Field("password") String password,
            @Field("password_confirmation") String password_confirmation,
            @Field("dob") String dob,
            @Field("fb_id") String fb_id,
            @Field("google_id") String google_id,
            @Field("account_type") String account_type
    );

    // RESET PASSWORD =======================================================================
    @FormUrlEncoded
    @POST("forgot/password")
    Call<JsonObject> resetPassword(
            @Field("email") String email
    );

}