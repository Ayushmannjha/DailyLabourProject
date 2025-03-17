package com.example.kamkendra.workerPanel;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Privacy extends AppCompatActivity {

    TextView edit, saveChanges;
    CheckBox showPassword;
    EditText password;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_privacy);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edit = findViewById(R.id.edit);
        saveChanges = findViewById(R.id.saveChanges);
        showPassword = findViewById(R.id.checkBox);
        password = findViewById(R.id.editTextText);

        Intent i = getIntent();
        token = i.getStringExtra("token");

        JWT jwt = new JWT(token);
        int id = jwt.getClaim("id").asInt();

        // Toggle password visibility with checkbox
        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            password.setSelection(password.length()); // Move cursor to the end
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setFocusable(true);
                password.setFocusableInTouchMode(true);
                password.setClickable(true);
                password.setVisibility(View.VISIBLE);
                Toast.makeText(Privacy.this, "  Now you can edit your password", Toast.LENGTH_SHORT).show();
            }
        });

        saveChanges.setOnClickListener(v -> {
            String newPassword = password.getText().toString().trim();
            if (!newPassword.isEmpty()) {
                update(id, newPassword);
            } else {
                Toast.makeText(Privacy.this, "Password field cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void update(int workerId, String password) {
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        WorkerPanel workerPanel = retrofit.create(WorkerPanel.class);

        workerPanel.updatePassword("Bearer " + token, workerId, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Privacy.this, "Your profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Privacy.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Privacy.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}
