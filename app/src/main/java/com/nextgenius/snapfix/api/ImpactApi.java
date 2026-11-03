package com.nextgenius.snapfix.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ImpactApi {

    @POST("predict-impact/{issue_id}")
    Call<JsonObject> predictImpact(
            @Path("issue_id") int issueId
    );
}