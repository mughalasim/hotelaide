package com.hotelaide.services;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.hotelaide.BuildConfig;
import com.hotelaide.utils.Database;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface LoginService {
    Database db = new Database();

    OkHttpClient okClient = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("X-Auth-Token", Database.userModel.user_token)
                            .header("Client-Identifier", BuildConfig.IDENTIFIER)
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            })
            .connectTimeout(BuildConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(BuildConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.MAIN_URL)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    // AUTHENTICATE PHONE ==========================================================================
    @FormUrlEncoded
    @POST("user-otp")
    Call<JsonObject> sendPhone(
            @Field("phone") String phone,
            @Field("country_code") String country_code
    );

    // AUTHENTICATE CODE ===========================================================================
    @FormUrlEncoded
    @POST("user-validate")
    Call<JsonObject> sendCode(
            @Field("otp") String otp,
            @Field("phone") String phone
    );


    // REGISTER ====================================================================================
    @FormUrlEncoded
    @POST("register")
    Call<JsonObject> register(
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("country_code") String country_code,
            @Field("fb_id") String fb_id
    );

    //  AUTHENTICATION FACEBOOK USER ===============================================================
    @GET("get-fb-user")
    Call<JsonObject> getFBUser(
            @Query("fb_id") String fb_id,
            @Query("fb_email") String email
    );

}