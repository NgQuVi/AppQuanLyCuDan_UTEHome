package com.example.quanlycudan_utehome.feature.member;

import android.content.Intent;
import android.os.Bundle;
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

public class MemberDetailActivity extends AppCompatActivity {

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

        findViewById(R.id.btnEdit).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditMemberActivity.class);
            int residentId = getIntent().getIntExtra("RESIDENT_ID", -1);
            String memberRole = getIntent().getStringExtra("MEMBER_ROLE");
            intent.putExtra("MEMBER_ID", residentId);
            intent.putExtra("MEMBER_ROLE", memberRole);
            startActivity(intent);
        });

        int residentId = getIntent().getIntExtra("RESIDENT_ID", -1);
        String memberRole = getIntent().getStringExtra("MEMBER_ROLE");

        if (residentId != -1) {
            loadResidentDetail(residentId, memberRole);
        }
    }

    private void loadResidentDetail(int residentId, String memberRole) {
        new Thread(() -> {
            Resident resident = AppDatabase.getInstance(this).residentDao().getResidentById(residentId);

            runOnUiThread(() -> {
                if (resident == null) {
                    return;
                }

                ((TextView) findViewById(R.id.tvMemberName)).setText(safeText(resident.fullName));
                ((TextView) findViewById(R.id.tvMemberRole)).setText(safeText(memberRole));
                ((TextView) findViewById(R.id.tvResidentId)).setText(String.valueOf(resident.id));
                ((TextView) findViewById(R.id.tvBirthDate)).setText(safeText(resident.dob));
                ((TextView) findViewById(R.id.tvGender)).setText(safeText(resident.gender));
                ((TextView) findViewById(R.id.tvDocumentType)).setText("CCCD");
                ((TextView) findViewById(R.id.tvDocumentNumber)).setText(safeText(resident.idNum));
                ((TextView) findViewById(R.id.tvPhoneNumber)).setText(safeText(resident.phone));
            });
        }).start();
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? "---" : value;
    }
}
