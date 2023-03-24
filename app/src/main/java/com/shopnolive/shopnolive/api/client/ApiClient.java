package com.shopnolive.shopnolive.api.client;


import static com.shopnolive.shopnolive.utils.Variable.accessToken;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static final String BASE_URL = "http://194.233.93.200/Api/";
    //public static final String BASE_URL = "https://aponseba.xyz/live.com/Api/";


    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.connectTimeout(3, TimeUnit.MINUTES);
            clientBuilder.readTimeout(3, TimeUnit.MINUTES);
            clientBuilder.writeTimeout(3, TimeUnit.MINUTES);
            clientBuilder.addInterceptor(new ResponseInterceptor());
            clientBuilder.addInterceptor(chain -> {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();
                return chain.proceed(newRequest);
            }).build();

            OkHttpClient client = clientBuilder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;

    }

}
