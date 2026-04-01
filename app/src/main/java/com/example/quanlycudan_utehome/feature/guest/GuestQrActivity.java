package com.example.quanlycudan_utehome.feature.guest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GuestQrActivity extends AppCompatActivity {

    private final SimpleDateFormat dateTimeParser = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
    private Bitmap currentQrBitmap;
    private String currentQrCodeText;
    private String currentSalt;
    private long currentValidFrom;
    private long currentValidTo;

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
            ivHistory.setOnClickListener(v -> {
                Intent intent = new Intent(GuestQrActivity.this, GuestQrHistoryActivity.class);
                startActivity(intent);
            });
        }

        setupDateTimePickers();

        Button btnGenerate = findViewById(R.id.btnGenerate);
        Button btnDownload = findViewById(R.id.btnDownload);
        Button btnRefresh = findViewById(R.id.btnRefresh);
        TextView tvQrStatus = findViewById(R.id.tvQrStatus);

        if (btnGenerate != null) {
            btnGenerate.setOnClickListener(v -> handleGenerateQr());
        }

        if (btnDownload != null) {
            btnDownload.setOnClickListener(v -> downloadCurrentQrImage());
        }

        if (btnRefresh != null && tvQrStatus != null) {
            btnRefresh.setOnClickListener(v -> {
                tvQrStatus.setText(getString(R.string.guest_qr_no_qr));
                ImageView ivQr = findViewById(R.id.ivQr);
                if (ivQr != null) {
                    ivQr.setImageResource(android.R.drawable.ic_menu_share);
                    ivQr.setAlpha(0.35f);
                }
                currentQrBitmap = null;
                currentQrCodeText = null;
                Toast.makeText(this, "Đã làm mới màn hình", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupDateTimePickers() {
        TextView tvFromDate = findViewById(R.id.tvFromDate);
        TextView tvFromTime = findViewById(R.id.tvFromTime);
        TextView tvToDate = findViewById(R.id.tvToDate);
        TextView tvToTime = findViewById(R.id.tvToTime);

        if (tvFromDate != null) {
            tvFromDate.setOnClickListener(v -> showDatePicker(tvFromDate));
        }
        if (tvToDate != null) {
            tvToDate.setOnClickListener(v -> showDatePicker(tvToDate));
        }
        if (tvFromTime != null) {
            tvFromTime.setOnClickListener(v -> showTimePicker(tvFromTime));
        }
        if (tvToTime != null) {
            tvToTime.setOnClickListener(v -> showTimePicker(tvToTime));
        }
    }

    private void showDatePicker(TextView target) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String formatted = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    target.setText(formatted);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void showTimePicker(TextView target) {
        Calendar cal = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String amPm = hourOfDay >= 12 ? "PM" : "AM";
                    int hour12;
                    if (hourOfDay == 0) {
                        hour12 = 12;
                    } else if (hourOfDay > 12) {
                        hour12 = hourOfDay - 12;
                    } else {
                        hour12 = hourOfDay;
                    }
                    String formatted = String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm);
                    target.setText(formatted);
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
        );
        dialog.show();
    }

    private void handleGenerateQr() {
        TextView tvFromDate = findViewById(R.id.tvFromDate);
        TextView tvFromTime = findViewById(R.id.tvFromTime);
        TextView tvToDate = findViewById(R.id.tvToDate);
        TextView tvToTime = findViewById(R.id.tvToTime);
        TextView tvQrStatus = findViewById(R.id.tvQrStatus);
        ImageView ivQr = findViewById(R.id.ivQr);

        if (tvFromDate == null || tvFromTime == null || tvToDate == null || tvToTime == null || tvQrStatus == null || ivQr == null) {
            Toast.makeText(this, "Thiếu thành phần giao diện", Toast.LENGTH_SHORT).show();
            return;
        }

        long validFrom = parseToMillis(tvFromDate.getText().toString(), tvFromTime.getText().toString());
        long validTo = parseToMillis(tvToDate.getText().toString(), tvToTime.getText().toString());

        if (validFrom <= 0 || validTo <= 0) {
            Toast.makeText(this, "Ngày giờ không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (validTo <= validFrom) {
            Toast.makeText(this, "Thời gian kết thúc phải sau thời gian bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }

        int apartmentId = 1;
        String salt = randomSalt(16);
        String code = generateCode(apartmentId, salt, validFrom);
        Log.d("GuestQR", "Generated code: " + code);

        Bitmap qrBitmap = generateQrBitmap(code, 400);
        Log.d("GuestQR", "QR bitmap created: " + (qrBitmap != null) + ", size: " + (qrBitmap != null ? qrBitmap.getWidth() + "x" + qrBitmap.getHeight() : "null"));

        if (qrBitmap == null) {
            Toast.makeText(this, "Không thể tạo hình QR", Toast.LENGTH_SHORT).show();
            return;
        }

        currentQrCodeText = code;
        currentQrBitmap = qrBitmap;
        currentSalt = salt;
        currentValidFrom = validFrom;
        currentValidTo = validTo;

        // Display short code for visibility, encode full code in QR
        String shortCode = code.substring(0, Math.min(16, code.length())).toUpperCase(Locale.getDefault());

        Log.d("GuestQR", "Setting bitmap to ImageView...");
        ivQr.setImageBitmap(qrBitmap);
        ivQr.setAlpha(1.0f);
        tvQrStatus.setText("QR: " + shortCode);

        // Save to database
        saveQrToDatabase(apartmentId, code, salt, validFrom, validTo);

        Log.d("GuestQR", "QR display completed");
        Toast.makeText(this, "Đã tạo mã QR khách", Toast.LENGTH_SHORT).show();
    }

    private Bitmap generateQrBitmap(String text, int sizePx) {
        try {
            Log.d("GuestQR", "Starting QR encode with size: " + sizePx);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, sizePx, sizePx);
            Log.d("GuestQR", "BitMatrix created successfully");

            Bitmap bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.RGB_565);
            Log.d("GuestQR", "Bitmap created: " + bitmap.getWidth() + "x" + bitmap.getHeight());

            int[] pixels = new int[sizePx * sizePx];
            for (int x = 0; x < sizePx; x++) {
                for (int y = 0; y < sizePx; y++) {
                    pixels[y * sizePx + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            Log.d("GuestQR", "Pixels array filled");

            bitmap.setPixels(pixels, 0, sizePx, 0, 0, sizePx, sizePx);
            Log.d("GuestQR", "Pixels set to bitmap");

            return bitmap;
        } catch (WriterException e) {
            Log.e("GuestQR", "WriterException: " + e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    private void downloadCurrentQrImage() {
        if (currentQrBitmap == null) {
            Toast.makeText(this, "Vui lòng tạo mã QR trước", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "guest_qr_" + System.currentTimeMillis() + ".png";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/UTEHome");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            Toast.makeText(this, "Không thể tạo file ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            if (out == null) {
                Toast.makeText(this, "Không thể mở luồng ghi ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            currentQrBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            Toast.makeText(this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues done = new ContentValues();
            done.put(MediaStore.Images.Media.IS_PENDING, 0);
            getContentResolver().update(uri, done, null, null);
        }

        Toast.makeText(this, "Đã tải ảnh QR vào thư viện", Toast.LENGTH_SHORT).show();
    }

    public static String generateCode(int apartmentId, String salt, long validFrom) {
        String raw = apartmentId + "|" + salt + "|" + validFrom;
        return sha256(raw);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) {
                    hex.append('0');
                }
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private long parseToMillis(String date, String time) {
        try {
            Date d = dateTimeParser.parse(date + " " + time);
            return d != null ? d.getTime() : -1L;
        } catch (ParseException e) {
            return -1L;
        }
    }

    private String randomSalt(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void saveQrToDatabase(int apartmentId, String code, String salt, long validFrom, long validTo) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(this);
                GuestPass pass = new GuestPass();
                pass.apartmentId = apartmentId;
                pass.code = code;
                pass.salt = salt;
                pass.createdAt = System.currentTimeMillis();
                pass.validFrom = validFrom;
                pass.validTo = validTo;

                // Set status based on current time
                long now = System.currentTimeMillis();
                if (now < validFrom) {
                    pass.status = "PENDING";
                } else if (now > validTo) {
                    pass.status = "EXPIRED";
                } else {
                    pass.status = "ACTIVE";
                }

                db.guestPassDao().insertGuestPass(pass);
                Log.d("GuestQR", "QR saved to database: " + code);
            } catch (Exception e) {
                Log.e("GuestQR", "Error saving to database: " + e.getMessage(), e);
            }
        }).start();
    }
}
