package com.nextgenius.snapfix.api;

import com.nextgenius.snapfix.Model.SendWinnerEmailRequest;
import com.nextgenius.snapfix.Model.SendWinnerEmailResponse;
import com.nextgenius.snapfix.Model.TopReportersResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ContestApi {

    @GET("top-reporters")
    Call<TopReportersResponse> getTopReporters(
            @Query("issue_location") String issueLocation
    );

    @POST("send-winner-email")
    Call<SendWinnerEmailResponse> sendWinnerEmail(
            @Body SendWinnerEmailRequest request
    );
}