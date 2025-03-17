package com.example.kamkendra.frontAPI;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface UserPanel {
    @GET("/user/profile")
    Call<ResponseBody> profile(@Header("Authorization") String authToken,@Query("id") int id);

    @PUT("/user/profile-update")
    Call<ResponseBody> profileUpdate(@Header("Authorization") String authToken,@Body Map<String,Object> changes);

    @GET("/user/search-worker")
    Call<ResponseBody> searchWork(@Header("Authorization") String authToken,@Body Map<String,Object> request);

    @POST("/user/upload-work")
   Call<ResponseBody> uploadWork(@Header("Authorization") String authToken, @Body Map<String,Object> request, @Query("id") int id);
}