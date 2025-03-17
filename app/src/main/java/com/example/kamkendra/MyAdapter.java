package com.example.kamkendra;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyAdapterHolder> {

    Context context;
    int[] image;
    String[] title;
    String[] location;

    // Constructor
    public MyAdapter(Context context, int[] image, String[] title, String[] location) {
        this.context = context;
        this.image = image;
        this.title = title;
        this.location = location;
    }

    @NonNull
    @Override
    public MyAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.content_one_item_view, parent, false);
        return new MyAdapterHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterHolder holder, int position) {
        holder.poster.setBackgroundResource(image[position]); // Set background for the layout
        holder.title.setText(title[position]);
        holder.location.setText(location[position]);

        Log.d("MyAdapter", "Title: " + title[position] + ", Location: " + location[position]);

    }

    @Override
    public int getItemCount() {
        return title.length; // Use the length of one of the arrays
    }

    public class MyAdapterHolder extends RecyclerView.ViewHolder {
        ConstraintLayout poster;
        TextView title;
        TextView location;


        public MyAdapterHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.poster);
            location = itemView.findViewById(R.id.location);
            title = itemView.findViewById(R.id.title);

        }
    }
}
