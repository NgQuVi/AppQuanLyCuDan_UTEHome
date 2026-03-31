package com.example.quanlycudan_utehome.feature.accesscard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
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
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.ApartmentWithMembers;
import com.example.quanlycudan_utehome.data.entity.Vehicle;
import com.example.quanlycudan_utehome.data.repository.ApartmentRepository;
import com.example.quanlycudan_utehome.feature.apartment.ApartmentMemberAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class AccessCardMemberListActivity extends AppCompatActivity {

    private int apartmentId = 1;
    private String apartmentDisplayLabel = "";
    private ApartmentRepository apartmentRepository;
    private Map<Integer, List<Vehicle>> vehiclesByResident = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_access_card_member_list);

        apartmentId = getIntent().getIntExtra("APARTMENT_ID", 1);
        // in ra log để debug
        android.util.Log.d("AccessCardMemberList", "Received APARTMENT_ID: " + apartmentId);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        apartmentRepository = new ApartmentRepository(this);

        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            ApartmentWithMembers awm = apartmentRepository.getApartmentWithMembers(apartmentId);

            if (awm != null && awm.members != null) {
                // Lưu label căn hộ
                if (awm.apartment != null) {
                    apartmentDisplayLabel = awm.apartment.apartmentCode + ", Tòa " + awm.apartment.buildingCode;
                }

                // Lấy danh sách residentId để truy vấn xe
                Set<Integer> residentIds = new HashSet<>();
                for (ApartmentWithMembers.ApartmentMemberDetail d : awm.members) {
                    if (d.resident != null) residentIds.add(d.resident.id);
                }

                vehiclesByResident.clear();
                if (!residentIds.isEmpty()) {
                    List<Integer> idList = new ArrayList<>(residentIds);
                    // Dùng query đồng bộ để lấy dữ liệu trực tiếp trên background thread
                    // (LiveData.getValue() luôn trả null nếu gọi trên background thread)
                    List<Vehicle> vehicles = AppDatabase.getInstance(this)
                            .vehicleDao()
                            .getVehiclesByResidentIdsSync(idList);
                    if (vehicles != null) {
                        for (Vehicle v : vehicles) {
                            if (v.apartmentId == apartmentId) {
                                List<Vehicle> list = vehiclesByResident.get(v.residentId);
                                if (list == null) {
                                    list = new ArrayList<>();
                                    vehiclesByResident.put(v.residentId, list);
                                }
                                list.add(v);
                            }
                        }
                    }
                }
            }

            runOnUiThread(() -> {
                if (awm != null) {
                    bindHeader(awm.apartment);
                    bindMemberList(awm.members);
                }
            });
        }).start();
    }

    private void bindHeader(Apartment apartment) {
        if (apartment == null) return;
        TextView tvApartmentName = findViewById(R.id.tvApartmentName);
        if (tvApartmentName != null) {
            tvApartmentName.setText(apartment.apartmentCode + ", Tòa " + apartment.buildingCode);
        }
    }

    private void bindMemberList(List<ApartmentWithMembers.ApartmentMemberDetail> members) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMembers);
        if (recyclerView == null) return;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApartmentMemberAdapter adapter = new ApartmentMemberAdapter(members, detail -> {
            String residentName = detail.resident != null && detail.resident.fullName != null
                    ? detail.resident.fullName
                    : "Không xác định";

            // Lấy vehicle đầu tiên của cư dân trong căn hộ hiện tại (nếu có)
            Vehicle selectedVehicle = null;
            if (detail.resident != null) {
                List<Vehicle> list = vehiclesByResident.get(detail.resident.id);
                if (list != null && !list.isEmpty()) {
                    selectedVehicle = list.get(0);
                }
            }

            // Biển số: luôn ưu tiên dữ liệu thật, nếu không có thì hiển thị '---'
            String plateNumber = (selectedVehicle != null && selectedVehicle.licensePlate != null
                    && !selectedVehicle.licensePlate.trim().isEmpty())
                    ? selectedVehicle.licensePlate
                    : "---";

            // Loại xe + hãng: từ vehicleType + brand, nếu thiếu thì '---'
            String typePart = selectedVehicle != null && selectedVehicle.vehicleType != null
                    ? selectedVehicle.vehicleType.trim()
                    : "";
            String brandPart = selectedVehicle != null && selectedVehicle.brand != null
                    ? selectedVehicle.brand.trim()
                    : "";
            String vehicleInfo;
            if (!typePart.isEmpty() && !brandPart.isEmpty()) {
                vehicleInfo = typePart + " • " + brandPart;
            } else if (!typePart.isEmpty()) {
                vehicleInfo = typePart;
            } else if (!brandPart.isEmpty()) {
                vehicleInfo = brandPart;
            } else {
                vehicleInfo = "---";
            }

            // Màu xe: lấy từ color, nếu thiếu thì '---'
            String vehicleColor = (selectedVehicle != null && selectedVehicle.color != null
                    && !selectedVehicle.color.trim().isEmpty())
                    ? selectedVehicle.color
                    : "---";

            Intent intent = new Intent(this, AccessCardActivity.class);
            intent.putExtra(AccessCardActivity.EXTRA_RESIDENT_NAME, residentName);
            intent.putExtra(AccessCardActivity.EXTRA_APARTMENT_LABEL,
                    apartmentDisplayLabel.isEmpty() ? "P.1205, Tòa S1" : apartmentDisplayLabel);
            intent.putExtra(AccessCardActivity.EXTRA_VEHICLE_COLOR, vehicleColor);
            intent.putExtra(AccessCardActivity.EXTRA_EXPIRED_DATE, "31/12/2024"); // chưa có hạn dùng trong Vehicle
            intent.putExtra(AccessCardActivity.EXTRA_PLATE_NUMBER, plateNumber);
            intent.putExtra(AccessCardActivity.EXTRA_VEHICLE_INFO, vehicleInfo);

            String qrContent = "RESIDENT_ID=" + (detail.resident != null ? detail.resident.id : -1)
                    + ";APARTMENT_ID=" + apartmentId
                    + ";LICENSE=" + plateNumber
                    + ";TYPE=" + vehicleInfo
                    + ";COLOR=" + vehicleColor;
            intent.putExtra(AccessCardActivity.EXTRA_QR_CONTENT, qrContent);

            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }
}
