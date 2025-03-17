package com.example.kamkendra.workerPanel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.example.kamkendra.R;
import com.example.kamkendra.dto.Work;
import com.example.kamkendra.frontAPI.WorkerPanel;
import com.example.kamkendra.services.ApiService;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchView extends AppCompatActivity {

    String token;
    RecyclerView list;
    EditText role;
    EditText location;

    int workerId;
    TextView search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        list  = findViewById(R.id.listofWorks);
        location = findViewById(R.id.location);
        role = findViewById(R.id.role);
        search = findViewById(R.id.search);
        TextView resultText = findViewById(R.id.textView43);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
         token = sharedPreferences.getString("authToken", null);
        JWT jwt = new JWT(token);
        workerId = jwt.getClaim("id").asInt();
        if (token == null) {
            Log.e("SearchView", "Token is null! User might not be logged in.");
        }
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roleText = role.getText().toString().trim();
                String locationText = location.getText().toString().trim();

                if (roleText.isEmpty()) {
                    role.setError("This field is required");
                    return;
                }
                if (locationText.isEmpty()) {
                    location.setError("This field is required");
                    return;
                }

                // Call API if inputs are valid

                serachForWork(roleText, locationText);
                resultText.setVisibility(View.VISIBLE);
                resultText.setText("Results of "+role.getText().toString() +" and "+location.getText().toString());
            }
        });


    }
    public void serachForWork(String role, String city){
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit =apiService.getRetrofit();
        WorkerPanel workerPanel = retrofit.create(WorkerPanel.class);

        workerPanel.searchWork("Bearer " + token, role, city).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        Log.e("SearchView", "API response is unsuccessful or empty: " + response.toString());
                        return;
                    }

                    // Extract response body as a string
                    String res = response.body().string();
                    Log.d("SearchView", "API Response: " + res);

                    // Parse JWT token
                    JWT jwt = new JWT(res);
                    List<Work> workList = jwt.getClaim("result").asList(Work.class); // Ensure claim name is correct

                    if (workList == null || workList.isEmpty()) {
                        Log.e("SearchView", "Work list is null or empty!");
                        return;
                    }

                    // Prepare data for RecyclerView
                    String[] details = new String[workList.size()];
                    int[] workIds = new int[workList.size()];
                    String location = workList.get(0).getCity();

                    for (int i = 0; i < workList.size(); i++) {
                        Work work = workList.get(i);

                        details[i] = String.format(
                                "Description: %s\nNeed: %s\nCity: %s, State: %s, Pincode: %s",
                                work.getDescription(),
                                work.getLaboursDetails().substring(1, work.getLaboursDetails().length() - 1),
                                work.getCity(),
                                work.getState(),
                                work.getPincode()
                        );
                        workIds[i] = work.getId();

                        Log.d("Works", "City: " + work.getCity() + ", Description: " + work.getDescription());
                    }

                    // Get token and worker ID from intent

                    if (token == null || workerId == -1) {
                        Log.e("SearchView", "Worker token or ID is missing!");
                        return;
                    }

                    // Set up RecyclerView
                    NearestWorkRow nearestWorkRow = new NearestWorkRow(SearchView.this, token, location, details, workIds, workerId);
                    list.setLayoutManager(new LinearLayoutManager(SearchView.this)); // Set layout manager first
                    list.setAdapter(nearestWorkRow);

                } catch (Exception e) {
                    Log.e("SearchView", "Error processing works data: " + e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("SearchView", "Something went wrong: " + t.getMessage(), t);
            }
        });

    }
}