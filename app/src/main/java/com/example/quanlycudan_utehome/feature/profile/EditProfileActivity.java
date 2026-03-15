package com.example.quanlycudan_utehome.feature.profile;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Resident;
import java.util.concurrent.Executors;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etEmail, etIdNum;
    private TextView etPhone, tvGenderSpinner, tvDobPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Bind Views
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        tvGenderSpinner = findViewById(R.id.tvGenderSpinner);
        tvDobPicker = findViewById(R.id.tvDobPicker);
        etIdNum = findViewById(R.id.etIdNum);

        // Header Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Save Button
        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> saveProfileChanges());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfileForEditing();
    }

    private void loadUserProfileForEditing() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Resident user = db.residentDao().getResidentById(1);

            if (user != null) {
                runOnUiThread(() -> {
                    etPhone.setText(user.phone != null ? user.phone : "");
                    etEmail.setText(user.email != null ? user.email : "");
                    tvGenderSpinner.setText(user.gender != null ? user.gender : "");
                    tvDobPicker.setText(user.dob != null ? user.dob : "");
                    etIdNum.setText(user.idNum != null ? user.idNum : "");
                });
            }
        });
    }

    private void saveProfileChanges() {
        String newEmail = etEmail.getText().toString().trim();
        String newGender = tvGenderSpinner.getText().toString().trim();
        String newDob = tvDobPicker.getText().toString().trim();
        String newIdNum = etIdNum.getText().toString().trim();

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Resident user = db.residentDao().getResidentById(1);

            if (user != null) {
                user.email = newEmail;
                user.gender = newGender;
                user.dob = newDob;
                user.idNum = newIdNum;
                // Phone is locked, so we don't update it from input
                
                db.residentDao().update(user);
                
                runOnUiThread(() -> {
                    Toast.makeText(EditProfileActivity.this, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
}
