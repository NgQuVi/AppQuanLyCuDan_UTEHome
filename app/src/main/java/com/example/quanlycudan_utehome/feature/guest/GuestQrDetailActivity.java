package com.example.quanlycudan_utehome.feature.guest;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.GuestPass;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GuestQrDetailActivity extends AppCompatActivity {

    private ImageView ivQrDetail;
    private TextView tvQrCode;
    private TextView tvStatus;
    private TextView tvCreatedDate;
    private TextView tvValidFrom;
    private TextView tvValidTo;
    private Button btnCancel;
    private Button btnBack;

    private GuestPass currentPass;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guest_qr_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get QR ID from intent
        int qrId = getIntent().getIntExtra("qr_id", -1);
        if (qrId == -1) {
            Toast.makeText(this, "Không tìm thấy mã QR", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ivQrDetail = findViewById(R.id.ivQrDetail);
        tvQrCode = findViewById(R.id.tvQrCode);
        tvStatus = findViewById(R.id.tvStatus);
        tvCreatedDate = findViewById(R.id.tvCreatedDate);
        tvValidFrom = findViewById(R.id.tvValidFrom);
        tvValidTo = findViewById(R.id.tvValidTo);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> cancelQrPass());

        loadQrDetail(qrId);
    }

    private void loadQrDetail(int qrId) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(this);
                currentPass = db.guestPassDao().findByIdSync(qrId);

                if (currentPass == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Không tìm thấy mã QR", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }

                runOnUiThread(this::displayQrDetail);
            } catch (Exception e) {
                Log.e("GuestQRDetail", "Error loading detail: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(this, "Lỗi tải chi tiết", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void displayQrDetail() {
        if (currentPass == null) return;

        // Generate and display QR bitmap
        Bitmap qrBitmap = generateQrBitmap(currentPass.code, 500);
        if (qrBitmap != null) {
            ivQrDetail.setImageBitmap(qrBitmap);
        }

        // Display QR code
        tvQrCode.setText(currentPass.code);

        // Display status with color
        tvStatus.setText(currentPass.status);
        int statusColor = getStatusColor(currentPass.status);
        tvStatus.setTextColor(statusColor);

        // Display dates
        tvCreatedDate.setText("Tạo lúc: " + dateFormat.format(new Date(currentPass.createdAt)));
        tvValidFrom.setText("Bắt đầu: " + dateFormat.format(new Date(currentPass.validFrom)));
        tvValidTo.setText("Kết thúc: " + dateFormat.format(new Date(currentPass.validTo)));

        // Disable cancel button if already cancelled or expired
        if ("CANCELLED".equals(currentPass.status) || "EXPIRED".equals(currentPass.status)) {
            btnCancel.setEnabled(false);
            btnCancel.setAlpha(0.5f);
        }
    }

    private void cancelQrPass() {
        if (currentPass == null) return;

        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Hủy mã QR")
                .setMessage("Bạn chắc chắn muốn hủy mã QR này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    updateQrStatus("CANCELLED");
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void updateQrStatus(String newStatus) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(this);
                currentPass.status = newStatus;
                db.guestPassDao().updateGuestPass(currentPass);
                Log.d("GuestQRDetail", "QR status updated to: " + newStatus);

                runOnUiThread(() -> {
                    tvStatus.setText(newStatus);
                    tvStatus.setTextColor(getStatusColor(newStatus));
                    btnCancel.setEnabled(false);
                    btnCancel.setAlpha(0.5f);
                    Toast.makeText(this, "Đã hủy mã QR", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                Log.e("GuestQRDetail", "Error updating status: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(this, "Lỗi cập nhật trạng thái", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private Bitmap generateQrBitmap(String text, int sizePx) {
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, sizePx, sizePx);
            Bitmap bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.RGB_565);

            int[] pixels = new int[sizePx * sizePx];
            for (int x = 0; x < sizePx; x++) {
                for (int y = 0; y < sizePx; y++) {
                    pixels[y * sizePx + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            bitmap.setPixels(pixels, 0, sizePx, 0, 0, sizePx, sizePx);
            return bitmap;
        } catch (WriterException e) {
            return null;
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "ACTIVE":
                return 0xFF00AA00; // Xanh lá
            case "PENDING":
                return 0xFFFF9800; // Cam
            case "EXPIRED":
                return 0xFFAA0000; // Đỏ
            case "CANCELLED":
                return 0xFF888888; // Xám
            default:
                return 0xFF000000; // Đen
        }
    }
}
