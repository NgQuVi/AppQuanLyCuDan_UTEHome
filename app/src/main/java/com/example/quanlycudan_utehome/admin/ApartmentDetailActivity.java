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

public class ApartmentDetailActivity extends AppCompatActivity {

    private int apartmentId = -1;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apartment_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddMember).setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng thêm thành viên đang phát triển", Toast.LENGTH_SHORT).show();
        });

        apartmentId = getIntent().getIntExtra("apartment_id", -1);
        if (apartmentId != -1) {
            loadApartmentDetails();
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu căn hộ", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadApartmentDetails() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Apartment apartment = db.apartmentDao().getApartmentById(apartmentId);
            Resident owner = null;
            if (apartment != null && apartment.accountId > 0) {
                owner = db.residentDao().getResidentByAccountId(apartment.accountId);
            }

            Apartment finalApartment = apartment;
            Resident finalOwner = owner;

            runOnUiThread(() -> {
                if (finalApartment != null) {
                    ((TextView) findViewById(R.id.tvRoomNameBig)).setText("P." + finalApartment.apartmentCode);
                    ((TextView) findViewById(R.id.tvBuildingDesc)).setText("Tòa " + finalApartment.buildingCode + " - UTE Home");
                    ((TextView) findViewById(R.id.tvCode)).setText("P." + finalApartment.apartmentCode);
                    ((TextView) findViewById(R.id.tvFloor)).setText("Tầng " + finalApartment.floor);
                    ((TextView) findViewById(R.id.tvBuilding)).setText("Tòa " + finalApartment.buildingCode);
                    ((TextView) findViewById(R.id.tvArea)).setText(finalApartment.area + " m²");
                    
                    if (finalOwner != null) {
                        ((TextView) findViewById(R.id.tvOwnerName)).setText(finalOwner.fullName);
                    } else {
                        ((TextView) findViewById(R.id.tvOwnerName)).setText("Chưa có chủ hộ");
                    }
                }
            });
        });
    }
}
