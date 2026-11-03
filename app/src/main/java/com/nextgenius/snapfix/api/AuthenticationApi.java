package com.nextgenius.snapfix.api;

import com.nextgenius.snapfix.Model.CreateUserRequest;
import com.nextgenius.snapfix.Model.CreateUserResponse;
import com.nextgenius.snapfix.Model.UserRoleResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthenticationApi {

    @GET("user-role")
    Call<UserRoleResponse> getUserRole(
            @Query("email") String email
    );

    @POST("create-user")
    Call<CreateUserResponse> createUser(
            @Body CreateUserRequest request
    );
}