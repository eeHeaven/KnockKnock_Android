package org.techtown.knockknock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://172.30.1.14:8081/";
    private static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

    public static Retrofit getInstance(){
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpCleint = new OkHttpClient.Builder();
        httpCleint.addInterceptor(loggingInterceptor);
        Gson gson = new GsonBuilder().setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpCleint.build())
                .build();
    }
}
