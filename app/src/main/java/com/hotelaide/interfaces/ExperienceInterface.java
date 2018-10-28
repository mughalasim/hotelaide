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
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import static com.hotelaide.utils.StaticVariables.ACCESS_TOKEN;

//import com.facebook.stetho.okhttp3.StethoInterceptor;

public interface ExperienceInterface {
    String TAG_LOG = "CALL: WORK";

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


    // GET ALL WORK EXPERIENCES ====================================================================
    @GET("user/{user_id}/work-experience")
    Call<JsonObject> getAllWorkExperiences(
            @Path("user_id") int user_id
    );


    // CREATE WORK EXPERIENCE ======================================================================
    @FormUrlEncoded
    @POST("user/{user_id}/work-experience")
    Call<JsonObject> setWorkExperience(
            @Path("user_id") int user_id,
            @Field("company_name") String company_name,
            @Field("position") String position,
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Field("responsibilities") String responsibilities,
            @Field("current") int current
    );

    // UPDATE WORK EXPERIENCE ======================================================================
    @FormUrlEncoded
    @PUT("user/{user_id}/work-experience/{work_experience_id}")
    Call<JsonObject> updateWorkExperience(
            @Path("user_id") int user_id,
            @Path("work_experience_id") int work_experience_id,
            @Field("company_name") String company_name,
            @Field("position") String position,
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Field("responsibilities") String responsibilities,
            @Field("current") int current
    );

    // DELETE ONE WORK EXPERIENCES =================================================================
    @DELETE("user/{user_id}/work-experience/{work_experience_id}")
    Call<JsonObject> deleteOneWorkExperience(
            @Path("user_id") int user_id,
            @Path("work_experience_id") int work_experience_id
    );


//==================================================================================================

    // GET ALL EDUCATION EXPERIENCES ===============================================================
    @GET("user/{user_id}/education-experience")
    Call<JsonObject> getAllEducationExperiences(
            @Path("user_id") int user_id
    );


    // CREATE EDUCATION EXPERIENCE =================================================================
    @FormUrlEncoded
    @POST("user/{user_id}/education-experience")
    Call<JsonObject> setEducationExperience(
            @Path("user_id") int user_id,
            @Field("institution_name") String institution_name,
            @Field("education_level") int education_level,
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Field("study_field") String study_field,
            @Field("current") int current
    );

    // UPDATE EDUCATION EXPERIENCE =================================================================
    @FormUrlEncoded
    @PUT("user/{user_id}/education-experience/{education_experience_id}")
    Call<JsonObject> updateEducationExperience(
            @Path("user_id") int user_id,
            @Path("education_experience_id") int education_experience_id,
            @Field("institution_name") String institution_name,
            @Field("education_level") int education_level,
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Field("study_field") String study_field,
            @Field("current") int current
    );

    // DELETE ONE EDUCATION EXPERIENCES ============================================================
    @DELETE("user/{user_id}/education-experience/{education_experience_id}")
    Call<JsonObject> deleteOneEducationExperience(
            @Path("user_id") int user_id,
            @Path("education_experience_id") int education_experience_id
    );



}