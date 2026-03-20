package com.example.quanlycudan_utehome.feature.vehicle;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_management);
        int accountID = getIntent().getIntExtra("accountId", 1);
        initView();
        loadVehicleList(accountID);
    }

    private void initView() {
        rvVehicles = findViewById(R.id.rvVehicles);

        adapter = new VehicleAdapter();
        rvVehicles.setLayoutManager(new LinearLayoutManager(this));
        rvVehicles.setAdapter(adapter);
    }

    private void loadVehicleList(int accountID) {
        // in ra log để kiểm tra accountID
        android.util.Log.d("VehicleManagement", "Loading vehicles for accountID: " + accountID);
        AppDatabase db = AppDatabase.getInstance(this);

        db.vehicleDao()
                .getVehiclesWithOwnerByAccountId(accountID)
                .observe(this, vehiclesWithOwner -> {
                    if (vehiclesWithOwner == null) {
                        adapter.setData(java.util.Collections.emptyList());
                    } else {
                        adapter.setData(vehiclesWithOwner);
                    }
                });
    }
}