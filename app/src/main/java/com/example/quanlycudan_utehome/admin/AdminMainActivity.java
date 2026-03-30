package com.example.quanlycudan_utehome.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.local.SessionManager;
import com.example.quanlycudan_utehome.feature.auth.login.LoginActivity;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminMainActivity extends AppCompatActivity {

    private TextView tvTotalResidents, tvTotalApartments, tvApartmentStatus, tvTotalRevenue;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTotalResidents = findViewById(R.id.tvTotalResidents);
        tvTotalApartments = findViewById(R.id.tvTotalApartments);
        tvApartmentStatus = findViewById(R.id.tvApartmentStatus);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);

        // Optional quick actions setup
        findViewById(R.id.btnAdminNotification).setOnClickListener(v -> 
            startActivity(new android.content.Intent(this, NotificationManagementActivity.class))
        );
        findViewById(R.id.btnAdminInvoice).setOnClickListener(v -> 
            startActivity(new android.content.Intent(this, InvoiceManagementActivity.class))
        );
        findViewById(R.id.btnAdminFeedback).setOnClickListener(v -> 
            Toast.makeText(this, "Chức năng quản lý phản ánh", Toast.LENGTH_SHORT).show()
        );

        // Dashboard Stats Click Listeners
        View cardResidents = findViewById(R.id.cardTotalResidents);
        if (cardResidents != null) {
            cardResidents.setOnClickListener(v -> {
                startActivity(new android.content.Intent(this, ResidentListActivity.class));
            });
        }

        View cardApartments = findViewById(R.id.cardTotalApartments);
        if (cardApartments != null) {
            cardApartments.setOnClickListener(v -> {
                startActivity(new android.content.Intent(this, ApartmentListActivity.class));
            });
        }
        
        View cardApartmentStatus = findViewById(R.id.cardApartmentStatus);
        if (cardApartmentStatus != null) {
            cardApartmentStatus.setOnClickListener(v -> {
                startActivity(new android.content.Intent(this, OccupancyListActivity.class));
            });
        }

        LinearLayout navManage = findViewById(R.id.navManage);
        navManage.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, ManageActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        LinearLayout navSettings = findViewById(R.id.navSettings);
        navSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng cài đặt", Toast.LENGTH_SHORT).show();
        });

        // Admin Logout Button
        findViewById(R.id.btnAdminLogout).setOnClickListener(v -> showLogoutConfirmDialog());

        loadDashboardData();
    }

    private void loadDashboardData() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            
            int totalResidents = db.residentDao().getResidentCount();
            int totalApartments = db.apartmentDao().getApartmentCount();
            int vacantCount = db.apartmentDao().getApartmentCountByStatus("Trống");
            int occupiedCount = db.apartmentDao().getApartmentCountByStatus("Đang sử dụng");
            long totalRevenue = db.paymentDao().getTotalPaidAmount();

            runOnUiThread(() -> {
                tvTotalResidents.setText(new DecimalFormat("#,###").format(totalResidents));
                tvTotalApartments.setText(new DecimalFormat("#,###").format(totalApartments));
                tvApartmentStatus.setText(vacantCount + " / " + occupiedCount);
                tvTotalRevenue.setText(formatCurrency(totalRevenue));
            });
        });
    }

    private String formatCurrency(long amount) {
        if (amount >= 1_000_000_000) {
            double v = amount / 1_000_000_000.0;
            return new DecimalFormat("#.##").format(v) + "B VNĐ";
        } else if (amount >= 1_000_000) {
            double v = amount / 1_000_000.0;
            return new DecimalFormat("#.##").format(v) + "M VNĐ";
        } else {
            return new DecimalFormat("#,###").format(amount) + " VNĐ";
        }
    }

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản quản trị không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        SessionManager.getInstance(this).logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
