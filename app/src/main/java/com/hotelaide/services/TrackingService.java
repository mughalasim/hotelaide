package com.hotelaide.services;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.hotelaide.BuildConfig;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
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

public interface TrackingService {

    String TAG_LOG = "SERVICE: TRACKING";
    Database db = new Database();

    OkHttpClient okClient = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("X-Auth-Token", Database.userModel.user_token)
                            .header("Client-Identifier", BuildConfig.IDENTIFIER)
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

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.MAIN_URL)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    // TRACK RESTAURANT CALLS ======================================================================
    @FormUrlEncoded
    @POST("restaurant/track-call")
    Call<JsonObject> trackRestaurantCall(
            @Field("rest_id") String rest_id
    );

    // TRACK RESTAURANT MENU VIEW ==================================================================
    @FormUrlEncoded
    @POST("restaurant/track-menuview")
    Call<JsonObject> trackRestaurantMenuView(
            @Field("rest_id") String rest_id
    );

    // TRACK RESTAURANT CALL TO ORDER ==============================================================
    @FormUrlEncoded
    @POST("restaurant/track-ordercall")
    Call<JsonObject> trackRestaurantCallToOrder(
            @Field("rest_id") String rest_id,
            @Field("delivery_partner_id") String delivery_partner_id
    );


}