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
        adapter.setOnDeleteListener(this::showDeleteConfirmDialog);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabAddApartment).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AddApartmentActivity.class));
        });

        loadApartments();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void showDeleteConfirmDialog(ApartmentWithOwner apartment) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Xóa căn hộ");
        builder.setMessage("Bạn có chắc muốn xóa căn hộ " + apartment.apartment.apartmentCode + "? Tất cả dữ liệu liên quan sẽ bị xóa.");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            deleteApartment(apartment);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void deleteApartment(ApartmentWithOwner apartment) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // Delete all apartment members
            db.apartmentMemberDao().deleteByApartmentId(apartment.apartment.id);

            // Delete all vehicles of this apartment
            db.vehicleDao().deleteVehiclesByApartmentId(apartment.apartment.id);

            // Delete all invoices of this apartment
            db.paymentDao().deleteInvoicesByApartmentId(apartment.apartment.id);

            // Delete the apartment itself
            db.apartmentDao().delete(apartment.apartment);

            runOnUiThread(() -> {
                android.widget.Toast.makeText(this, "Xóa căn hộ thành công!", android.widget.Toast.LENGTH_SHORT).show();
                loadApartments();
            });
        });
    }
}
