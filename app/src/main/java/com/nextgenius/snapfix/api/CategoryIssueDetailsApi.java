package com.nextgenius.snapfix.api;

import com.nextgenius.snapfix.Model.CategoryIssueDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CategoryIssueDetailsApi {

    @GET("issue-report/{reportId}")
    Call<CategoryIssueDetails> getIssueReport(
            @Path("reportId") int reportId
    );
}