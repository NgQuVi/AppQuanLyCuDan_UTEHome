package com.example.quanlycudan_utehome.feature.guest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;

public class GuestQrActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guest_qr);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        ImageView ivHistory = findViewById(R.id.ivHistory);
        if (ivHistory != null) {
            ivHistory.setOnClickListener(v -> Toast.makeText(this, "Lịch sử QR sẽ được cập nhật sau", Toast.LENGTH_SHORT).show());
        }

        Button btnGenerate = findViewById(R.id.btnGenerate);
        Button btnDownload = findViewById(R.id.btnDownload);
        Button btnRefresh = findViewById(R.id.btnRefresh);
        TextView tvQrStatus = findViewById(R.id.tvQrStatus);

        if (btnGenerate != null && tvQrStatus != null) {
            btnGenerate.setOnClickListener(v -> {
                tvQrStatus.setText("QR87291");
                Toast.makeText(this, "Đã tạo mã QR khách", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnDownload != null) {
            btnDownload.setOnClickListener(v -> Toast.makeText(this, "Tính năng tải xuống sẽ được cập nhật sau", Toast.LENGTH_SHORT).show());
        }

        if (btnRefresh != null && tvQrStatus != null) {
            btnRefresh.setOnClickListener(v -> {
                tvQrStatus.setText(getString(R.string.guest_qr_no_qr));
                Toast.makeText(this, "Đã làm mới màn hình", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
