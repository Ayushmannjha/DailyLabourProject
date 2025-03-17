package com.example.kamkendra.userPanel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.example.kamkendra.R;
import com.example.kamkendra.frontAPI.UserPanel;
import com.example.kamkendra.services.ApiService;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UploadWork extends AppCompatActivity {

    private Map<String, Object> request = new HashMap<>();
    private Map<String, Integer> labourDetails = new HashMap<>();
    private UploadWorkRoleRow workRoleAdapter;
    private String token;
    private RecyclerView requirementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_work);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        token = sharedPreferences.getString("authToken", null);
        if(token==null){
            Intent i = getIntent();
            token = i.getStringExtra("token");
        }
        if (token == null) {
            Toast.makeText(this, "Authentication Token Missing", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Step 1 Views
        EditText stateName = findViewById(R.id.workState);
        EditText cityName = findViewById(R.id.workCity);
        EditText pincode = findViewById(R.id.worrkPincode);
        EditText description = findViewById(R.id.description);
        TextView next = findViewById(R.id.workNext);
        TextView title = findViewById(R.id.title);

        // Step 2 Views
        TextView type = findViewById(R.id.type);
        EditText quantity = findViewById(R.id.quantity);
        requirementList = findViewById(R.id.listOfRequirements);
        TextView add = findViewById(R.id.textView46);
        TextView next2 = findViewById(R.id.workNext2);

        // Step 3 Views
        EditText budget = findViewById(R.id.budget);
        TextView finish = findViewById(R.id.finish);

        // Setup RecyclerView
        workRoleAdapter = new UploadWorkRoleRow(labourDetails);
        requirementList.setAdapter(workRoleAdapter);
        requirementList.setLayoutManager(new LinearLayoutManager(this));

        // Step 1 Visibility
        stateName.setVisibility(View.VISIBLE);
        cityName.setVisibility(View.VISIBLE);
        pincode.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("Work Address Details");

        next.setOnClickListener(v -> {
            boolean isValid = true;

            if (stateName.getText().toString().isEmpty()) {
                stateName.setError("State is required");
                isValid = false;
            }
            if (cityName.getText().toString().isEmpty()) {
                cityName.setError("City is required");
                isValid = false;
            }
            if (pincode.getText().toString().isEmpty()) {
                pincode.setError("Pincode is required");
                isValid = false;
            }
            if (description.getText().toString().isEmpty()) {
                description.setError("Description is required");
                isValid = false;
            }

            if (isValid) {
                request.put("city", cityName.getText().toString());
                request.put("state", stateName.getText().toString());
                request.put("pincode", pincode.getText().toString());
                request.put("description", description.getText().toString());

                stateName.setVisibility(View.GONE);
                cityName.setVisibility(View.GONE);
                pincode.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                next.setVisibility(View.GONE);

                type.setVisibility(View.VISIBLE);
                quantity.setVisibility(View.VISIBLE);
                requirementList.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);
                next2.setVisibility(View.VISIBLE);
                title.setText("Work Requirements");
            }
        });

        type.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(UploadWork.this, type);
            String[] roles = {"Mason", "Carpenter", "Painter", "Welder", "Tile Setter",
                    "Roofer", "Electrician", "Plumber", "AC Technician", "Refrigeration Mechanic"};
            for (String role : roles) {
                popupMenu.getMenu().add(role);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                type.setText(item.getTitle());
                return true;
            });
            popupMenu.show();
        });

        Map<String,Integer> roles = new HashMap<>();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roles.put(type.getText().toString(),Integer.parseInt(quantity.getText().toString()));
                UploadWorkRoleRow workRoleRowroleRow =new  UploadWorkRoleRow(roles);
                requirementList.setAdapter(workRoleRowroleRow);
                requirementList.setLayoutManager(new LinearLayoutManager(UploadWork.this)); // Vertical layout

            }
        });

        next2.setOnClickListener(v -> {
            boolean isValid = true;
            if(requirementList.getAdapter().getItemCount()==0){
                Toast.makeText(UploadWork.this,"Select any Role", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if(isValid){
                request.put("labourDetails", labourDetails);
                type.setVisibility(View.GONE);
                quantity.setVisibility(View.GONE);
                requirementList.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
                next2.setVisibility(View.GONE);

                budget.setVisibility(View.VISIBLE);
                finish.setVisibility(View.VISIBLE);
                title.setText("Your Budget");
            }

        });

        finish.setOnClickListener(v -> {
            try {
                request.put("budget", Double.parseDouble(budget.getText().toString()));
                uploadWork(request);
            } catch (NumberFormatException e) {
                Toast.makeText(UploadWork.this, "Invalid budget amount", Toast.LENGTH_SHORT).show();
            }
            budget.setVisibility(View.GONE);
            finish.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
        });

    }


    private void uploadWork(Map<String, Object> request) {
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        UserPanel userPanel = retrofit.create(UserPanel.class);

        JWT jwt = new JWT(token);
        Integer id = jwt.getClaim("id").asInt();
        if (id == null) {
            Toast.makeText(this, "Invalid token", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ResponseBody> call = userPanel.uploadWork("Bearer " + token, request, id);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ImageView done = findViewById(R.id.imageView23);
                TextView wish = findViewById(R.id.textView42);
                done.setVisibility(View.VISIBLE);
                wish.setVisibility(View.VISIBLE);
                Toast.makeText(UploadWork.this, response.isSuccessful() ? "Work uploaded successfully" : "Error occurred", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UploadWork.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
