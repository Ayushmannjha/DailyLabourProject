package com.example.kamkendra.workerPanel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kamkendra.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WorkerWorkRow extends RecyclerView.Adapter<WorkerWorkRow.MyViewAdapterHolder> {
    private final Context context;
    private final String location;
    private final String[] details;
    private final int[] status;
    private final int[] workIds;
    private final int workerId;
    private final String token;

    public WorkerWorkRow(Context context, String token, String location, String[] details, int[] workIds, int workerId, int[] status) {
        this.context = context;
        this.location = location;
        this.details = details;
        this.workIds = workIds;
        this.workerId = workerId;
        this.token = token;
        this.status = status;
    }

    @NonNull
    @Override
    public MyViewAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_nearest_work_row, parent, false);
        return new MyViewAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewAdapterHolder holder, int position) {
        holder.location.setText(location);
        holder.details.setText(details[position]);

        if (status[position] == 0) {
            holder.apply.setTextColor(Color.BLACK);
            holder.apply.setText("Applied");
            holder.apply.setBackground(ContextCompat.getDrawable(context, R.drawable.login_button_background));
            holder.apply.setEnabled(false);
        } else if (status[position] == 1) {
            holder.apply.setTextColor(Color.YELLOW);
            holder.apply.setText("Selected");
            holder.receive.setVisibility(View.VISIBLE);
            holder.receive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,CashfreePayment.class);
                    context.startActivity(i);
                }
            });
            holder.apply.setBackground(ContextCompat.getDrawable(context, R.drawable.login_button_background));
            holder.apply.setEnabled(false);
        } else if (status[position] == 2) {
            holder.apply.setTextColor(Color.GREEN);
            holder.apply.setText("Completed");
            holder.apply.setBackground(ContextCompat.getDrawable(context, R.drawable.login_button_background));
            holder.apply.setEnabled(false);
        } else {
            holder.apply.setTextColor(Color.RED);
            holder.apply.setText("X");
            holder.apply.setBackground(ContextCompat.getDrawable(context, R.drawable.login_button_background));
            holder.apply.setEnabled(false);
        }
    }



    @Override
    public int getItemCount() {
        return details.length;
    }

    public static class MyViewAdapterHolder extends RecyclerView.ViewHolder {
        TextView location, details, apply, receive;

        public MyViewAdapterHolder(@NonNull View itemView) {
            super(itemView);
            apply = itemView.findViewById(R.id.applyButton);
            location = itemView.findViewById(R.id.address);
            details = itemView.findViewById(R.id.details);
            receive = itemView.findViewById(R.id.receive);
        }
    }
}
