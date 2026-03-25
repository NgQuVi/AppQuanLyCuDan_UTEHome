package com.example.quanlycudan_utehome.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddApartmentActivity extends AppCompatActivity {

    private EditText etApartmentCode, etArea;
    private TextView btnStatusEmpty, btnStatusOccupied, btnStatusHandover;
    
    private String selectedStatus = "Trống";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_apartment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etApartmentCode = findViewById(R.id.etApartmentCode);
        etArea = findViewById(R.id.etArea);
        btnStatusEmpty = findViewById(R.id.btnStatusEmpty);
        btnStatusOccupied = findViewById(R.id.btnStatusOccupied);
        btnStatusHandover = findViewById(R.id.btnStatusHandover);

        // Status Toggle Logic
        btnStatusEmpty.setOnClickListener(v -> updateStatus("Trống"));
        btnStatusOccupied.setOnClickListener(v -> updateStatus("Đang sử dụng"));
        btnStatusHandover.setOnClickListener(v -> updateStatus("Bàn giao"));

        findViewById(R.id.btnCreateApartment).setOnClickListener(v -> saveApartment());
    }

    private void updateStatus(String status) {
        selectedStatus = status;
        
        btnStatusEmpty.setBackgroundResource(R.drawable.bg_status_toggle_unselected);
        btnStatusEmpty.setTextColor(Color.parseColor("#8E8E8E")); // text_secondary
        
        btnStatusOccupied.setBackgroundResource(R.drawable.bg_status_toggle_unselected);
        btnStatusOccupied.setTextColor(Color.parseColor("#8E8E8E"));

        btnStatusHandover.setBackgroundResource(R.drawable.bg_status_toggle_unselected);
        btnStatusHandover.setTextColor(Color.parseColor("#8E8E8E"));

        if ("Trống".equals(status)) {
            btnStatusEmpty.setBackgroundResource(R.drawable.bg_status_toggle_selected);
            btnStatusEmpty.setTextColor(Color.parseColor("#C05030")); // highlight color
        } else if ("Đang sử dụng".equals(status)) {
            btnStatusOccupied.setBackgroundResource(R.drawable.bg_status_toggle_selected);
            btnStatusOccupied.setTextColor(Color.parseColor("#C05030"));
        } else {
            btnStatusHandover.setBackgroundResource(R.drawable.bg_status_toggle_selected);
            btnStatusHandover.setTextColor(Color.parseColor("#C05030"));
        }
    }

    private void saveApartment() {
        String code = etApartmentCode.getText().toString().trim();
        String areaStr = etArea.getText().toString().trim();

        if (code.isEmpty() || areaStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ Mã căn hộ và Diện tích", Toast.LENGTH_SHORT).show();
            return;
        }

        double area;
        try {
            area = Double.parseDouble(areaStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Diện tích không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Apartment apt = new Apartment();
        apt.apartmentCode = code;
        apt.area = (float) area;
        apt.status = selectedStatus;
        // Defaulting to S1 and floor 12 for demo purposes as drop downs are static in UI
        apt.buildingCode = "S1"; 
        apt.floor = 12;

        executorService.execute(() -> {
            AppDatabase.getInstance(this).apartmentDao().insertApartment(apt);
            runOnUiThread(() -> {
                Toast.makeText(this, "Thêm căn hộ thành công!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
