package com.example.quanlycudan_utehome.feature.member;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.Resident;
import com.example.quanlycudan_utehome.data.repository.ApartmentRepository;
import com.example.quanlycudan_utehome.data.repository.ResidentRepository;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddMemberActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private ImageView ivMemberAvatar;
    private ImageView ivCameraIcon;
    private EditText etFullName;
    private EditText etGender;
    private EditText etIdNumber;
    private EditText etRelation;
    private EditText etDateOfBirth;
    private EditText etPhoneNumber;
    private Button btnSaveMember;

    private ResidentRepository residentRepository;
    private ApartmentRepository apartmentRepository;
    private int apartmentId;

    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_member);

        // Get apartment ID from intent
        apartmentId = getIntent().getIntExtra("apartmentId", -1);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        // Initialize views
        initializeViews();
        initializeGenderDropdown();

        // Initialize repositories
        residentRepository = new ResidentRepository(this);
        apartmentRepository = new ApartmentRepository(this);

        // Back button
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        // Avatar click listener
        ivMemberAvatar.setOnClickListener(v -> showImagePickerDialog());
        ivCameraIcon.setOnClickListener(v -> showImagePickerDialog());

        // Date of birth click listener
        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Save button
        btnSaveMember.setOnClickListener(v -> saveMember());
    }

    private void initializeViews() {
        ivMemberAvatar = findViewById(R.id.ivMemberAvatar);
        ivCameraIcon = findViewById(R.id.ivCameraIcon);
        etFullName = findViewById(R.id.etFullName);
        etGender = findViewById(R.id.etGender);
        etIdNumber = findViewById(R.id.etIdNumber);
        etRelation = findViewById(R.id.etRelation);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSaveMember = findViewById(R.id.btnSaveMember);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeGenderDropdown() {
        etGender.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                showGenderDialog();
            }
            return true;
        });
    }

    private void showImagePickerDialog() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String dateString = String.format(Locale.US, "%02d/%02d/%04d",
                            selectedMonth + 1, selectedDay, selectedYear);
                    etDateOfBirth.setText(dateString);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showGenderDialog() {
        String[] genderOptions = getResources().getStringArray(R.array.gender_options);
        new android.app.AlertDialog.Builder(this)
                .setTitle("Chọn giới tính")
                .setItems(genderOptions, (dialog, which) -> {
                    etGender.setText(genderOptions[which]);
                })
                .show();
    }

    private void saveMember() {
        // Validate inputs
        String fullName = etFullName.getText().toString().trim();
        String gender = etGender.getText().toString();
        String idNumber = etIdNumber.getText().toString().trim();
        String relation = etRelation.getText().toString().trim();
        String dateOfBirthStr = etDateOfBirth.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        // Validate required fields
        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(relation)) {
            Toast.makeText(this, "Vui lòng nhập quan hệ với chủ hộ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Resident object
        Resident resident = new Resident();
        resident.fullName = fullName;
        resident.gender = gender;
        resident.indentificationType = "";
        resident.indentificationNumber = idNumber;
        resident.phoneNumber = phoneNumber;

        // Parse date of birth
        if (!TextUtils.isEmpty(dateOfBirthStr)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Date date = sdf.parse(dateOfBirthStr);
                resident.dateOfBirth = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                resident.dateOfBirth = 0;
            }
        } else {
            resident.dateOfBirth = 0;
        }

        // Save resident and apartment member on background thread
        new Thread(() -> {
            try {
                // Insert resident and get ID
                long residentId = residentRepository.insertResidentSync(resident);

                // Create apartment member with the new resident ID
                ApartmentMember apartmentMember = new ApartmentMember();
                apartmentMember.apartmentId = apartmentId;
                apartmentMember.residentId = (int) residentId;
                apartmentMember.role = relation;
                apartmentMember.residentType = "Thành viên";

                // Save apartment member immediately (synchronous)
                new ApartmentRepository(AddMemberActivity.this).insertApartmentMemberSync(apartmentMember);

                runOnUiThread(() -> {
                    Toast.makeText(AddMemberActivity.this, "Thêm thành viên thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(AddMemberActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ivMemberAvatar.setImageBitmap(bitmap);
                    selectedImageUri = imageUri;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
