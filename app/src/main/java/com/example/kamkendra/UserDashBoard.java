package com.example.kamkendra;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auth0.android.jwt.JWT;
import com.example.kamkendra.userPanel.Settings;
import com.example.kamkendra.userPanel.UploadWork;

public class UserDashBoard extends AppCompatActivity {

    private TextView animationText,logout;
    String text = "अब श्रमिक ढूंढना हुआ आसान ☺";
    private int index = 0;
    private long delay = 150; // Typing speed in milliseconds

    private Handler handler = new Handler();

private String token;
TextView name;
    ImageView uploadWork, mostTrustedWorker, directContact, yourWork;
    SearchView searchWorker;

    ImageView settings, notification, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dash_board);

        // Set up window insets listener for system bar handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        animationText = findViewById(R.id.animationText);  // Only initialize once
        searchWorker = findViewById(R.id.serachWorker);   // Ensure correct ID
        uploadWork = findViewById(R.id.uploadWork);
        yourWork = findViewById(R.id.yourWorks);
        directContact = findViewById(R.id.directContact);
        mostTrustedWorker = findViewById(R.id.mostTrustedWorker);
        settings = findViewById(R.id.setting);
        notification = findViewById(R.id.notification);
        profile = findViewById(R.id.profile);
        // Start typing animation

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserDashBoard.this, Settings.class);
                startActivity(i);
            }
        });


        animateText();

        // Hide the text after 20 seconds
        handler.postDelayed(() -> animationText.setVisibility(View.INVISIBLE), 20000); // 20 seconds
        name = findViewById(R.id.name);
        token = "";
        Intent i = getIntent();
        token = i.getStringExtra("token");
        if (token != null) {
            JWT jwt = new JWT(token);
            String userRole = jwt.getClaim("role").asString(); // Replace "role" with the actual claim name
            String username = jwt.getClaim("name").asString(); // Replace "email" with the actual claim name

            // Set the user's email to the TextView
            name.setText(username);

            // Log claims for debugging
            Log.d("USERDashboard", "Role: " + userRole);

        } else {
            Log.e("USERDashboard", "Token is null");
        }
        try {

            // Set hint for SearchView
            searchWorker.setQueryHint("search for worker by role, example = 'plumber'");


            // Upload Work button click listener
            uploadWork.setOnClickListener(v -> {
                Intent i2 = new Intent(UserDashBoard.this, UploadWork.class);
                i2.putExtra("token", token); // Pass token
                startActivity(i2); // Start activity
            });

        } catch (Exception e) {
            Log.e("USER", "Error reading token: " + e.getMessage(), e);
        }
    }



    // Typing animation function
    private void animateText() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index <= text.length()) {
                    animationText.setText(text.substring(0, index++));
                    handler.postDelayed(this, delay); // Continue animation
                }
            }
        }, delay);
    }
}
