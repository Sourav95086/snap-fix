package com.nextgenius.snapfix.api;

import com.nextgenius.snapfix.Model.HotspotResponse;
import com.nextgenius.snapfix.Model.IssueResponse;
import com.nextgenius.snapfix.Model.SolutionPriorityResponse;
import com.nextgenius.snapfix.Model.SolutionPriorityResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HotspotApi {

    @GET("hotspots/{city}")
    Call<HotspotResponse> getHotspots(
            @Path("city") String city
    );


    @GET("hotspots/{city}/{categoryId}/issues")
    Call<IssueResponse> getIssues(
            @Path("city") String city,
            @Path("categoryId") int categoryId
    );

    @GET("priorities")
    Call<SolutionPriorityResponse> getPriorities(
            @Query("city") String city
    );
}
