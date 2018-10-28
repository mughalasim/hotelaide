package com.hotelaide.interfaces;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hotelaide.BuildConfig;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
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

import static com.hotelaide.utils.StaticVariables.ACCESS_TOKEN;

//import com.facebook.stetho.okhttp3.StethoInterceptor;

public interface EstablishmentInterface {
    String TAG_LOG = "CALL: ESTABLISHMENT";

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


    // GET ESTABLISHMENT BY ID =============================================================================
    @GET("establishment/{establishment_id}")
    Call<JsonObject> getEstablishment(
            @Path("establishment_id") int establishment_id
    );

    // GET JOB BY ID ===============================================================================
    @GET("jobs/{job_id}")
    Call<JsonObject> getJob(
            @Path("job_id") int job_id
    );

    // APPLY FOR JOB ===============================================================================
    @FormUrlEncoded
    @POST("jobs/apply")
    Call<JsonObject> applyForJob(
            @Field("user_id") int user_id,
            @Field("jobvacancy_id") int jobvacancy_id
    );

    // GET APPLIED JOBS ============================================================================
    @GET("jobs/{user_id}/applications")
    Call<JsonObject> getAppliedJobs(
            @Path("user_id") int user_id
    );

}