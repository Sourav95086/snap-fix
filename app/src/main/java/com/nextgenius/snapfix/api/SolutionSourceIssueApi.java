package com.nextgenius.snapfix.api;

import com.nextgenius.snapfix.Model.SolutionSourceIssue;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SolutionSourceIssueApi {

    @GET("solution-source-issue/{solution_id}")
    Call<SolutionSourceIssue> getSourceIssueId(
            @Path("solution_id") String solutionId
    );
}