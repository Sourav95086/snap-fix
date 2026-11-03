package com.nextgenius.snapfix.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DemographicRetrofitClient {

    private static final String BASE_URL =
            "https://demographic-production-739f.up.railway.app/";

    private static Retrofit retrofit;

    private static DemographicApi api;


    public static DemographicApi getApi() {

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
        }


        if (api == null) {

            api = retrofit.create(
                    DemographicApi.class
            );
        }


        return api;
    }
}