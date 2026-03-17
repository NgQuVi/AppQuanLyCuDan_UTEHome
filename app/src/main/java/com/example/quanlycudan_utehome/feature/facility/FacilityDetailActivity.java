package com.example.quanlycudan_utehome.feature.facility;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.Facility;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_detail);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        Facility facility = (Facility) getIntent().getSerializableExtra("facility");
        if (facility != null) {
            setupViews(facility);
        }

        setupInteractions();

        findViewById(R.id.btnBook).setOnClickListener(v -> {
            if (selectedTimeIndex == -1) {
                Toast.makeText(this, "Vui lòng chọn khung giờ!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
                finish();
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

        for (int i = 0; i < times.length; i++) {
            final int index = i;
            // E.g. make time 5 (14:00) disabled
            if (i == 4) {
                times[i].setOnClickListener(v -> Toast.makeText(this, "Khung giờ này đã đầy", Toast.LENGTH_SHORT).show());
                continue;
            }
            times[i].setOnClickListener(v -> selectTime(index));
        }

        updateSelectionUI();
    }

    private void selectDate(int index) {
        selectedDateIndex = index;
        selectedTimeIndex = -1; // Reset time when date changes
        updateSelectionUI();
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
            if (i == 4) continue; // Disabled time

            if (i == selectedTimeIndex) {
                times[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFF3E0")));
                times[i].setTextColor(getResources().getColor(R.color.home_primary_orange));
                times[i].setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                times[i].setBackgroundTintList(null);
                times[i].setTextColor(getResources().getColor(R.color.home_text_secondary));
                times[i].setTypeface(null, android.graphics.Typeface.NORMAL);
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
