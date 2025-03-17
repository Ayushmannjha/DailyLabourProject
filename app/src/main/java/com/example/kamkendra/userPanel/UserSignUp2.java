package com.example.kamkendra.userPanel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auth0.android.jwt.JWT;
import com.example.kamkendra.R;
import com.example.kamkendra.UserDashBoard;
import com.example.kamkendra.frontAPI.AuthHandler;
import com.example.kamkendra.services.ApiService;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserSignUp2 extends AppCompatActivity {

    EditText state, city, pincode, password;
    TextView finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_sign_up2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        state = findViewById(R.id.user_state);
        city = findViewById(R.id.user_city);
        pincode = findViewById(R.id.pincode);
        password = findViewById(R.id.user_password);
        finish = findViewById(R.id.finish);

        // Get the data from the previous activity
        Intent i = getIntent();
        Map<String, Object> userRegistrationData = (Map<String, Object>) i.getSerializableExtra("userRegistrationData");
        Log.d("UserSignUp2", "Received data from UserSignUp1: " + userRegistrationData);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add the additional user input to the map
                userRegistrationData.put("state", state.getText().toString());
                userRegistrationData.put("city", city.getText().toString());
                userRegistrationData.put("pincode", pincode.getText().toString());
                userRegistrationData.put("password", password.getText().toString());

                Log.d("UserSignUp2", "Final user data before registration: " + userRegistrationData);

                // Call the register method
                registerWorkerAndDecodeToken(userRegistrationData);
            }
        });
    }

    private void registerWorkerAndDecodeToken(Map<String, Object> requestBody) {
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        AuthHandler authHandler = retrofit.create(AuthHandler.class);

        // Log the request payload
        Log.d("UserSignUp2", "Sending registration request: " + requestBody);

        authHandler.registerUser(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Retrieve and log the token from the response
                        String token = response.body().string();
                        Log.d("UserSignUp2", "Received token: " + token);

                        if (token.equals("Worker already exists")) {
                            Intent intent = new Intent(UserSignUp2.this, UserDashBoard.class);
                            intent.putExtra("errorFlag", true);
                            startActivity(intent);
                            return;
                        }

                        Toast.makeText(UserSignUp2.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        JWT jwt = new JWT(token);
                        String userRole = jwt.getClaim("role").asString();
                        String userEmail = jwt.getClaim("email").asString();

                        Log.d("UserSignUp2", "Decoded JWT - Role: " + userRole + ", Email: " + userEmail);

                        Intent intent = new Intent(UserSignUp2.this, UserDashBoard.class);
                        intent.putExtra("role", userRole);
                        intent.putExtra("email", userEmail);
                        startActivity(intent);

                    } catch (Exception e) {
                        Log.e("UserSignUp2", "Error reading token: " + e.getMessage(), e);
                    }
                } else {
                    Log.e("UserSignUp2", "Registration failed: " + response.message());
                    Toast.makeText(UserSignUp2.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("UserSignUp2", "Network Error: " + t.getMessage(), t);
                Toast.makeText(UserSignUp2.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
