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
    private TextView tvSelectedBuilding, tvSelectedFloor, tvSelectedApartmentType;
    private android.view.View layoutSelectBuilding, layoutSelectFloor, layoutSelectApartmentType;
    
    private String selectedStatus = "Trống";
    private String selectedBuilding = "";
    private int selectedFloor = -1;
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

        tvSelectedBuilding = findViewById(R.id.tvSelectedBuilding);
        tvSelectedFloor = findViewById(R.id.tvSelectedFloor);
        tvSelectedApartmentType = findViewById(R.id.tvSelectedApartmentType);
        layoutSelectBuilding = findViewById(R.id.layoutSelectBuilding);
        layoutSelectFloor = findViewById(R.id.layoutSelectFloor);
        layoutSelectApartmentType = findViewById(R.id.layoutSelectApartmentType);

        layoutSelectBuilding.setOnClickListener(v -> showBuildingMenu());
        layoutSelectFloor.setOnClickListener(v -> showFloorMenu());
        layoutSelectApartmentType.setOnClickListener(v -> showApartmentTypeMenu());

        // Status Toggle Logic
        btnStatusEmpty.setOnClickListener(v -> updateStatus("Trống"));
        btnStatusOccupied.setOnClickListener(v -> updateStatus("Đang sử dụng"));
        btnStatusHandover.setOnClickListener(v -> updateStatus("Bàn giao"));

        findViewById(R.id.btnCreateApartment).setOnClickListener(v -> saveApartment());
    }

    private void showBuildingMenu() {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, layoutSelectBuilding);
        String[] buildings = {"S1", "S2", "S3", "S5"};
        for (String b : buildings) popup.getMenu().add(b);
        popup.setOnMenuItemClickListener(item -> {
            selectedBuilding = item.getTitle().toString();
            tvSelectedBuilding.setText(selectedBuilding);
            return true;
        });
        popup.show();
    }

    private void showFloorMenu() {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, layoutSelectFloor);
        for (int i = 1; i <= 25; i++) popup.getMenu().add("Tầng " + i);
        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            selectedFloor = Integer.parseInt(title.replace("Tầng ", ""));
            tvSelectedFloor.setText(title);
            return true;
        });
        popup.show();
    }

    private void showApartmentTypeMenu() {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, layoutSelectApartmentType);
        String[] types = {"1PN", "2PN", "3PN", "Studio", "Penthouse"};
        for (String t : types) popup.getMenu().add(t);
        popup.setOnMenuItemClickListener(item -> {
            tvSelectedApartmentType.setText(item.getTitle());
            return true;
        });
        popup.show();
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

        if (selectedBuilding.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn Tòa nhà", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFloor == -1) {
            Toast.makeText(this, "Vui lòng chọn Tầng", Toast.LENGTH_SHORT).show();
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
        apt.buildingCode = selectedBuilding; 
        apt.floor = selectedFloor;

        executorService.execute(() -> {
            AppDatabase.getInstance(this).apartmentDao().insertApartment(apt);
            runOnUiThread(() -> {
                Toast.makeText(this, "Thêm căn hộ thành công!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
