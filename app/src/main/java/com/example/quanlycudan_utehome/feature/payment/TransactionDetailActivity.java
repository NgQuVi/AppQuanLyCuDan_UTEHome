package com.example.quanlycudan_utehome.feature.payment;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.TransactionHistory;
import com.example.quanlycudan_utehome.data.entity.InvoiceItem;
import com.example.quanlycudan_utehome.data.repository.PaymentRepository;

import java.text.DecimalFormat;
import java.util.List;

public class TransactionDetailActivity extends AppCompatActivity {

    // 1. Khai báo các biến để điều khiển Giao diện (TextView)
    private TextView tvHeaderTotalAmount, tvFooterTotalAmount;
    private TextView tvTransactionCodeDetail, tvTransactionDateDetail, tvPaymentMethodDetail;
    private TextView tvElectricAmountDetail, tvWaterAmountDetail;
    private TextView tvParkingAmountDetail, tvInternetAmountDetail; // ← THÊM
    private TextView tvApartmentNameDetail;                         // ← THÊM

    // 2. Khai báo biến dữ liệu
    private TransactionHistory transaction;
    private PaymentRepository repository;
    private DecimalFormat df = new DecimalFormat("#,###"); // Bộ định dạng tiền tệ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        // Bước 1: Ánh xạ các TextView (Nối Java với XML qua ID bạn vừa thêm)
        initViews();

        // Bước 2: Nhận "Hộp quà" (Dữ liệu) từ màn hình Lịch sử gửi sang
        transaction = (TransactionHistory) getIntent().getSerializableExtra("DATA_KEY");

        // Bước 3: Khởi tạo Repository để truy cập cơ sở dữ liệu
        repository = new PaymentRepository(getApplication());

        // Bước 4: Kiểm tra và hiển thị dữ liệu
        if (transaction != null) {
            displayBasicInfo(); // Hiện thông tin có sẵn trong Transaction
            fetchAndDisplayDetails(); // Đi vào DB lấy thêm tiền điện, nước
        }

        // Xử lý nút quay lại
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        tvHeaderTotalAmount     = findViewById(R.id.tvHeaderTotalAmount);
        tvFooterTotalAmount     = findViewById(R.id.tvFooterTotalAmount);
        tvTransactionCodeDetail = findViewById(R.id.tvTransactionCodeDetail);
        tvTransactionDateDetail = findViewById(R.id.tvTransactionDateDetail);
        tvPaymentMethodDetail   = findViewById(R.id.tvPaymentMethodDetail);
        tvElectricAmountDetail  = findViewById(R.id.tvElectricAmountDetail);
        tvWaterAmountDetail     = findViewById(R.id.tvWaterAmountDetail);
        tvParkingAmountDetail   = findViewById(R.id.tvParkingAmountDetail);  // ← THÊM
        tvInternetAmountDetail  = findViewById(R.id.tvInternetAmountDetail); // ← THÊM
        tvApartmentNameDetail   = findViewById(R.id.tvApartmentNameDetail);  // ← THÊM
    }

    private void displayBasicInfo() {
        // Gán dữ liệu có sẵn từ màn hình trước vào TextView
        tvHeaderTotalAmount.setText(df.format(transaction.paidAmount) + "đ");
        tvFooterTotalAmount.setText(df.format(transaction.paidAmount) + "đ");
        tvTransactionCodeDetail.setText(transaction.transactionCode);
        tvTransactionDateDetail.setText(transaction.transactionTime);
        tvPaymentMethodDetail.setText(transaction.paymentMethod);
    }

    private void fetchAndDisplayDetails() {
        repository.getInvoiceItemsDetails(transaction.invoiceId).observe(this, items -> {
            if (items == null) return;
            for (InvoiceItem item : items) {
                switch (item.serviceType) {
                    case "ELECTRIC":
                        tvElectricAmountDetail.setText(df.format(item.amount) + "đ");
                        break;
                    case "WATER":
                        tvWaterAmountDetail.setText(df.format(item.amount) + "đ");
                        break;
                    case "PARKING":
                        tvParkingAmountDetail.setText(df.format(item.amount) + "đ");
                        break;
                    case "INTERNET":
                        tvInternetAmountDetail.setText(df.format(item.amount) + "đ");
                        break;
                }
            }
        });

        // Hiển thị tên căn hộ dựa vào invoiceId → lấy invoice → lấy apartmentId
        repository.getInvoiceById(transaction.invoiceId).observe(this, invoice -> {
            if (invoice != null && tvApartmentNameDetail != null) {
                // apartmentId = "1" → hiển thị "Căn hộ #1 (P.1205)"
                // Nếu muốn tên đẹp hơn, cần thêm query join apartments
                tvApartmentNameDetail.setText("Căn hộ #" + invoice.apartmentId);
            }
        });
    }
}
