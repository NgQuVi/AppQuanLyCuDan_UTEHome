package com.example.quanlycudan_utehome.feature.vehicle;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;

public class VehicleManagementActivity extends AppCompatActivity {

    private RecyclerView rvVehicles;
    private VehicleAdapter adapter;
    private int accountId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_management);

        accountId = getIntent().getIntExtra("accountId", -1);

        initView();
        initActions();
        loadVehicleList(accountId);
    }

    private void initView() {
        rvVehicles = findViewById(R.id.rvVehicles);

        adapter = new VehicleAdapter();
        rvVehicles.setLayoutManager(new LinearLayoutManager(this));
        rvVehicles.setAdapter(adapter);
    }

    private void initActions() {
        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            Intent intent = new Intent(VehicleManagementActivity.this, VehicleRegisterActivity.class);
            // nếu cần truyền thêm accountId hoặc apartmentId thì có thể putExtra ở đây
            startActivity(intent);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadVehicleList(int accountId) {
        AppDatabase db = AppDatabase.getInstance(this);

        if (accountId == -1) {
            // nếu không truyền được accountId thì tạm thời hiển thị tất cả như cũ
            db.vehicleDao()
                    .getVehiclesWithOwner()
                    .observe(this, vehiclesWithOwner -> {
                        if (vehiclesWithOwner == null) {
                            adapter.setData(java.util.Collections.emptyList());
                        } else {
                            adapter.setData(vehiclesWithOwner);
                        }
                    });
            return;
        }

        // Lọc theo accountId => chỉ các xe thuộc căn hộ của tài khoản hiện tại
        db.vehicleDao()
                .getVehiclesWithOwnerByAccountId(accountId)
                .observe(this, vehiclesWithOwner -> {
                    if (vehiclesWithOwner == null) {
                        adapter.setData(java.util.Collections.emptyList());
                    } else {
                        adapter.setData(vehiclesWithOwner);
                    }
                });
    }
}