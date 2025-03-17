package com.example.kamkendra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.example.kamkendra.frontAPI.AuthHandler;
import com.example.kamkendra.services.ApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SignUpPart4 extends AppCompatActivity {

    // Declare UI elements and variables
    private AutoCompleteTextView editTextWorkplace; // Text field for workplace selection
    private EditText editTextPassword; // Text field for entering password
    private Button buttonAdd, buttonNext; // Buttons to add workplace and proceed to next step
    private RecyclerView recyclerViewSelectedWorkplaces; // RecyclerView for displaying selected workplaces
    private ArrayAdapter<String> autoCompleteAdapter; // Adapter for workplace AutoCompleteTextView
    private List<String> workplaceList; // List of available workplaces
    private List<String> selectedWorkplaces; // List of workplaces selected by the user
    private SelectedWorkplacesAdapter selectedAdapter; // Adapter for RecyclerView
    private Map<String, Object> registrationData; // Map to store registration data across screens

    private static final String TAG = "Registration"; // Tag for logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_part4);

        // Initialize UI elements by linking with XML layout IDs
        editTextWorkplace = findViewById(R.id.editTextWorkplace);
        editTextPassword = findViewById(R.id.editTextTextPassword); // Password input
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonNext = findViewById(R.id.buttonNext);
        recyclerViewSelectedWorkplaces = findViewById(R.id.recyclerViewSelectedWorkplaces);

        // Sample list of workplace options for the AutoCompleteTextView
        workplaceList = new ArrayList<>(Arrays.asList("Workplace 1", "Workplace 2", "Workplace 3", "Workplace 4", "Workplace 5"));
        selectedWorkplaces = new ArrayList<>(); // Initialize the selected workplaces list

        // Retrieve registration data from previous activity (passed as a Map)
        registrationData = (Map<String, Object>) getIntent().getSerializableExtra("registrationData");

        // Set up the AutoCompleteTextView with workplace options
        autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, workplaceList);
        editTextWorkplace.setAdapter(autoCompleteAdapter);

        // Initially hide the password field until enough workplaces are selected
        editTextPassword.setVisibility(View.GONE);

        // Add workplace to the selected list when the Add button is clicked
        buttonAdd.setOnClickListener(v -> {
            String enteredWorkplace = editTextWorkplace.getText().toString().trim();
            if (!enteredWorkplace.isEmpty() && !selectedWorkplaces.contains(enteredWorkplace)) {
                selectedWorkplaces.add(enteredWorkplace); // Add to selected list
                selectedAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                updateNextButtonState(); // Enable or disable Next button based on conditions
                editTextWorkplace.setText(""); // Clear the input field
            }
        });

        // Setup RecyclerView for displaying selected workplaces
        selectedAdapter = new SelectedWorkplacesAdapter(selectedWorkplaces);
        recyclerViewSelectedWorkplaces.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSelectedWorkplaces.setAdapter(selectedAdapter);

        // Handle Next button click to proceed with registration
        buttonNext.setOnClickListener(v -> {
            // Check if the minimum requirements are met (at least 3 workplaces and password)
            if (selectedWorkplaces.size() >= 3 && editTextPassword.getText().toString().trim().length() > 0) {
                // Store selected workplaces and password in registration data
                registrationData.put("workPlaces", new ArrayList<>(selectedWorkplaces).toString());
                registrationData.put("password", editTextPassword.getText().toString().trim());

                // Make API call to register worker and handle token processing
                registerWorkerAndDecodeToken(registrationData);
            } else {
                // Show a message if conditions are not met
                Toast.makeText(SignUpPart4.this, "Please select at least three workplaces and enter a password.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to register worker using API and decode the returned token
    private void registerWorkerAndDecodeToken(Map<String, Object> requestBody) {
        // Get API service instance and create an AuthHandler instance
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        AuthHandler authHandler = retrofit.create(AuthHandler.class);

        // Make an asynchronous network call using Retrofit
        authHandler.registerWorker(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Retrieve and log the token from the response
                        String token = response.body().string();
                        if (token.equals("Worker already exists")) {
                            // Redirect to the first page (replace FirstActivity.class with your first activity)
                            Intent intent = new Intent(SignUpPart4.this, SignUpForm.class);
                            intent.putExtra("token",token);
                            intent.putExtra("errorFlag", true);
                            startActivity(intent);
                            return;
                        }
                        Log.d(TAG, "Login successful, Token: " + token);
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("authToken", token);
                        editor.apply();
                        // Display success message
                        Toast.makeText(SignUpPart4.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Decode the JWT token to retrieve user details
                        JWT jwt = new JWT(token);
                        String userRole = jwt.getClaim("role").asString();
                        String userEmail = jwt.getClaim("email").asString();

                        // Start Dashboard activity with extracted role and email
                        Intent intent = new Intent(SignUpPart4.this, DashBoard.class);
                        intent.putExtra("role", userRole);
                        intent.putExtra("email", userEmail);
                        startActivity(intent);

                    } catch (Exception e) {
                        // Log and display any token reading error
                        Log.e(TAG, "Error reading token: " + e.getMessage(), e);
                    }
                } else {
                    // Log failure and show an error message if registration failed
                    Log.e(TAG, "Registration failed: " + response.message());
                    Toast.makeText(SignUpPart4.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log and display a message if there was a network error
                Log.e(TAG, "Error: " + t.getMessage(), t);
                Toast.makeText(SignUpPart4.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update state of the Next button and visibility of password field
    private void updateNextButtonState() {
        boolean isReadyForPassword = selectedWorkplaces.size() >= 3; // Require at least 3 workplaces
        buttonNext.setEnabled(isReadyForPassword); // Enable or disable Next button
        editTextPassword.setVisibility(isReadyForPassword ? View.VISIBLE : View.GONE); // Show or hide password field
    }

    // Adapter for RecyclerView to display selected workplaces
    private static class SelectedWorkplacesAdapter extends RecyclerView.Adapter<SelectedWorkplacesAdapter.ViewHolder> {
        private final List<String> selectedWorkplaces; // List of selected workplaces

        SelectedWorkplacesAdapter(List<String> selectedWorkplaces) {
            this.selectedWorkplaces = selectedWorkplaces;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate a simple list item layout for each workplace
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Bind each selected workplace to a view holder
            holder.bind(selectedWorkplaces.get(position));
        }

        @Override
        public int getItemCount() {
            return selectedWorkplaces.size(); // Return total count of selected workplaces
        }

        // ViewHolder class for managing individual list items in RecyclerView
        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textView; // Text view for displaying workplace name

            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1); // Link text view to layout
            }

            void bind(String workplace) {
                textView.setText(workplace); // Set workplace name on text view
            }
        }
    }
}
