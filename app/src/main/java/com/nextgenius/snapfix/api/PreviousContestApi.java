package com.nextgenius.snapfix.api;

import com.nextgenius.snapfix.PreviousContestResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PreviousContestApi {

    @GET("top-reporters")
    Call<PreviousContestResponse> getTopReporters(
            @Query("issue_location") String location
    );
}