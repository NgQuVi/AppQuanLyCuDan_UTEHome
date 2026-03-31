package com.example.quanlycudan_utehome.feature.auth.login;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.local.SessionManager;
import com.example.quanlycudan_utehome.feature.auth.forgotpassword.ForgotPasswordActivity;
import com.example.quanlycudan_utehome.feature.auth.register.RegisterActivity;
import com.example.quanlycudan_utehome.feature.auth.reset.ResetPasswordActivity;
import com.example.quanlycudan_utehome.feature.auth.service.AuthService;

public class LoginActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        TextView tvSignUp = findViewById(R.id.tvSignUp);
        String prefix = getString(R.string.login_sign_up_prefix);
        String link = getString(R.string.login_sign_up_link);
        SpannableString spannable = new SpannableString(prefix + link);
        int linkStart = prefix.length();
        int linkEnd = spannable.length();
        int orange = getResources().getColor(R.color.login_primary, getTheme());
        spannable.setSpan(new ForegroundColorSpan(orange), linkStart, linkEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), linkStart, linkEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSignUp.setText(spannable);
        tvSignUp.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        findViewById(R.id.tvForgotPassword)
                .setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));

        EditText edtPassword = findViewById(R.id.edtPassword);
        ImageView ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye_outline);
            } else {
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye_off_outline);
            }
            edtPassword.setSelection(edtPassword.length());
        });

        EditText edtPhone = findViewById(R.id.edtPhone);
        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (phone.isEmpty()) {
                edtPhone.setError("Vui lòng nhập số điện thoại");
                edtPhone.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                edtPassword.setError("Vui lòng nhập mật khẩu");
                edtPassword.requestFocus();
                return;
            }

            AuthService.getInstance(this).login(phone, password, loginResult -> {
                switch (loginResult.status) {
                    case ADMIN_SUCCESS:
                        startActivity(new Intent(this, com.example.quanlycudan_utehome.admin.AdminMainActivity.class));
                        finish();
                        break;
                    case RESIDENT_SUCCESS:
                        saveResidentSession(loginResult.residentId, loginResult.apartmentId);
                        startActivity(new Intent(this, com.example.quanlycudan_utehome.MainActivity.class));
                        finish();
                        break;
                    case MUST_CHANGE_PASSWORD:
                        Intent resetIntent = new Intent(this, ResetPasswordActivity.class);
                        resetIntent.putExtra("PHONE", loginResult.phone);
                        resetIntent.putExtra("FORCE_FIRST_LOGIN", true);
                        startActivity(resetIntent);
                        break;
                    case INACTIVE_ACCOUNT:
                        android.widget.Toast.makeText(this, "Tài khoản đang bị vô hiệu hóa", android.widget.Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_CREDENTIALS:
                    default:
                        android.widget.Toast.makeText(this, "Thông tin đăng nhập không hợp lệ", android.widget.Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginRoot), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        com.example.quanlycudan_utehome.data.database.DatabaseInitializer.initializeSampleData(this);
    }

    private void saveResidentSession(int residentId, String apartmentId) {
        SessionManager sessionManager = SessionManager.getInstance(this);
        sessionManager.saveResidentId(residentId);
        sessionManager.saveApartmentId(apartmentId);
    }
}
