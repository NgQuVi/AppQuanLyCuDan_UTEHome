package com.example.quanlycudan_utehome.feature.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycudan_utehome.MainActivity;
import com.example.quanlycudan_utehome.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        // Get details from Intent
        long totalSum = getIntent().getLongExtra("TOTAL_SUM", 0L);

        String formattedTotal = String.format("%,dđ", totalSum).replace(',', '.');
        String method = getIntent().getStringExtra("METHOD");
        if (method == null) method = "Ví MoMo";

        // Update UI
        TextView valTotal = findViewById(R.id.valTotal);
        TextView valMethod = findViewById(R.id.valMethod);
        TextView valTime = findViewById(R.id.valTime);

        if (valTotal != null) valTotal.setText(formattedTotal);
        if (valMethod != null) {
            valMethod.setText(method);
            int methodIconRes = R.drawable.ic_momo;
            if ("Thẻ ngân hàng".equals(method)) {
                methodIconRes = R.drawable.ic_bank_card;
            } else if ("Chuyển khoản".equals(method)) {
                methodIconRes = R.drawable.ic_transfer;
            }
            valMethod.setCompoundDrawablesWithIntrinsicBounds(methodIconRes, 0, 0, 0);
        }
        
        // Set current time
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
        if (valTime != null) valTime.setText(sdf.format(new Date()));

        // Setup Home Button
        findViewById(R.id.btnHome).setOnClickListener(v -> {
            // Navigate back to the home screen, clearing the back stack
            Intent intent = new Intent(PaymentSuccessActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
    
    @Override
    public void onBackPressed() {
        // Prevent going back to confirmation or invoice screen, redirect to home instead
        super.onBackPressed();
        Intent intent = new Intent(PaymentSuccessActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
