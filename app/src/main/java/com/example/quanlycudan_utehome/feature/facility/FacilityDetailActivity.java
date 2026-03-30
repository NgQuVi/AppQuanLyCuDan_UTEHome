package com.example.quanlycudan_utehome.feature.facility;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Facility;
import com.example.quanlycudan_utehome.data.entity.FacilityBooking;
import com.example.quanlycudan_utehome.data.local.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class FacilityDetailActivity extends AppCompatActivity {

    private LinearLayout[] dates;
    private TextView[] dateDays;
    private TextView[] dateNumbers;
    private TextView[] times;
    private TextView tvSelectedTime;

    private int selectedDateIndex = 0; // Default to first (TH 2, 15)
    private int selectedTimeIndex = -1; // None initially

    private final String[] DATES = {"Thứ 2, 15/10", "Thứ 3, 16/10", "Thứ 4, 17/10"};
    private final String[] TIMES = {"08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "14:00 - 15:00"};
    
    private AppDatabase db;
    private int currentResidentId;
    private Facility currentFacility;
    private List<String> bookedTimes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_detail);

        db = AppDatabase.getInstance(this);
        currentResidentId = SessionManager.getInstance(this).getResidentId();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        currentFacility = (Facility) getIntent().getSerializableExtra("facility");
        if (currentFacility != null) {
            setupViews(currentFacility);
        }

        setupInteractions();

        findViewById(R.id.btnBook).setOnClickListener(v -> {
            if (currentResidentId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập để đặt lịch!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedTimeIndex == -1) {
                Toast.makeText(this, "Vui lòng chọn khung giờ!", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(() -> {
                    FacilityBooking booking = new FacilityBooking();
                    booking.facilityId = currentFacility.id;
                    booking.residentId = currentResidentId;
                    booking.bookingDate = DATES[selectedDateIndex];
                    booking.DayBooking = DATES[selectedDateIndex].split(",")[0];
                    String[] timeParts = TIMES[selectedTimeIndex].split(" - ");
                    booking.startTime = timeParts[0];
                    booking.endTime = timeParts[1];
                    booking.status = "PENDING";
                    booking.cancelReason = "";
                    
                    db.facilityBookingDao().insertBooking(booking);
                    
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }).start();
            }
        });
    }

    private void setupInteractions() {
        tvSelectedTime = findViewById(R.id.tvSelectedTime);

        dates = new LinearLayout[]{
            findViewById(R.id.btnDate1),
            findViewById(R.id.btnDate2),
            findViewById(R.id.btnDate3)
        };
        
        // Assuming TextViews inside the LinearLayout at index 0 and 1
        dateDays = new TextView[]{
            (TextView) dates[0].getChildAt(0),
            (TextView) dates[1].getChildAt(0),
            (TextView) dates[2].getChildAt(0)
        };
        dateNumbers = new TextView[]{
            (TextView) dates[0].getChildAt(1),
            (TextView) dates[1].getChildAt(1),
            (TextView) dates[2].getChildAt(1)
        };

        times = new TextView[]{
            findViewById(R.id.tvTime1),
            findViewById(R.id.tvTime2),
            findViewById(R.id.tvTime3),
            findViewById(R.id.tvTime4),
            findViewById(R.id.tvTime5)
        };

        for (int i = 0; i < dates.length; i++) {
            final int index = i;
            dates[i].setOnClickListener(v -> selectDate(index));
        }

        fetchBookingsForSelectedDate();
    }
    
    private void fetchBookingsForSelectedDate() {
        if (currentFacility == null) return;
        new Thread(() -> {
            String date = DATES[selectedDateIndex];
            List<FacilityBooking> bookings = db.facilityBookingDao().getBookingsByFacilityAndDate(currentFacility.id, date);
            
            bookedTimes.clear();
            for (FacilityBooking b : bookings) {
                // Block slots that are pending or approved
                if ("PENDING".equalsIgnoreCase(b.status) || "APPROVED".equalsIgnoreCase(b.status) || "BOOKED".equalsIgnoreCase(b.status)) {
                    bookedTimes.add(b.startTime + " - " + b.endTime);
                }
            }
            runOnUiThread(() -> updateSelectionUI());
        }).start();
    }

    private void selectDate(int index) {
        selectedDateIndex = index;
        selectedTimeIndex = -1; // Reset time when date changes
        fetchBookingsForSelectedDate(); // Also updates UI
    }

    private void selectTime(int index) {
        selectedTimeIndex = index;
        updateSelectionUI();
    }

    private void updateSelectionUI() {
        // Update Dates
        for (int i = 0; i < dates.length; i++) {
            if (i == selectedDateIndex) {
                dates[i].setBackgroundResource(R.drawable.bg_card_orange_gradient);
                dateDays[i].setTextColor(getResources().getColor(R.color.white));
                dateNumbers[i].setTextColor(getResources().getColor(R.color.white));
            } else {
                dates[i].setBackgroundResource(R.drawable.bg_card_white);
                dateDays[i].setTextColor(getResources().getColor(R.color.home_text_secondary));
                dateNumbers[i].setTextColor(getResources().getColor(R.color.home_text_primary));
            }
        }

        // Update Times
        for (int i = 0; i < times.length; i++) {
            boolean isBooked = bookedTimes.contains(TIMES[i]);
            
            if (isBooked) {
                times[i].setBackgroundResource(R.drawable.bg_card_white);
                times[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F5F5F5")));
                times[i].setTextColor(android.graphics.Color.parseColor("#BDBDBD"));
                times[i].setTypeface(null, android.graphics.Typeface.NORMAL);
                times[i].setOnClickListener(v -> Toast.makeText(this, "Khung giờ này đã đầy", Toast.LENGTH_SHORT).show());
            } else if (i == selectedTimeIndex) {
                times[i].setBackgroundResource(R.drawable.bg_card_white);
                times[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFF3E0")));
                times[i].setTextColor(getResources().getColor(R.color.home_primary_orange));
                times[i].setTypeface(null, android.graphics.Typeface.BOLD);
                times[i].setOnClickListener(v -> { /* nothing */ });
            } else {
                times[i].setBackgroundResource(R.drawable.bg_time_slot_available);
                times[i].setBackgroundTintList(null);
                times[i].setTextColor(getResources().getColor(R.color.home_text_secondary));
                times[i].setTypeface(null, android.graphics.Typeface.NORMAL);
                int finalI = i;
                times[i].setOnClickListener(v -> selectTime(finalI));
            }
        }

        // Update text
        if (selectedTimeIndex != -1) {
            tvSelectedTime.setText(DATES[selectedDateIndex] + " • " + TIMES[selectedTimeIndex]);
        } else {
            tvSelectedTime.setText("Chưa chọn • Hãy chọn giờ");
        }
    }

    private void setupViews(Facility facility) {
        ImageView ivImage = findViewById(R.id.ivFacilityImage);
        TextView tvName = findViewById(R.id.tvFacilityName);
        TextView tvDesc = findViewById(R.id.tvDesc);
        TextView tvCapacity = findViewById(R.id.tvCapacity);
        TextView tvHours = findViewById(R.id.tvHours);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvStatus = findViewById(R.id.tvStatus);

        if (facility.imageResId != 0) {
            ivImage.setImageResource(facility.imageResId);
        }
        tvName.setText(facility.name);
        tvDesc.setText(facility.description != null ? facility.description : "");
        tvCapacity.setText(facility.capacity + " người");
        tvHours.setText(facility.openTime.replace(" - ", " -\n"));
        tvLocation.setText(facility.location.replace(" ", "\n")); // basic fake wrap

        if (facility.isOpen) {
            tvStatus.setText("ĐANG MỞ CỬA");
        } else {
            tvStatus.setText("ĐÓNG CỬA");
            tvStatus.setTextColor(0xFFD32F2F); // Red
        }
    }
}
