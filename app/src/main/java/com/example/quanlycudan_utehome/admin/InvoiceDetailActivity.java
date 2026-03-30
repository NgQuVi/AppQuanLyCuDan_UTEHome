package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Invoice;
import com.example.quanlycudan_utehome.data.entity.InvoiceItem;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InvoiceDetailActivity extends AppCompatActivity {

    private String invoiceId;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final DecimalFormat currencyFormat = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invoice_detail);

        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        invoiceId = getIntent().getStringExtra("invoice_id");
        if (invoiceId == null || invoiceId.isEmpty()) {
            Toast.makeText(this, "Không có thông tin hóa đơn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa hóa đơn này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    executorService.execute(() -> {
                        AppDatabase db = AppDatabase.getInstance(this);
                        db.paymentDao().deleteInvoiceItems(invoiceId);
                        db.paymentDao().deleteInvoice(invoiceId);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Đã xóa hóa đơn thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
        });

        findViewById(R.id.btnConfirmPayment).setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                .setTitle("Thu tiền mặt")
                .setMessage("Xác nhận đã thu đủ tiền mặt cho hóa đơn này?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    executorService.execute(() -> {
                        AppDatabase db = AppDatabase.getInstance(this);
                        Invoice inv = db.paymentDao().getInvoiceByIdSync(invoiceId);
                        if (inv != null && !"PAID".equals(inv.status)) {
                            db.paymentDao().markInvoiceAsPaid(invoiceId);
                            db.paymentDao().markAllInvoiceItemsAsPaid(invoiceId);
                            
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm", java.util.Locale.getDefault());
                            String time = sdf.format(new java.util.Date());
                            String transCode = "#CASH" + (System.currentTimeMillis() % 100000);
                            
                            com.example.quanlycudan_utehome.data.entity.TransactionHistory trans = 
                                new com.example.quanlycudan_utehome.data.entity.TransactionHistory(
                                    transCode, invoiceId, "Tiền mặt", time, inv.totalAmount, "SUCCESS"
                                );
                            db.paymentDao().insertTransaction(trans);
                        }
                        
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Đã xác nhận thu tiền!", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
        });

        loadInvoiceDetails();
    }

    private void loadInvoiceDetails() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            // using synchronous observe is not possible with LiveData easily on background
            // thread,
            // since getInvoiceById returns LiveData. We should use standard observer
            // instead.

            runOnUiThread(() -> {
                db.paymentDao().getInvoiceById(invoiceId).observe(this, this::bindInvoice);
                db.paymentDao().getInvoiceItemsDetails(invoiceId).observe(this, this::bindItems);
            });
        });
    }

    private void bindInvoice(Invoice invoice) {
        if (invoice == null)
            return;

        TextView tvStatusDetail = findViewById(R.id.tvStatusDetail);
        TextView tvTotalAmountDetail = findViewById(R.id.tvTotalAmountDetail);
        TextView tvInvoiceMetaDetail = findViewById(R.id.tvInvoiceMetaDetail);

        if ("PAID".equalsIgnoreCase(invoice.status)) {
            tvStatusDetail.setText("ĐÃ THANH TOÁN");
            tvStatusDetail.setTextColor(android.graphics.Color.parseColor("#1F7343"));
            tvStatusDetail.setBackgroundResource(R.drawable.bg_tag_paid);
            if (findViewById(R.id.btnEdit) != null) {
                findViewById(R.id.btnEdit).setAlpha(0.5f);
                findViewById(R.id.btnEdit).setEnabled(false);
            }
            if (findViewById(R.id.btnConfirmPayment) != null) {
                findViewById(R.id.btnConfirmPayment).setAlpha(0.5f);
                findViewById(R.id.btnConfirmPayment).setEnabled(false);
            }
        } else if ("PARTIALLY_PAID".equalsIgnoreCase(invoice.status)) {
            tvStatusDetail.setText("ĐÓNG 1 PHẦN");
            tvStatusDetail.setTextColor(android.graphics.Color.parseColor("#2B6CB0"));
            tvStatusDetail.setBackgroundResource(R.drawable.bg_tag_unpaid);
            if (findViewById(R.id.btnEdit) != null) {
                findViewById(R.id.btnEdit).setAlpha(1.0f);
                findViewById(R.id.btnEdit).setEnabled(true);
                findViewById(R.id.btnEdit).setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(this, ComposeInvoiceActivity.class);
                    intent.putExtra("edit_invoice_id", invoice.id);
                    startActivity(intent);
                });
            }
            if (findViewById(R.id.btnConfirmPayment) != null) {
                findViewById(R.id.btnConfirmPayment).setAlpha(1.0f);
                findViewById(R.id.btnConfirmPayment).setEnabled(true);
            }
        } else {
            tvStatusDetail.setText("CHƯA THANH TOÁN");
            tvStatusDetail.setTextColor(android.graphics.Color.parseColor("#C05030"));
            tvStatusDetail.setBackgroundResource(R.drawable.bg_tag_unpaid);
            if (findViewById(R.id.btnEdit) != null) {
                findViewById(R.id.btnEdit).setAlpha(1.0f);
                findViewById(R.id.btnEdit).setEnabled(true);
                findViewById(R.id.btnEdit).setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(this, ComposeInvoiceActivity.class);
                    intent.putExtra("edit_invoice_id", invoice.id);
                    startActivity(intent);
                });
            }
            if (findViewById(R.id.btnConfirmPayment) != null) {
                findViewById(R.id.btnConfirmPayment).setAlpha(1.0f);
                findViewById(R.id.btnConfirmPayment).setEnabled(true);
            }
        }

        tvTotalAmountDetail.setText(currencyFormat.format(invoice.totalAmount) + "đ");
        tvInvoiceMetaDetail.setText("#" + invoice.id + " • " + invoice.billingMonth);

        // We could fetch Apartment / Resident here for tvResidentNameDetail /
        // tvApartmentSpecDetail
        // But for mock data it's okay to let them be defaults, or we can fetch them.
    }

    private void bindItems(List<InvoiceItem> items) {
        if (items == null || items.isEmpty())
            return;

        TextView tvManagementFee = findViewById(R.id.tvManagementFee);
        TextView tvParkingFee = findViewById(R.id.tvParkingFee);
        TextView tvElectricityFee = findViewById(R.id.tvElectricityFee);
        TextView tvWaterFee = findViewById(R.id.tvWaterFee);

        for (InvoiceItem item : items) {
            String formatAmt = currencyFormat.format(item.amount) + "đ";
            boolean isPaid = "PAID".equalsIgnoreCase(item.status);
            if (isPaid) {
                formatAmt += " (Đã thu)";
            }
            
            String type = item.serviceType != null ? item.serviceType.toUpperCase() : "";
            
            if ("MANAGEMENT".equals(type) && tvManagementFee != null) {
                tvManagementFee.setText(formatAmt);
                if (isPaid) tvManagementFee.setTextColor(android.graphics.Color.parseColor("#1F7343"));
            } else if ("PARKING".equals(type) && tvParkingFee != null) {
                tvParkingFee.setText(formatAmt);
                if (isPaid) tvParkingFee.setTextColor(android.graphics.Color.parseColor("#1F7343"));
            } else if ("ELECTRIC".equals(type) && tvElectricityFee != null) {
                tvElectricityFee.setText(formatAmt);
                if (isPaid) tvElectricityFee.setTextColor(android.graphics.Color.parseColor("#1F7343"));
            } else if ("WATER".equals(type) && tvWaterFee != null) {
                tvWaterFee.setText(formatAmt);
                if (isPaid) tvWaterFee.setTextColor(android.graphics.Color.parseColor("#1F7343"));
            }
        }
    }
}
