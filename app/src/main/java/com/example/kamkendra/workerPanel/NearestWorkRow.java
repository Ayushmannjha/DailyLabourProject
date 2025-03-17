package com.example.kamkendra.workerPanel;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.example.kamkendra.R;
import com.example.kamkendra.dto.ApplicationStatus;
import com.example.kamkendra.frontAPI.WorkerPanel;
import com.example.kamkendra.services.ApiService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.Response;
import retrofit2.Retrofit;

public class
NearestWorkRow extends RecyclerView.Adapter<NearestWorkRow.MyViewAdapterHolder> {

    private String token;
    private final Context context;
    private final String location;
    private final String[] details;

    private int[] workIds;
    private  int workerId;
    // Constructor

    public NearestWorkRow(){
        token = "";
        context = null;
        location = "";
        details = null;
    }

    public NearestWorkRow(Context context,String token ,String location, String[] details, int[] workIds, int workerId) {
        this.context = context;
        this.location = location;
        this.details = details;
        this.workIds = workIds;
        this.workerId = workerId;
        this.token = token;
    }

    @NonNull
    @Override
    public MyViewAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the row layout
        View view = LayoutInflater.from(context).inflate(R.layout.activity_nearest_work_row, parent, false);
        return new MyViewAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewAdapterHolder holder, int position) {
        // Set the location and details for each row
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        WorkerPanel workerPanel = retrofit.create(WorkerPanel.class);
        holder.location.setText(location); // Static location for all rows
        holder.details.setText(details[position]); // Dynamic details for each row
        Log.d("NearestWorkRow", "Token: " + token);

        workerPanel.getWorkStatus("Bearer " + token, workIds[position], workerId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                // Convert the response body to a string
                                String appStatusToken = response.body().string();
                                JWT jwt = new JWT(appStatusToken);

                                // Retrieve claims from the JWT token
                                String applicationString = jwt.getClaim("applicationStatus").asString();
                                ApplicationStatus applicationStatus = jwt.getClaim("applicationStatus").asObject(ApplicationStatus.class); // Read claim as String
                                if(applicationStatus!=null){
                                    holder.apply.setTextColor(Color.BLACK);
                                    holder.apply.setText("Applied"); // Change button text

                                    // Set button background using drawable resource
                                    holder.apply.setBackground(ContextCompat.getDrawable(context, R.drawable.login_button_background));

                                    // Disable further clicks (optional, if required)
                                    holder.apply.setEnabled(false);
                                }
                                Log.d("NearestWorkRow", "AppStatusToken: " + appStatusToken);
                                // Set text color based on status
                                Log.d("NearestWorkRow", "applicationStatus: " + applicationStatus);
                                Log.d("NearestWorkRow", "applicationStatus: " + applicationString);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("NearestWorkRow", "Error reading response body: " + e.getMessage());
                            }
                        } else {
                            Log.e("NearestWorkRow", "Response not successful: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Handle the error
                        Log.e("NearestWorkRow", "Request failed: " + t.getMessage());
                    }
                });


        // Handle Apply button click
        holder.apply.setOnClickListener(v -> {
              workerPanel.applyForJob("Bearer " + token, workIds[position], workerId)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.isSuccessful() && response.body() != null){
                                try {
                                    holder.apply.setTextColor(Color.BLACK);
                                    holder.apply.setText("Applied"); // Change button text

                                    // Set button background using drawable resource
                                    holder.apply.setBackground(ContextCompat.getDrawable(context, R.drawable.login_button_background));

                                    // Disable further clicks (optional, if required)
                                    holder.apply.setEnabled(false);
                                }catch (Exception e){

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
            // Set text color

        });
    }

    @Override
    public int getItemCount() {
        return details.length; // Number of rows
    }

    // ViewHolder Class
    public static class MyViewAdapterHolder extends RecyclerView.ViewHolder {
        TextView location, details, apply;

        public MyViewAdapterHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            apply = itemView.findViewById(R.id.applyButton);
            location = itemView.findViewById(R.id.address);
            details = itemView.findViewById(R.id.details);
        }
    }
}
