package com.example.quanlycudan_utehome.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.AppNotification;
import com.example.quanlycudan_utehome.data.entity.FacilityBooking;
import com.example.quanlycudan_utehome.data.entity.FacilityBookingRow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FacilityBookingAdminActivity extends AppCompatActivity {

    private RecyclerView rvBookings;
    private FacilityBookingAdminAdapter adapter;
    private List<FacilityBookingRow> allBookings = new ArrayList<>();
    private String currentFilter = "ALL";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private TextView chipAll, chipPending, chipApproved, chipRejected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_facility_booking_admin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Navigate to Facility Management (CRUD)
        findViewById(R.id.btnManageFacilities).setOnClickListener(v -> {
            startActivity(new Intent(this, FacilityManagementActivity.class));
        });

        // Filter chips
        chipAll = findViewById(R.id.chipAll);
        chipPending = findViewById(R.id.chipPending);
        chipApproved = findViewById(R.id.chipApproved);
        chipRejected = findViewById(R.id.chipRejected);

        chipAll.setOnClickListener(v -> setFilter("ALL"));
        chipPending.setOnClickListener(v -> setFilter("PENDING"));
        chipApproved.setOnClickListener(v -> setFilter("APPROVED"));
        chipRejected.setOnClickListener(v -> setFilter("REJECTED"));

        // RecyclerView
        rvBookings = findViewById(R.id.rvBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FacilityBookingAdminAdapter();
        rvBookings.setAdapter(adapter);

        adapter.setOnActionListener(new FacilityBookingAdminAdapter.OnActionListener() {
            @Override
            public void onApprove(FacilityBookingRow booking) {
                showApproveConfirmDialog(booking);
            }

            @Override
            public void onReject(FacilityBookingRow booking) {
                showRejectDialog(booking);
            }
        });

        loadBookings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }

    private void loadBookings() {
        executorService.execute(() -> {
            List<FacilityBookingRow> rows = AppDatabase.getInstance(this)
                    .facilityBookingDao().getAdminFacilityBookings();
            allBookings = rows != null ? rows : new ArrayList<>();
            runOnUiThread(() -> applyFilter());
        });
    }

    private void setFilter(String filter) {
        currentFilter = filter;

        // Reset all chips
        int inactiveColor = Color.parseColor("#718096");
        int inactiveBg = R.drawable.bg_compose_chip;

        chipAll.setBackgroundResource(inactiveBg);
        chipAll.setTextColor(inactiveColor);
        chipPending.setBackgroundResource(inactiveBg);
        chipPending.setTextColor(inactiveColor);
        chipApproved.setBackgroundResource(inactiveBg);
        chipApproved.setTextColor(inactiveColor);
        chipRejected.setBackgroundResource(inactiveBg);
        chipRejected.setTextColor(inactiveColor);

        // Highlight active
        TextView activeChip = null;
        switch (filter) {
            case "ALL":     activeChip = chipAll; break;
            case "PENDING": activeChip = chipPending; break;
            case "APPROVED": activeChip = chipApproved; break;
            case "REJECTED": activeChip = chipRejected; break;
        }
        if (activeChip != null) {
            activeChip.setBackgroundResource(R.drawable.bg_pill_orange);
            activeChip.setTextColor(Color.WHITE);
        }

        applyFilter();
    }

    private void applyFilter() {
        List<FacilityBookingRow> filtered = new ArrayList<>();
        for (FacilityBookingRow b : allBookings) {
            String status = b.status != null ? b.status.toUpperCase() : "PENDING";
            if ("ALL".equals(currentFilter)) {
                filtered.add(b);
            } else if ("REJECTED".equals(currentFilter)) {
                if ("REJECTED".equals(status) || "CANCELLED".equals(status)) filtered.add(b);
            } else {
                if (currentFilter.equals(status)) filtered.add(b);
            }
        }
        adapter.setData(filtered);
    }

    private void showApproveConfirmDialog(FacilityBookingRow booking) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận duyệt")
                .setMessage("Duyệt lịch đặt \"" + booking.facilityName + "\" của " + booking.residentName + "?")
                .setPositiveButton("Duyệt", (dialog, which) -> {
                    updateBookingStatus(booking, "APPROVED", null);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showRejectDialog(FacilityBookingRow booking) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cancel_booking, null);
        EditText etReason = view.findViewById(R.id.etReason);

        new AlertDialog.Builder(this)
                .setTitle("Từ chối lịch đặt")
                .setView(view)
                .setPositiveButton("Từ chối", (dialog, which) -> {
                    String reason = etReason.getText().toString().trim();
                    if (reason.isEmpty()) reason = "Admin từ chối";
                    updateBookingStatus(booking, "REJECTED", reason);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateBookingStatus(FacilityBookingRow bookingRow, String newStatus, String reason) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // 1. Update booking status
            db.facilityBookingDao().updateBookingStatus(bookingRow.bookingId, newStatus, reason);

            // 2. Push notification to resident
            AppNotification notif = new AppNotification();
            notif.type = "UTILITY";
            notif.timestamp = System.currentTimeMillis();
            notif.isRead = false;

            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            notif.dateStr = currentDate;
            notif.timeStr = currentTime;

            if ("APPROVED".equals(newStatus)) {
                notif.title = "Lịch đặt tiện ích được duyệt";
                notif.shortDescription = "Lịch đặt " + bookingRow.facilityName + " của bạn vào " +
                        (bookingRow.DayBooking != null ? bookingRow.DayBooking : bookingRow.bookingDate) +
                        " (" + bookingRow.startTime + " - " + bookingRow.endTime + ") đã được phê duyệt.";
                notif.fullContent = "Ban quản lý đã xác nhận lịch đặt " + bookingRow.facilityName +
                        " vào ngày " + (bookingRow.DayBooking != null ? bookingRow.DayBooking : bookingRow.bookingDate) +
                        " từ " + bookingRow.startTime + " đến " + bookingRow.endTime + ". Vui lòng đến đúng giờ.";
                notif.affectedScope = "Cư dân: " + bookingRow.residentName;
                notif.imageResId = R.drawable.ic_check_circle_green;
            } else {
                notif.title = "Lịch đặt tiện ích bị từ chối";
                notif.shortDescription = "Lịch đặt " + bookingRow.facilityName + " vào " +
                        (bookingRow.DayBooking != null ? bookingRow.DayBooking : bookingRow.bookingDate) +
                        " đã bị từ chối.";
                notif.fullContent = "Ban quản lý đã từ chối lịch đặt " + bookingRow.facilityName +
                        " vào ngày " + (bookingRow.DayBooking != null ? bookingRow.DayBooking : bookingRow.bookingDate) +
                        ". Lý do: " + (reason != null ? reason : "Không có lý do cụ thể.");
                notif.affectedScope = "Cư dân: " + bookingRow.residentName;
                notif.imageResId = R.drawable.ic_info_orange;
            }

            db.appNotificationDao().insertNotification(notif);

            runOnUiThread(() -> {
                String message = "APPROVED".equals(newStatus) ? "Đã duyệt & gửi thông báo" : "Đã từ chối & gửi thông báo";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                loadBookings();
            });
        });
    }
}
