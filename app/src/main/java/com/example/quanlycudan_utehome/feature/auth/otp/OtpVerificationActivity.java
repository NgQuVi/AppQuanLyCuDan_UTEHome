package com.example.quanlycudan_utehome.feature.auth.otp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.feature.auth.login.LoginActivity;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText[] otpFields;
    private TextView tvResendTimer;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_verification);

        otpFields = new EditText[] {
                findViewById(R.id.otp1), findViewById(R.id.otp2),
                findViewById(R.id.otp3), findViewById(R.id.otp4),
                findViewById(R.id.otp5), findViewById(R.id.otp6)
        };
        tvResendTimer = findViewById(R.id.tvResendTimer);

        setupOtpInputs();
        startResendTimer();

        String phone = getIntent().getStringExtra("PHONE");

        findViewById(R.id.btnConfirmOtp).setOnClickListener(v -> {
            StringBuilder otp = new StringBuilder();
            for (EditText et : otpFields) {
                otp.append(et.getText().toString());
            }
            if (otp.length() < 6) {
                Toast.makeText(this, getString(R.string.otp_error_incomplete), Toast.LENGTH_SHORT).show();
            } else {
                boolean isValid = com.example.quanlycudan_utehome.feature.auth.service.OtpService.getInstance().verifyOtp(phone, otp.toString());
                if (isValid) {
                    Toast.makeText(this, getString(R.string.otp_success), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, com.example.quanlycudan_utehome.feature.auth.reset.ResetPasswordActivity.class);
                    intent.putExtra("PHONE", phone);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Mã OTP không đúng hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.tvChangePhone).setOnClickListener(v -> finish()); // Go back

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.otpRoot), (view, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });
    }

    private void setupOtpInputs() {
        for (int i = 0; i < otpFields.length; i++) {
            final int currentIndex = i;
            EditText et = otpFields[i];

            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && currentIndex < otpFields.length - 1) {
                        otpFields[currentIndex + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            // Handle backspace to move to previous field
            et.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (et.getText().toString().isEmpty() && currentIndex > 0) {
                        otpFields[currentIndex - 1].requestFocus();
                        otpFields[currentIndex - 1].setText("");
                        return true;
                    }
                }
                return false;
            });
        }
    }

    private void startResendTimer() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        tvResendTimer.setEnabled(false);

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvResendTimer.setText(getString(R.string.otp_resend_timer, seconds));
            }

            @Override
            public void onFinish() {
                tvResendTimer.setText(getString(R.string.otp_resend_now));
                tvResendTimer.setEnabled(true);
                tvResendTimer.setOnClickListener(v -> {
                    String phoneToResend = getIntent().getStringExtra("PHONE");
                    if (phoneToResend != null) {
                        com.example.quanlycudan_utehome.feature.auth.service.OtpService.getInstance().generateOtp(phoneToResend);
                    }
                    Toast.makeText(OtpVerificationActivity.this, getString(R.string.otp_sent_again), Toast.LENGTH_SHORT)
                            .show();
                    startResendTimer(); // restart timer
                });
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null)
            countDownTimer.cancel();
    }
}
