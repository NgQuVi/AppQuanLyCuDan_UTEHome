package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Resident;
import com.example.quanlycudan_utehome.data.entity.Vehicle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VehicleDetailAdminActivity extends AppCompatActivity {

    private int vehicleId;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vehicle_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contentScroll), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        vehicleId = getIntent().getIntExtra("vehicle_id", -1);
        if (vehicleId == -1) {
            Toast.makeText(this, "Không có thông tin phương tiện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        findViewById(R.id.btnApprove).setOnClickListener(v -> {
            Toast.makeText(this, "Đã duyệt phương tiện", Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.btnReject).setOnClickListener(v -> {
            Toast.makeText(this, "Đã từ chối đăng ký", Toast.LENGTH_SHORT).show();
            finish();
        });

        loadVehicle();
    }

    private void loadVehicle() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Vehicle vehicle = db.vehicleDao().getVehicleByIdSync(vehicleId);
            
            if (vehicle == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Không tìm thấy phương tiện", Toast.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }

            // Try to load owner info
            Resident owner = db.residentDao().getResidentById(vehicle.residentId);

            runOnUiThread(() -> bindData(vehicle, owner));
        });
    }

    private void bindData(Vehicle vehicle, Resident owner) {
        TextView tvDetailType = findViewById(R.id.tvDetailType);
        TextView tvDetailPlate = findViewById(R.id.tvDetailPlate);
        TextView tvDetailDesc = findViewById(R.id.tvDetailDesc);
        TextView tvDetailOwner = findViewById(R.id.tvDetailOwner);
        TextView tvDetailApt = findViewById(R.id.tvDetailApt);
        TextView tvStatusTitle = findViewById(R.id.tvStatusTitle);

        String type = vehicle.vehicleType != null ? vehicle.vehicleType : "Không rõ";
        tvDetailType.setText(type);
        tvDetailPlate.setText(vehicle.licensePlate != null ? vehicle.licensePlate : "-");
        tvDetailDesc.setText((vehicle.brand != null ? vehicle.brand : "") + " - " + (vehicle.color != null ? vehicle.color : ""));
        
        if (owner != null) {
            tvDetailOwner.setText(owner.fullName);
        } else {
            tvDetailOwner.setText("Không xác định");
        }
        
        tvDetailApt.setText("Căn hộ #" + vehicle.apartmentId);
        
        // Status display
        String status = vehicle.status != null ? vehicle.status : "PENDING";
        switch (status.toUpperCase()) {
            case "APPROVED":
                tvStatusTitle.setText("Đã được duyệt");
                tvStatusTitle.setTextColor(android.graphics.Color.parseColor("#1F7343"));
                break;
            case "REJECTED":
                tvStatusTitle.setText("Đã từ chối");
                tvStatusTitle.setTextColor(android.graphics.Color.parseColor("#C62828"));
                break;
            default:
                tvStatusTitle.setText("Đang chờ duyệt");
                tvStatusTitle.setTextColor(getResources().getColor(R.color.brand_orange, null));
        }
    }
}
