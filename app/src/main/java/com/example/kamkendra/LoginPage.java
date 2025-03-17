package com.example.kamkendra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auth0.android.jwt.JWT;
import com.example.kamkendra.dto.LoginRequest;
import com.example.kamkendra.frontAPI.AuthHandler;
import com.example.kamkendra.services.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginPage extends AppCompatActivity {

    private String text = "Bridging Workers with Opportunities, Anytime, Anywhere!";
    private TextView tagLine ;
    private int index = 0;
    private long delay = 150;
    private Handler handler = new Handler();
    private void animateText() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index <= text.length()) {
                    tagLine.setText(text.substring(0, index++));
                    handler.postDelayed(this, delay); // Continue animation
                }
            }
        }, delay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try {
            initializeComponent();
        }catch (Exception e){
            e.printStackTrace();
        }
        tagLine = findViewById(R.id.tagLine);
        animateText();

    }

    private static final String TAG = "LoginPage";

    private void initializeComponent() {
        Log.d(TAG, "Initializing components");

        TextInputEditText email = findViewById(R.id.email);
        TextInputEditText password = findViewById(R.id.password);
        TextView submit = findViewById(R.id.button);

        // Initialize API service and handler
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        AuthHandler authHandler = retrofit.create(AuthHandler.class);


        submit.setOnClickListener(v -> {
            Log.d(TAG, "Submit button clicked");

            // Collect email and password input
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();

            Log.d(TAG, "Email: " + emailInput);
            Log.d(TAG, "Password: " + passwordInput);

            LoginRequest request = new LoginRequest();
            request.setEmail(emailInput);
            request.setPassword(passwordInput);

            authHandler.login(request).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            // Directly access the token string from the response
                            String token = response.body().string();
                            Log.d(TAG, "Login successful, Token: " + token);

                            if(token.equals("{\"success\":false,\"message\":\"Invalid Username or password\"}")){
                                Toast.makeText(LoginPage.this, "Login failed: " + "Invalid username or password", Toast.LENGTH_SHORT).show();
                            }
                           // Toast.makeText(LoginPage.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            JWT jwt = new JWT(token);

                            // Retrieve claims from the JWT token
                            String userRole = jwt.getClaim("role").asString(); // Replace "role" with actual claim name
                            String userEmail = jwt.getClaim("email").asString(); // Replace "email" with actual claim name
                            // Extract other claims as needed
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("authToken", token);
                            editor.apply();


                            Log.d(TAG, "Role " + userRole);
                            if(userRole.equals("USER")){
                                Intent i= new Intent(LoginPage.this, UserDashBoard.class);
                                i.putExtra("token",token);
                                startActivity(i);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }

                            else{
                                Intent i = new Intent(LoginPage.this, DashBoard.class);
                                i.putExtra("token",token);
                                startActivity(i);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                            Log.d(TAG, "Decoded JWT Role: " + userRole);
                            Log.d(TAG, "Decoded JWT Email: " + userEmail);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading token: " + e.getMessage(), e);
                        }
                    } else {
                        Log.e(TAG, "Login failed: " + response.message());
                        Toast.makeText(LoginPage.this, "Login failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "Error: " + t.getMessage(), t);
                    Toast.makeText(LoginPage.this, "Network Error", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}