package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditApartmentActivity extends AppCompatActivity {

    private int apartmentId;
    private Apartment apartment;
    private Resident currentOwner;
    private int selectedAccountId = 0;
    private List<Resident> allResidents = new java.util.ArrayList<>();

    private EditText etApartmentCode, etArea, etBuildingCode, etFloor;
    private TextView tvOwnerName, btnChangeOwner;
    private TextView btnStatusEmpty, btnStatusOccupied;

    private String selectedStatus;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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
        etBuildingCode = findViewById(R.id.etBuildingCode);
        etFloor = findViewById(R.id.etFloor);
        tvOwnerName = findViewById(R.id.tvOwnerName);
        btnChangeOwner = findViewById(R.id.btnChangeOwner);

        btnStatusEmpty = findViewById(R.id.btnStatusEmpty);
        btnStatusOccupied = findViewById(R.id.btnStatusOccupied);

        btnChangeOwner.setOnClickListener(v -> showChangeOwnerDialog());

        btnStatusEmpty.setOnClickListener(v -> updateStatus("Trống"));
        btnStatusOccupied.setOnClickListener(v -> updateStatus("Đang sử dụng"));

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

            // Load all residents
            allResidents = db.residentDao().getAllResidentsWithAccount();

            runOnUiThread(() -> {
                if (apartment != null) {
                    etApartmentCode.setText(apartment.apartmentCode);
                    etArea.setText(String.valueOf(apartment.area));
                    etBuildingCode.setText(apartment.buildingCode);
                    etFloor.setText(String.valueOf(apartment.floor));
                    selectedStatus = apartment.status;
                    
                    if (currentOwner != null) {
                        tvOwnerName.setText(currentOwner.fullName);
                    }
                    
                    updateStatus(selectedStatus);
                }
            });
        });
    }

    private void showChangeOwnerDialog() {
        if (allResidents.isEmpty()) {
            Toast.makeText(this, "Không có cư dân nào để chọn", Toast.LENGTH_SHORT).show();
            return;
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Thay đổi chủ hộ");

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(20, 20, 20, 20);

        // Search EditText
        EditText etSearch = new EditText(this);
        etSearch.setHint("Tìm kiếm theo tên...");
        etSearch.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        dialogLayout.addView(etSearch);

        // RecyclerView for residents
        RecyclerView rvResidents = new RecyclerView(this);
        rvResidents.setLayoutManager(new LinearLayoutManager(this));
        rvResidents.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                500
        ));

        ResidentPickerAdapter adapter = new ResidentPickerAdapter(allResidents, resident -> {
            selectedAccountId = resident.accountId;
            currentOwner = resident;
            tvOwnerName.setText(resident.fullName);
            updateStatus("Đang sử dụng");

            android.app.AlertDialog dialog = (android.app.AlertDialog) etSearch.getTag();
            if (dialog != null) dialog.dismiss();
        });
        rvResidents.setAdapter(adapter);

        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    adapter.setResidents(allResidents);
                } else {
                    List<Resident> filtered = new java.util.ArrayList<>();
                    for (Resident r : allResidents) {
                        if (r.fullName.toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(r);
                        }
                    }
                    adapter.setResidents(filtered);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        dialogLayout.addView(rvResidents);

        builder.setView(dialogLayout);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("Xóa chủ hộ", (dialog, which) -> {
            selectedAccountId = 0;
            currentOwner = null;
            tvOwnerName.setText("Chưa có chủ hộ");
            updateStatus("Trống");
        });

        android.app.AlertDialog dialog = builder.create();
        etSearch.setTag(dialog);
        dialog.show();
    }

    private void updateStatus(String status) {
        selectedStatus = status;
        
        btnStatusEmpty.setBackgroundResource(R.drawable.bg_status_toggle_unselected);
        btnStatusEmpty.setTextColor(Color.parseColor("#8E8E8E"));
        btnStatusOccupied.setBackgroundResource(R.drawable.bg_status_toggle_unselected);
        btnStatusOccupied.setTextColor(Color.parseColor("#8E8E8E"));

        if ("Trống".equalsIgnoreCase(status)) {
            btnStatusEmpty.setBackgroundResource(R.drawable.bg_status_toggle_selected);
            btnStatusEmpty.setTextColor(Color.parseColor("#C05030"));
        } else if ("Đang sử dụng".equalsIgnoreCase(status)) {
            btnStatusOccupied.setBackgroundResource(R.drawable.bg_status_toggle_selected);
            btnStatusOccupied.setTextColor(Color.parseColor("#C05030"));
        }
    }

    private void saveChanges() {
        String code = etApartmentCode.getText().toString().trim();
        String areaStr = etArea.getText().toString().trim();
        String buildingCode = etBuildingCode.getText().toString().trim();
        String floorStr = etFloor.getText().toString().trim();

        if (code.isEmpty() || areaStr.isEmpty() || buildingCode.isEmpty() || floorStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        float area;
        int floor;
        try {
            area = Float.parseFloat(areaStr);
            floor = Integer.parseInt(floorStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Diện tích và tầng phải là số", Toast.LENGTH_SHORT).show();
            return;
        }

        if (floor <= 0) {
            Toast.makeText(this, "Tầng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        apartment.apartmentCode = code;
        apartment.area = area;
        apartment.buildingCode = buildingCode;
        apartment.floor = floor;
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
