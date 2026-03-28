package com.example.quanlycudan_utehome.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

public class EditApartmentActivity extends AppCompatActivity {

    private int apartmentId;
    private Apartment apartment;
    private Resident currentOwner;
    private int selectedAccountId = 0;

    private EditText etApartmentCode, etArea;
    private TextView tvOwnerName, tvSelectedBuilding, tvSelectedFloor;
    private TextView btnStatusEmpty, btnStatusOccupied, btnStatusHandover;
    private View layoutSelectBuilding, layoutSelectFloor, layoutSelectOwner;

    private String selectedStatus;
    private String selectedBuilding;
    private int selectedFloor;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<Intent> pickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedAccountId = result.getData().getIntExtra("account_id", 0);
                    String name = result.getData().getStringExtra("resident_name");
                    tvOwnerName.setText(name);
                    updateStatus("Đang ở"); 
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_apartment);

        apartmentId = getIntent().getIntExtra("apartment_id", -1);
        if (apartmentId == -1) {
            finish();
            return;
        }

        initViews();
        loadData();
    }

    private void initViews() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etApartmentCode = findViewById(R.id.etApartmentCode);
        etArea = findViewById(R.id.etArea);
        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvSelectedBuilding = findViewById(R.id.tvSelectedBuilding);
        tvSelectedFloor = findViewById(R.id.tvSelectedFloor);
        
        btnStatusEmpty = findViewById(R.id.btnStatusEmpty);
        btnStatusOccupied = findViewById(R.id.btnStatusOccupied);
        btnStatusHandover = findViewById(R.id.btnStatusHandover);

        layoutSelectBuilding = findViewById(R.id.layoutSelectBuilding);
        layoutSelectFloor = findViewById(R.id.layoutSelectFloor);
        layoutSelectOwner = findViewById(R.id.layoutSelectOwner);

        layoutSelectBuilding.setOnClickListener(v -> showBuildingMenu());
        layoutSelectFloor.setOnClickListener(v -> showFloorMenu());
        layoutSelectOwner.setOnClickListener(v -> {
            Intent intent = new Intent(this, ResidentPickerActivity.class);
            pickerLauncher.launch(intent);
        });

        btnStatusEmpty.setOnClickListener(v -> updateStatus("Trống"));
        btnStatusOccupied.setOnClickListener(v -> updateStatus("Đang ở"));
        btnStatusHandover.setOnClickListener(v -> updateStatus("Bàn giao"));

        findViewById(R.id.btnSave).setOnClickListener(v -> saveChanges());
    }

    private void loadData() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            apartment = db.apartmentDao().getApartmentById(apartmentId);
            if (apartment != null && apartment.accountId > 0) {
                currentOwner = db.residentDao().getResidentByAccountId(apartment.accountId);
                selectedAccountId = apartment.accountId;
            }

            runOnUiThread(() -> {
                if (apartment != null) {
                    etApartmentCode.setText(apartment.apartmentCode);
                    etArea.setText(String.valueOf(apartment.area));
                    selectedBuilding = apartment.buildingCode;
                    selectedFloor = apartment.floor;
                    selectedStatus = apartment.status;
                    
                    tvSelectedBuilding.setText(selectedBuilding);
                    tvSelectedFloor.setText(String.valueOf(selectedFloor));
                    
                    if (currentOwner != null) {
                        tvOwnerName.setText(currentOwner.fullName);
                    }
                    
                    updateStatus(selectedStatus);
                }
            });
        });
    }

    private void showBuildingMenu() {
        PopupMenu popup = new PopupMenu(this, layoutSelectBuilding);
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
        PopupMenu popup = new PopupMenu(this, layoutSelectFloor);
        for (int i = 1; i <= 25; i++) popup.getMenu().add("Tầng " + i);
        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            selectedFloor = Integer.parseInt(title.replace("Tầng ", ""));
            tvSelectedFloor.setText(String.valueOf(selectedFloor));
            return true;
        });
        popup.show();
    }

    private void updateStatus(String status) {
        selectedStatus = status;
        
        btnStatusEmpty.setBackgroundResource(R.drawable.bg_status_toggle_unselected);
        btnStatusEmpty.setTextColor(Color.parseColor("#8E8E8E"));
        btnStatusOccupied.setBackgroundResource(R.drawable.bg_status_toggle_unselected);
        btnStatusOccupied.setTextColor(Color.parseColor("#8E8E8E"));
        btnStatusHandover.setBackgroundResource(R.drawable.bg_status_toggle_unselected);
        btnStatusHandover.setTextColor(Color.parseColor("#8E8E8E"));

        if ("Trống".equalsIgnoreCase(status)) {
            btnStatusEmpty.setBackgroundResource(R.drawable.bg_status_toggle_selected);
            btnStatusEmpty.setTextColor(Color.parseColor("#C05030"));
        } else if ("Đang ở".equalsIgnoreCase(status) || "Đang sử dụng".equalsIgnoreCase(status)) {
            btnStatusOccupied.setBackgroundResource(R.drawable.bg_status_toggle_selected);
            btnStatusOccupied.setTextColor(Color.parseColor("#C05030"));
        } else {
            btnStatusHandover.setBackgroundResource(R.drawable.bg_status_toggle_selected);
            btnStatusHandover.setTextColor(Color.parseColor("#C05030"));
        }
    }

    private void saveChanges() {
        String code = etApartmentCode.getText().toString().trim();
        String areaStr = etArea.getText().toString().trim();

        if (code.isEmpty() || areaStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        float area = Float.parseFloat(areaStr);
        
        apartment.apartmentCode = code;
        apartment.area = area;
        apartment.buildingCode = selectedBuilding;
        apartment.floor = selectedFloor;
        apartment.status = selectedStatus;
        apartment.accountId = selectedAccountId;

        executorService.execute(() -> {
            AppDatabase.getInstance(this).apartmentDao().update(apartment);
            runOnUiThread(() -> {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
