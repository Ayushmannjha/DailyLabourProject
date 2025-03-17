package com.example.kamkendra.workerPanel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.example.kamkendra.DashBoard;
import com.example.kamkendra.R;
import com.example.kamkendra.dto.JobNotification;
import com.example.kamkendra.dto.ReadNotification;
import com.example.kamkendra.frontAPI.WorkerPanel;
import com.example.kamkendra.services.ApiService;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationJobUpdateRow extends RecyclerView.Adapter<NotificationJobUpdateRow.MyViewAdapterHolder> {

    private final List<JobNotification> jobNotification;
    private final Context context;
    private final String token;

    List<ReadNotification> readNotifications;

    public NotificationJobUpdateRow(Context context, List<JobNotification> jobNotification, String token,List<ReadNotification> readNotifications) {
        this.jobNotification = jobNotification;
        this.context = context;
        this.token = token;
        this.readNotifications = readNotifications;
    }

    @NonNull
    @Override
    public NotificationJobUpdateRow.MyViewAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.job_notification_one_item, parent, false);
        return new NotificationJobUpdateRow.MyViewAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewAdapterHolder holder, int position) {

        ReadNotification readNotification = readNotifications.get(position);

        if(readNotification.getIsRead()==0){
        holder.oval.setVisibility(View.VISIBLE);
    }
        holder.msg.setText("New Job update in your location");
        holder.nearestJob.setOnClickListener(v -> setNearestWork());
    }

    private void setNearestWork() {
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        WorkerPanel workerPanel = retrofit.create(WorkerPanel.class);

        JWT jwt = new JWT(token);
        Integer id = jwt.getClaim("id").asInt();

        if (id == null) {
            Log.e("NotificationJobUpdateRow", "Invalid JWT token: ID is null");
            return;
        }

        Log.d("NotificationJobUpdateRow", "Fetching nearest work for Worker ID: " + id);

        Call<ResponseBody> call = workerPanel.nearestWork("Bearer " + token, id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String works = response.body().string();
                        Intent intent = new Intent(context, NearestWorkAdapter.class);
                        intent.putExtra("worker", token);
                        intent.putExtra("works", works);
                        intent.putExtra("workerId", id);
                        context.startActivity(intent);
                    } catch (IOException e) {
                        Log.e("NotificationJobUpdateRow", "Error parsing response", e);
                    }
                } else {
                    Log.e("NotificationJobUpdateRow", "API Error: " + response.code());
                    Toast.makeText(context, "Failed to fetch work details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("NotificationJobUpdateRow", "API call failed: " + t.getMessage());
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return readNotifications.size();
    }

    public static class MyViewAdapterHolder extends RecyclerView.ViewHolder {
        TextView msg, nearestJob;
        View oval;
        public MyViewAdapterHolder(@NonNull View itemView) {
            super(itemView);
            oval = itemView.findViewById(R.id.oval);
            msg = itemView.findViewById(R.id.msg);
            nearestJob = itemView.findViewById(R.id.jobs);
        }
    }
}
