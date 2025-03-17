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

public interface WorkerPanel {
    @GET("/worker/profile")
    Call<ResponseBody> profile(@Header("Authorization") String authToken,@Query("id") int id);

    @PUT("/worker/profile-update")
    Call<ResponseBody> profileUpdate(@Header("Authorization") String authToken,@Body Map<String,Object> changes, @Query("id")int id);

    @GET("/worker/searchWork")
    Call<ResponseBody> searchWork(@Header("Authorization") String authToken, @Query("role") String role,@Query("city") String city);

    @GET("/worker/wallet-history")
    Call<ResponseBody> walletHistory(@Header("Authorization") String authToken,@Query("id") int id);

    @GET("/worker/nearest-work")
    Call<ResponseBody> nearestWork(@Header("Authorization") String authToken, @Query("id") int id);

    @GET("/worker/get-work-status")
    Call<ResponseBody> getWorkStatus(@Header("Authorization") String authToken, @Query("workId") int workId, @Query("workerId") int workerId);

    @POST("/worker/applyForJob")
    Call<ResponseBody> applyForJob(@Header("Authorization") String authToken, @Query("workId") int workId, @Query("workerId") int workerId);

    @GET("/worker/get-work-by-workerId")
    Call<ResponseBody> workerWorkes(@Header("Authorization") String authToken, @Query("workerId") int workerId);

    @GET("/worker/get-highest-paying-jobs")
    Call<ResponseBody> highPayingJobs(@Header("Authorization") String authToken, @Query("workerId") int workerId);

    @GET("/worker/get-notifications")
    Call<ResponseBody> getNotifications(@Header("Authorization") String token,  @Query("workerId") int workerId);

    @GET("/worker/get-notification-count")
    Call<ResponseBody> getNotificationsCount(@Header("Authorization") String token, @Query("workerId") int workerId);

    @POST("/worker/request")
    Call<ResponseBody> requests(@Header("Authorization") String token, @Query("position") int position, @Query("status")int status, @Query("userId")int userId, @Query("workerId") int workerId);

    @POST("/worker/change-password")
    Call<ResponseBody> updatePassword(@Header("Authorization") String token, @Query("workerId") int workerId, @Query("password") String password);


    @POST("/worker/update-notification")
    Call<ResponseBody> updateNotification(@Header("Authorization") String token, @Query("workerId") int workerId);
}
