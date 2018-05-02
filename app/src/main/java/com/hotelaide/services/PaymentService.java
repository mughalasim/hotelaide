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

public interface PaymentService {

    String TAG_LOG = "SERVICE: PAYMENT";
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

    // CALCULATE DISCOUNT ==========================================================================
    @FormUrlEncoded
    @POST("calculate-discount")
    Call<JsonObject> calculate_discount(
            @Field("offer_id") int offer_id,
            @Field("amount") String amount
    );


    // REQUEST THE MPESA CHECKOUT ==================================================================
    @FormUrlEncoded
    @POST("request-stk-checkout")
    Call<JsonObject> requestMpesaCheckout(
            @Field("user_id") int user_id,
            @Field("rest_id") String rest_id,
            @Field("offer_id") int offer_id,
            @Field("amount") Float amount,        // Ksh
            @Field("phone") String phone           // 254722111111
    );
    //returns merchant_transaction_id


    // QUERY THE MPESA CHECKOUT ====================================================================
    @FormUrlEncoded
    @POST("query-stk-status")
    Call<JsonObject> queryMpesaCheckout(
            @Field("checkout_request_id") String checkout_request_id
    );


}