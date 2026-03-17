package com.example.quanlycudan_utehome.feature.member;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class EditMemberActivity extends AppCompatActivity {

    private EditText etFullName, etIdNumber, etRelationship, etPhone;
    private TextView tvGender, tvDob, tvTitle;
    private FrameLayout dobContainer;
    private Button btnSave;

    private String selectedGender = "";
    private int memberId;
    private String originalRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_member);

        memberId    = getIntent().getIntExtra("MEMBER_ID", -1);
        originalRole = getIntent().getStringExtra("MEMBER_ROLE");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        // Change title to "Chỉnh sửa thông tin"
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Chỉnh sửa thông tin");

        // Back
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        // Bind views
        etFullName     = findViewById(R.id.etFullName);
        etIdNumber     = findViewById(R.id.etIdNumber);
        etRelationship = findViewById(R.id.etRelationship);
        etPhone        = findViewById(R.id.etPhone);
        tvGender       = findViewById(R.id.tvGender);
        tvDob          = findViewById(R.id.tvDob);
        btnSave        = findViewById(R.id.btnSave);
        dobContainer   = findViewById(R.id.dobContainer);

        // Gender dropdown
        tvGender.setOnClickListener(v -> showGenderMenu());

        // Date picker
        dobContainer.setOnClickListener(v -> showDatePicker());

        // Save / Update
        btnSave.setOnClickListener(v -> saveChanges());

        // Pre-fill existing data
        if (memberId != -1) {
            loadExistingData();
        }
    }

    private void loadExistingData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Resident resident = db.residentDao().getResidentById(memberId);

            if (resident != null) {
                runOnUiThread(() -> {
                    etFullName.setText(resident.fullName != null ? resident.fullName : "");
                    etIdNumber.setText(resident.idNum != null ? resident.idNum : "");
                    
                    // Pre-fill relationship/role
                    if (originalRole != null && !originalRole.isEmpty()) {
                        etRelationship.setText(originalRole);
                    }
                    
                    // Phone: strip the +84 prefix if present
                    if (resident.phone != null) {
                        String phone = resident.phone;
                        if (phone.startsWith("+84")) {
                            phone = phone.substring(3);
                        }
                        etPhone.setText(phone.trim());
                    }
                    
                    // DOB
                    if (resident.dob != null && !resident.dob.isEmpty()) {
                        tvDob.setText(resident.dob);
                        tvDob.setTextColor(getColor(R.color.home_text_primary));
                    }
                    
                    // Gender
                    if (resident.gender != null && !resident.gender.isEmpty()) {
                        selectedGender = resident.gender;
                        tvGender.setText(resident.gender);
                        tvGender.setTextColor(getColor(R.color.home_text_primary));
                    }
                });
            }
        });
    }

    private void showGenderMenu() {
        PopupMenu popup = new PopupMenu(this, tvGender);
        popup.getMenu().add("Nam");
        popup.getMenu().add("Nữ");
        popup.setOnMenuItemClickListener(item -> {
            selectedGender = item.getTitle().toString();
            tvGender.setText(selectedGender);
            tvGender.setTextColor(getColor(R.color.home_text_primary));
            return true;
        });
        popup.show();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year  = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day   = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String date = String.format("%02d/%02d/%d", d, m + 1, y);
            tvDob.setText(date);
            tvDob.setTextColor(getColor(R.color.home_text_primary));
        }, year, month, day);

        dialog.show();
    }

    private void saveChanges() {
        String fullName     = etFullName.getText().toString().trim();
        String idNumber     = etIdNumber.getText().toString().trim();
        String relationship = etRelationship.getText().toString().trim();
        String phone        = etPhone.getText().toString().trim();
        String dob          = tvDob.getText().toString();
        if (dob.equals("mm/dd/yyyy")) dob = "";

        // Validation
        if (fullName.isEmpty()) {
            etFullName.setError("Vui lòng nhập họ và tên");
            etFullName.requestFocus();
            return;
        }

        final String finalDob = dob;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Resident resident = db.residentDao().getResidentById(memberId);

            if (resident != null) {
                resident.fullName = fullName;
                resident.idNum    = idNumber;
                resident.gender   = selectedGender;
                resident.phone    = phone.isEmpty() ? null : ("+84" + phone);
                resident.dob      = finalDob;

                db.residentDao().update(resident);

                // Update the role in ApartmentMember if changed
                if (!relationship.isEmpty()) {
                    List<ApartmentMember> memberList = db.apartmentMemberDao().getAllMembers();
                    for (ApartmentMember m : memberList) {
                        if (m.residentId == memberId) {
                            m.role = relationship;
                            db.apartmentMemberDao().update(m);
                            break;
                        }
                    }
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã cập nhật thông tin thành viên", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
        });
    }
}
