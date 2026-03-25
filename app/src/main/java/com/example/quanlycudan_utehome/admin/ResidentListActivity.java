package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.ResidentWithApartment;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResidentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ResidentAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resident_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        recyclerView = findViewById(R.id.recyclerViewResidents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResidentAdapter();
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabAddResident).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AddResidentActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadResidents();
    }

    private void loadResidents() {
        executorService.execute(() -> {
            List<ResidentWithApartment> residents = AppDatabase.getInstance(this).residentDao().getResidentsWithApartments();
            runOnUiThread(() -> {
                adapter.setResidents(residents);
                // Update total count in header
                TextView tvCount = findViewById(R.id.listHeader).findViewById(android.R.id.text1); // wait, it's not text1
                // Let's find it by index or text since it has no ID
                android.widget.LinearLayout listHeader = findViewById(R.id.listHeader);
                if (listHeader.getChildCount() > 0 && listHeader.getChildAt(0) instanceof TextView) {
                    ((TextView) listHeader.getChildAt(0)).setText("Hiển thị " + residents.size() + " cư dân");
                }
            });
        });
    }
}
