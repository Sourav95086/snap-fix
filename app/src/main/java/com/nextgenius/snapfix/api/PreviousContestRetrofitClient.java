package com.nextgenius.snapfix.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PreviousContestRetrofitClient {

    private static final String BASE_URL =
            "https://ward-api-production.up.railway.app/";

    private static Retrofit retrofit;

    public static PreviousContestApi getApi() {

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
        }

        return retrofit.create(
                PreviousContestApi.class
        );
    }
}