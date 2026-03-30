package com.example.quanlycudan_utehome.feature.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Resident;
import com.example.quanlycudan_utehome.data.local.SessionManager;
import com.example.quanlycudan_utehome.feature.auth.login.LoginActivity;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvResidentName, tvUserNameDisplay, tvResidentDob;
    private TextView tvResidentPhone, tvResidentGender, tvResidentIdType, tvResidentIdNum, tvResidentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Bind Views
        tvResidentName = findViewById(R.id.tvResidentName);
        tvUserNameDisplay = findViewById(R.id.tvUserNameDisplay);
        tvResidentDob = findViewById(R.id.tvResidentDob);
        tvResidentPhone = findViewById(R.id.tvResidentPhone);
        tvResidentGender = findViewById(R.id.tvResidentGender);
        tvResidentIdType = findViewById(R.id.tvResidentIdType);
        tvResidentIdNum = findViewById(R.id.tvResidentIdNum);
        tvResidentEmail = findViewById(R.id.tvResidentEmail);

        // Header Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Edit Profile Button
        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Facility History Button
        findViewById(R.id.btnFacilityHistory).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, com.example.quanlycudan_utehome.feature.facility.FacilityHistoryActivity.class);
            startActivity(intent);
        });

        // Logout Button
        findViewById(R.id.btnLogout).setOnClickListener(v -> showLogoutConfirmDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            int residentId = SessionManager.getInstance(this).getResidentId();
            if (residentId == -1) return;
            Resident user = db.residentDao().getResidentById(residentId);

            if (user != null) {
                runOnUiThread(() -> {
                    tvUserNameDisplay.setText(user.fullName);
                    tvResidentName.setText(user.fullName);
                    tvResidentPhone.setText(user.phone != null ? user.phone : "--");
                    tvResidentEmail.setText(user.email != null ? user.email : "--");
                    tvResidentDob.setText(user.dob != null ? user.dob : "--");
                    tvResidentGender.setText(user.gender != null ? user.gender : "--");
                    tvResidentIdType.setText("CCCD");
                    tvResidentIdNum.setText(user.idNum != null ? user.idNum : "--");
                });
            }
        });
    }

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        SessionManager.getInstance(this).logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
