package com.example.quanlycudan_utehome;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.repository.ApartmentRepository;
import com.example.quanlycudan_utehome.feature.apartment.ApartmentInfoActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apartment_info);


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

//        findViewById(R.id.cardApartment).setOnClickListener(v -> {
//            android.content.Intent intent = new android.content.Intent(MainActivity.this, ApartmentInfoActivity.class);
//            startActivity(intent);
//        });


        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.cardApartment).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.apartment.ApartmentInfoActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardFinancial).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.invoice.InvoiceActivity.class);
            startActivity(intent);
        });

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.profile.ProfileActivity.class));
                return true;
            }
            // Add other navigation logic here if needed
            return true;
        });
    }


}

