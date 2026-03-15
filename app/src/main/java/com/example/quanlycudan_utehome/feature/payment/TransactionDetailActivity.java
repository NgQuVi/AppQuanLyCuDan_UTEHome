package com.example.quanlycudan_utehome.feature.payment;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.TransactionHistory;

public class TransactionDetailActivity extends AppCompatActivity {

    private TransactionHistory transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        transaction = (TransactionHistory) getIntent().getSerializableExtra("DATA_KEY");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transaction_detail);


    }
}
