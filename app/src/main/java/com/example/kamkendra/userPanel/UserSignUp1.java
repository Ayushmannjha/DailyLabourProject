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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserSignUp1 extends AppCompatActivity {

    EditText name, email, phone, state, city, locality, pincode, password;
    TextView next;
    private static final String TAG = "UserSignUp1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_sign_up1);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        locality = findViewById(R.id.locality);
        pincode = findViewById(R.id.pincode);
        password = findViewById(R.id.password);
        next = findViewById(R.id.next); // Make sure this matches your actual TextView ID in XML

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> userRegistrationData = new HashMap<>();
                try {
                    // Capture the text values from EditText fields
                    String userName = name.getText().toString();
                    String userEmail = email.getText().toString();
                    String userPhone = phone.getText().toString();

                    // Add values to the map
                    userRegistrationData.put("name", userName);
                    userRegistrationData.put("email", userEmail);
                    userRegistrationData.put("phone", userPhone);
                    userRegistrationData.put("state", state.getText().toString());
                    userRegistrationData.put("city", city.getText().toString());
                    userRegistrationData.put("locality", locality.getText().toString());
                    userRegistrationData.put("pincode", pincode.getText().toString());
                    userRegistrationData.put("password", password.getText().toString());
                    // Log the values for debugging
                    Log.d(TAG, "Name: " + userName);
                    Log.d(TAG, "Email: " + userEmail);
                    Log.d(TAG, "Phone: " + userPhone);

                    // Pass the map to the next activity
                    registerWorkerAndDecodeToken(userRegistrationData);


                } catch (Exception e) {
                    Log.e(TAG, "Error in data preparation: ", e);
                    e.printStackTrace();
                }
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

                        if (token.equals("User already exist")) {
                             email.setError("User Already exist");
                        }


                        JWT jwt = new JWT(token);
                        String userRole = jwt.getClaim("role").asString();
                        String userEmail = jwt.getClaim("email").asString();

                        Log.d("UserSignUp2", "Decoded JWT - Role: " + userRole + ", Email: " + userEmail);

                        Intent intent = new Intent(UserSignUp1.this, UserDashBoard.class);
                        intent.putExtra("role", userRole);
                        intent.putExtra("token",token);
                        intent.putExtra("email", userEmail);
                        startActivity(intent);

                    } catch (Exception e) {
                        Log.e("UserSignUp2", "Error reading token: " + e.getMessage(), e);
                    }
                } else {
                    Log.e("UserSignUp2", "Registration failed: " + response.message());
                    Toast.makeText(UserSignUp1.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("UserSignUp2", "Network Error: " + t.getMessage(), t);
                Toast.makeText(UserSignUp1.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
