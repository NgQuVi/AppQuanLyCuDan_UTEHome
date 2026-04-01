package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.util.Patterns;
import android.util.Log;
import android.widget.EditText;
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
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.util.SmtpEmailService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddResidentActivity extends AppCompatActivity {

    private static final String TAG = "AddResidentActivity";

    private EditText etFullName;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etIdCard;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Apartment selectedApartment;

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

        LinearLayout btnSelectApartment = findViewById(R.id.btnSelectApartment);
        btnSelectApartment.setOnClickListener(v -> loadAndShowApartmentDialog());

        findViewById(R.id.btnCreateAccount).setOnClickListener(v -> createResident());
    }

    private void loadAndShowApartmentDialog() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Apartment> apartments = db.apartmentDao().getAvailableApartments();
            String[] names = new String[apartments.size()];
            for (int i = 0; i < apartments.size(); i++) {
                Apartment apartment = apartments.get(i);
                names[i] = apartment.apartmentCode + " - Toa " + apartment.buildingCode;
            }

            runOnUiThread(() -> {
                if (apartments.isEmpty()) {
                    Toast.makeText(this, "Khong con can ho trong de phan bo", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(this)
                        .setTitle("Chon can ho")
                        .setItems(names, (dialog, which) -> {
                            selectedApartment = apartments.get(which);
                            updateSelectedApartmentLabel(names[which]);
                        })
                        .show();
            });
        });
    }

    private void updateSelectedApartmentLabel(String selectedLabel) {
        LinearLayout apartmentSelector = findViewById(R.id.btnSelectApartment);
        if (apartmentSelector.getChildCount() > 0 && apartmentSelector.getChildAt(0) instanceof TextView) {
            ((TextView) apartmentSelector.getChildAt(0)).setText(selectedLabel);
        }
    }

    private void createResident() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String idCard = etIdCard.getText().toString().trim();

        if (!validateForm(fullName, phone, email)) {
            return;
        }
        if (!SmtpEmailService.isConfigured()) {
            Toast.makeText(
                    this,
                    "Chua cau hinh SMTP trong local.properties nen chua the gui email tu dong",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }
        String configHint = SmtpEmailService.getConfigurationHint();
        if (!configHint.isEmpty()) {
            Toast.makeText(this, configHint, Toast.LENGTH_LONG).show();
            return;
        }

        findViewById(R.id.btnCreateAccount).setEnabled(false);
        executorService.execute(() -> {
            ResidentOnboardingService onboardingService = new ResidentOnboardingService(this);
            ResidentOnboardingService.OnboardingResult result = null;
            try {
                result = onboardingService.createResident(fullName, phone, email, idCard, selectedApartment);
                SmtpEmailService.sendResidentOnboardingEmail(
                        result.resident.email,
                        result.resident.fullName,
                        result.resident.phone,
                        result.temporaryPassword
                );
                onboardingService.activateAccount(result.resident.accountId);

                runOnUiThread(() -> {
                    findViewById(R.id.btnCreateAccount).setEnabled(true);
                    Toast.makeText(this, "Da tao cu dan va gui email tu dong thanh cong", Toast.LENGTH_LONG).show();
                    finish();
                });
            } catch (IllegalArgumentException | IllegalStateException ex) {
                runOnUiThread(() -> {
                    findViewById(R.id.btnCreateAccount).setEnabled(true);
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                });
            } catch (Exception ex) {
                ResidentOnboardingService.OnboardingResult finalResult = result;
                Log.e(TAG, "Send onboarding email failed", ex);
                runOnUiThread(() -> {
                    findViewById(R.id.btnCreateAccount).setEnabled(true);
                    String message = finalResult == null
                            ? "Khong the tao cu dan luc nay"
                            : "Da tao tai khoan nhung gui email that bai. " + readableEmailError(ex)
                            + ". Tai khoan dang tam khoa cho den khi gui mail thanh cong.";
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private boolean validateForm(String fullName, String phone, String email) {
        if (fullName.isEmpty()) {
            etFullName.setError("Vui long nhap ho va ten");
            etFullName.requestFocus();
            return false;
        }
        if (phone.isEmpty()) {
            etPhone.setError("Vui long nhap so dien thoai");
            etPhone.requestFocus();
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email la thong tin bat buoc");
            etEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email chua dung dinh dang");
            etEmail.requestFocus();
            return false;
        }
        if (selectedApartment == null) {
            Toast.makeText(this, "Vui long chon can ho", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String readableEmailError(Exception ex) {
        String message = ex.getMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Loi SMTP";
        }
        if (message.toLowerCase().contains("app password")) {
            return "Gmail can App Password 16 ky tu";
        }
        return message;
    }
}
