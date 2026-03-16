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
import com.example.quanlycudan_utehome.feature.member.MemberDetailActivity;

public class ApartmentInfoActivity extends AppCompatActivity {

    private ApartmentRepository apartmentRepository;
    private RecyclerView recyclerViewMembers;
    private ApartmentMemberAdapter memberAdapter;
    private int apartmentId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apartment_info);

        apartmentId = getIntent().getIntExtra("APARTMENT_ID", 1);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        findViewById(R.id.btnAddMember).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMemberActivity.class);
            intent.putExtra("APARTMENT_ID", apartmentId);
            startActivity(intent);
        });

        apartmentRepository = new ApartmentRepository(this);
        DatabaseInitializer.initializeSampleData(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApartmentData();
    }

    private void loadApartmentData() {
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
        TextView tvMainApartmentCode = findViewById(R.id.tvMainApartmentCode);
        TextView tvMainBuilding = findViewById(R.id.tvMainBuilding);

        tvMainApartmentCode.setText(apartment.apartmentCode);
        tvMainBuilding.setText("Toà" + apartment.buildingCode);
    }

    private void displayMembers(java.util.List<ApartmentWithMembers.ApartmentMemberDetail> members) {
        recyclerViewMembers = findViewById(R.id.recyclerViewMembers);
        if (recyclerViewMembers != null) {
            recyclerViewMembers.setLayoutManager(new LinearLayoutManager(this));
            memberAdapter = new ApartmentMemberAdapter(members, detail -> {
                Intent intent = new Intent(this, MemberDetailActivity.class);
                intent.putExtra("RESIDENT_ID", detail.resident.id);
                intent.putExtra("MEMBER_ROLE", detail.apartmentMember.role);
                startActivity(intent);
            });
            recyclerViewMembers.setAdapter(memberAdapter);
        }
    }
}
