package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Invoice;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComposeInvoiceActivity extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_compose_invoice);

        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnSaveInvoice).setOnClickListener(v -> saveInvoice());
    }

    private void saveInvoice() {
        executorService.execute(() -> {
            // For Demo: generating a hardcoded total amount and linking to default apartment "1"
            String newId = "INV-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            Invoice invoice = new Invoice(newId, "1", "10/2023", 3206000, "11/05/2023", "UNPAID");
            
            AppDatabase.getInstance(this).paymentDao().insertInvoice(invoice);
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Đã tạo hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
