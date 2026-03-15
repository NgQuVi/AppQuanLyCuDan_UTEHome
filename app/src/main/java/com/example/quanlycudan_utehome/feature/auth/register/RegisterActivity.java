package com.example.quanlycudan_utehome.feature.auth.register;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Build description with bold highlighted parts
        TextView tvDesc = findViewById(R.id.tvRegisterDesc);
        buildDescription(tvDesc);

        // Contact button opens the phone dialer
        findViewById(R.id.btnContact).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:0000000000"));
            startActivity(intent);
        });

        // Edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.registerRoot), (view, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    view.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                });
    }

    private void buildDescription(TextView tv) {
        // "Để đảm bảo tính bảo mật và xác thực cư dân, vui lòng liên hệ trực tiếp với
        // Ban quản lý chung cư để được hỗ trợ đăng ký tài khoản."
        // Bold: "Ban quản lý chung cư"
        String full = getString(R.string.register_desc_full);
        String bold = getString(R.string.register_desc_bold);
        int boldStart = full.indexOf(bold);
        if (boldStart < 0) {
            tv.setText(full);
            return;
        }
        int boldEnd = boldStart + bold.length();
        SpannableString sp = new SpannableString(full);
        int dark = getResources().getColor(R.color.login_text_primary, getTheme());
        sp.setSpan(new StyleSpan(Typeface.BOLD), boldStart, boldEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(dark), boldStart, boldEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(sp);
    }
}
