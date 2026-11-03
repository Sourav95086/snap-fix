package com.nextgenius.snapfix.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRetrofitClient {

    private static final String BASE_URL =
            "https://responsible-charm-production-004a.up.railway.app/";

    private static Retrofit retrofit;

    public static UserApi getApi() {

        if (retrofit == null) {

            retrofit =
                    new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            )
                            .build();
        }

        return retrofit.create(UserApi.class);
    }
}