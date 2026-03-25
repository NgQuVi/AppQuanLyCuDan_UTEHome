package com.example.quanlycudan_utehome.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.feature.facility.FacilityListActivity;
import com.example.quanlycudan_utehome.feature.vehicle.VehicleManagementActivity;

public class ManageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup Card Listeners
        LinearLayout cardManageVehicles = findViewById(R.id.cardManageVehicles);
        LinearLayout cardManageUtilities = findViewById(R.id.cardManageUtilities);

        cardManageVehicles.setOnClickListener(v -> {
            // Using existing VehicleManagementActivity. We assume accountId=1 for Admin demo, or it will handle it.
            Intent intent = new Intent(this, VehicleManagementActivity.class);
            intent.putExtra("accountId", 1); 
            startActivity(intent);
        });

        cardManageUtilities.setOnClickListener(v -> {
            // Using existing FacilityListActivity
            Intent intent = new Intent(this, FacilityListActivity.class);
            startActivity(intent);
        });

        // Setup Custom Bottom Navigation logic exactly matching activity_manage.xml
        LinearLayout navDashboard = findViewById(R.id.navDashboard);
        LinearLayout navManage = findViewById(R.id.navManage);
        LinearLayout navSettings = findViewById(R.id.navSettings);

        navDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminMainActivity.class));
            finish();
            overridePendingTransition(0, 0); // Remove animation for tab switch feel
        });

        // navManage is already active, so do nothing or refresh
        
        navSettings.setOnClickListener(v -> {
            android.widget.Toast.makeText(this, "Chức năng cài đặt", android.widget.Toast.LENGTH_SHORT).show();
        });
    }
}
