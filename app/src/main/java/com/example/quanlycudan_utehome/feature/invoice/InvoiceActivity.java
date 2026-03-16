package com.example.quanlycudan_utehome.feature.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.feature.payment.PaymentHistoryActivity;

public class InvoiceActivity extends AppCompatActivity {

    private CheckBox cbElec, cbWater, cbPark, cbInternet;
    private TextView tvSumValue;
    
    // Hardcoded prices matching the UI design for simplicity
    private final int PRICE_ELEC = 472500;
    private final int PRICE_WATER = 270000;
    private final int PRICE_PARK = 1200000;
    private final int PRICE_INTERNET = 350000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // Bind views
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        // Nút lịch sử chuyển sang PaymentHistoryActivity
        findViewById(R.id.btnHistory).setOnClickListener(v -> {
            Intent intent = new Intent(InvoiceActivity.this, PaymentHistoryActivity.class);
            startActivity(intent);
        });

        cbElec = findViewById(R.id.cbElec);
        cbWater = findViewById(R.id.cbWater);
        cbPark = findViewById(R.id.cbPark);
        cbInternet = findViewById(R.id.cbInternet);
        tvSumValue = findViewById(R.id.tvSumValue);

        // Setup CheckBox Listeners
        cbElec.setOnCheckedChangeListener((buttonView, isChecked) -> calculateTotal());
        cbWater.setOnCheckedChangeListener((buttonView, isChecked) -> calculateTotal());
        cbPark.setOnCheckedChangeListener((buttonView, isChecked) -> calculateTotal());
        cbInternet.setOnCheckedChangeListener((buttonView, isChecked) -> calculateTotal());

        // Setup Pay Button
        findViewById(R.id.btnPay).setOnClickListener(v -> {
            Intent intent = new Intent(InvoiceActivity.this, PaymentConfirmationActivity.class);
            // Optionally pass the total sum to the next screen if needed
            intent.putExtra("TOTAL_SUM", calculateCurrentTotal());
            intent.putExtra("HAS_ELEC", cbElec.isChecked());
            intent.putExtra("HAS_WATER", cbWater.isChecked());
            intent.putExtra("HAS_PARK", cbPark.isChecked());
            intent.putExtra("HAS_INTERNET", cbInternet.isChecked());
            startActivity(intent);
        });

        // Initial calculation
        calculateTotal();
    }

    private int calculateCurrentTotal() {
        int total = 0;
        if (cbElec.isChecked()) total += PRICE_ELEC;
        if (cbWater.isChecked()) total += PRICE_WATER;
        if (cbPark.isChecked()) total += PRICE_PARK;
        if (cbInternet.isChecked()) total += PRICE_INTERNET;
        return total;
    }

    private void calculateTotal() {
        int total = calculateCurrentTotal();
        // Format as VNĐ currency style, e.g. 2.292.500đ
        tvSumValue.setText(String.format("%,dđ", total).replace(',', '.'));
    }
}
