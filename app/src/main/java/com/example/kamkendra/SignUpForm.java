package com.example.kamkendra;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.kamkendra.frontAPI.AuthHandler;
import com.example.kamkendra.services.ApiService;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SignUpForm extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPhone, state, city , localAddress;

    private TextView buttonNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.step1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editTextName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email);
        editTextPhone = findViewById(R.id.phone);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        localAddress = findViewById(R.id.localAddress);
        Map<String, Object> registrationData = new HashMap<>();
        buttonNext = findViewById(R.id.nextButton);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    registrationData.put("name",editTextName.getText().toString());
                    registrationData.put("email",editTextEmail.getText().toString());
                    registrationData.put("phone",editTextPhone.getText().toString());
                    registrationData.put("state",state.getText().toString());
                    registrationData.put("city",city.getText().toString());
                    registrationData.put("locality",localAddress.getText().toString());

                    ApiService apiService = ApiService.getInstance();
                    Retrofit retrofit = apiService.getRetrofit();
                    AuthHandler authHandler = retrofit.create(AuthHandler.class);

                    authHandler.checkExistingUser(editTextPhone.getText().toString()).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String responseString = response.body().string().trim(); // Convert ResponseBody to String
                                boolean flag = Boolean.parseBoolean(responseString);
                                Log.d("SignUpForm", "Response: " + responseString); // Log actual response

                                if (flag) {
                                    Intent i = new Intent(SignUpForm.this, SignUpPart2.class);
                                    i.putExtra("registrationData", (Serializable) registrationData);
                                    startActivity(i);
                                } else {
                                    editTextPhone.setError("User already exists");
                                    Toast.makeText(SignUpForm.this, "User already exists", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(SignUpForm.this,"Error occured",Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });

    }

    private boolean validateInputs() {
        // Validate Name
        if (TextUtils.isEmpty(editTextName.getText().toString().trim())) {
            editTextName.setError("Name is required");
            return false;
        }

        // Validate Email
        if (TextUtils.isEmpty(editTextEmail.getText().toString().trim())) {
            editTextEmail.setError("Email is required");
            return false;
        }

        // Validate Phone
        if (TextUtils.isEmpty(editTextPhone.getText().toString().trim())) {
            editTextPhone.setError("Phone number is required");
            return false;
        }

        if (TextUtils.isEmpty(state.getText().toString().trim())) {
            editTextPhone.setError("state name is required");
            return false;
        }
        if (TextUtils.isEmpty(city.getText().toString().trim())) {
            editTextPhone.setError("city name is required");
            return false;
        }
        if (TextUtils.isEmpty(localAddress.getText().toString().trim())) {
            editTextPhone.setError("local address is required");
            return false;
        }
        // Validate Gender


        // All validations passed
        return true;
    }
}