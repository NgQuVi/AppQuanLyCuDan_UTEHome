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
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            com.example.quanlycudan_utehome.data.local.SessionManager session =
                    com.example.quanlycudan_utehome.data.local.SessionManager.getInstance(this);

            int residentId = session.getResidentId();
            Resident resident = residentId == -1 ? null : db.residentDao().getResidentById(residentId);

            if (resident != null) {
                runOnUiThread(() -> {
                    selectedResidentId = resident.id;
                    tvSelectedResident.setText(resident.fullName);
                });
                return;
            }

            Integer apartmentId = resolveCurrentApartmentId(db);
            if (apartmentId == null) {
                runOnUiThread(() -> tvSelectedResident.setText("Chọn cư dân"));
                return;
            }

            List<ApartmentMember> members = db.apartmentMemberDao().getMembers(apartmentId);
            for (ApartmentMember m : members) {
                Resident r = db.residentDao().getResidentById(m.residentId);
                if (r != null) {
                    runOnUiThread(() -> {
                        selectedResidentId = r.id;
                        tvSelectedResident.setText(r.fullName);
                    });
                    return;
                }
            }

            runOnUiThread(() -> tvSelectedResident.setText("Chọn cư dân"));
        });
    }

    private void showResidentPickerDialog() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            Integer apartmentId = resolveCurrentApartmentId(db);
            if (apartmentId == null) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Không xác định được căn hộ hiện tại", Toast.LENGTH_SHORT).show());
                return;
            }

            List<ApartmentMember> members = db.apartmentMemberDao().getMembers(apartmentId);
            List<Resident> residents = new ArrayList<>();
            List<String> names = new ArrayList<>();

            for (ApartmentMember m : members) {
                Resident r = db.residentDao().getResidentById(m.residentId);
                if (r != null) {
                    residents.add(r);
                    String displayName = r.fullName;
                    if (displayName == null || displayName.trim().isEmpty()) {
                        displayName = "Cư dân #" + r.id;
                    }
                    names.add(displayName);
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
                            String chosenName = chosen.fullName;
                            if (chosenName == null || chosenName.trim().isEmpty()) {
                                chosenName = "Cư dân #" + chosen.id;
                            }
                            tvSelectedResident.setText(chosenName);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });
        });
    }

    private Integer resolveCurrentApartmentId(AppDatabase db) {
        com.example.quanlycudan_utehome.data.local.SessionManager session =
                com.example.quanlycudan_utehome.data.local.SessionManager.getInstance(this);

        int residentId = session.getResidentId();
        if (residentId != -1) {
            Integer byResident = db.apartmentMemberDao().getApartmentIdByResidentId(residentId);
            if (byResident != null) return byResident;
        }

        String apartmentIdRaw = session.getApartmentId();
        if (apartmentIdRaw != null && !apartmentIdRaw.trim().isEmpty()) {
            try {
                return Integer.parseInt(apartmentIdRaw.trim());
            } catch (NumberFormatException ignored) {
                // fallback under malformed session data
            }
        }

        return null;
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
