package com.example.kamkendra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auth0.android.jwt.JWT;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private TextView buttonSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("authToken", null);

        if (token != null) {
            // Token exists, decode it to get the role
            JWT jwt = new JWT(token);
            String userRole = jwt.getClaim("role").asString();

            Intent intent;
            if ("USER".equals(userRole)) {
                intent = new Intent(MainActivity.this, UserDashBoard.class);
                intent.putExtra("token",token);
            } else {
                intent = new Intent(MainActivity.this, DashBoard.class);
                intent.putExtra("token",token);
            }
            startActivity(intent);
            finish(); // Close MainActivity
        } else {
            // No token found, show login page
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);
        }
        buttonSignUp = findViewById(R.id.button4);
        TextView login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Animation
        });

        buttonSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpAs.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Animation
        });



        ImageView circleImageView = findViewById(R.id.circle);

        // Load the rotate animation
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.mainpage);

        // Start the animation
        circleImageView.startAnimation(rotateAnimation);
    }
}