package com.nextgenius.snapfix.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImpactRetrofitClient {

    private static final String BASE_URL =
            "https://api2-production-6328.up.railway.app/";

    private static Retrofit retrofit;

    public static com.nextgenius.snapfix.api.ImpactApi getApi() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(com.nextgenius.snapfix.api.ImpactApi.class);
    }
}