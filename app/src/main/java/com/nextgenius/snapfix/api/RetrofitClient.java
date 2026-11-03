package com.nextgenius.snapfix.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL =
            "https://hotspotmapping-production-dfff.up.railway.app/";

    private static Retrofit retrofit;

    public static HotspotApi getApi() {

        if (retrofit == null) {

            OkHttpClient client =
                    new OkHttpClient.Builder()
                            .connectTimeout(
                                    30,
                                    TimeUnit.SECONDS
                            )
                            .readTimeout(
                                    60,
                                    TimeUnit.SECONDS
                            )
                            .writeTimeout(
                                    60,
                                    TimeUnit.SECONDS
                            )
                            .build();

            retrofit =
                    new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            )
                            .build();
        }

        return retrofit.create(
                HotspotApi.class
        );
    }
}