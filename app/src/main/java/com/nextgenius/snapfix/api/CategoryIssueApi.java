package com.nextgenius.snapfix.api;

import com.nextgenius.snapfix.Model.CategoryResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CategoryIssueApi {

    @GET("issues/by-category-location")
    Call<CategoryResponse> getCategoryIssues(
            @Query("issue_category") String issueCategory,
            @Query("issue_location") String issueLocation
    );
}