package com.nextgenius.snapfix.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SolutionPriorityRetrofitClient {

    private static final String BASE_URL =
            "https://solutionpriority-production-e551.up.railway.app/";

    private static Retrofit retrofit;

    private static SolutionPriorityApi api;


    private SolutionPriorityRetrofitClient() {
        // Prevent accidental object creation
    }


    public static SolutionPriorityApi getApi() {

        if (api == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();

            api = retrofit.create(
                    SolutionPriorityApi.class
            );
        }

        return api;
    }
}