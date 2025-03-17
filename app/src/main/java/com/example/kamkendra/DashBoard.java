package com.example.kamkendra;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auth0.android.jwt.JWT;
import com.example.kamkendra.frontAPI.WorkerPanel;
import com.example.kamkendra.services.ApiService;
import com.example.kamkendra.workerPanel.HighestPayingJobAdapter;
import com.example.kamkendra.workerPanel.NearestWorkAdapter;
import com.example.kamkendra.workerPanel.Notification;
import com.example.kamkendra.workerPanel.Profile;
import com.example.kamkendra.workerPanel.Settings;
import com.example.kamkendra.workerPanel.WalletDetails;
import com.example.kamkendra.workerPanel.Workerworke;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DashBoard extends AppCompatActivity {

    ImageView home,setting,notification,profile,highPayingWork,nearestWork,yourWorks,income;
    TextView name;
TextView notificationCount;
    String token;
SearchView searchView;
int workerId;

    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        token = "";
        Intent i = getIntent();
        token = i.getStringExtra("token");

        searchView = findViewById(R.id.searchView);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashBoard.this, com.example.kamkendra.workerPanel.SearchView.class);
                startActivity(i);
            }
        });


        Handler handler = new Handler(Looper.getMainLooper()); // Ensure Handler is initialized

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    getNotificationCount(); // Ensure this method is safe
                    handler.postDelayed(this, 1000); // Call every second
                } catch (Exception e) {
                    Log.d("DashBoard",e.getMessage());
                    e.printStackTrace();

                    handler.removeCallbacks(this); // Stop on failure
                }
            }
        };

        handler.post(runnable); // Start calling API



        name = findViewById(R.id.name);

        if (token != null) {
            JWT jwt = new JWT(token);
            String userRole = jwt.getClaim("role").asString(); // Replace "role" with the actual claim name
            String username = jwt.getClaim("name").asString(); // Replace "email" with the actual claim name

            // Set the user's email to the TextView
            name.setText(username);

            // Log claims for debugging
            Log.d("Dashboard", "Role: " + userRole);

        } else {
            Log.e("Dashboard", "Token is null");
        }

        home = findViewById(R.id.home);
        setting = findViewById(R.id.setting);
        notification = findViewById(R.id.notification);
        profile = findViewById(R.id.profile);
        notificationCount = findViewById(R.id.notificationCount);
        highPayingWork = findViewById(R.id.highPayingWorks);
        nearestWork = findViewById(R.id.nearestWork);
        yourWorks = findViewById(R.id.yourWorks);
        income = findViewById(R.id.income);




        nearestWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNearestWork();
            }
        });
        yourWorks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWorkerWorks();
            }
        });

        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashBoard.this, WalletDetails.class);
                startActivity(i);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id;
                 String name = "";
                 String email = "";
                 String phone = "";
                 String role = "";
                 String password = "";
                 String workPlaces = "";
                 int avability ;
                 String state = "";
                 String city = "";
                 String locality = "";
                 String pincode = "";
                 double wallet;
                if (token != null) {
                    JWT jwt = new JWT(token);
                    role = jwt.getClaim("role").asString(); // Replace "role" with the actual claim name
                    name = jwt.getClaim("name").asString(); // Replace "email" with the actual claim name
                    email = jwt.getClaim("email").asString();
                    workPlaces = jwt.getClaim("work_places").asString();
                    state = jwt.getClaim("state").asString();
                    city = jwt.getClaim("city").asString();
                    locality = jwt.getClaim("locality").asString();
                    phone = jwt.getClaim("phone").asString();
                    // Set the user's email to the TextView


                    // Log claims for debugging
                    Log.d("DashboardLo", "  Locality: " + locality);

                } else {
                    Log.e("Dashboard", "Token is null");
                }


                Intent i = new Intent(DashBoard.this, Settings.class);
                i.putExtra("name",name);
                i.putExtra("email",email);
                i.putExtra("phone",phone);
                i.putExtra("state",state);
                i.putExtra("city",city);
                i.putExtra("role",role);
                i.putExtra("locality",locality);
                i.putExtra("workPlaces",workPlaces);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        highPayingWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gethighPayingWork();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashBoard.this, Profile.class);
                startActivity(i);
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashBoard.this,  Notification.class);
                JWT jwt = new JWT(token);
                int id = jwt.getClaim("id").asInt();
                i.putExtra("worker", token);
                i.putExtra("workerId",id);
                startActivity(i);
            }
        });
    }

    public void getWorkerWorks(){
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit =apiService.getRetrofit();
        WorkerPanel workerPanel = retrofit.create(WorkerPanel.class);
        JWT jwt = new JWT(token);
        int id = jwt.getClaim("id").asInt();
        Log.d("DashBoard","getWorkerWork");
        Call<ResponseBody> call = workerPanel.workerWorkes("Bearer "+token,id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("DashBoard", "Failed to fetch worker works: " + response.code());
                    Toast.makeText(DashBoard.this, "Failed to load works", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(DashBoard.this, Workerworke.class);
                try {
                    String works = response.body().string();
                    i.putExtra("worker", token);
                    i.putExtra("works", works);
                    i.putExtra("workerId", id);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (IOException e) {
                    Log.e("DashBoard", "Error reading response body", e);
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Error", "Login failed: " + name.getError());
                Toast.makeText(DashBoard.this, "Login failed: " + name.getError(), Toast.LENGTH_SHORT).show();

                t.printStackTrace();
            }
        });
    }

    public void setNearestWork() {
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        WorkerPanel workerPanel = retrofit.create(WorkerPanel.class);
        JWT jwt = new JWT(token);
        int id = jwt.getClaim("id").asInt();
        Log.d("Dashboard","setNearestWork");

        Call<ResponseBody> call = workerPanel.nearestWork("Bearer " + token,  id);
        call.enqueue(new Callback<>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               Intent i = new Intent(DashBoard.this, NearestWorkAdapter.class);
                try {
                    String works = response.body().string();
                    i.putExtra("worker", token);
                    i.putExtra("works",works);
                    i.putExtra("workerId",id);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Error", "Login failed: " + name.getError());
                Toast.makeText(DashBoard.this, "Login failed: " + name.getError(), Toast.LENGTH_SHORT).show();

                t.printStackTrace();
            }
        });
    }

    public void gethighPayingWork(){
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        WorkerPanel workerPanel = retrofit.create(WorkerPanel.class);
        JWT jwt = new JWT(token);
        int id = jwt.getClaim("id").asInt();
        Log.d("Dashboard","setNearestWork");

        Call<ResponseBody> call = workerPanel.highPayingJobs("Bearer " + token,  id);
        call.enqueue(new Callback<>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent i = new Intent(DashBoard.this, HighestPayingJobAdapter.class);
                try {
                    String works = response.body().string();
                    i.putExtra("worker", token);
                    i.putExtra("works",works);
                    i.putExtra("workerId",id);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Error", "Login failed: " + name.getError());
                Toast.makeText(DashBoard.this, "Login failed: " + name.getError(), Toast.LENGTH_SHORT).show();

                t.printStackTrace();
            }
        });
    }

    public void getNotificationCount(){
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        WorkerPanel workerPanel = retrofit.create(WorkerPanel.class);
        JWT jwt = new JWT(token);
        int id = jwt.getClaim("id").asInt();
        workerPanel.getNotificationsCount("Bearer "+token,id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String count = response.body().string();
                    if(!count.equalsIgnoreCase("0")){
                        notificationCount.setVisibility(View.VISIBLE);
                        notificationCount.setText(count);
                    }

                    Log.d("DashBoard",count);
                } catch (IOException e) {
                    Log.d("DashBoard","Error occured");
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("DashBoard","Failure");
            }
        });
    }
}