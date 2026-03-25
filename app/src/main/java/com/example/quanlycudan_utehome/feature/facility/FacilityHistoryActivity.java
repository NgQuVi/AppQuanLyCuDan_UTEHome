package com.example.quanlycudan_utehome.feature.facility;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.FacilityBooking;
import com.example.quanlycudan_utehome.data.local.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class FacilityHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private FacilityHistoryAdapter adapter;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_history);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvHistory = findViewById(R.id.rvHistory);
        emptyView = findViewById(R.id.emptyView);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FacilityHistoryAdapter(new ArrayList<>());
        rvHistory.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {
        int residentId = SessionManager.getInstance(this).getResidentId();
        if (residentId == -1) {
            emptyView.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
            return;
        }

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<FacilityBooking> bookings = db.facilityBookingDao().getBookingsByResident(residentId);

            runOnUiThread(() -> {
                if (bookings == null || bookings.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    rvHistory.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    rvHistory.setVisibility(View.VISIBLE);
                    adapter.updateData(bookings);
                }
            });
        }).start();
    }
}
