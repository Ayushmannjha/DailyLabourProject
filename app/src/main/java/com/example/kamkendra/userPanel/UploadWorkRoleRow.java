package com.example.kamkendra.userPanel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kamkendra.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UploadWorkRoleRow extends RecyclerView.Adapter<UploadWorkRoleRow.ViewHolder> {
    private final List<Map.Entry<String, Integer>> itemList;

    public UploadWorkRoleRow(Map<String, Integer> itemList) {
        this.itemList = new ArrayList<>(itemList.entrySet()); // Convert Map to List
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item_of_upload_work_role_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, Integer> item = itemList.get(position);
        holder.selectedRole.setText(item.getKey()); // Set selectedRole from Map key
        holder.quantity.setText(String.valueOf(item.getValue())); // Set quantity from Map value
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView selectedRole;
        EditText quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selectedRole = itemView.findViewById(R.id.selectedRole);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }
}
