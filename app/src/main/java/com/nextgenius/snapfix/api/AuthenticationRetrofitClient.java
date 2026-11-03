package com.nextgenius.snapfix.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthenticationRetrofitClient {

    private static final String BASE_URL =
            "https://authentication-production-2de8.up.railway.app/";

    private static Retrofit retrofit;

    public static AuthenticationApi getApi() {

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
        }

        return retrofit.create(AuthenticationApi.class);
    }
}