package com.example.quanlycudan_utehome.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Apartment;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddApartmentActivity extends AppCompatActivity {

    private EditText etApartmentCode, etArea, etBuildingCode, etFloor;
    private TextView btnStatusEmpty, btnStatusOccupied;
    private android.view.View layoutSelectOwner;

    private String selectedStatus = "Trống";
    private int selectedAccountId = 0;
    private List<com.example.quanlycudan_utehome.data.entity.Resident> allResidents = new java.util.ArrayList<>();
    private com.example.quanlycudan_utehome.data.entity.Resident selectedResident = null;
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
        etBuildingCode = findViewById(R.id.etBuildingCode);
        etFloor = findViewById(R.id.etFloor);
        btnStatusEmpty = findViewById(R.id.btnStatusEmpty);
        btnStatusOccupied = findViewById(R.id.btnStatusOccupied);
        layoutSelectOwner = findViewById(R.id.layoutSelectOwner);

        layoutSelectOwner.setOnClickListener(v -> showOwnerPickerDialog());

        // Status Toggle Logic
        btnStatusEmpty.setOnClickListener(v -> updateStatus("Trống"));
        btnStatusOccupied.setOnClickListener(v -> updateStatus("Đang sử dụng"));

        findViewById(R.id.btnCreateApartment).setOnClickListener(v -> saveApartment());

        loadAllResidents();
    }

    private void loadAllResidents() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            allResidents = db.residentDao().getAllResidentsWithAccount();
            runOnUiThread(() -> {
                // Danh sách đã được load, sẵn sàng để chọn
            });
        });
    }

    private void showOwnerPickerDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn chủ hộ");

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
                500  // Fixed height for scrolling
        ));

        ResidentPickerAdapter adapter = new ResidentPickerAdapter(allResidents, resident -> {
            selectedResident = resident;
            selectedAccountId = resident.accountId;

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
                    List<com.example.quanlycudan_utehome.data.entity.Resident> filtered = new java.util.ArrayList<>();
                    for (com.example.quanlycudan_utehome.data.entity.Resident r : allResidents) {
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
        builder.setNeutralButton("Xóa chọn", (dialog, which) -> {
            selectedResident = null;
            selectedAccountId = 0;
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

        if ("Trống".equals(status)) {
            btnStatusEmpty.setBackgroundResource(R.drawable.bg_status_toggle_selected);
            btnStatusEmpty.setTextColor(Color.parseColor("#C05030"));
        } else if ("Đang sử dụng".equals(status)) {
            btnStatusOccupied.setBackgroundResource(R.drawable.bg_status_toggle_selected);
            btnStatusOccupied.setTextColor(Color.parseColor("#C05030"));
        }
    }

    private void saveApartment() {
        String code = etApartmentCode.getText().toString().trim();
        String areaStr = etArea.getText().toString().trim();
        String buildingCode = etBuildingCode.getText().toString().trim();
        String floorStr = etFloor.getText().toString().trim();

        if (code.isEmpty() || areaStr.isEmpty() || buildingCode.isEmpty() || floorStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin: Mã căn hộ, Tòa nhà, Tầng và Diện tích", Toast.LENGTH_SHORT).show();
            return;
        }

        int floor;
        double area;
        try {
            floor = Integer.parseInt(floorStr);
            area = Double.parseDouble(areaStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Tầng phải là số, Diện tích phải là số thập phân", Toast.LENGTH_SHORT).show();
            return;
        }

        if (floor <= 0) {
            Toast.makeText(this, "Tầng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        Apartment apt = new Apartment();
        apt.apartmentCode = code;
        apt.area = (float) area;
        apt.status = selectedStatus;
        apt.buildingCode = buildingCode;
        apt.floor = floor;
        apt.accountId = selectedAccountId;

        executorService.execute(() -> {
            AppDatabase.getInstance(this).apartmentDao().insertApartment(apt);
            runOnUiThread(() -> {
                Toast.makeText(this, "Thêm căn hộ thành công!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
