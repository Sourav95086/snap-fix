package com.nextgenius.snapfix.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportIdRetrofitClient {

    private static final String BASE_URL =
            "https://api1-production-a997.up.railway.app/";

    private static Retrofit retrofit;

    private static ReportIdApi api;

    public static ReportIdApi getApi() {

        if (retrofit == null) {

            retrofit =
                    new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            )
                            .build();
        }

        if (api == null) {

            api =
                    retrofit.create(
                            ReportIdApi.class
                    );
        }

        return api;
    }
}