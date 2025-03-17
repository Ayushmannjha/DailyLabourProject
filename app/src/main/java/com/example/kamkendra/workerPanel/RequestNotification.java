package com.example.kamkendra.workerPanel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kamkendra.R;
import com.example.kamkendra.dto.Request;
import com.example.kamkendra.frontAPI.WorkerPanel;
import com.example.kamkendra.services.ApiService;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RequestNotification extends RecyclerView.Adapter<RequestNotification.MyViewAdapterHolder> {

    List<Request> request;

    private final Context context;

    List<Integer> userIds;

    String token;

    int workerId;
    public RequestNotification(Context context, List<Request> request, List<Integer> userIds,int workerId,String token){
        this.request = request;
        this.context = context;
        this.token = token;
        this.userIds = userIds;
        this.workerId = workerId;
    }

    @NonNull
    @Override
    public MyViewAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_update,parent,false);

        return new RequestNotification.MyViewAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewAdapterHolder holder, int position) {
        // Ensure msg list is not null and has a valid index
        if (request != null && position < request.size()) {
            holder.msg.setText("Send the request");
        } else {
            holder.msg.setText("No message"); // Fallback in case of issues
        }

        // Ensure userIds list is not null and has a valid index
        if (userIds != null && position < userIds.size()) {
            holder.accept.setOnClickListener(v -> request(holder ,token, position, 1, userIds.get(position), workerId));
            holder.deny.setOnClickListener(v -> request(holder,token, position, 2, userIds.get(position), workerId));
        } else {
            holder.accept.setOnClickListener(null);
            holder.deny.setOnClickListener(null);
        }
    }


    public void request(@NonNull MyViewAdapterHolder holder,String token,int position, int status, int userId, int workerId){
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        WorkerPanel workerPanel = retrofit.create(WorkerPanel.class);

        workerPanel.requests("Bearer "+token, position, status,userId, workerId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Request",response.toString());
                    holder.accept.setText("response reorded");
                    holder.deny.setVisibility(View.GONE);
                }else {
                    Log.d("Request",response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Request","Something went wrong");
            }
        });
    }

    @Override
    public int getItemCount() {
        return request.size();
    }

    public static class MyViewAdapterHolder extends RecyclerView.ViewHolder {
        TextView msg, accept, deny;

        public MyViewAdapterHolder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.msg);
            accept = itemView.findViewById(R.id.accept);
            deny = itemView.findViewById(R.id.deny);
        }
    }
}
