package com.nextgenius.snapfix.api;

import com.nextgenius.snapfix.UserIssuesResponse;
import com.nextgenius.snapfix.UserPointsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserApi {

    @GET("user-issues")
    Call<UserIssuesResponse> getUserIssues(
            @Query("email") String email
    );

    @GET("user-points")
    Call<UserPointsResponse> getUserPoints(
            @Query("email") String email
    );
}