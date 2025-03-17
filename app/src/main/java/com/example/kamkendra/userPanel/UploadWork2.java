package com.example.kamkendra.userPanel;

import android.content.Intent;
import android.graphics.Color;
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
import com.example.kamkendra.frontAPI.UserPanel;
import com.example.kamkendra.services.ApiService;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UploadWork2 extends AppCompatActivity {

    private static final String TAG = "UploadWork2"; // Debug tag
    private String token;
    private Map<String, Object> request;
    private Map<String, Integer> labourDetails = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_work2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        TextView rupeeIcon = findViewById(R.id.rupeeicon);
        TextView done = findViewById(R.id.done);
        TextView otherText = findViewById(R.id.other);
        EditText otherType = findViewById(R.id.otherType);
        EditText budget = findViewById(R.id.budget);
        EditText otherQuantity = findViewById(R.id.otherQuantity);
        TextView finish = findViewById(R.id.finishUpload);
        TextView setOtherType = findViewById(R.id.otherTypeOk);

        TextView[] labourTextViews = {
                findViewById(R.id.plumberText),
                findViewById(R.id.electricianText),
                findViewById(R.id.DailyLabourText),
                findViewById(R.id.cookText)
        };

        EditText[] quantityEditTexts = {
                findViewById(R.id.plumberQuantity),
                findViewById(R.id.electricianQuantity),
                findViewById(R.id.DailyLabourQuantity),
                findViewById(R.id.cookQuantity)
        };

        // Retrieve intent data
        Intent i = getIntent();
        token = i.getStringExtra("token");
        request = (Map<String, Object>) i.getSerializableExtra("request");

        // Selection logic for labours
        View.OnClickListener handleSelection = view -> {
            TextView textView = (TextView) view;
            EditText editText = null;

            for (int idx = 0; idx < labourTextViews.length; idx++) {
                if (textView == labourTextViews[idx]) {
                    editText = quantityEditTexts[idx];
                    break;
                }
            }

            if (textView == otherText) {
                otherType.setVisibility(View.VISIBLE);
                setOtherType.setVisibility(View.VISIBLE);
                textView.setTextColor(Color.parseColor("#FF9933"));
                textView.setTag("selected");
            } else if (editText != null) {
                if ("selected".equals(textView.getTag())) {
                    textView.setBackgroundColor(Color.TRANSPARENT);
                    editText.setVisibility(View.GONE);
                    textView.setTag("unselected");
                } else {
                    textView.setBackgroundColor(Color.parseColor("#FF9933"));
                    editText.setVisibility(View.VISIBLE);
                    textView.setTag("selected");
                }
            }
        };

        for (TextView textView : labourTextViews) {
            textView.setTag("unselected");
            textView.setOnClickListener(handleSelection);
        }
        otherText.setTag("unselected");
        otherText.setOnClickListener(handleSelection);

        // Handle other type input visibility
        setOtherType.setOnClickListener(v -> otherQuantity.setVisibility(View.VISIBLE));

        // Finalize upload process
        finish.setOnClickListener(v -> {
            labourDetails.clear();
            for (int idx = 0; idx < labourTextViews.length; idx++) {
                validateAndAdd(labourTextViews[idx], quantityEditTexts[idx], labourTextViews[idx].getText().toString().toLowerCase(), labourDetails);
            }
            validateAndAdd(otherText, otherQuantity, otherType.getText().toString(), labourDetails);

            if (!labourDetails.isEmpty()) {
                request.put("labourDetails", labourDetails);
                budget.setVisibility(View.VISIBLE);
                done.setVisibility(View.VISIBLE);
                rupeeIcon.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(UploadWork2.this, "Select at least one labour type and quantity", Toast.LENGTH_SHORT).show();
            }
        });

        done.setOnClickListener(v -> {
            String budgetStr = budget.getText().toString().trim();
            if (!budgetStr.isEmpty()) {
                try {
                    double budgetValue = Double.parseDouble(budgetStr);
                    request.put("budget", budgetValue);
                    uploadWork(request);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid budget format", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter your budget", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateAndAdd(TextView textView, EditText editText, String key, Map<String, Integer> details) {
        if ("selected".equals(textView.getTag())) {
            String qtyStr = editText.getText().toString();
            if (!qtyStr.isEmpty()) {
                details.put(key, Integer.parseInt(qtyStr));
            }
        }
    }

    private void uploadWork(Map<String, Object> request) {
        ApiService apiService = ApiService.getInstance();
        Retrofit retrofit = apiService.getRetrofit();
        UserPanel userPanel = retrofit.create(UserPanel.class);
        JWT jwt = new JWT(token);
        int id = jwt.getClaim("id").asInt();

        Call<ResponseBody> call = userPanel.uploadWork("Bearer " + token, request, id);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(UploadWork2.this, response.isSuccessful() ? "Work uploaded successfully" : "Error occurred", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UploadWork2.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
