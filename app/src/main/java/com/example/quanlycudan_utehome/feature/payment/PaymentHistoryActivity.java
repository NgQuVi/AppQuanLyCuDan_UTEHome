package com.example.quanlycudan_utehome.feature.payment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.repository.PaymentRepository;

import java.util.ArrayList;

public class PaymentHistoryActivity extends AppCompatActivity {

    // 1. Thêm các biến khai báo đầu class
    private PaymentHistoryAdapter adapter;
    private PaymentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        // Xử lý nút quay lại
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        // 2. Thiết lập RecyclerView
        RecyclerView rvHistory = findViewById(R.id.rvPaymentHistory);
        // Khởi tạo adapter với danh sách rỗng ban đầu
        adapter = new PaymentHistoryAdapter(new ArrayList<>(), tx -> {
            // Xử lý khi nhấn vào: Chuyển sang màn hình chi tiết
            Intent intent = new Intent(this, TransactionDetailActivity.class);
            intent.putExtra("DATA_KEY", tx);
            // Truyền ID hóa đơn sang màn hình sau
            startActivity(intent);
        });
        rvHistory.setAdapter(adapter);

        // 3. Kết nối Repository và Lắng nghe dữ liệu (LiveData)
        repository = new PaymentRepository(getApplication());
        repository.getPaymentHistory().observe(this, list -> {
            if (list != null) {
                adapter.setTransactions(list); // Tự động cập nhật danh sách khi DB thay đổi
            }
        });
    }

}
