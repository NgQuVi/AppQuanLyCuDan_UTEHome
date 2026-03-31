package com.example.quanlycudan_utehome.feature.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycudan_utehome.MainActivity;
import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.repository.PaymentRepository;

public class PaymentConfirmationActivity extends AppCompatActivity {
    private PaymentRepository paymentRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        paymentRepository = new PaymentRepository(getApplication());
        // Bind back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Get total sum from Intent
        long totalSum = getIntent().getLongExtra("TOTAL_SUM", 0L); // Default to a mocked value if not passed
        String formattedTotal = String.format("%,dđ", totalSum).replace(',', '.');

        String invoiceId = getIntent().getStringExtra("INVOICE_ID");

        // Setup Invoice Details Visibility
        boolean hasElec = getIntent().getBooleanExtra("HAS_ELEC", true);
        boolean hasWater = getIntent().getBooleanExtra("HAS_WATER", true);
        boolean hasPark = getIntent().getBooleanExtra("HAS_PARK", true);
        boolean hasInternet = getIntent().getBooleanExtra("HAS_INTERNET", true);

        View cardElec = findViewById(R.id.cardElec);
        View cardWater = findViewById(R.id.cardWater);
        View cardPark = findViewById(R.id.cardPark);
        View cardInternet = findViewById(R.id.cardInternet);

        if (cardElec != null) cardElec.setVisibility(hasElec ? View.VISIBLE : View.GONE);
        if (cardWater != null) cardWater.setVisibility(hasWater ? View.VISIBLE : View.GONE);
        if (cardPark != null) cardPark.setVisibility(hasPark ? View.VISIBLE : View.GONE);
        if (cardInternet != null) cardInternet.setVisibility(hasInternet ? View.VISIBLE : View.GONE);

        // Update UI with calculated total
        TextView tvSubtotalVal = findViewById(R.id.tvSubtotalVal);
        TextView tvTotalValue = findViewById(R.id.tvTotalValue);
        if (tvSubtotalVal != null) tvSubtotalVal.setText(formattedTotal);
        if (tvTotalValue != null) tvTotalValue.setText(formattedTotal);

        // Removed old Payment Method Radio Buttons logic because VNPAY is now the only method.

        // Setup Confirm Payment Button
        findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            Intent intent = new Intent(PaymentConfirmationActivity.this, VNPayWebActivity.class);
            intent.putExtra("INVOICE_ID", invoiceId);
            intent.putExtra("TOTAL_SUM", totalSum);
            intent.putExtra("HAS_ELEC", hasElec);
            intent.putExtra("HAS_WATER", hasWater);
            intent.putExtra("HAS_PARK", hasPark);
            intent.putExtra("HAS_INTERNET", hasInternet);
            startActivity(intent);
        });

    }
}
