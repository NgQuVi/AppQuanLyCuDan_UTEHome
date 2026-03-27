package com.example.quanlycudan_utehome.feature.vehicle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.admin.VehicleDetailAdminActivity;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.local.SessionManager;
import com.example.quanlycudan_utehome.feature.accesscard.AccessCardActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VehicleManagementActivity extends AppCompatActivity {

    private RecyclerView rvVehicles;
    private VehicleAdapter adapter;
    private int accountId;
    private int resolvedAccountId = -1;
    private androidx.lifecycle.LiveData<java.util.List<VehicleWithOwner>> currentVehicleSource;
    private TextView chipAll, chipCar, chipMotor, chipElectric;
    private final List<VehicleWithOwner> allVehicles = new ArrayList<>();
    private String currentFilter = "ALL";
    private boolean isAdmin = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_management);

        accountId = getIntent().getIntExtra("accountId", -1);
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        initView();
        initActions();
        subscribeVehicleList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribeVehicleList();
    }

    private void initView() {
        rvVehicles = findViewById(R.id.rvVehicles);
        chipAll = findViewById(R.id.chipAll);
        chipCar = findViewById(R.id.chipCar);
        chipMotor = findViewById(R.id.chipMotor);
        chipElectric = findViewById(R.id.chipElectric);

        adapter = new VehicleAdapter();
        rvVehicles.setLayoutManager(new LinearLayoutManager(this));
        rvVehicles.setAdapter(adapter);

        // Mở Activity khác nhau tùy theo user type (admin hoặc user thường)
        adapter.setOnItemClickListener(vehicle -> {
            if (isAdmin) {
                // Mở Activity chi tiết cho admin (với nút duyệt, từ chối, hủy)
                Intent intent = new Intent(VehicleManagementActivity.this, VehicleDetailAdminActivity.class);
                intent.putExtra("vehicle_id", vehicle.id);
                startActivity(intent);
            } else {
                // Mở AccessCardActivity cho user thường
                Intent intent = new Intent(VehicleManagementActivity.this, AccessCardActivity.class);
                intent.putExtra(AccessCardActivity.EXTRA_PLATE_NUMBER,
                        vehicle.licensePlate != null ? vehicle.licensePlate : "");
                intent.putExtra(AccessCardActivity.EXTRA_VEHICLE_INFO,
                        (vehicle.vehicleType != null ? vehicle.vehicleType : "")
                                + (vehicle.brand != null ? " • " + vehicle.brand : ""));
                intent.putExtra(AccessCardActivity.EXTRA_VEHICLE_COLOR,
                        vehicle.color != null ? vehicle.color : "");
                intent.putExtra(AccessCardActivity.EXTRA_RESIDENT_NAME,
                        vehicle.ownerName != null ? vehicle.ownerName : "");
                intent.putExtra(AccessCardActivity.EXTRA_APARTMENT_LABEL,
                        "Căn hộ #" + vehicle.apartmentId);
                intent.putExtra(AccessCardActivity.EXTRA_EXPIRED_DATE, "");
                intent.putExtra(AccessCardActivity.EXTRA_QR_CONTENT,
                        "Plate=" + vehicle.licensePlate + ";Owner=" + vehicle.ownerName);
                startActivity(intent);
            }
        });

        setupFilterChips();
    }

    private void setupFilterChips() {
        chipAll.setOnClickListener(v -> {
            currentFilter = "ALL";
            applyFilter();
        });
        chipCar.setOnClickListener(v -> {
            currentFilter = "CAR";
            applyFilter();
        });
        chipMotor.setOnClickListener(v -> {
            currentFilter = "MOTOR";
            applyFilter();
        });
        chipElectric.setOnClickListener(v -> {
            currentFilter = "ELECTRIC";
            applyFilter();
        });
    }

    private void initActions() {
        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            Intent intent = new Intent(VehicleManagementActivity.this, VehicleRegisterActivity.class);
            // nếu cần truyền thêm accountId hoặc apartmentId thì có thể putExtra ở đây
            startActivity(intent);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void subscribeVehicleList() {
        AppDatabase db = AppDatabase.getInstance(this);

        if (currentVehicleSource != null) {
            currentVehicleSource.removeObservers(this);
            currentVehicleSource = null;
        }

        currentVehicleSource = db.vehicleDao().getVehiclesWithOwner();
        currentVehicleSource.observe(this, vehiclesWithOwner -> {
            allVehicles.clear();
            if (vehiclesWithOwner != null) {
                allVehicles.addAll(vehiclesWithOwner);
            }
            applyFilter();
            Toast.makeText(this, "Đã tải " + allVehicles.size() + " phương tiện", Toast.LENGTH_SHORT).show();
        });
    }

    private void applyFilter() {
        List<VehicleWithOwner> filtered = new ArrayList<>();
        for (VehicleWithOwner v : allVehicles) {
            String type = v.vehicleType == null ? "" : v.vehicleType.toLowerCase(Locale.ROOT);
            switch (currentFilter) {
                case "CAR":
                    if (type.contains("ô tô") || type.contains("oto") || type.contains("car")) filtered.add(v);
                    break;
                case "MOTOR":
                    if (type.contains("xe máy") || type.contains("xemay") || type.contains("motor")) filtered.add(v);
                    break;
                case "ELECTRIC":
                    if (type.contains("điện") || type.contains("dien") || type.contains("electric")) filtered.add(v);
                    break;
                default:
                    filtered.add(v);
                    break;
            }
        }

        adapter.setData(filtered);
    }

    private int resolveAccountId() {
        if (accountId != -1) {
            return accountId;
        }

        Integer apartmentId = resolveApartmentId();
        if (apartmentId != null) {
            Apartment apartment = AppDatabase.getInstance(this).apartmentDao().getApartmentById(apartmentId);
            if (apartment != null) {
                return apartment.accountId;
            }
        }

        return -1;
    }

    private Integer resolveApartmentId() {
        String apartmentIdRaw = SessionManager.getInstance(this).getApartmentId();
        if (apartmentIdRaw != null && !apartmentIdRaw.trim().isEmpty()) {
            try {
                return Integer.parseInt(apartmentIdRaw.trim());
            } catch (NumberFormatException ignored) {
                // fall through
            }
        }
        return null;
    }
}