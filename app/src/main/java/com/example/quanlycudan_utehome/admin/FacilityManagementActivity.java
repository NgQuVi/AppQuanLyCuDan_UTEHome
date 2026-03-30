package com.example.quanlycudan_utehome.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Facility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FacilityManagementActivity extends AppCompatActivity {

    private RecyclerView rvFacilitiesAdmin;
    private FacilityAdminAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_facility_management);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnAddFacility).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditFacilityActivity.class);
            startActivity(intent);
        });

        rvFacilitiesAdmin = findViewById(R.id.rvFacilitiesAdmin);
        rvFacilitiesAdmin.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FacilityAdminAdapter(
                facility -> {
                    // Edit
                    Intent intent = new Intent(this, AddEditFacilityActivity.class);
                    intent.putExtra("facility", facility);
                    startActivity(intent);
                },
                facility -> confirmDelete(facility)
        );
        rvFacilitiesAdmin.setAdapter(adapter);

        loadFacilities();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFacilities();
    }

    private void loadFacilities() {
        executorService.execute(() -> {
            List<Facility> list = AppDatabase.getInstance(this).facilityDao().getAllFacilities();
            runOnUiThread(() -> adapter.setData(list));
        });
    }

    private void confirmDelete(Facility facility) {
        new AlertDialog.Builder(this)
                .setTitle("Xoá tiện ích")
                .setMessage("Bạn có chắc muốn xoá \"" + facility.name + "\"? Các lịch đặt liên quan có thể bị ảnh hưởng.")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    executorService.execute(() -> {
                        AppDatabase.getInstance(this).facilityDao().deleteFacility(facility);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Đã xoá " + facility.name, Toast.LENGTH_SHORT).show();
                            loadFacilities();
                        });
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // --- Inner Adapter ---
    static class FacilityAdminAdapter extends RecyclerView.Adapter<FacilityAdminAdapter.VH> {

        interface OnEdit { void onEdit(Facility f); }
        interface OnDelete { void onDelete(Facility f); }

        private List<Facility> list = new ArrayList<>();
        private final OnEdit onEdit;
        private final OnDelete onDelete;

        FacilityAdminAdapter(OnEdit onEdit, OnDelete onDelete) {
            this.onEdit = onEdit;
            this.onDelete = onDelete;
        }

        void setData(List<Facility> data) {
            list = data != null ? data : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_facility_admin, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Facility f = list.get(position);
            holder.tvName.setText(f.name);
            holder.tvLocation.setText(f.location);
            holder.tvCapacity.setText("Sức chứa: " + f.capacity + " người • " + f.openTime + " – " + f.CloseTime);
            holder.viewDot.setBackgroundResource(f.isOpen ? R.drawable.bg_dot_green : R.drawable.bg_dot_orange);
            holder.btnEdit.setOnClickListener(v -> onEdit.onEdit(f));
            holder.btnDelete.setOnClickListener(v -> onDelete.onDelete(f));
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvLocation, tvCapacity;
            ImageButton btnEdit, btnDelete;
            View viewDot;

            VH(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvFacilityNameAdmin);
                tvLocation = itemView.findViewById(R.id.tvFacilityLocationAdmin);
                tvCapacity = itemView.findViewById(R.id.tvFacilityCapacity);
                btnEdit = itemView.findViewById(R.id.btnEditFacility);
                btnDelete = itemView.findViewById(R.id.btnDeleteFacility);
                viewDot = itemView.findViewById(R.id.viewStatusDot);
            }
        }
    }
}
