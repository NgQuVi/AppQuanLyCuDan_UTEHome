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
            Toast.makeText(this, "Xóa hóa đơn " + invoiceId, Toast.LENGTH_SHORT).show();
            finish();
        });

        loadInvoiceDetails();
    }

    private void loadInvoiceDetails() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            // using synchronous observe is not possible with LiveData easily on background thread, 
            // since getInvoiceById returns LiveData. We should use standard observer instead.
            
            runOnUiThread(() -> {
                db.paymentDao().getInvoiceById(invoiceId).observe(this, this::bindInvoice);
                db.paymentDao().getInvoiceItemsDetails(invoiceId).observe(this, this::bindItems);
            });
        });
    }

    private void bindInvoice(Invoice invoice) {
        if (invoice == null) return;
        
        TextView tvStatusDetail = findViewById(R.id.tvStatusDetail);
        TextView tvTotalAmountDetail = findViewById(R.id.tvTotalAmountDetail);
        TextView tvInvoiceMetaDetail = findViewById(R.id.tvInvoiceMetaDetail);
        
        if ("PAID".equalsIgnoreCase(invoice.status)) {
            tvStatusDetail.setText("ĐÃ THANH TOÁN");
            tvStatusDetail.setTextColor(android.graphics.Color.parseColor("#1F7343"));
            tvStatusDetail.setBackgroundResource(R.drawable.bg_tag_paid);
            findViewById(R.id.btnEdit).setAlpha(0.5f);
            findViewById(R.id.btnEdit).setEnabled(false);
        } else if ("PARTIALLY_PAID".equalsIgnoreCase(invoice.status)) {
            tvStatusDetail.setText("ĐÓNG 1 PHẦN");
            tvStatusDetail.setTextColor(android.graphics.Color.parseColor("#2B6CB0"));
            tvStatusDetail.setBackgroundResource(R.drawable.bg_tag_unpaid);
            findViewById(R.id.btnEdit).setAlpha(0.5f);
            findViewById(R.id.btnEdit).setEnabled(false);
        } else {
            tvStatusDetail.setText("CHƯA THANH TOÁN");
            tvStatusDetail.setTextColor(android.graphics.Color.parseColor("#C05030"));
            tvStatusDetail.setBackgroundResource(R.drawable.bg_tag_unpaid);
            findViewById(R.id.btnEdit).setAlpha(1.0f);
            findViewById(R.id.btnEdit).setEnabled(true);
            findViewById(R.id.btnEdit).setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(this, ComposeInvoiceActivity.class);
                intent.putExtra("edit_invoice_id", invoice.id);
                startActivity(intent);
            });
        }

        tvTotalAmountDetail.setText(currencyFormat.format(invoice.totalAmount) + "đ");
        tvInvoiceMetaDetail.setText("#" + invoice.id + " • " + invoice.billingMonth);
        
        // We could fetch Apartment / Resident here for tvResidentNameDetail / tvApartmentSpecDetail
        // But for mock data it's okay to let them be defaults, or we can fetch them.
    }

    private void bindItems(List<InvoiceItem> items) {
        if (items == null || items.isEmpty()) return;

        TextView tvManagementFee = findViewById(R.id.tvManagementFee);
        TextView tvParkingFee = findViewById(R.id.tvParkingFee);
        TextView tvElectricityFee = findViewById(R.id.tvElectricityFee);
        TextView tvWaterFee = findViewById(R.id.tvWaterFee);

        for (InvoiceItem item : items) {
            String formatAmt = currencyFormat.format(item.amount) + "đ";
            String name = item.serviceType != null ? item.serviceType.toLowerCase() : "";
            if (name.contains("quản lý") && tvManagementFee != null) {
                tvManagementFee.setText(formatAmt);
            } else if (name.contains("gửi xe") && tvParkingFee != null) {
                tvParkingFee.setText(formatAmt);
            } else if (name.contains("điện") && tvElectricityFee != null) {
                tvElectricityFee.setText(formatAmt);
            } else if (name.contains("nước") && tvWaterFee != null) {
                tvWaterFee.setText(formatAmt);
            }
        }
    }
}
