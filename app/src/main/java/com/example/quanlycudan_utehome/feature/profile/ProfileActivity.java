package com.example.quanlycudan_utehome.feature.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Resident;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvResidentName, tvUserNameDisplay, tvResidentCode, tvResidentDob;
    private TextView tvResidentPhone, tvResidentGender, tvResidentIdType, tvResidentIdNum, tvResidentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Bind Views
        tvResidentName = findViewById(R.id.tvResidentName);
        tvUserNameDisplay = findViewById(R.id.tvUserNameDisplay);
        tvResidentCode = findViewById(R.id.tvResidentCode);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            int residentId = com.example.quanlycudan_utehome.data.local.SessionManager.getInstance(this).getResidentId();
            if (residentId == -1) return;
            Resident user = db.residentDao().getResidentById(residentId);

            if (user != null) {
                runOnUiThread(() -> {
                    tvUserNameDisplay.setText(user.fullName);
                    tvResidentName.setText(user.fullName);
                    tvResidentCode.setText(user.residentCode != null ? user.residentCode : "--");
                    tvResidentPhone.setText(user.phone != null ? user.phone : "--");
                    tvResidentEmail.setText(user.email != null ? user.email : "--");
                    tvResidentDob.setText(user.dob != null ? user.dob : "--");
                    tvResidentGender.setText(user.gender != null ? user.gender : "--");
                    tvResidentIdType.setText(user.idType != null ? user.idType : "--");
                    tvResidentIdNum.setText(user.idNum != null ? user.idNum : "--");
                });
            }
        });
    }
}
