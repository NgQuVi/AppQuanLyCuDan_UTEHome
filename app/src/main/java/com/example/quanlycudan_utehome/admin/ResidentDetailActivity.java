package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
            Toast.makeText(this, "Không tìm thấy dữ liệu cư dân", Toast.LENGTH_SHORT).show();
            finish();
        }
        
        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng xoá cư dân đang được phát triển...", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadResidentDetails() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Resident resident = db.residentDao().getResidentById(residentId);
            Apartment apartment = null;
            if (resident != null) {
                apartment = db.apartmentDao().getApartmentByAccountId(resident.accountId);
            }
            
            Resident finalResident = resident;
            Apartment finalApartment = apartment;

            runOnUiThread(() -> {
                if (finalResident != null) {
                    ((TextView) findViewById(R.id.tvName)).setText(finalResident.fullName);
                    
                    String resIdPadded = String.valueOf(finalResident.id);
                    ((TextView) findViewById(R.id.tvResidentCode)).setText("RES000" + resIdPadded);
                    
                    ((TextView) findViewById(R.id.tvDob)).setText(finalResident.dob != null ? finalResident.dob : "N/A");
                    ((TextView) findViewById(R.id.tvGender)).setText(finalResident.gender != null ? finalResident.gender : "N/A");
                    
                    if (finalApartment != null) {
                        ((TextView) findViewById(R.id.tvApartment)).setText("P." + finalApartment.apartmentCode + " - Tòa " + finalApartment.buildingCode);
                    } else {
                        ((TextView) findViewById(R.id.tvApartment)).setText("Chưa phân bổ căn hộ");
                    }

                    ((TextView) findViewById(R.id.tvIdType)).setText("CCCD");
                    ((TextView) findViewById(R.id.tvIdNumber)).setText(finalResident.idNum != null ? finalResident.idNum : "N/A");
                    ((TextView) findViewById(R.id.tvPhone)).setText(finalResident.phone != null ? finalResident.phone : "N/A");
                }
            });
        });
    }
}
