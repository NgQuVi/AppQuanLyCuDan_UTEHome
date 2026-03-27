package com.example.quanlycudan_utehome.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.example.quanlycudan_utehome.feature.vehicle.VehicleWithOwner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VehicleApprovalListActivity extends AppCompatActivity {

    private RecyclerView rvVehicles;
    private VehicleApprovalAdapter adapter;
    private Spinner spinnerApartment;
    private List<VehicleWithOwner> allVehicles = new ArrayList<>();
    private List<Apartment> apartmentList = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean spinnerInitialized = false;
    private boolean vehiclesLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vehicle_approval_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadApartments(); // Load apartments first to setup spinner
        // Vehicles will be loaded after apartments are ready
    }

    private void initViews() {
        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // RecyclerView
        rvVehicles = findViewById(R.id.rvVehicles);
        adapter = new VehicleApprovalAdapter();
        rvVehicles.setLayoutManager(new LinearLayoutManager(this));
        rvVehicles.setAdapter(adapter);

        // Apartment Spinner
        spinnerApartment = findViewById(R.id.spinnerApartment);

        // Item click listener
        adapter.setOnItemClickListener(vehicle -> {
            Intent intent = new Intent(VehicleApprovalListActivity.this, VehicleDetailAdminActivity.class);
            intent.putExtra("vehicle_id", vehicle.id);
            startActivity(intent);
        });

        // Filter by apartment
        spinnerApartment.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (spinnerInitialized) {
                    filterVehicles();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void loadApartments() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Apartment> apartments = db.apartmentDao().getAllApartmentsSync();

            runOnUiThread(() -> {
                apartmentList = apartments;
                setupApartmentSpinner();
            });
        });
    }

    private void setupApartmentSpinner() {
        // Create adapter with "All Apartments" option
        List<String> apartmentLabels = new ArrayList<>();
        apartmentLabels.add("Tất cả căn hộ");
        for (Apartment apt : apartmentList) {
            apartmentLabels.add(apt.apartmentCode);
        }

        android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                apartmentLabels
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerApartment.setAdapter(spinnerAdapter);
        spinnerInitialized = true;

        // Load all vehicles after spinner is ready
        loadAllVehicles();
    }

    private void loadAllVehicles() {
        AppDatabase db = AppDatabase.getInstance(this);

        // Observe LiveData on main thread - get ALL vehicles regardless of status
        db.vehicleDao().getVehiclesWithOwner().observe(this, allVehiclesData -> {
            List<VehicleWithOwner> allVehiclesList = new ArrayList<>();

            if (allVehiclesData != null) {
                // Load all vehicles (no status filter)
                allVehiclesList.addAll(allVehiclesData);
            }

            this.allVehicles = allVehiclesList;
            filterVehicles();

            if (allVehiclesList.isEmpty()) {
                Toast.makeText(this, "Không có phương tiện nào", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterVehicles() {
        if (!spinnerInitialized || apartmentList.isEmpty()) {
            return; // Not ready to filter yet
        }

        int selectedPosition = spinnerApartment.getSelectedItemPosition();
        List<VehicleWithOwner> filtered = new ArrayList<>();

        if (selectedPosition == 0) {
            // Show all apartments
            filtered.addAll(allVehicles);
        } else if (selectedPosition > 0 && selectedPosition <= apartmentList.size()) {
            // Filter by selected apartment
            Apartment selectedApt = apartmentList.get(selectedPosition - 1);
            for (VehicleWithOwner v : allVehicles) {
                if (v.apartmentId == selectedApt.id) {
                    filtered.add(v);
                }
            }
        }

        adapter.setData(filtered);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllVehicles();
    }
}
