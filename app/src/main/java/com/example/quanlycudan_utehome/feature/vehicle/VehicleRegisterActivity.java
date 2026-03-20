package com.example.quanlycudan_utehome.feature.vehicle;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.Resident;
import com.example.quanlycudan_utehome.data.entity.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class VehicleRegisterActivity extends AppCompatActivity {

    private TextView tvSelectedResident, tvVehicleType;
    private EditText etLicensePlate, etBrand, etColor;
    private Button btnSubmit;

    private int selectedResidentId = -1;
    private String selectedVehicleType = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_register);

        initViews();
        setupEvents();
        loadDefaultResident();
    }

    private void initViews() {
        tvSelectedResident = findViewById(R.id.tvSelectedResident);
        tvVehicleType = findViewById(R.id.tvVehicleType);
        etLicensePlate = findViewById(R.id.etLicensePlate);
        etBrand = findViewById(R.id.etBrand);
        etColor = findViewById(R.id.etColor);
        btnSubmit = findViewById(R.id.btnSubmit);

        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private void setupEvents() {
        LinearLayout layoutResidentSelector = findViewById(R.id.layoutResidentSelector);
        layoutResidentSelector.setOnClickListener(v -> showResidentPickerDialog());

        LinearLayout layoutVehicleType = findViewById(R.id.layoutVehicleType);
        layoutVehicleType.setOnClickListener(v -> showVehicleTypeDialog());

        btnSubmit.setOnClickListener(v -> submitVehicle());
    }

    private void loadDefaultResident() {
        // Lấy residentId từ Session, nếu có
        int residentId = com.example.quanlycudan_utehome.data.local.SessionManager
                .getInstance(this)
                .getResidentId();

        if (residentId == -1) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Resident resident = db.residentDao().getResidentById(residentId);

            runOnUiThread(() -> {
                if (resident != null) {
                    selectedResidentId = resident.id;
                    tvSelectedResident.setText(resident.fullName);
                }
            });
        });
    }

    private void showResidentPickerDialog() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // Lấy residentId hiện tại từ Session để suy ra apartment
            int currentResidentId = com.example.quanlycudan_utehome.data.local.SessionManager
                    .getInstance(this)
                    .getResidentId();

            if (currentResidentId == -1) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Không xác định được cư dân hiện tại", Toast.LENGTH_SHORT).show());
                return;
            }

            Integer apartmentId = db.apartmentMemberDao().getApartmentIdByResidentId(currentResidentId);
            if (apartmentId == null) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Bạn chưa thuộc căn hộ nào", Toast.LENGTH_SHORT).show());
                return;
            }

            List<ApartmentMember> members = db.apartmentMemberDao().getMembers(apartmentId);
            List<Resident> residents = new ArrayList<>();
            List<String> names = new ArrayList<>();

            for (ApartmentMember m : members) {
                Resident r = db.residentDao().getResidentById(m.residentId);
                if (r != null) {
                    residents.add(r);
                    names.add(r.fullName);
                }
            }

            runOnUiThread(() -> {
                if (residents.isEmpty()) {
                    Toast.makeText(this, "Không có cư dân nào trong căn hộ", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        names
                );

                new AlertDialog.Builder(this)
                        .setTitle("Chọn cư dân")
                        .setAdapter(adapter, (dialog, which) -> {
                            Resident chosen = residents.get(which);
                            selectedResidentId = chosen.id;
                            tvSelectedResident.setText(chosen.fullName);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });
        });
    }

    private void showVehicleTypeDialog() {
        String[] types = new String[]{"Ô tô", "Xe máy"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn loại xe")
                .setItems(types, (dialog, which) -> {
                    selectedVehicleType = types[which];
                    tvVehicleType.setText(selectedVehicleType);
                })
                .show();
    }

    private void submitVehicle() {
        if (selectedResidentId == -1) {
            Toast.makeText(this, "Vui lòng chọn cư dân", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedVehicleType == null || selectedVehicleType.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn loại xe", Toast.LENGTH_SHORT).show();
            return;
        }

        String plate = etLicensePlate.getText().toString().trim();
        String brand = etBrand.getText().toString().trim();
        String color = etColor.getText().toString().trim();

        if (plate.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập biển số xe", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // Xác định apartmentId của cư dân được chọn
            Integer apartmentId = db.apartmentMemberDao().getApartmentIdByResidentId(selectedResidentId);
            if (apartmentId == null) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Cư dân chưa thuộc căn hộ nào", Toast.LENGTH_SHORT).show());
                return;
            }

            Vehicle v = new Vehicle();
            v.apartmentId = apartmentId;
            v.residentId = selectedResidentId;
            v.vehicleType = selectedVehicleType;
            v.licensePlate = plate;
            v.brand = brand;
            v.color = color;
            v.status = "Chờ duyệt";

            db.vehicleDao().insertVehicle(v);

            runOnUiThread(() -> {
                Toast.makeText(this, "Đã gửi đăng ký phương tiện", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
