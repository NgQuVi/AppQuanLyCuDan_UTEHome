package com.example.quanlycudan_utehome;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.Resident;
import com.example.quanlycudan_utehome.data.repository.ApartmentRepository;
import com.example.quanlycudan_utehome.feature.apartment.ApartmentInfoActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize sample data if empty
        com.example.quanlycudan_utehome.data.database.DatabaseInitializer.initializeSampleData(this);

        findViewById(R.id.cardApartment).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.apartment.ApartmentInfoActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardFinancial).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.invoice.InvoiceActivity.class);
            startActivity(intent);
        });

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.profile.ProfileActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_utilities) {
                startActivity(new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.facility.FacilityListActivity.class));
                return true;
            }
            // Add other navigation logic here if needed
            return true;
        });

        loadUserData();
    }

    private void loadUserData() {
        int residentId = com.example.quanlycudan_utehome.data.local.SessionManager.getInstance(this).getResidentId();
        if (residentId == -1) {
            // No valid session, redirect to login
            startActivity(new android.content.Intent(this, com.example.quanlycudan_utehome.feature.auth.login.LoginActivity.class));
            finish();
            return;
        }

        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            Resident user = AppDatabase.getInstance(this).residentDao().getResidentById(residentId);

            int apartmentId = AppDatabase.getInstance(this).apartmentMemberDao().getApartmentIdByResidentId(residentId);
            Apartment apartment = AppDatabase.getInstance(this).apartmentDao().getApartmentById(apartmentId);
            if (user != null) {
                runOnUiThread(() -> {
                    TextView tvUserName = findViewById(R.id.tvUserName);
                    if (tvUserName != null) {
                        tvUserName.setText(user.fullName);
                    }
                    TextView tvApartmentName = findViewById(R.id.tvApartmentName);
                    if (tvApartmentName != null) {
                        tvApartmentName.setText(apartment != null ? apartment.apartmentCode + ", Tòa " + apartment.buildingCode : "No Apartment");
                    }



                });
            }
        });
    }


}

