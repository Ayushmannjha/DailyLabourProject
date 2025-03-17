package com.example.kamkendra.workerPanel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.example.kamkendra.frontAPI.WorkerPanel;
import com.example.kamkendra.services.ApiService;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AccountSetting extends AppCompatActivity {
    EditText name, email, phone, state, city, locality, roles, workPlaces;
    TextView editName, editState, editCity, editLocality, editRoles, editWorkPlaces;
    TextView saveName, saveState, saveCity, saveLocality, saveRoles, saveWorkPlaces, saveChanges;
    String token;
    Map<String, Object> request = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setInitialValues();
        setupEditAndSaveListeners();
    }

    private void initializeViews() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        roles = findViewById(R.id.roles);
        workPlaces = findViewById(R.id.workPlaces);
        locality = findViewById(R.id.locality);

        editName = findViewById(R.id.editName);
        editCity = findViewById(R.id.editCity);
        editState = findViewById(R.id.editState);
        editRoles = findViewById(R.id.editRoles);
        editWorkPlaces = findViewById(R.id.editWorkplaces);
        editLocality = findViewById(R.id.editLocality);

        saveName = findViewById(R.id.saveName);
        saveCity = findViewById(R.id.saveCity);
        saveState = findViewById(R.id.saveState);
        saveRoles = findViewById(R.id.saveRoles);
        saveWorkPlaces = findViewById(R.id.saveWorkplaces);
        saveLocality = findViewById(R.id.saveLocality);
        saveChanges = findViewById(R.id.saveChanges);
    }

    private void setInitialValues() {
        Intent i = getIntent();
        token = i.getStringExtra("token");
        name.setText(i.getStringExtra("name"));
        state.setText(i.getStringExtra("state"));
        city.setText(i.getStringExtra("city"));
        locality.setText(i.getStringExtra("locality"));
        roles.setText(i.getStringExtra("role"));
        workPlaces.setText(i.getStringExtra("workPlaces"));
    }

    private void setupEditAndSaveListeners() {
        setupEditSavePair(editName, saveName, name, "name");
        setupEditSavePair(editState, saveState, state, "state");
        setupEditSavePair(editCity, saveCity, city, "city");
        setupEditSavePair(editLocality, saveLocality, locality, "locality");
        setupEditSavePair(editRoles, saveRoles, roles, "roles");
        setupEditSavePair(editWorkPlaces, saveWorkPlaces, workPlaces, "workPlaces");

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!request.isEmpty()){
                    profileUpdate();
                }else {
                    Toast.makeText(AccountSetting.this, "No change detected", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void setupEditSavePair(TextView editButton, TextView saveButton, EditText editText, String key) {
        editButton.setOnClickListener(v -> {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setClickable(true);
            saveButton.setVisibility(View.VISIBLE);
            editText.requestFocus();

            // Show keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }

            Toast.makeText(AccountSetting.this, "Now you can change your " + key, Toast.LENGTH_SHORT).show();
        });

        saveButton.setOnClickListener(v -> {
            request.put(key, editText.getText().toString());
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.setClickable(false);
            hideKeyboard(editText);
            saveButton.setVisibility(View.GONE);
        });
    }


    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public void profileUpdate() {
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        WorkerPanel workerPanel = retrofit.create(WorkerPanel.class);
        JWT jwt = new JWT(token);
        int id = jwt.getClaim("id").asInt();

        workerPanel.profileUpdate("Bearer " + token, request, id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String newToken = response.body().string().trim();

                        if (!newToken.isEmpty()) { // Ensure the new token is valid
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("authToken", newToken);
                            editor.apply();

                            Toast.makeText(AccountSetting.this, "Profile updated & token refreshed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AccountSetting.this, "Profile updated but token missing", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(AccountSetting.this, "Failed to process response", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(AccountSetting.this, "Profile update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AccountSetting.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

    }
}
