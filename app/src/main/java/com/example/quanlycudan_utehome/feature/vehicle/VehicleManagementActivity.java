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

        initView();
        loadVehicleList();
    }

    private void initView() {
        rvVehicles = findViewById(R.id.rvVehicles);

        adapter = new VehicleAdapter();
        rvVehicles.setLayoutManager(new LinearLayoutManager(this));
        rvVehicles.setAdapter(adapter);
    }

    private void loadVehicleList() {
        AppDatabase db = AppDatabase.getInstance(this);

        db.vehicleDao().getAllVehicles().observe(this, vehicles -> {
            adapter.setData(vehicles);
        });
    }
}