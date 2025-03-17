package com.example.kamkendra;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.Serializable;
import java.util.Map;

public class SignUpPart3 extends AppCompatActivity {
    private EditText editTextState, editTextCity, editTextLocality, editTextPincode;
    private TextView buttonNextPart3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_part3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.step3), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editTextState = findViewById(R.id.state);
        editTextCity = findViewById(R.id.city);
        editTextLocality = findViewById(R.id.locality);
        editTextPincode = findViewById(R.id.pincode);
        buttonNextPart3 = findViewById(R.id.next);
        Map<String, String> registrationData = (Map<String, String>) getIntent().getSerializableExtra("registrationData");

        buttonNextPart3.setOnClickListener(v -> {
            if (validateInputs()) {
                // Save data and navigate to Part 4
                registrationData.put("state",editTextState.getText().toString());
                registrationData.put("city",editTextCity.getText().toString());
                registrationData.put("locality",editTextLocality.getText().toString());
                registrationData.put("pincode",editTextPincode.getText().toString());
                Intent intent = new Intent(SignUpPart3.this, SignUpPart4.class);
                intent.putExtra("registrationData", (Serializable) registrationData);
                startActivity(intent);
            }
        });
    }


    private boolean validateInputs() {
        // Validate state
        if (TextUtils.isEmpty(editTextState.getText().toString().trim())) {
            editTextState.setError("State is required");
            return false;
        }

        // Validate city
        if (TextUtils.isEmpty(editTextCity.getText().toString().trim())) {
            editTextCity.setError("City is required");
            return false;
        }

        // Validate locality
        if (TextUtils.isEmpty(editTextLocality.getText().toString().trim())) {
            editTextLocality.setError("Locality is required");
            return false;
        }

        // Validate Gender
        if (TextUtils.isEmpty(editTextPincode.getText().toString().trim())) {
            editTextPincode.setError("Pincode is required");
            return false;
        }

        // All validations passed
        return true;
    }
}
