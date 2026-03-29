package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.widget.TextView;
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
import com.example.quanlycudan_utehome.data.entity.Resident;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.Vehicle;
import com.example.quanlycudan_utehome.data.entity.Invoice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApartmentDetailActivity extends AppCompatActivity {

    private int apartmentId = -1;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private MemberDetailAdapter memberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apartment_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnAddMember).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, ResidentPickerActivity.class);
            memberPickerLauncher.launch(intent);
        });

        findViewById(R.id.btnEdit).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, EditApartmentActivity.class);
            intent.putExtra("apartment_id", apartmentId);
            startActivity(intent);
        });

        RecyclerView rvMembers = findViewById(R.id.rvMembers);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        memberAdapter = new MemberDetailAdapter();
        rvMembers.setAdapter(memberAdapter);

        apartmentId = getIntent().getIntExtra("apartment_id", -1);
        if (apartmentId != -1) {
            loadApartmentDetails();
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu căn hộ", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> memberPickerLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    int residentId = result.getData().getIntExtra("resident_id", -1);
                    String name = result.getData().getStringExtra("resident_name");
                    if (residentId != -1) {
                        showAddMemberConfirmDialog(residentId, name);
                    }
                }
            }
    );

    private void showAddMemberConfirmDialog(int residentId, String name) {
        String[] roles = {"Vợ/Chồng", "Con cái", "Bố mẹ", "Người thân", "Bạn bè", "Khác"};
        String[] types = {"Thường trú", "Tạm trú"};
        
        final String[] selectedRole = {roles[0]};
        final String[] selectedType = {types[0]};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Thêm thành viên: " + name);

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        android.widget.TextView tvRole = new android.widget.TextView(this);
        tvRole.setText("Quan hệ với chủ hộ:");
        layout.addView(tvRole);

        android.widget.Spinner spinnerRole = new android.widget.Spinner(this);
        spinnerRole.setAdapter(new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles));
        layout.addView(spinnerRole);

        android.widget.TextView tvType = new android.widget.TextView(this);
        tvType.setText("\nHình thức cư trú:");
        layout.addView(tvType);

        android.widget.Spinner spinnerType = new android.widget.Spinner(this);
        spinnerType.setAdapter(new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types));
        layout.addView(spinnerType);

        builder.setView(layout);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String role = (String) spinnerRole.getSelectedItem();
            String type = (String) spinnerType.getSelectedItem();
            saveMember(residentId, role, type);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void saveMember(int residentId, String role, String type) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            // Check for duplicates
            boolean exists = false;
            List<ApartmentMember> currentMembers = db.apartmentMemberDao().getMembers(apartmentId);
            for (ApartmentMember m : currentMembers) {
                if (m.residentId == residentId) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                runOnUiThread(() -> Toast.makeText(this, "Người này đã là thành viên căn hộ", Toast.LENGTH_SHORT).show());
                return;
            }

            ApartmentMember member = new ApartmentMember();
            member.apartmentId = apartmentId;
            member.residentId = residentId;
            member.role = role;
            member.residentType = type;
            db.apartmentMemberDao().insert(member);

            runOnUiThread(() -> {
                Toast.makeText(this, "Đã thêm thành viên mới", Toast.LENGTH_SHORT).show();
                loadApartmentDetails();
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (apartmentId != -1) {
            loadApartmentDetails();
        }
    }

    private void loadApartmentDetails() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Apartment apartment = db.apartmentDao().getApartmentById(apartmentId);
            Resident owner = null;
            if (apartment != null && apartment.accountId > 0) {
                owner = db.residentDao().getResidentByAccountId(apartment.accountId);
            }

            // Load members
            List<ApartmentMember> members = db.apartmentMemberDao().getMembers(apartmentId);
            List<MemberDetailAdapter.ResidentWithRole> memberList = new ArrayList<>();
            List<Integer> residentIds = new ArrayList<>();
            for (ApartmentMember am : members) {
                Resident r = db.residentDao().getResidentById(am.residentId);
                if (r != null) {
                    memberList.add(new MemberDetailAdapter.ResidentWithRole(r, am.role));
                    residentIds.add(am.residentId);
                }
            }

            // Load vehicles count for this apartment
            List<Vehicle> vehicles = db.vehicleDao().getVehiclesByResidentIdsSync(residentIds);
            int vehicleCount = (vehicles != null) ? vehicles.size() : 0;

            // Load unpaid fees
            long totalFee = 0;
            List<Invoice> invoices = db.paymentDao().getAllInvoices();
            for (Invoice inv : invoices) {
                if (inv.apartmentId != null && inv.apartmentId.equals(String.valueOf(apartmentId)) && "UNPAID".equals(inv.status)) {
                    totalFee += inv.totalAmount;
                }
            }

            Resident finalOwner = owner;
            int finalVehicleCount = vehicleCount;
            long finalTotalFee = totalFee;

            runOnUiThread(() -> {
                if (apartment != null) {
                    ((TextView) findViewById(R.id.tvRoomNameBig)).setText("P." + apartment.apartmentCode);
                    ((TextView) findViewById(R.id.tvBuildingDesc)).setText("Tòa " + apartment.buildingCode + " - UTE Home");
                    ((TextView) findViewById(R.id.tvCode)).setText("P." + apartment.apartmentCode);
                    ((TextView) findViewById(R.id.tvFloor)).setText("Tầng " + apartment.floor);
                    ((TextView) findViewById(R.id.tvBuilding)).setText("Tòa " + apartment.buildingCode);
                    ((TextView) findViewById(R.id.tvArea)).setText(apartment.area + " m²");
                    
                    // Update Status Pill
                    TextView tvStatusPill = findViewById(R.id.tvStatusPill);
                    tvStatusPill.setText(apartment.status);
                    if ("Trống".equalsIgnoreCase(apartment.status)) {
                        tvStatusPill.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#8E8E8E")));
                    } else if ("Đang sử dụng".equalsIgnoreCase(apartment.status) || "Đang ở".equalsIgnoreCase(apartment.status)) {
                        tvStatusPill.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF7A50")));
                    } else { // "Bàn giao" or others
                        tvStatusPill.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4A7BFF")));
                    }

                    if (finalOwner != null) {
                        ((TextView) findViewById(R.id.tvOwnerName)).setText(finalOwner.fullName);
                        ((TextView) findViewById(R.id.tvOwnerId)).setText("ID: " + (finalOwner.idNum != null ? finalOwner.idNum : "--"));
                    } else {
                        ((TextView) findViewById(R.id.tvOwnerName)).setText("Chưa có chủ hộ");
                        ((TextView) findViewById(R.id.tvOwnerId)).setText("ID: --");
                    }

                    ((TextView) findViewById(R.id.tvMemberCount)).setText("THÀNH VIÊN (" + String.format("%02d", memberList.size()) + ")");
                    memberAdapter.setMembers(memberList);

                    ((TextView) findViewById(R.id.tvVehicleCount)).setText(String.format("%02d", finalVehicleCount));
                    
                    java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
                    ((TextView) findViewById(R.id.tvUnpaidFee)).setText(nf.format(finalTotalFee));
                }
            });
        });
    }
}
