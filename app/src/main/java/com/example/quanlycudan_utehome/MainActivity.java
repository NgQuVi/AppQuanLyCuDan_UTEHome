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
    private int currentApartmentId = -1;
    private java.util.List<Apartment> userApartments = new java.util.ArrayList<>();

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
            if (currentApartmentId != -1) {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.apartment.ApartmentInfoActivity.class);
                intent.putExtra("APARTMENT_ID", currentApartmentId);
                startActivity(intent);
            } else {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.apartment.ApartmentInfoActivity.class);
                startActivity(intent);
            }
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

        findViewById(R.id.layoutAccessCard).setOnClickListener(v -> {
            android.widget.Toast.makeText(this, "Tính năng đang tải...", android.widget.Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.layoutGuestQr).setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.guest.GuestQrActivity.class));
        });
        findViewById(R.id.layoutVehicle).setOnClickListener(v -> {
            android.widget.Toast.makeText(this, "Tính năng đang tải...", android.widget.Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.layoutFeedback).setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.feedback.FeedbackActivity.class));
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

            java.util.List<Integer> apartmentIds = AppDatabase.getInstance(this).apartmentMemberDao().getApartmentIdsByResidentId(residentId);
            java.util.List<Apartment> apartments = new java.util.ArrayList<>();
            if (apartmentIds != null) {
                for (int id : apartmentIds) {
                    Apartment ap = AppDatabase.getInstance(this).apartmentDao().getApartmentById(id);
                    if (ap != null) apartments.add(ap);
                }
            }

            if (user != null) {
                runOnUiThread(() -> {
                    TextView tvUserName = findViewById(R.id.tvUserName);
                    if (tvUserName != null) {
                        tvUserName.setText(user.fullName);
                    }
                    
                    userApartments.clear();
                    userApartments.addAll(apartments);
                    
                    if (!userApartments.isEmpty()) {
                        setupApartmentDropdown(userApartments.get(0));
                    } else {
                        TextView tvApartmentName = findViewById(R.id.tvApartmentName);
                        if (tvApartmentName != null) {
                            tvApartmentName.setText("No Apartment");
                        }
                    }
                });
            }
        });
    }

    private void setupApartmentDropdown(Apartment selectedApartment) {
        currentApartmentId = selectedApartment.id;
        TextView tvApartmentName = findViewById(R.id.tvApartmentName);
        if (tvApartmentName != null) {
            tvApartmentName.setText(selectedApartment.apartmentCode + ", Tòa " + selectedApartment.buildingCode);
        }

        android.widget.ImageView ivDropdownArrow = findViewById(R.id.ivDropdownArrow);
        if (ivDropdownArrow != null) {
            ivDropdownArrow.setVisibility(android.view.View.VISIBLE);
            android.view.View layoutApartmentName = findViewById(R.id.layoutApartmentName);
            if (layoutApartmentName != null) {
                // Always set clickable, even if only 1 apartment, so user can see it works
                layoutApartmentName.setOnClickListener(v -> {
                    android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, layoutApartmentName);
                    for (int i = 0; i < userApartments.size(); i++) {
                        Apartment ap = userApartments.get(i);
                        popupMenu.getMenu().add(0, i, i, ap.apartmentCode + ", Tòa " + ap.buildingCode);
                    }
                    popupMenu.setOnMenuItemClickListener(item -> {
                        Apartment newlySelected = userApartments.get(item.getItemId());
                        setupApartmentDropdown(newlySelected);
                        return true;
                    });
                    popupMenu.show();
                });
            }
        }
    }


}

