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
        // Ánh xạ các TextView bằng ID bạn đã thêm vào file XML
        tvHeaderTotalAmount = findViewById(R.id.tvHeaderTotalAmount);
        tvFooterTotalAmount = findViewById(R.id.tvFooterTotalAmount);
        tvTransactionCodeDetail = findViewById(R.id.tvTransactionCodeDetail);
        tvTransactionDateDetail = findViewById(R.id.tvTransactionDateDetail);
        tvPaymentMethodDetail = findViewById(R.id.tvPaymentMethodDetail);
        tvElectricAmountDetail = findViewById(R.id.tvElectricAmountDetail);
        tvWaterAmountDetail = findViewById(R.id.tvWaterAmountDetail);
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
        // Dùng invoiceId để đi tìm chi tiết tiền điện, tiền nước trong bảng invoice_items
        repository.getInvoiceItemsDetails(transaction.invoiceId).observe(this, items -> {
            if (items != null) {
                for (InvoiceItem item : items) {
                    // Nếu là Tiền điện
                    if (item.serviceType.equals("ELECTRIC")) {
                        tvElectricAmountDetail.setText(df.format(item.amount) + "đ");
                    }
                    // Nếu là Tiền nước
                    if (item.serviceType.equals("WATER")) {
                        tvWaterAmountDetail.setText(df.format(item.amount) + "đ");
                    }
                }
            }
        });
    }
}
