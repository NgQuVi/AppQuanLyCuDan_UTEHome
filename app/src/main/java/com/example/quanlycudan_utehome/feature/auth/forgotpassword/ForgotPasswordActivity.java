package com.example.quanlycudan_utehome.feature.auth.forgotpassword;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;

import android.content.Intent;
import com.example.quanlycudan_utehome.feature.auth.otp.OtpVerificationActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        EditText edtPhone = findViewById(R.id.edtForgotPhone);

        // Send OTP button
        findViewById(R.id.btnSendOtp).setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                edtPhone.setError(getString(R.string.forgot_error_empty_phone));
                edtPhone.requestFocus();
            } else {
                boolean exists = com.example.quanlycudan_utehome.feature.auth.service.AuthService.getInstance().checkPhoneExists(phone);
                if (exists) {
                    com.example.quanlycudan_utehome.feature.auth.service.OtpService.getInstance().generateOtp(phone);
                    Toast.makeText(this,
                            getString(R.string.forgot_otp_sent),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, OtpVerificationActivity.class);
                    intent.putExtra("PHONE", phone);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Số điện thoại không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Back to login link
        TextView tvBack = findViewById(R.id.tvBackToLogin);
        tvBack.setOnClickListener(v -> finish());

        // Edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.forgotRoot), (view, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    view.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                });
    }
}
