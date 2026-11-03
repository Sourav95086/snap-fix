package com.nextgenius.snapfix.api;

import com.nextgenius.snapfix.Model.PriorityResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DemographicApi {

    @GET("priorities")
    Call<PriorityResponse> getPriorities(
            @Query("city") String city
    );
}