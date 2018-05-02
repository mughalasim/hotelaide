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
import retrofit2.http.Query;

public interface RestaurantService {

    String TAG_LOG = "SERVICE: RESTAURANT";
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

    //  GET ONE RESTAURANT =========================================================================
    @GET("restaurant/{rest_id}")
    Call<JsonObject> getOneRestaurant(
            @Path("rest_id") String rest_id
    );

    // GET RESTAURANT COLLECTION ===================================================================
    @GET("collections/restaurants/{city_id}")
    Call<JsonObject> getRestaurantCollection(
            @Path("city_id") String city_id,
            @Query("page") String page_number
    );

    //  GET ALL RESTAURANT COLLECTIONS =============================================================
    @GET("collections/")
    Call<JsonObject> getAllRestaurantCollections(
            @Query("featured") String featured,
            @Query("city_id") String city_id
    );

    // POST ONE RESTAURANTS REVIEW =================================================================
    @FormUrlEncoded
    @POST("post-review")
    Call<JsonObject> postRestaurantsReviews(
            @Field("rest_id") String rest_id,
            @Field("user_id") int user_id,
            @Field("rating_food") int rating_food,
            @Field("rating_service") int rating_service,
            @Field("rating_ambiance") int rating_ambiance,
            @Field("rating_value") int rating_value,
            @Field("rating_average") int rating_average,
            @Field("review_text") String review_text
    );

    // GET ONE RESTAURANTS REVIEWS ===================================================================
    @GET("restaurant/reviews/{rest_id}")
    Call<JsonObject> getOneRestaurantsReviews(
            @Path("rest_id") String rest_id,
            @Query("page") String page_number
    );

    // CLAIM OFFER FROM ONE RESTAURANT =============================================================
    @FormUrlEncoded
    @POST("claim-offer")
    Call<JsonObject> claimOffer(
            @Field("offer_id") int offer_id,
            @Field("user_id") int user_id,
            @Field("confirmation_code") String code,
            @Field("action") String action
    );

    // FAVORITE ONE RESTAURANT =====================================================================
    @FormUrlEncoded
    @POST("favorite")
    Call<JsonObject> favoriteOneRestaurant(
            @Field("rest_id") String rest_id,
            @Field("user_id") int user_id
    );

    //  SEARCH RESTAURANT ==========================================================================
    @GET("restaurants/search/")
    Call<JsonObject> searchRestaurant(
            @Query("q") String searchTerm,
            @Query("page") String page_number,
            @Query("city") String city,
            @Query("area") String area,
            @Query("cuisines[]") String cuisines,
            @Query("restaurant_type") String type,
            @Query("lat") String lat,
            @Query("lng") String lng,
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("discounts_only") String discount,
            @Query("offers_only") String offers
    );


}