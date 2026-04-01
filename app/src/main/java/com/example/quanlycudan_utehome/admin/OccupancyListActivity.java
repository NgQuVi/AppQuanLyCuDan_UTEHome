package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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
import com.example.quanlycudan_utehome.data.entity.ApartmentWithOwner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OccupancyListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OccupancyAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<ApartmentWithOwner> allApartments = new ArrayList<>();
    
    private TextView tabAll, tabOccupied, tabEmpty;
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_occupancy_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        tabAll = findViewById(R.id.tabAll);
        tabOccupied = findViewById(R.id.tabOccupied);
        tabEmpty = findViewById(R.id.tabEmpty);

        tabAll.setOnClickListener(v -> setFilter("All"));
        tabOccupied.setOnClickListener(v -> setFilter("Đang sử dụng"));
        tabEmpty.setOnClickListener(v -> setFilter("Trống"));

        recyclerView = findViewById(R.id.rvApartments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OccupancyAdapter();
        recyclerView.setAdapter(adapter);

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadApartments();
    }

    private void loadApartments() {
        executorService.execute(() -> {
            allApartments = AppDatabase.getInstance(this).apartmentDao().getApartmentsWithOwners();
            runOnUiThread(() -> applyFilter(((EditText) findViewById(R.id.etSearch)).getText().toString()));
        });
    }

    private void setFilter(String filterText) {
        currentFilter = filterText;
        
        tabAll.setBackgroundResource(R.drawable.bg_chip_unselected);
        tabAll.setTextColor(getResources().getColor(R.color.text_secondary, null));
        
        tabOccupied.setBackgroundResource(R.drawable.bg_chip_unselected);
        tabOccupied.setTextColor(getResources().getColor(R.color.text_secondary, null));
        
        tabEmpty.setBackgroundResource(R.drawable.bg_chip_unselected);
        tabEmpty.setTextColor(getResources().getColor(R.color.text_secondary, null));

        if ("All".equals(filterText)) {
            tabAll.setBackgroundResource(R.drawable.bg_chip_selected);
            tabAll.setTextColor(android.graphics.Color.WHITE);
        } else if ("Đang sử dụng".equals(filterText)) {
            tabOccupied.setBackgroundResource(R.drawable.bg_chip_selected);
            tabOccupied.setTextColor(android.graphics.Color.WHITE);
        } else {
            tabEmpty.setBackgroundResource(R.drawable.bg_chip_selected);
            tabEmpty.setTextColor(android.graphics.Color.WHITE);
        }

        applyFilter(((EditText) findViewById(R.id.etSearch)).getText().toString());
    }

    private void applyFilter(String query) {
        List<ApartmentWithOwner> filteredList = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (ApartmentWithOwner item : allApartments) {
            boolean matchesFilter = "All".equals(currentFilter) || currentFilter.equalsIgnoreCase(item.apartment.status);
            boolean matchesSearch = item.apartment.apartmentCode.toLowerCase().contains(lowerQuery) || 
                                    (item.getOwnerName() != null && item.getOwnerName().toLowerCase().contains(lowerQuery));

            if (matchesFilter && matchesSearch) {
                filteredList.add(item);
            }
        }
        adapter.setApartments(filteredList);
    }
}
