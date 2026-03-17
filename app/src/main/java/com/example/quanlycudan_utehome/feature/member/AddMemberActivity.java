package com.example.quanlycudan_utehome.feature.member;

import android.app.DatePickerDialog;
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
import java.util.concurrent.Executors;

public class AddMemberActivity extends AppCompatActivity {

    private EditText etFullName, etIdNumber, etRelationship, etPhone;
    private TextView tvGender, tvDob;
    private FrameLayout avatarContainer, dobContainer;
    private Button btnSave;

    private String selectedGender = "";
    private int apartmentId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_member);

        // Get apartment ID from intent if provided
        apartmentId = getIntent().getIntExtra("APARTMENT_ID", 1);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

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
        avatarContainer = findViewById(R.id.avatarContainer);
        dobContainer   = findViewById(R.id.dobContainer);

        // Gender dropdown
        tvGender.setOnClickListener(v -> showGenderMenu());

        // Date picker
        dobContainer.setOnClickListener(v -> showDatePicker());

        // Save
        btnSave.setOnClickListener(v -> saveMember());
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

    private void saveMember() {
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
        if (relationship.isEmpty()) {
            etRelationship.setError("Vui lòng nhập quan hệ với chủ hộ");
            etRelationship.requestFocus();
            return;
        }

        final String finalDob = dob;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // Create Resident
            Resident resident = new Resident();
            resident.fullName    = fullName;
            resident.idNum       = idNumber;
            resident.gender      = selectedGender;
            resident.phone       = phone.isEmpty() ? null : ("+84" + phone);
            resident.dob         = finalDob;
            resident.residentCode = "RES" + System.currentTimeMillis();

            long residentId = db.residentDao().insert(resident);

            // Create ApartmentMember
            ApartmentMember member = new ApartmentMember();
            member.apartmentId  = apartmentId;
            member.residentId   = (int) residentId;
            member.role         = relationship;
            member.residentType = "Thành viên";

            db.apartmentMemberDao().insert(member);

            runOnUiThread(() -> {
                Toast.makeText(this, "Đã thêm thành viên thành công", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }
}
