package com.example.kamkendra.workerPanel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.example.kamkendra.R;
import com.example.kamkendra.dto.JobNotification;
import com.example.kamkendra.dto.ReadNotification;
import com.example.kamkendra.dto.Request;
import com.example.kamkendra.frontAPI.WorkerPanel;
import com.example.kamkendra.services.ApiService;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Notification extends AppCompatActivity {

    RecyclerView jobs;
    RecyclerView request;

    String token;

    String notificationToken;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private Runnable runnable;

    int workerId;

    List<JobNotification> jobNotifications;
    List<Request> requestNotifications;

    List<ReadNotification> readNotifications;
    List<Integer> userIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        jobs = findViewById(R.id.jobs);
        request = findViewById(R.id.requests);
        Intent i = getIntent();
        token = i.getStringExtra("worker");
        workerId = i.getIntExtra("workerId", -1);
        JWT jwt = null;
        if (token != null) {
            jwt = new JWT(token);
            String userRole = jwt.getClaim("role").asString(); // Replace "role" with the actual claim name
            String username = jwt.getClaim("name").asString(); // Replace "email" with the actual claim name

            // Set the user's email to the TextView


            // Log claims for debugging
            Log.d("Notifications", token);

        } else {
            Log.e("Notifications", "Token is null");
        }


        getNotification(workerId);

    }

    public void getNotification(int workerId) {
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        WorkerPanel workerPanel = retrofit.create(WorkerPanel.class);

        workerPanel.getNotifications("Bearer " + token, workerId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        notificationToken = response.body().string();

                        // ✅ Parse JWT after receiving response
                        JWT jwt = new JWT(notificationToken);
                        jobNotifications = jwt.getClaim("jobNotifications").asList(JobNotification.class);
                        requestNotifications = jwt.getClaim("requestNotification").asList(Request.class);
                        userIds = jwt.getClaim("userIds").asList(Integer.class);
                        readNotifications = jwt.getClaim("readNotification").asList(ReadNotification.class);
                        if (jobNotifications != null && !jobNotifications.isEmpty()) {
                            Log.d("Notification", "Jobs: " + jobNotifications);

                            // ✅ Set layout manager before setting adapter
                            jobs.setLayoutManager(new LinearLayoutManager(Notification.this));

                            // ✅ Create and set adapter
                            NotificationJobUpdateRow jobNotificationAdapter = new NotificationJobUpdateRow(Notification.this, jobNotifications,token,readNotifications);
                            jobs.setAdapter(jobNotificationAdapter);

                            // ✅ Notify dataset changed
                            jobNotificationAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("Notification", "Job notifications are empty.");
                        }
                        if (requestNotifications != null && !requestNotifications.isEmpty()){
                            Log.d("Notification", "Jobs: " + requestNotifications);

                            // ✅ Set layout manager before setting adapter
                            request.setLayoutManager(new LinearLayoutManager(Notification.this));

                            // ✅ Create and set adapter
                            RequestNotification jobNotificationAdapter = new RequestNotification(Notification.this, requestNotifications, userIds ,workerId,token);
                            request.setAdapter(jobNotificationAdapter);

                            // ✅ Notify dataset changed
                            jobNotificationAdapter.notifyDataSetChanged();
                    }else{

                        }

                    } else {
                        Log.e("Notification", "Failed to fetch notifications. Response: " + response.errorBody().string());
                    }
                } catch (Exception e) {
                    Log.e("Notification", "Error parsing response: ", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Notification", "Failed to fetch the notification data", t);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateNotification(workerId);
    }
    public void updateNotification(int workerId){
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        WorkerPanel workerPanel = retrofit.create(WorkerPanel.class);
        workerPanel.updateNotification("Bearer "+token,workerId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Notification","destroy method successfully");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Notification","destroy method error");
            }
        });
    }
}