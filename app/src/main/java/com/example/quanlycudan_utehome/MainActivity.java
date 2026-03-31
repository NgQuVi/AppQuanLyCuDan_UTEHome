package com.example.quanlycudan_utehome;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.Resident;
import com.example.quanlycudan_utehome.data.repository.ApartmentRepository;
import com.example.quanlycudan_utehome.feature.apartment.ApartmentInfoActivity;
import com.example.quanlycudan_utehome.feature.vehicle.VehicleManagementActivity;
import com.example.quanlycudan_utehome.feature.accesscard.AccessCardMemberListActivity;

public class MainActivity extends AppCompatActivity {
    private int currentApartmentId = 1;
    private java.util.List<Apartment> userApartments = new java.util.ArrayList<>();
    private boolean hasShownInvoiceAlert = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize sample data if empty
        com.example.quanlycudan_utehome.data.database.DatabaseInitializer.initializeSampleData(this);

        findViewById(R.id.cardApartment).setOnClickListener(v -> {
            if (currentApartmentId != -1) {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, ApartmentInfoActivity.class);
                intent.putExtra("APARTMENT_ID", currentApartmentId);
                startActivity(intent);
            } else {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.apartment.ApartmentInfoActivity.class);
                startActivity(intent);
            }
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
            } else if (item.getItemId() == R.id.nav_utilities) {
                startActivity(new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.facility.FacilityListActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_notification) {
                startActivity(new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.notification.NotificationListActivity.class));
                return true;
            }
            // Add other navigation logic here if needed
            return true;
        });

        // Thẻ cư dân: mở màn danh sách thành viên có thẻ ra vào
        findViewById(R.id.layoutAccessCard).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, AccessCardMemberListActivity.class);
            if (currentApartmentId != -1) {
                intent.putExtra("APARTMENT_ID", currentApartmentId);
                // in ra log để debug
                android.util.Log.d("MainActivity", "Opening AccessCardMemberListActivity with APARTMENT_ID: " + currentApartmentId);
            }
            startActivity(intent);
        });
        findViewById(R.id.layoutGuestQr).setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.guest.GuestQrActivity.class));
        });
        // Phương tiện
        findViewById(R.id.layoutVehicle).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, VehicleManagementActivity.class);
            intent.putExtra("accountId", 1);
            startActivity(intent);
        });
        findViewById(R.id.layoutFeedback).setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.feedback.FeedbackActivity.class));
        });

        // Xử lý nút Đăng xuất
        android.view.View btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> handleLogout());
        }

        // Xử lý sự kiện Event & Offer click
        android.view.View cardEvent = findViewById(R.id.cardEvent);
        if (cardEvent != null) {
            cardEvent.setOnClickListener(v -> showInfoDialog("Sự kiện", "Thông tin chi tiết về Lễ hội thả diều mùa hè. Thời gian diễn ra vào cuối tuần này tại công viên trung tâm."));
        }

        android.view.View cardOffer = findViewById(R.id.cardOffer);
        if (cardOffer != null) {
            cardOffer.setOnClickListener(v -> showInfoDialog("Ưu đãi", "Giảm ngay 20% phí gửi xe tháng tới dành cho mọi cư dân thanh toán trước ngày 15."));
        }

        loadUserData();
    }

    private void loadUserData() {
        int residentId = com.example.quanlycudan_utehome.data.local.SessionManager.getInstance(this).getResidentId();
        if (residentId == -1) {
            // No valid session, redirect to login
            startActivity(new android.content.Intent(this, com.example.quanlycudan_utehome.feature.auth.login.LoginActivity.class));
            finish();
            return;
        }

        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Resident user = db.residentDao().getResidentById(residentId);

            java.util.List<Integer> apartmentIds = db.apartmentMemberDao().getApartmentIdsByResidentId(residentId);
            if ((apartmentIds == null || apartmentIds.isEmpty()) && user != null && user.accountId > 0) {
                Integer legacyApartmentId = db.apartmentDao().getApartmentIdByAccountId(user.accountId);
                if (legacyApartmentId != null) {
                    apartmentIds = new java.util.ArrayList<>();
                    apartmentIds.add(legacyApartmentId);
                }
            }
            java.util.List<Apartment> apartments = new java.util.ArrayList<>();
            if (apartmentIds != null) {
                for (int id : apartmentIds) {
                    Apartment ap = db.apartmentDao().getApartmentById(id);
                    if (ap != null) apartments.add(ap);
                }
            }

            if (user != null) {
                runOnUiThread(() -> {
                    TextView tvUserName = findViewById(R.id.tvUserName);
                    if (tvUserName != null) {
                        tvUserName.setText(user.fullName);
                    }

                    userApartments.clear();
                    userApartments.addAll(apartments);

                    if (!userApartments.isEmpty()) {
                        setupApartmentDropdown(userApartments.get(0));
                    } else {
                        TextView tvApartmentName = findViewById(R.id.tvApartmentName);
                        if (tvApartmentName != null) {
                            tvApartmentName.setText("No Apartment");
                        }
                        com.example.quanlycudan_utehome.data.local.SessionManager.getInstance(this).saveApartmentId("");
                    }
                });
            }
        });
    }

    private void setupApartmentDropdown(Apartment selectedApartment) {
        currentApartmentId = selectedApartment.id;
        com.example.quanlycudan_utehome.data.local.SessionManager.getInstance(this)
                .saveApartmentId(String.valueOf(selectedApartment.id));
        TextView tvApartmentName = findViewById(R.id.tvApartmentName);
        if (tvApartmentName != null) {
            tvApartmentName.setText(selectedApartment.apartmentCode + ", Tòa " + selectedApartment.buildingCode);
        }

        com.example.quanlycudan_utehome.data.repository.PaymentRepository payRepo =
                new com.example.quanlycudan_utehome.data.repository.PaymentRepository(getApplication());

        payRepo.getUnpaidInvoices(String.valueOf(currentApartmentId)).observe(this, invoices -> {
            TextView tvFinancialStatus = findViewById(R.id.tvFinancialStatus);
            TextView tvFinancialDesc = findViewById(R.id.tvFinancialDesc);
            android.widget.ImageView ivCheckMark = findViewById(R.id.ivCheckMark);

            if (invoices != null && !invoices.isEmpty()) {
                // Có hóa đơn nợ
                tvFinancialStatus.setText("Cần thanh toán");
                tvFinancialStatus.setTextColor(android.graphics.Color.parseColor("#FFD54F")); // Đổi màu vàng cảnh báo
                tvFinancialDesc.setText("Bạn đang có hóa đơn phí dịch vụ cần được thanh toán ngay.");
                ivCheckMark.setImageResource(R.drawable.ic_history_outline); // Đổi icon cảnh báo (có thể dùng icon khác tùy bạn)

                // Show an alert dialog if we haven't shown it yet
                if (!hasShownInvoiceAlert) {
                    hasShownInvoiceAlert = true;
                    new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle("Thông báo cước phí")
                            .setMessage("Bạn đang có " + invoices.size() + " tháng phí dịch vụ chưa thanh toán (hoặc đóng thiếu) cho căn hộ " + selectedApartment.apartmentCode + ". Vui lòng kiểm tra và thanh toán để tránh gián đoạn dịch vụ.")
                            .setPositiveButton("Đến trang Hóa đơn", (dialog, which) -> {
                                android.content.Intent intent = new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.invoice.InvoiceActivity.class);
                                startActivity(intent);
                            })
                            .setNegativeButton("Đóng", null)
                            .show();
                }
            } else {
                // Không có hóa đơn nợ
                tvFinancialStatus.setText("Không có hóa đơn");
                tvFinancialStatus.setTextColor(android.graphics.Color.WHITE);
                tvFinancialDesc.setText("Bạn đã đóng đủ phí tháng hiện tại.");
                ivCheckMark.setImageResource(R.drawable.ic_check_circle_orange);
            }
        });

        android.widget.ImageView ivDropdownArrow = findViewById(R.id.ivDropdownArrow);
        if (ivDropdownArrow != null) {
            ivDropdownArrow.setVisibility(android.view.View.VISIBLE);
            android.view.View layoutApartmentName = findViewById(R.id.layoutApartmentName);
            if (layoutApartmentName != null) {
                // Always set clickable, even if only 1 apartment, so user can see it works
                layoutApartmentName.setOnClickListener(v -> {
                    android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, layoutApartmentName);
                    for (int i = 0; i < userApartments.size(); i++) {
                        Apartment ap = userApartments.get(i);
                        popupMenu.getMenu().add(0, i, i, ap.apartmentCode + ", Tòa " + ap.buildingCode);
                    }
                    popupMenu.setOnMenuItemClickListener(item -> {
                        Apartment newlySelected = userApartments.get(item.getItemId());
                        setupApartmentDropdown(newlySelected);
                        return true;
                    });
                    popupMenu.show();
                });
            }
        }
    }

    private void handleLogout() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    com.example.quanlycudan_utehome.data.local.SessionManager.getInstance(this).logout();
                    android.content.Intent intent = new android.content.Intent(MainActivity.this, com.example.quanlycudan_utehome.feature.auth.login.LoginActivity.class);
                    intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showInfoDialog(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Đóng", null)
                .show();
    }

}
