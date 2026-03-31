package com.example.quanlycudan_utehome.feature.accesscard;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycudan_utehome.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;
import java.util.Map;

public class AccessCardActivity extends AppCompatActivity {

    public static final String EXTRA_RESIDENT_NAME = "EXTRA_RESIDENT_NAME";
    public static final String EXTRA_APARTMENT_LABEL = "EXTRA_APARTMENT_LABEL";
    public static final String EXTRA_VEHICLE_COLOR = "EXTRA_VEHICLE_COLOR";
    public static final String EXTRA_EXPIRED_DATE = "EXTRA_EXPIRED_DATE";
    public static final String EXTRA_PLATE_NUMBER = "EXTRA_PLATE_NUMBER";
    public static final String EXTRA_VEHICLE_INFO = "EXTRA_VEHICLE_INFO";
    public static final String EXTRA_QR_CONTENT = "EXTRA_QR_CONTENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_card);

        findViewById(R.id.ivBack).setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        // Nhận dữ liệu từ Intent
        String residentName = getIntent().getStringExtra(EXTRA_RESIDENT_NAME);
        if (residentName == null) residentName = "Nguyễn Văn A";

        String apartmentLabel = getIntent().getStringExtra(EXTRA_APARTMENT_LABEL);
        if (apartmentLabel == null) apartmentLabel = "P.1205, Tòa S1";

        String vehicleColor = getIntent().getStringExtra(EXTRA_VEHICLE_COLOR);
        if (vehicleColor == null) vehicleColor = "Trắng";

        String expiredDate = getIntent().getStringExtra(EXTRA_EXPIRED_DATE);
        if (expiredDate == null) expiredDate = "31/12/2024";

        String plateNumber = getIntent().getStringExtra(EXTRA_PLATE_NUMBER);
        if (plateNumber == null) plateNumber = "30A - 888.88";

        String vehicleInfo = getIntent().getStringExtra(EXTRA_VEHICLE_INFO);
        if (vehicleInfo == null) vehicleInfo = "Ô tô • Mercedes-Benz GLC 300";

        String qrContent = getIntent().getStringExtra(EXTRA_QR_CONTENT);
        if (qrContent == null) {
            qrContent = "Resident=" + residentName + ";Apartment=" + apartmentLabel;
        }

        // Bind dữ liệu lên UI
        ((TextView) findViewById(R.id.tvPlateNumber)).setText(plateNumber);
        ((TextView) findViewById(R.id.tvVehicleInfo)).setText(vehicleInfo);
        ((TextView) findViewById(R.id.tvResidentName)).setText(residentName);
        ((TextView) findViewById(R.id.tvApartmentValue)).setText(apartmentLabel);
        ((TextView) findViewById(R.id.tvColorValue)).setText(vehicleColor);
        ((TextView) findViewById(R.id.tvExpiredValue)).setText(expiredDate);

        ImageView ivQrCode = findViewById(R.id.ivQrCode);
        generateQrCode(qrContent, ivQrCode);
    }

    private void generateQrCode(String content, ImageView imageView) {
        if (content == null || content.isEmpty()) return;

        int size = getResources().getDimensionPixelSize(R.dimen.qr_code_size);
        if (size <= 0) size = 600;

        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = new QRCodeWriter().encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    size,
                    size,
                    hints
            );

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];

            int black = 0xFF000000;
            int white = 0xFFFFFFFF;

            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? black : white;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            imageView.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}