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

public class ApartmentInfoActivity extends AppCompatActivity {

    private ApartmentRepository apartmentRepository;
    private RecyclerView recyclerViewMembers;
    private ApartmentMemberAdapter memberAdapter;

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

        TextView btnAddMember = findViewById(R.id.btnAddMember);
        if (btnAddMember != null) {
            btnAddMember.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.quanlycudan_utehome.feature.member.AddMemberActivity.class);
                intent.putExtra("APARTMENT_ID", 1);
                startActivityForResult(intent, 100);
            });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Refresh member list after adding new member
            loadApartmentData();
        }
    }


    private void showAddMemberDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
        builder.setView(view);
        android.app.AlertDialog dialog = builder.create();

        // Make dialog background transparent to show curved corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        TextView tvRole = view.findViewById(R.id.tvRole);
        TextView tvGender = view.findViewById(R.id.tvGender);
        
        tvRole.setOnClickListener(v -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(this, tvRole);
            popup.getMenu().add("Chủ hộ");
            popup.getMenu().add("Vợ/Chồng");
            popup.getMenu().add("Con");
            popup.getMenu().add("Khách");
            popup.setOnMenuItemClickListener(item -> {
                tvRole.setText(item.getTitle());
                return true;
            });
            popup.show();
        });

        tvGender.setOnClickListener(v -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(this, tvGender);
            popup.getMenu().add("Nam");
            popup.getMenu().add("Nữ");
            popup.setOnMenuItemClickListener(item -> {
                tvGender.setText(item.getTitle());
                return true;
            });
            popup.show();
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnSave).setOnClickListener(v -> {
            android.widget.EditText etName = view.findViewById(R.id.etName);
            android.widget.EditText etPhone = view.findViewById(R.id.etPhone);
            android.widget.EditText etDob = view.findViewById(R.id.etDob);

            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String dob = etDob.getText().toString();
            String role = tvRole.getText().toString();
            String gender = tvGender.getText().toString();

            if (name.isEmpty() || phone.isEmpty()) {
                android.widget.Toast.makeText(this, "Vui lòng nhập tên và số điện thoại", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            saveNewMember(name, phone, dob, role, gender);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void saveNewMember(String name, String phone, String dob, String role, String gender) {
        new Thread(() -> {
            com.example.quanlycudan_utehome.data.database.AppDatabase db = com.example.quanlycudan_utehome.data.database.AppDatabase.getInstance(this);
            
            // Create new Resident
            com.example.quanlycudan_utehome.data.entity.Resident newResident = new com.example.quanlycudan_utehome.data.entity.Resident();
            newResident.fullName = name;
            newResident.phone = phone;
            newResident.dob = dob;
            newResident.gender = gender;
            newResident.residentCode = "RES" + System.currentTimeMillis(); // Generate a random code
            
            // Insert Resident and get ID
            long residentId = db.residentDao().insert(newResident);
            
            // Create new ApartmentMember
            com.example.quanlycudan_utehome.data.entity.ApartmentMember newMember = new com.example.quanlycudan_utehome.data.entity.ApartmentMember();
            newMember.apartmentId = 1; // Assuming adding to apartment 1
            newMember.residentId = (int) residentId;
            newMember.role = role;
            newMember.residentType = "Thành viên";
            
            // Insert ApartmentMember
            db.apartmentMemberDao().insert(newMember);

            // Reload data
            runOnUiThread(() -> {
                android.widget.Toast.makeText(this, "Thêm thành viên thành công", android.widget.Toast.LENGTH_SHORT).show();
                loadApartmentData();
            });
        }).start();
    }

    private void loadApartmentData() {
        // Lấy dữ liệu căn hộ đầu tiên (ID = 1)
        int apartmentId = 1;

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
        // Cập nhật thông tin căn hộ
        TextView tvMainApartmentCode = findViewById(R.id.tvMainApartmentCode);
        TextView tvMainBuilding = findViewById(R.id.tvMainBuilding);

        tvMainApartmentCode.setText(apartment.apartmentCode);
        tvMainBuilding.setText("Tòa " + apartment.buildingCode);

        // Cập nhật chi tiết căn hộ bằng cách tìm các TextView có ID cụ thể
        // (Những TextViews này sẽ được thêm vào layout hoặc cập nhật bằng các ID riêng)
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
