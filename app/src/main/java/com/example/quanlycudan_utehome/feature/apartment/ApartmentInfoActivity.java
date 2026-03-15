package com.example.quanlycudan_utehome.feature.apartment;

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
import com.example.quanlycudan_utehome.data.database.DatabaseInitializer;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.ApartmentWithMembers;
import com.example.quanlycudan_utehome.data.repository.ApartmentRepository;
import com.example.quanlycudan_utehome.feature.member.AddMemberActivity;

public class ApartmentInfoActivity extends AppCompatActivity {

    private ApartmentRepository apartmentRepository;
    private RecyclerView recyclerViewMembers;
    private ApartmentMemberAdapter memberAdapter;
    private int currentApartmentId = 1; // Default apartment ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apartment_info);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        // Add member button
        TextView btnAddMember = findViewById(R.id.btnAddMember);
        if (btnAddMember != null) {
            btnAddMember.setOnClickListener(v -> openAddMemberActivity());
        }

        // Khởi tạo repository
        apartmentRepository = new ApartmentRepository(this);

        // Khởi tạo dữ liệu mẫu
        DatabaseInitializer.initializeSampleData(this);

        // Chờ một chút để dữ liệu được thêm vào database
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Chờ 1 giây
                loadApartmentData();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void openAddMemberActivity() {
        Intent intent = new Intent(this, AddMemberActivity.class);
        intent.putExtra("apartmentId", currentApartmentId);
        startActivity(intent);
    }

    private void loadApartmentData() {
        // Lấy dữ liệu căn hộ đầu tiên (ID = 1)
        int apartmentId = 1;
        currentApartmentId = apartmentId;

        new Thread(() -> {
            ApartmentWithMembers apartmentWithMembers = apartmentRepository.getApartmentWithMembers(apartmentId);

            runOnUiThread(() -> {
                if (apartmentWithMembers != null && apartmentWithMembers.apartment != null) {
                    displayApartmentInfo(apartmentWithMembers.apartment);
                    displayMembers(apartmentWithMembers.members);
                }
            });
        }).start();
    }

    private void displayApartmentInfo(Apartment apartment) {
        // Cập nhật thông tin căn hộ chính
        TextView tvMainApartmentCode = findViewById(R.id.tvMainApartmentCode);
        TextView tvMainBuilding = findViewById(R.id.tvMainBuilding);

        tvMainApartmentCode.setText(apartment.apartmentCode);
        tvMainBuilding.setText("Tòa " + apartment.buildingCode);

        // Cập nhật thông tin chi tiết căn hộ từ database
        // Những giá trị này được cập nhật động từ data layer
    }

    private void displayMembers(java.util.List<ApartmentWithMembers.ApartmentMemberDetail> members) {
        recyclerViewMembers = findViewById(R.id.recyclerViewMembers);
        if (recyclerViewMembers != null) {
            recyclerViewMembers.setLayoutManager(new LinearLayoutManager(this));
            memberAdapter = new ApartmentMemberAdapter(members);
            recyclerViewMembers.setAdapter(memberAdapter);
        }
    }
}
