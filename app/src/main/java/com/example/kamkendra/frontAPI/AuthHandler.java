package com.example.kamkendra.frontAPI;



import com.example.kamkendra.dto.LoginRequest;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AuthHandler {

    @POST("/auth/register-worker")
    Call<ResponseBody> registerWorker(@Body Map<String,Object> request);


    @POST("/auth/register-user")
    Call<ResponseBody> registerUser(@Body Map<String,Object> request);



    @POST("/auth/login")
    Call<ResponseBody> login(@Body LoginRequest request);

    @POST("/auth/check-existing-worker")
    Call<ResponseBody> checkExistingUser(@Query("phone") String phone);

}
