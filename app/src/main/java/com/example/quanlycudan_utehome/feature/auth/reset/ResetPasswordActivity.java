package com.example.quanlycudan_utehome.feature.auth.reset;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.feature.auth.login.LoginActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        EditText edtNewPassword = findViewById(R.id.edtNewPassword);
        EditText edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        ImageView ivToggleNew = findViewById(R.id.ivToggleNewPassword);
        ImageView ivToggleConfirm = findViewById(R.id.ivToggleConfirmPassword);

        // Toggle New Password
        ivToggleNew.setOnClickListener(v -> {
            isNewPasswordVisible = !isNewPasswordVisible;
            if (isNewPasswordVisible) {
                edtNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivToggleNew.setImageResource(R.drawable.ic_eye_outline);
            } else {
                edtNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivToggleNew.setImageResource(R.drawable.ic_eye_off_outline);
            }
            edtNewPassword.setSelection(edtNewPassword.length());
        });

        // Toggle Confirm Password
        ivToggleConfirm.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                edtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivToggleConfirm.setImageResource(R.drawable.ic_eye_outline);
            } else {
                edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivToggleConfirm.setImageResource(R.drawable.ic_eye_off_outline);
            }
            edtConfirmPassword.setSelection(edtConfirmPassword.length());
        });

        String phone = getIntent().getStringExtra("PHONE");

        findViewById(R.id.btnResetConfirm).setOnClickListener(v -> {
            String newPw = edtNewPassword.getText().toString();
            String confirmPw = edtConfirmPassword.getText().toString();

            if (TextUtils.isEmpty(newPw)) {
                edtNewPassword.setError(getString(R.string.reset_error_empty));
                edtNewPassword.requestFocus();
            } else if (newPw.length() < 8) {
                edtNewPassword.setError(getString(R.string.reset_error_short));
                edtNewPassword.requestFocus();
            } else if (!newPw.equals(confirmPw)) {
                edtConfirmPassword.setError(getString(R.string.reset_error_mismatch));
                edtConfirmPassword.requestFocus();
            } else {
                if (phone != null) {
                    com.example.quanlycudan_utehome.feature.auth.service.AuthService.getInstance().updatePassword(phone, newPw);
                }
                Intent intent = new Intent(this, PasswordSuccessActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.resetRoot), (view, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });
    }
}
