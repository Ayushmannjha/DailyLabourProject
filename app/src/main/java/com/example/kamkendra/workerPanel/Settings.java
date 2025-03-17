package com.example.kamkendra.workerPanel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kamkendra.MainActivity;
import com.example.kamkendra.R;

public class Settings extends AppCompatActivity {

    TextView logout,profileDetails;
    String token;
    View pd,accountSetting,privacy;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        logout = findViewById(R.id.logout);
        profileDetails = findViewById(R.id.profileDetails);
        pd = findViewById(R.id.view6);
        accountSetting = findViewById(R.id.view5);
        Intent pi = getIntent();
        name = pi.getStringExtra("name");
        email = pi.getStringExtra("email");
        phone = pi.getStringExtra("phone");
        role = pi.getStringExtra("role");
        workPlaces = pi.getStringExtra("workPlaces");
        state = pi.getStringExtra("state");
        city = pi.getStringExtra("city");
        locality = pi.getStringExtra("locality");

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("authToken", null);


        privacy = findViewById(R.id.view4);
        pd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Settings.this, ProfileDetails.class);
                i.putExtra("name",name);
                i.putExtra("email",email);
                i.putExtra("phone",phone);
                i.putExtra("state",state);
                i.putExtra("city",city);
                i.putExtra("locality",locality);
                i.putExtra("workPlaces",workPlaces);
                i.putExtra("role",role);
                startActivity(i);
            }
        });
        accountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Settings.this,AccountSetting.class);
                i.putExtra("name",name);
                i.putExtra("email",email);
                i.putExtra("phone",phone);
                i.putExtra("state",state);
                i.putExtra("city",city);
                i.putExtra("locality",locality);
                i.putExtra("workPlaces",workPlaces);
                i.putExtra("role",role);
                i.putExtra("token", token);

                startActivity(i);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Settings.this,Privacy.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LogoutDebug", "Logout button clicked"); // Debug Log

                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("authToken");  // Remove the token
                editor.apply();

                // Redirect to MainActivity (Login Page)
                Intent intent = new Intent(Settings.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                startActivity(intent);
                finish();
            }
        });
    }
}