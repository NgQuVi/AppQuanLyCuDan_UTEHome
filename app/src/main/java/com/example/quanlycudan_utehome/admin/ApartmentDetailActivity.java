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

    private Apartment currentApartment;
    private List<com.example.quanlycudan_utehome.data.entity.Resident> allResidents = new java.util.ArrayList<>();

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

        // Add click listener for editing floor and building
        findViewById(R.id.tvFloor).setOnClickListener(v -> showEditFloorDialog());
        findViewById(R.id.tvBuilding).setOnClickListener(v -> showEditBuildingDialog());

        // Add click listener for changing owner
        try {
            findViewById(R.id.btnChangeOwner).setOnClickListener(v -> showChangeOwnerDialog());
        } catch (Exception e) {
            // View might not exist, skip
        }

        findViewById(R.id.btnAddMember).setOnClickListener(v -> {
            showAddNewMemberDialog();
        });

        findViewById(R.id.btnEdit).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, EditApartmentActivity.class);
            intent.putExtra("apartment_id", apartmentId);
            startActivity(intent);
        });

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            showDeleteConfirmDialog();
        });

        RecyclerView rvMembers = findViewById(R.id.rvMembers);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        memberAdapter = new MemberDetailAdapter((member, position) -> {
            // Edit member
            showEditMemberDialog(member.resident, member.role, position);
        }, (member, position) -> {
            // Delete member
            deleteMember(member.resident.id, position);
        });
        rvMembers.setAdapter(memberAdapter);

        apartmentId = getIntent().getIntExtra("apartment_id", -1);
        if (apartmentId != -1) {
            loadApartmentDetails();
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu căn hộ", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // ...existing code...

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
            currentApartment = apartment;

            Resident owner = null;
            if (apartment != null && apartment.accountId > 0) {
                owner = db.residentDao().getResidentByAccountId(apartment.accountId);
            }

            // Load members
            List<ApartmentMember> members = db.apartmentMemberDao().getMembers(apartmentId);
            List<MemberDetailAdapter.ResidentWithRole> memberList = new ArrayList<>();
            for (ApartmentMember am : members) {
                Resident r = db.residentDao().getResidentById(am.residentId);
                if (r != null) {
                    memberList.add(new MemberDetailAdapter.ResidentWithRole(r, am.role));
                }
            }

            // Load all residents for owner change dialog
            allResidents = db.residentDao().getAllResidentsWithAccount();

            Resident finalOwner = owner;

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
                    } else if ("Đang sử dụng".equalsIgnoreCase(apartment.status)) {
                        tvStatusPill.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF7A50")));
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
                }
            });
        });
    }

    private void showEditBuildingDialog() {
        if (currentApartment == null) return;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Sửa Tòa nhà");

        android.widget.EditText etBuilding = new android.widget.EditText(this);
        etBuilding.setText(currentApartment.buildingCode);
        etBuilding.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        etBuilding.setSingleLine(true);

        builder.setView(etBuilding);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newBuilding = etBuilding.getText().toString().trim();
            if (!newBuilding.isEmpty()) {
                updateApartmentBuilding(newBuilding);
            } else {
                Toast.makeText(this, "Tòa nhà không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void updateApartmentBuilding(String buildingCode) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            currentApartment.buildingCode = buildingCode;
            db.apartmentDao().update(currentApartment);
            runOnUiThread(() -> {
                Toast.makeText(this, "Cập nhật tòa nhà thành công!", Toast.LENGTH_SHORT).show();
                loadApartmentDetails();
            });
        });
    }

    private void showEditFloorDialog() {
        if (currentApartment == null) return;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Sửa Tầng");

        android.widget.EditText etFloor = new android.widget.EditText(this);
        etFloor.setText(String.valueOf(currentApartment.floor));
        etFloor.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etFloor.setSingleLine(true);

        builder.setView(etFloor);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String floorStr = etFloor.getText().toString().trim();
            if (!floorStr.isEmpty()) {
                try {
                    int floor = Integer.parseInt(floorStr);
                    if (floor > 0) {
                        updateApartmentFloor(floor);
                    } else {
                        Toast.makeText(this, "Tầng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Tầng phải là số", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Tầng không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void updateApartmentFloor(int floor) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            currentApartment.floor = floor;
            db.apartmentDao().update(currentApartment);
            runOnUiThread(() -> {
                Toast.makeText(this, "Cập nhật tầng thành công!", Toast.LENGTH_SHORT).show();
                loadApartmentDetails();
            });
        });
    }

    private void showChangeOwnerDialog() {
        if (allResidents.isEmpty()) {
            Toast.makeText(this, "Không có cư dân nào để chọn", Toast.LENGTH_SHORT).show();
            return;
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chuyển đổi chủ hộ");

        android.widget.LinearLayout dialogLayout = new android.widget.LinearLayout(this);
        dialogLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
        dialogLayout.setPadding(20, 20, 20, 20);

        // Search EditText
        android.widget.EditText etSearch = new android.widget.EditText(this);
        etSearch.setHint("Tìm kiếm theo tên...");
        etSearch.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        dialogLayout.addView(etSearch);

        // RecyclerView for residents
        RecyclerView rvResidents = new RecyclerView(this);
        rvResidents.setLayoutManager(new LinearLayoutManager(this));
        rvResidents.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                500
        ));

        ResidentPickerAdapter adapter = new ResidentPickerAdapter(allResidents, resident -> {
            changeOwner(resident);
            android.app.AlertDialog dialog = (android.app.AlertDialog) etSearch.getTag();
            if (dialog != null) dialog.dismiss();
        });
        rvResidents.setAdapter(adapter);

        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    adapter.setResidents(allResidents);
                } else {
                    List<com.example.quanlycudan_utehome.data.entity.Resident> filtered = new java.util.ArrayList<>();
                    for (com.example.quanlycudan_utehome.data.entity.Resident r : allResidents) {
                        if (r.fullName.toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(r);
                        }
                    }
                    adapter.setResidents(filtered);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        dialogLayout.addView(rvResidents);

        builder.setView(dialogLayout);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("Xóa chủ hộ", (dialog, which) -> {
            removeOwner();
        });

        android.app.AlertDialog dialog = builder.create();
        etSearch.setTag(dialog);
        dialog.show();
    }

    private void changeOwner(com.example.quanlycudan_utehome.data.entity.Resident resident) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // Clear old owner relationship
            if (currentApartment.accountId > 0) {
                currentApartment.accountId = 0;
            }

            // Set new owner
            currentApartment.accountId = resident.accountId;
            currentApartment.status = "Đang sử dụng";
            db.apartmentDao().update(currentApartment);

            runOnUiThread(() -> {
                Toast.makeText(this, "Chuyển đổi chủ hộ thành công!", Toast.LENGTH_SHORT).show();
                loadApartmentDetails();
            });
        });
    }

    private void removeOwner() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            currentApartment.accountId = 0;
            currentApartment.status = "Trống";
            db.apartmentDao().update(currentApartment);

            runOnUiThread(() -> {
                Toast.makeText(this, "Xóa chủ hộ thành công!", Toast.LENGTH_SHORT).show();
                loadApartmentDetails();
            });
        });
    }

    private void showEditMemberDialog(Resident resident, String currentRole, int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa thành viên");

        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        // Full Name
        android.widget.TextView tvName = new android.widget.TextView(this);
        tvName.setText("Tên:");
        tvName.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        layout.addView(tvName);

        android.widget.EditText etName = new android.widget.EditText(this);
        etName.setText(resident.fullName);
        etName.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layout.addView(etName);

        // Phone
        android.widget.TextView tvPhone = new android.widget.TextView(this);
        tvPhone.setText("Số điện thoại:");
        tvPhone.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        tvPhone.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((android.widget.LinearLayout.LayoutParams) tvPhone.getLayoutParams()).setMargins(0, 16, 0, 0);
        layout.addView(tvPhone);

        android.widget.EditText etPhone = new android.widget.EditText(this);
        etPhone.setText(resident.phone != null ? resident.phone : "");
        layout.addView(etPhone);

        // Email
        android.widget.TextView tvEmail = new android.widget.TextView(this);
        tvEmail.setText("Email:");
        tvEmail.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        tvEmail.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((android.widget.LinearLayout.LayoutParams) tvEmail.getLayoutParams()).setMargins(0, 16, 0, 0);
        layout.addView(tvEmail);

        android.widget.EditText etEmail = new android.widget.EditText(this);
        etEmail.setText(resident.email != null ? resident.email : "");
        layout.addView(etEmail);

        // Date of Birth
        android.widget.TextView tvDOB = new android.widget.TextView(this);
        tvDOB.setText("Ngày sinh (DD/MM/YYYY):");
        tvDOB.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        tvDOB.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((android.widget.LinearLayout.LayoutParams) tvDOB.getLayoutParams()).setMargins(0, 16, 0, 0);
        layout.addView(tvDOB);

        android.widget.EditText etDOB = new android.widget.EditText(this);
        etDOB.setText(resident.dob != null ? resident.dob : "");
        layout.addView(etDOB);

        // Gender
        android.widget.TextView tvGender = new android.widget.TextView(this);
        tvGender.setText("Giới tính:");
        tvGender.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        tvGender.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((android.widget.LinearLayout.LayoutParams) tvGender.getLayoutParams()).setMargins(0, 16, 0, 0);
        layout.addView(tvGender);

        android.widget.EditText etGender = new android.widget.EditText(this);
        etGender.setText(resident.gender != null ? resident.gender : "");
        etGender.setHint("VD: Nam, Nữ, Khác");
        layout.addView(etGender);

        // CCCD
        android.widget.TextView tvCCCD = new android.widget.TextView(this);
        tvCCCD.setText("CCCD/Hộ chiếu:");
        tvCCCD.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        tvCCCD.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((android.widget.LinearLayout.LayoutParams) tvCCCD.getLayoutParams()).setMargins(0, 16, 0, 0);
        layout.addView(tvCCCD);

        android.widget.EditText etCCCD = new android.widget.EditText(this);
        etCCCD.setText(resident.idNum != null ? resident.idNum : "");
        layout.addView(etCCCD);

        // Role
        android.widget.TextView tvRole = new android.widget.TextView(this);
        tvRole.setText("Vai trò:");
        tvRole.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        tvRole.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((android.widget.LinearLayout.LayoutParams) tvRole.getLayoutParams()).setMargins(0, 16, 0, 0);
        layout.addView(tvRole);

        android.widget.EditText etRole = new android.widget.EditText(this);
        etRole.setText(currentRole);
        layout.addView(etRole);

        // Bottom padding
        android.view.View spacer = new android.view.View(this);
        spacer.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                40
        ));
        layout.addView(spacer);

        scrollView.addView(layout);
        builder.setView(scrollView);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = etName.getText().toString().trim();
            String newPhone = etPhone.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            String newDOB = etDOB.getText().toString().trim();
            String newGender = etGender.getText().toString().trim();
            String newCCCD = etCCCD.getText().toString().trim();
            String newRole = etRole.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            updateMemberInfo(resident.id, newName, newPhone, newEmail, newDOB, newGender, newCCCD, newRole);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void updateMemberInfo(int residentId, String name, String phone, String email, String dob, String gender, String cccd, String role) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // Update resident
            Resident resident = db.residentDao().getResidentById(residentId);
            if (resident != null) {
                resident.fullName = name;
                resident.phone = phone;
                resident.email = email;
                resident.dob = dob;
                resident.gender = gender;
                resident.idNum = cccd;
                db.residentDao().update(resident);
            }

            // Update member role
            List<ApartmentMember> members = db.apartmentMemberDao().getMembers(apartmentId);
            for (ApartmentMember member : members) {
                if (member.residentId == residentId) {
                    member.role = role;
                    db.apartmentMemberDao().update(member);
                    break;
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Cập nhật thành viên thành công!", Toast.LENGTH_SHORT).show();
                loadApartmentDetails();
            });
        });
    }

    private void deleteMember(int residentId, int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Xóa thành viên");
        builder.setMessage("Bạn có chắc muốn xóa thành viên này khỏi căn hộ?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            executorService.execute(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                db.apartmentMemberDao().deleteByResidentId(residentId);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Xóa thành viên thành công!", Toast.LENGTH_SHORT).show();
                    loadApartmentDetails();
                });
            });
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showAddNewMemberDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Thêm thành viên mới");

        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        // Full Name
        android.widget.TextView tvName = new android.widget.TextView(this);
        tvName.setText("Tên:");
        tvName.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        layout.addView(tvName);

        android.widget.EditText etName = new android.widget.EditText(this);
        etName.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layout.addView(etName);

        // Phone
        android.widget.TextView tvPhone = new android.widget.TextView(this);
        tvPhone.setText("Số điện thoại:");
        tvPhone.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        tvPhone.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((android.widget.LinearLayout.LayoutParams) tvPhone.getLayoutParams()).setMargins(0, 16, 0, 0);
        layout.addView(tvPhone);

        android.widget.EditText etPhone = new android.widget.EditText(this);
        layout.addView(etPhone);

        // Email
        android.widget.TextView tvEmail = new android.widget.TextView(this);
        tvEmail.setText("Email:");
        tvEmail.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        tvEmail.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((android.widget.LinearLayout.LayoutParams) tvEmail.getLayoutParams()).setMargins(0, 16, 0, 0);
        layout.addView(tvEmail);

        android.widget.EditText etEmail = new android.widget.EditText(this);
        layout.addView(etEmail);

        // Date of Birth
        android.widget.TextView tvDOB = new android.widget.TextView(this);
        tvDOB.setText("Ngày sinh (DD/MM/YYYY):");
        tvDOB.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        tvDOB.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((android.widget.LinearLayout.LayoutParams) tvDOB.getLayoutParams()).setMargins(0, 16, 0, 0);
        layout.addView(tvDOB);

        android.widget.EditText etDOB = new android.widget.EditText(this);
        layout.addView(etDOB);

        // Gender
        android.widget.TextView tvGender = new android.widget.TextView(this);
        tvGender.setText("Giới tính:");
        tvGender.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        tvGender.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((android.widget.LinearLayout.LayoutParams) tvGender.getLayoutParams()).setMargins(0, 16, 0, 0);
        layout.addView(tvGender);

        android.widget.EditText etGender = new android.widget.EditText(this);
        etGender.setHint("VD: Nam, Nữ, Khác");
        layout.addView(etGender);

        // CCCD
        android.widget.TextView tvCCCD = new android.widget.TextView(this);
        tvCCCD.setText("CCCD/Hộ chiếu:");
        tvCCCD.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        tvCCCD.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((android.widget.LinearLayout.LayoutParams) tvCCCD.getLayoutParams()).setMargins(0, 16, 0, 0);
        layout.addView(tvCCCD);

        android.widget.EditText etCCCD = new android.widget.EditText(this);
        layout.addView(etCCCD);

        // Role
        android.widget.TextView tvRole = new android.widget.TextView(this);
        tvRole.setText("Vai trò:");
        tvRole.setTextColor(android.graphics.Color.parseColor("#8E8E8E"));
        tvRole.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ((android.widget.LinearLayout.LayoutParams) tvRole.getLayoutParams()).setMargins(0, 16, 0, 0);
        layout.addView(tvRole);

        android.widget.EditText etRole = new android.widget.EditText(this);
        layout.addView(etRole);

        // Bottom padding
        android.view.View spacer = new android.view.View(this);
        spacer.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                40
        ));
        layout.addView(spacer);

        scrollView.addView(layout);
        builder.setView(scrollView);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String dob = etDOB.getText().toString().trim();
            String gender = etGender.getText().toString().trim();
            String cccd = etCCCD.getText().toString().trim();
            String role = etRole.getText().toString().trim();

            if (name.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Tên và vai trò không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            addNewMember(name, phone, email, dob, gender, cccd, role);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void addNewMember(String name, String phone, String email, String dob, String gender, String cccd, String role) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // Create new resident (không có account)
            Resident newResident = new Resident();
            newResident.fullName = name;
            newResident.phone = phone;
            newResident.email = email;
            newResident.dob = dob;
            newResident.gender = gender;
            newResident.idNum = cccd;
            newResident.accountId = 0; // Không có account

            long residentId = db.residentDao().insert(newResident);

            // Add as apartment member
            ApartmentMember member = new ApartmentMember();
            member.apartmentId = apartmentId;
            member.residentId = (int) residentId;
            member.role = role;
            member.residentType = "Thường trú";
            db.apartmentMemberDao().insert(member);

            runOnUiThread(() -> {
                Toast.makeText(this, "Thêm thành viên mới thành công!", Toast.LENGTH_SHORT).show();
                loadApartmentDetails();
            });
        });
    }

    private void showDeleteConfirmDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Xóa căn hộ");
        builder.setMessage("Bạn có chắc muốn xóa căn hộ này? Tất cả dữ liệu liên quan (thành viên, phương tiện, hóa đơn) sẽ bị xóa.");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            deleteApartment();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void deleteApartment() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // Delete all apartment members
            db.apartmentMemberDao().deleteByApartmentId(apartmentId);

            // Delete all vehicles of this apartment
            db.vehicleDao().deleteVehiclesByApartmentId(apartmentId);

            // Delete all invoices of this apartment
            db.paymentDao().deleteInvoicesByApartmentId(apartmentId);

            // Delete the apartment itself
            if (currentApartment != null) {
                db.apartmentDao().delete(currentApartment);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Xóa căn hộ thành công!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
