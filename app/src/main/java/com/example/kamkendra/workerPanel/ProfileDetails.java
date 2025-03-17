package com.example.kamkendra.workerPanel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kamkendra.R;

public class ProfileDetails extends AppCompatActivity {

    TextView name, email, phone, state, city, locality, roles, workPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize TextViews
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        roles = findViewById(R.id.roles);
        workPlaces = findViewById(R.id.workPlaces);
        locality = findViewById(R.id.locality);
        // Get Intent Data
        Intent pi = getIntent();
        String nameText = pi.getStringExtra("name");
        String emailText = pi.getStringExtra("email");
        String phoneText = pi.getStringExtra("phone");
        String rolesText = pi.getStringExtra("role");
        String workPlacesText = pi.getStringExtra("workPlaces");
        String stateText = pi.getStringExtra("state");
        String cityText = pi.getStringExtra("city");
        String localityText = pi.getStringExtra("locality");
        // Set Text to TextViews
        name.setText(nameText != null ? nameText : "N/A");
        email.setText(emailText != null ? emailText : "N/A");
        phone.setText(phoneText != null ? phoneText : "N/A");
        roles.setText(rolesText != null ? rolesText : "N/A");
        workPlaces.setText(workPlacesText != null ? workPlacesText : "N/A");
        state.setText(stateText != null ? stateText : "N/A");
        city.setText(cityText != null ? cityText : "N/A");
        locality.setText(localityText != null ? localityText : "N/A");
    }
}
