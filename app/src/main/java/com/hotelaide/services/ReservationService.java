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
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReservationService {

    String TAG_LOG = "SERVICE: RESERVATIONS";
    Database db = new Database();

    OkHttpClient okClient = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("X-Auth-Token", Database.userModel.token)
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


    // RESERVATION FUNCTIONS =======================================================================
    // VALIDATE A RESERVATION ======================================================================
    @FormUrlEncoded
    @POST("validate-reservation")
    Call<JsonObject> validateReservation(
            @Field("date") String date,
            @Field("time") String time,
            @Field("rest_id") String rest_id
    );


    // MAKE A RESERVATION ==========================================================================
    @FormUrlEncoded
    @POST("make-reservation")
    Call<JsonObject> makeReservation(
            @Field("date") String date,
            @Field("time") String time,
            @Field("rest_id") String rest_id,
            @Field("user_id") int user_id,
            @Field("persons") String persons,
            @Field("children") String children,
            @Field("country_code") String country_code,
            @Field("phone_number") String phone_number,
            @Field("email") String email,
            @Field("special_requirements") String special_requirements
    );


    // GET ALL RESERVATIONS ========================================================================
    @GET("user/reservations/{user_id}")
    Call<JsonObject> getAllReservations(
            @Path("user_id") int user_id
    );


}