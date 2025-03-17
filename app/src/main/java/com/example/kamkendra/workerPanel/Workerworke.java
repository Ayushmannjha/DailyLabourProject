package com.example.kamkendra.workerPanel;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.kamkendra.dto.ApplicationStatus;
import com.example.kamkendra.dto.Work;

import java.util.List;

public class Workerworke extends AppCompatActivity {
    RecyclerView worksRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workerworke);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        worksRecycler = findViewById(R.id.workesList); // RecyclerView initialization
        // Retrieve data from Intent
        Intent intent = getIntent();
        String works = intent.getStringExtra("works");

        // Null check for works string
        if (works == null || works.isEmpty()) {
            Log.e("Works", "No works data found!");
            return; // Exit to avoid further errors
        }
        try {
            // Decode JWT token
            JWT jwt = new JWT(works);
            List<Work> workList = jwt.getClaim("workes").asList(Work.class); // Corrected claim name
            List<ApplicationStatus> applicationStatusList = jwt.getClaim("workerWorkes").asList(ApplicationStatus.class);

            // Null or empty check for the workList
            if (workList == null || workList.isEmpty()) {
                Log.e("Works", "Work list is null or empty!");
                return; // Exit if no data is available
            }
            String[] details = new String[workList.size()];
            String location = workList.get(0).getCity();
            int index = 0;
            int index1 = 0;
            int[] status = new int[applicationStatusList.size()];
            int[] workIds = new int[workList.size()];
            int workerId = intent.getIntExtra("workerId",0);
            // Populate details array and log data
            int ip = 0;
            for(ApplicationStatus app:applicationStatusList){

                int sta = app.getStatus();

                status[ip++] = sta;

            }


            for (Work work : workList) {


                String formattedDetails = String.format(
                        "Description:  %s\nNeed: %s\nCity: %s, State: %s, Pincode: %s",
                        work.getDescription(),

                        work.getLaboursDetails().substring(1, work.getLaboursDetails().length() - 1),
                        work.getCity(),
                        work.getState(),
                        work.getPincode()
                );
                details[index++] = formattedDetails;
                workIds[index1++] = work.getId();
                Log.d("Works", "City: " + work.getCity() + ", Description: " + work.getDescription());
            }
            Intent i = getIntent();
            String token = i.getStringExtra("worker");

            // Set up RecyclerView with Adapter
            WorkerWorkRow nearestWorkRow = new WorkerWorkRow(this,token,location, details,workIds, workerId, status);
            worksRecycler.setAdapter(nearestWorkRow);
            worksRecycler.setLayoutManager(new LinearLayoutManager(this)); // Vertical layout


        } catch (Exception e) {
            Log.e("Works", "Error processing works data: " + e.getMessage());
        }
        // Retrieve data from Intent

    }

}