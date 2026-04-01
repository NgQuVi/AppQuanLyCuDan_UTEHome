package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Facility;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditFacilityActivity extends AppCompatActivity {

    private EditText etName, etLocation, etCapacity, etOpenTime, etCloseTime, etDescription;
    private SwitchCompat switchIsOpen;
    private TextView tvTitle;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Facility existingFacility = null; // null = add mode, non-null = edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_facility);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTitle = findViewById(R.id.tvTitle);
        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etCapacity = findViewById(R.id.etCapacity);
        etOpenTime = findViewById(R.id.etOpenTime);
        etCloseTime = findViewById(R.id.etCloseTime);
        etDescription = findViewById(R.id.etDescription);
        switchIsOpen = findViewById(R.id.switchIsOpen);
        Button btnSave = findViewById(R.id.btnSave);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Check if editing
        if (getIntent().hasExtra("facility")) {
            existingFacility = (Facility) getIntent().getSerializableExtra("facility");
            populateFields(existingFacility);
            tvTitle.setText("CHỈNH SỬA TIỆN ÍCH");
        }

        btnSave.setOnClickListener(v -> saveFacility());
    }

    private void populateFields(Facility f) {
        etName.setText(f.name);
        etLocation.setText(f.location);
        etCapacity.setText(String.valueOf(f.capacity));
        etOpenTime.setText(f.openTime);
        etCloseTime.setText(f.CloseTime);
        etDescription.setText(f.description);
        switchIsOpen.setChecked(f.isOpen);
    }

    private void saveFacility() {
        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String capacityStr = etCapacity.getText().toString().trim();
        String openTime = etOpenTime.getText().toString().trim();
        String closeTime = etCloseTime.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        boolean isOpen = switchIsOpen.isChecked();

        if (name.isEmpty()) {
            etName.setError("Vui lòng nhập tên tiện ích");
            etName.requestFocus();
            return;
        }
        if (location.isEmpty()) {
            etLocation.setError("Vui lòng nhập vị trí");
            etLocation.requestFocus();
            return;
        }

        int capacity = 0;
        if (!capacityStr.isEmpty()) {
            try { capacity = Integer.parseInt(capacityStr); } catch (NumberFormatException ignored) {}
        }
        if (openTime.isEmpty()) openTime = "06:00";
        if (closeTime.isEmpty()) closeTime = "22:00";

        Facility facility = existingFacility != null ? existingFacility : new Facility();
        facility.name = name;
        facility.location = location;
        facility.capacity = capacity;
        facility.openTime = openTime;
        facility.CloseTime = closeTime;
        facility.description = description;
        facility.isOpen = isOpen;
        // Keep existing imageResId or default to 0
        if (existingFacility == null) {
            facility.imageResId = 0;
        }

        final Facility toSave = facility;
        final boolean isEditMode = existingFacility != null;

        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            if (isEditMode) {
                db.facilityDao().updateFacility(toSave);
            } else {
                db.facilityDao().insertFacility(toSave);
            }
            runOnUiThread(() -> {
                String msg = isEditMode ? "Đã cập nhật tiện ích" : "Đã thêm tiện ích mới";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
