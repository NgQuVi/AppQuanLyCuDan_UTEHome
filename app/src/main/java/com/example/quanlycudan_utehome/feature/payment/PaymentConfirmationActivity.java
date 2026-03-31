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
    private View cardElec, cardWater, cardPark, cardInternet;
    private View cardMomo, cardBank, cardTransfer, cardVNPay;
    private android.widget.RadioButton rbMomo, rbBank, rbTransfer, rbVNPay;

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

        cardMomo      = findViewById(R.id.cardMomo);
        cardBank      = findViewById(R.id.cardBank);
        cardTransfer  = findViewById(R.id.cardTransfer);
        cardVNPay     = findViewById(R.id.cardVNPay);

        rbMomo        = findViewById(R.id.rbMomo);
        rbBank        = findViewById(R.id.rbBank);
        rbTransfer    = findViewById(R.id.rbTransfer);
        rbVNPay       = findViewById(R.id.rbVNPay);

        // ── 3. Ẩn card nào người dùng KHÔNG chọn ─────────────────
        cardElec.setVisibility(    hasElec     ? View.VISIBLE : View.GONE);
        cardWater.setVisibility(   hasWater    ? View.VISIBLE : View.GONE);
        cardPark.setVisibility(    hasPark     ? View.VISIBLE : View.GONE);
        cardInternet.setVisibility(hasInternet ? View.VISIBLE : View.GONE);

        // ── 4. Hiển thị tổng tiền đã tính từ InvoiceActivity ─────
        String fmtTotal = fmt(totalSum) + "đ";
        tvSubtotalVal.setText(fmtTotal);
        tvTotalValue.setText(fmtTotal);

        // ── 5. Setup RadioButtons ────────────────────────────────
        setupPaymentMethods();

        // ── 6. Repository ─────────────────────────────────────────
        repository = new PaymentRepository(getApplication());

        // ── 7. Nút bấm ───────────────────────────────────────────
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnConfirm).setOnClickListener(v -> processPayment());
    }

    private void setupPaymentMethods() {
        View.OnClickListener listener = v -> {
            rbMomo.setChecked(v.getId() == R.id.cardMomo);
            rbBank.setChecked(v.getId() == R.id.cardBank);
            rbTransfer.setChecked(v.getId() == R.id.cardTransfer);
            rbVNPay.setChecked(v.getId() == R.id.cardVNPay);
        };
        cardMomo.setOnClickListener(listener);
        cardBank.setOnClickListener(listener);
        cardTransfer.setOnClickListener(listener);
        cardVNPay.setOnClickListener(listener);
    }

    /** Tạo TransactionHistory, lưu DB, chuyển sang màn PaymentHistory */
    private void processPayment() {
        if (invoiceId.isEmpty()) {
            Toast.makeText(this, "Lỗi: không tìm thấy hóa đơn", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rbVNPay.isChecked()) {
            // Thanh toán qua VNPay
            Intent intent = new Intent(this, VNPayActivity.class);
            intent.putExtra("INVOICE_ID", invoiceId);
            intent.putExtra("TOTAL_SUM", totalSum);
            intent.putExtra("HAS_ELEC", hasElec);
            intent.putExtra("HAS_WATER", hasWater);
            intent.putExtra("HAS_PARK", hasPark);
            intent.putExtra("HAS_INTERNET", hasInternet);
            startActivityForResult(intent, 1234);
        } else {
            // Thanh toán giả lập cho các phương thức khác
            String method = "Ví MoMo";
            if (rbBank.isChecked()) method = "Thẻ ngân hàng";
            else if (rbTransfer.isChecked()) method = "Chuyển khoản";

            repository.processMockPayment(invoiceId, totalSum, method, hasElec, hasWater, hasPark, hasInternet);
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
            finishPayment();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            finishPayment();
        }
    }

    private void finishPayment() {
        Intent intent = new Intent(this, PaymentHistoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private String fmt(long n) {
        return df.format(n).replace(',', '.');
    }
}

