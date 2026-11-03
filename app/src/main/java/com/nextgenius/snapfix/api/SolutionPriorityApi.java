package com.nextgenius.snapfix.api;

import com.nextgenius.snapfix.Model.SolutionPriorityResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SolutionPriorityApi {

    @GET("priorities")
    Call<SolutionPriorityResponse> getPriorities(
            @Query("city") String city
    );
}