package com.example.kamkendra;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpPart2 extends AppCompatActivity {


    private EditText role;
    private Map<String, String> registrationData; // Store registration details

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_part2);


        role = findViewById(R.id.roles);


        Map<String, String> registrationData = (Map<String, String>) getIntent().getSerializableExtra("registrationData");

        TextView next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInputs()){
                    Intent i = new Intent(SignUpPart2.this, SignUpPart4.class);
                    registrationData.put("role",role.getText().toString());
                    i.putExtra("registrationData",(Serializable) registrationData);
                    startActivity(i);
                }
            }
        });

        registrationData.put("role", role.getText().toString());



    }


    private boolean validateInputs() {
        // Validate Name
        if (TextUtils.isEmpty(role.getText().toString().trim())) {
            role.setError("This fields is required");
            return false;
        }
        return true;
    }
    /**
     * Sets up listeners for CheckBoxes to change background color when selected.
     */
}
