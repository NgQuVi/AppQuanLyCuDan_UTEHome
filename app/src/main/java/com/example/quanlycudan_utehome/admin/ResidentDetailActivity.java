package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResidentDetailActivity extends AppCompatActivity {

    private int residentId = -1;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resident_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        residentId = getIntent().getIntExtra("resident_id", -1);
        if (residentId != -1) {
            loadResidentDetails();
        } else {
            Toast.makeText(this, "Khong tim thay du lieu cu dan", Toast.LENGTH_SHORT).show();
            finish();
        }

        findViewById(R.id.btnDelete).setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadResidentDetails() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Resident resident = db.residentDao().getResidentById(residentId);
            Apartment apartment = null;
            if (resident != null) {
                Integer apartmentId = db.apartmentMemberDao().getApartmentIdByResidentId(resident.id);
                if (apartmentId != null) {
                    apartment = db.apartmentDao().getApartmentById(apartmentId);
                } else if (resident.accountId > 0) {
                    apartment = db.apartmentDao().getApartmentByAccountId(resident.accountId);
                }
            }

            Resident finalResident = resident;
            Apartment finalApartment = apartment;

            runOnUiThread(() -> {
                if (finalResident == null) {
                    Toast.makeText(this, "Khong tim thay cu dan", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                ((TextView) findViewById(R.id.tvName)).setText(safeText(finalResident.fullName, "--"));
                ((TextView) findViewById(R.id.tvResidentCode)).setText("RES" + String.format("%04d", finalResident.id));
                ((TextView) findViewById(R.id.tvDob)).setText(safeText(finalResident.dob, "N/A"));
                ((TextView) findViewById(R.id.tvGender)).setText(safeText(finalResident.gender, "N/A"));
                ((TextView) findViewById(R.id.tvIdType)).setText("CCCD");
                ((TextView) findViewById(R.id.tvIdNumber)).setText(safeText(finalResident.idNum, "N/A"));
                ((TextView) findViewById(R.id.tvPhone)).setText(safeText(finalResident.phone, "N/A"));

                TextView tvApartment = findViewById(R.id.tvApartment);
                if (finalApartment != null) {
                    tvApartment.setText(finalApartment.apartmentCode + " - Toa " + finalApartment.buildingCode);
                } else {
                    tvApartment.setText("Chua phan bo can ho");
                }
            });
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Xoa cu dan")
                .setMessage("Ban co chac chan muon xoa cu dan nay khong?")
                .setNegativeButton("Huy", null)
                .setPositiveButton("Xoa", (dialog, which) -> deleteResident())
                .show();
    }

    private void deleteResident() {
        findViewById(R.id.btnDelete).setEnabled(false);
        executorService.execute(() -> {
            try {
                new ResidentDeletionService(this).deleteResident(residentId);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Da xoa cu dan thanh cong", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (IllegalStateException ex) {
                runOnUiThread(() -> {
                    findViewById(R.id.btnDelete).setEnabled(true);
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                });
            } catch (Exception ex) {
                runOnUiThread(() -> {
                    findViewById(R.id.btnDelete).setEnabled(true);
                    Toast.makeText(this, "Khong the xoa cu dan luc nay", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private String safeText(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }
}
