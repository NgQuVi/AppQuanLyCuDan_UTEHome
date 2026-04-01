package com.example.quanlycudan_utehome.feature.facility;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Facility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FacilityListActivity extends AppCompatActivity {

    private RecyclerView rvFacilities;
    private FacilityAdapter adapter;
    private List<Facility> facilityList;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_list);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnHistory).setOnClickListener(v -> {
            Intent intent = new Intent(FacilityListActivity.this, FacilityHistoryActivity.class);
            startActivity(intent);
        });

        facilityList = new ArrayList<>();
        rvFacilities = findViewById(R.id.rvFacilities);
        rvFacilities.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FacilityAdapter(facilityList, facility -> {
            Intent intent = new Intent(FacilityListActivity.this, FacilityDetailActivity.class);
            intent.putExtra("facility", facility);
            startActivity(intent);
        });

        rvFacilities.setAdapter(adapter);

        loadFacilities();
    }

    private void loadFacilities() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Facility> dbFacilities = db.facilityDao().getAllFacilities();

            if (dbFacilities == null || dbFacilities.isEmpty()) {
                // Seed database lần đầu
                db.facilityDao().insertFacility(new Facility(0, "Sân bóng đá", "Khu thể thao ngoài trời", R.drawable.img_football_field, 22, "06:00", "22:00", true, "Sân bóng đá cỏ nhân tạo đạt chuẩn phục vụ cư dân."));
                db.facilityDao().insertFacility(new Facility(0, "Sân bóng chuyền", "Khu thể thao ngoài trời", R.drawable.img_volleyball_court, 12, "06:00", "22:00", true, "Sân cát ngoài trời tiêu chuẩn cho hoạt động thể thao bóng chuyền."));
                db.facilityDao().insertFacility(new Facility(0, "Sân cầu lông", "Tầng thượng Tòa S2", R.drawable.img_badminton_court, 4, "06:00", "22:00", true, "Sân cầu lông trong nhà với hệ thống ánh sáng chống chói."));

                dbFacilities = db.facilityDao().getAllFacilities();
            } else {
                // Cập nhật imageResId theo tên để sửa ảnh bị sai từ database cũ
                for (Facility f : dbFacilities) {
                    int correctResId = getImageResIdByName(f.name);
                    if (correctResId != 0 && f.imageResId != correctResId) {
                        f.imageResId = correctResId;
                        db.facilityDao().updateFacility(f);
                    }
                }
            }

            final List<Facility> fList = dbFacilities;
            runOnUiThread(() -> {
                facilityList.clear();
                facilityList.addAll(fList);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private int getImageResIdByName(String facilityName) {
        if (facilityName == null) return 0;
        switch (facilityName.trim()) {
            case "Sân bóng đá":    return R.drawable.img_football_field;
            case "Sân bóng chuyền": return R.drawable.img_volleyball_court;
            case "Sân cầu lông":   return R.drawable.img_badminton_court;
            default:               return 0;
        }
    }
}
