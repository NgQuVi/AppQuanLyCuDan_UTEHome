package com.example.quanlycudan_utehome.feature.member;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.concurrent.Executors;

public class MemberDetailActivity extends AppCompatActivity {

    private int memberId;
    private String memberRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_member_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        memberId = getIntent().getIntExtra("MEMBER_ID", -1);
        memberRole = getIntent().getStringExtra("MEMBER_ROLE");

        if (memberId != -1) {
            loadMemberData();
        }

        // Wire Edit button
        Button btnEdit = findViewById(R.id.btnEdit);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditMemberActivity.class);
                intent.putExtra("MEMBER_ID", memberId);
                intent.putExtra("MEMBER_ROLE", memberRole);
                startActivityForResult(intent, 200);
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            // Reload member info after edit
            loadMemberData();
        }
    }

    private void loadMemberData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Resident resident = db.residentDao().getResidentById(memberId);

            if (resident != null) {
                runOnUiThread(() -> displayMemberInfo(resident));
            }
        });
    }

    private void displayMemberInfo(Resident resident) {
        TextView tvMemberName = findViewById(R.id.tvMemberName);
        TextView tvMemberRole = findViewById(R.id.tvMemberRole);
        TextView tvResidentId = findViewById(R.id.tvResidentId);
        TextView tvBirthDate = findViewById(R.id.tvBirthDate);
        TextView tvGender = findViewById(R.id.tvGender);
        TextView tvDocumentType = findViewById(R.id.tvDocumentType);
        TextView tvDocumentNumber = findViewById(R.id.tvDocumentNumber);
        TextView tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        
        // Remove or hide the avatar placeholder as we're switching to letters or no image
        // Or if we need an image view we just leave it with placeholder

        tvMemberName.setText(resident.fullName != null ? resident.fullName : "N/A");
        tvMemberRole.setText(memberRole != null ? memberRole : "Thành viên");
        tvResidentId.setText(resident.residentCode != null ? resident.residentCode : "N/A");
        tvBirthDate.setText(resident.dob != null ? resident.dob : "N/A");
        tvGender.setText(resident.gender != null ? resident.gender : "N/A");
        tvPhoneNumber.setText(resident.phone != null ? resident.phone : "N/A");
        
        // Handling optional ID type and number which might not exist in the basic add flow
        if (resident.idType != null && !resident.idType.isEmpty()) {
            tvDocumentType.setText(resident.idType);
        } else {
            tvDocumentType.setText("Chưa cập nhật");
        }
        
        if (resident.idNum != null && !resident.idNum.isEmpty()) {
            tvDocumentNumber.setText(resident.idNum);
        } else {
            tvDocumentNumber.setText("Chưa cập nhật");
        }
    }
}
