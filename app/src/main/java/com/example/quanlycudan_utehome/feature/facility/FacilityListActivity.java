package com.example.quanlycudan_utehome.feature.facility;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.Facility;

import java.util.ArrayList;
import java.util.List;

public class FacilityListActivity extends AppCompatActivity {

    private RecyclerView rvFacilities;
    private FacilityAdapter adapter;
    private List<Facility> facilityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_list);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Initialize Data
        facilityList = new ArrayList<>();
        facilityList.add(new Facility(1, "Sân bóng đá", "Khu thể thao ngoài trời", R.drawable.img_football_field, 22, "06:00", "22:00", true, "Sân bóng đá cỏ nhân tạo đạt chuẩn phục vụ cư dân."));
        facilityList.add(new Facility(2, "Sân bóng chuyền", "Khu thể thao ngoài trời", R.drawable.img_volleyball_court, 12, "06:00", "22:00", true, "Sân cát ngoài trời tiêu chuẩn cho hoạt động thể thao bóng chuyền."));
        facilityList.add(new Facility(3, "Sân cầu lông", "Tầng thượng Tòa S2", R.drawable.img_badminton_court, 4, "06:00", "22:00", true, "Sân cầu lông trong nhà với hệ thống ánh sáng chống chói."));

        rvFacilities = findViewById(R.id.rvFacilities);
        rvFacilities.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new FacilityAdapter(facilityList, facility -> {
            Intent intent = new Intent(FacilityListActivity.this, FacilityDetailActivity.class);
            intent.putExtra("facility", facility);
            startActivity(intent);
        });
        
        rvFacilities.setAdapter(adapter);
    }
}
