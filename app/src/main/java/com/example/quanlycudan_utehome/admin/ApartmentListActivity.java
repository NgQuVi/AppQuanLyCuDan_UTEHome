package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;

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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApartmentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApartmentAdminAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apartment_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        recyclerView = findViewById(R.id.rvApartments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApartmentAdminAdapter();
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabAddApartment).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AddApartmentActivity.class));
        });

        loadApartments();
    }

    private void loadApartments() {
        executorService.execute(() -> {
            List<ApartmentWithOwner> apartments = AppDatabase.getInstance(this).apartmentDao().getApartmentsWithOwners();
            runOnUiThread(() -> {
                adapter.setApartments(apartments);
            });
        });
    }
}
