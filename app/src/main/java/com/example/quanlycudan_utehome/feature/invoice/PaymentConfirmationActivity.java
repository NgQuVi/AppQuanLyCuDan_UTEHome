package com.example.quanlycudan_utehome.feature.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycudan_utehome.MainActivity;
import com.example.quanlycudan_utehome.R;

public class PaymentConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        // Bind back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Get total sum from Intent
        int totalSum = getIntent().getIntExtra("TOTAL_SUM", 4380000); // Default to a mocked value if not passed
        String formattedTotal = String.format("%,dđ", totalSum).replace(',', '.');

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

        // Setup Payment Method Radio Buttons
        View cardMomo = findViewById(R.id.cardMomo);
        View cardBank = findViewById(R.id.cardBank);
        View cardTransfer = findViewById(R.id.cardTransfer);
        
        RadioButton rbMomo = findViewById(R.id.rbMomo);
        RadioButton rbBank = findViewById(R.id.rbBank);
        RadioButton rbTransfer = findViewById(R.id.rbTransfer);

        cardMomo.setOnClickListener(v -> {
            rbMomo.setChecked(true);
            rbBank.setChecked(false);
            if (rbTransfer != null) rbTransfer.setChecked(false);
        });

        cardBank.setOnClickListener(v -> {
            rbMomo.setChecked(false);
            rbBank.setChecked(true);
            if (rbTransfer != null) rbTransfer.setChecked(false);
        });
        
        if (cardTransfer != null) {
            cardTransfer.setOnClickListener(v -> {
                rbMomo.setChecked(false);
                rbBank.setChecked(false);
                if (rbTransfer != null) rbTransfer.setChecked(true);
            });
        }

        // Setup Confirm Payment Button
        findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            Intent intent = new Intent(PaymentConfirmationActivity.this, PaymentSuccessActivity.class);
            intent.putExtra("TOTAL_SUM", totalSum);
            
            String selectedMethod = "Ví MoMo";
            if (rbBank.isChecked()) selectedMethod = "Thẻ ngân hàng";
            else if (rbTransfer != null && rbTransfer.isChecked()) selectedMethod = "Chuyển khoản";
            
            intent.putExtra("METHOD", selectedMethod);
            startActivity(intent);
            // Finish this activity so you can't go back to confirmation from success screen
            finish();
        });
    }
}
