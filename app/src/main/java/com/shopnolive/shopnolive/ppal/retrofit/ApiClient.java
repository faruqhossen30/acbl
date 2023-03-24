package com.shopnolive.shopnolive.ppal.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shopnolive.shopnolive.ppal.util.Common;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static final Object LOCK = new Object();

    public static void clear(){
        synchronized (LOCK){
            retrofit = null;
        }

    }

    public static Retrofit getApiClient(){
        synchronized (LOCK){
            if (retrofit == null){
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

                OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(40, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .build();

                retrofit = new Retrofit.Builder()
                        .client(okHttpClient)
                        .baseUrl(Common.BASE_URL_MAIN)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }
            return retrofit;
        }
    }
}
