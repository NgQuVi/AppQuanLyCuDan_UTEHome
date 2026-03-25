package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Account;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddResidentActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etEmail, etIdCard, etPassword;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isPasswordVisible = false;
    private Apartment selectedApartment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_resident);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etIdCard = findViewById(R.id.etIdCard);
        etPassword = findViewById(R.id.etPassword);

        // Password Toggle
        ImageView btnTogglePwd = findViewById(R.id.btnTogglePwd);
        btnTogglePwd.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnTogglePwd.setImageResource(R.drawable.ic_eye_outline); // or eye_off
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnTogglePwd.setImageResource(R.drawable.ic_eye_outline); 
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // Generate Password
        LinearLayout btnGeneratePwd = findViewById(R.id.btnGeneratePwd);
        btnGeneratePwd.setOnClickListener(v -> {
            String randomPwd = generateRandomPassword(6);
            etPassword.setText(randomPwd);
        });

        // Select Apartment
        LinearLayout btnSelectApartment = findViewById(R.id.btnSelectApartment);
        btnSelectApartment.setOnClickListener(v -> loadAndShowApartmentDialog());

        // Create Account
        findViewById(R.id.btnCreateAccount).setOnClickListener(v -> createResident());
    }

    private void loadAndShowApartmentDialog() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            // Could load only vacant, but let's load all for now
            List<Apartment> apartments = db.apartmentDao().getAllApartments();
            String[] names = new String[apartments.size()];
            for (int i = 0; i < apartments.size(); i++) {
                names[i] = "P." + apartments.get(i).apartmentCode + " - Tòa " + apartments.get(i).buildingCode;
            }

            runOnUiThread(() -> {
                if (apartments.isEmpty()) {
                    Toast.makeText(this, "Không có căn hộ nào trong danh sách", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(this)
                        .setTitle("Chọn căn hộ")
                        .setItems(names, (dialog, which) -> {
                            selectedApartment = apartments.get(which);
                            TextView tvSelect = ((LinearLayout) findViewById(R.id.btnSelectApartment)).findViewById(android.R.id.text1);
                            if (tvSelect != null) {
                                tvSelect.setText(names[which]);
                            } else {
                                // Specific handling due to lack of ID inside LinearLayout
                                LinearLayout linear = findViewById(R.id.btnSelectApartment);
                                if (linear.getChildAt(0) instanceof TextView) {
                                    ((TextView) linear.getChildAt(0)).setText(names[which]);
                                }
                            }
                        }).show();
            });
        });
    }

    private void createResident() {
        String phone = etPhone.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String idCard = etIdCard.getText().toString().trim();

        if (phone.isEmpty() || pass.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ các thông tin bắt buộc (*)", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            
            // 1. Create Account
            Account newAcc = new Account();
            newAcc.phone = phone;
            newAcc.password = pass;
            newAcc.role = "User"; // default
            long accId = db.accountDao().insert(newAcc);

            // 2. Create Resident
            Resident res = new Resident();
            res.accountId = (int) accId;
            res.fullName = name;
            res.phone = phone;
            res.email = email;
            res.idNum = idCard;
            db.residentDao().insert(res);

            // 3. Update Apartment if selected
            if (selectedApartment != null) {
                selectedApartment.accountId = (int) accId;
                selectedApartment.status = "Đang sử dụng";
                db.apartmentDao().update(selectedApartment);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Thêm cư dân thành công!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}
