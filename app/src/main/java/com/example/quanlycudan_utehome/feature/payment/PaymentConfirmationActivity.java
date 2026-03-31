package com.example.quanlycudan_utehome.feature.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.TransactionHistory;
import com.example.quanlycudan_utehome.data.repository.PaymentRepository;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentConfirmationActivity extends AppCompatActivity {

    private TextView tvSubtotalVal, tvTotalValue;

    // 4 card container – ẩn/hiện tùy theo checkbox đã chọn ở màn hóa đơn
    private View cardElec, cardWater, cardPark, cardInternet;

    private PaymentRepository repository;
    private final DecimalFormat df = new DecimalFormat("#,###");

    private String  invoiceId  = "";
    private long    totalSum   = 0;
    private boolean hasElec, hasWater, hasPark, hasInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        // ── 1. Nhận dữ liệu từ InvoiceActivity qua Intent ────────
        invoiceId   = getIntent().getStringExtra("INVOICE_ID");
        if (invoiceId == null) invoiceId = "";
        totalSum    = getIntent().getLongExtra("TOTAL_SUM", 0);
        // Checkbox nào được tick = true, bỏ chọn = false
        hasElec     = getIntent().getBooleanExtra("HAS_ELEC",     true);
        hasWater    = getIntent().getBooleanExtra("HAS_WATER",    true);
        hasPark     = getIntent().getBooleanExtra("HAS_PARK",     true);
        hasInternet = getIntent().getBooleanExtra("HAS_INTERNET", true);

        // ── 2. Ánh xạ Views ──────────────────────────────────────
        tvSubtotalVal = findViewById(R.id.tvSubtotalVal);
        tvTotalValue  = findViewById(R.id.tvTotalValue);
        cardElec      = findViewById(R.id.cardElec);
        cardWater     = findViewById(R.id.cardWater);
        cardPark      = findViewById(R.id.cardPark);
        cardInternet  = findViewById(R.id.cardInternet);

        // ── 3. Ẩn card nào người dùng KHÔNG chọn ─────────────────
        //   View.GONE → card biến mất, không chiếm không gian
        cardElec.setVisibility(    hasElec     ? View.VISIBLE : View.GONE);
        cardWater.setVisibility(   hasWater    ? View.VISIBLE : View.GONE);
        cardPark.setVisibility(    hasPark     ? View.VISIBLE : View.GONE);
        cardInternet.setVisibility(hasInternet ? View.VISIBLE : View.GONE);

        // ── 4. Hiển thị tổng tiền đã tính từ InvoiceActivity ─────
        String fmtTotal = fmt(totalSum) + "đ";
        tvSubtotalVal.setText(fmtTotal);
        tvTotalValue.setText(fmtTotal);

        // ── 5. Repository ─────────────────────────────────────────
        repository = new PaymentRepository(getApplication());

        // ── 6. Nút bấm ───────────────────────────────────────────
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnConfirm).setOnClickListener(v -> processPayment());
    }

    /** Chuyển sang màn hình thanh toán VNPay */
    private void processPayment() {
        if (invoiceId.isEmpty()) {
            Toast.makeText(this, "Lỗi: không tìm thấy hóa đơn", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, com.example.quanlycudan_utehome.feature.invoice.VNPayWebActivity.class);
        intent.putExtra("INVOICE_ID", invoiceId);
        intent.putExtra("TOTAL_SUM", totalSum);
        intent.putExtra("HAS_ELEC", hasElec);
        intent.putExtra("HAS_WATER", hasWater);
        intent.putExtra("HAS_PARK", hasPark);
        intent.putExtra("HAS_INTERNET", hasInternet);
        startActivity(intent);
    }

    private String fmt(long n) {
        return df.format(n).replace(',', '.');
    }
}
