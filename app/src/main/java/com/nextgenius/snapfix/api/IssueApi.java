package com.nextgenius.snapfix.api;

import com.nextgenius.snapfix.Model.IssuesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IssueApi {

    @GET("issues/by-category-location")
    Call<IssuesResponse> getIssuesByCategoryLocation(
            @Query("issue_category") String issueCategory,
            @Query("issue_location") String issueLocation
    );
}