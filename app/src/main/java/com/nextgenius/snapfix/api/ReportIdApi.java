package com.nextgenius.snapfix.api;

import com.nextgenius.snapfix.Model.ReportIdResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReportIdApi {

    @GET("report-id")
    Call<ReportIdResponse> getReportId(
            @Query("issue_id") int issueId
    );
}