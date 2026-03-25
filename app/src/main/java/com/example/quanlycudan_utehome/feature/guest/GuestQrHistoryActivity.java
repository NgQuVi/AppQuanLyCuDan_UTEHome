package com.example.quanlycudan_utehome.feature.guest;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.GuestPass;

import java.util.ArrayList;
import java.util.List;

public class GuestQrHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private GuestQrHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guest_qr_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        rvHistory = findViewById(R.id.rvHistory);
        if (rvHistory != null) {
            rvHistory.setLayoutManager(new LinearLayoutManager(this));
            adapter = new GuestQrHistoryAdapter(new ArrayList<>());
            rvHistory.setAdapter(adapter);
        }

        loadHistory();
    }

    private void loadHistory() {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(this);
                List<GuestPass> passes = db.guestPassDao().getGuestPassesByApartmentIdSync(1);
                Log.d("GuestQRHistory", "Loaded " + passes.size() + " guest passes");

                runOnUiThread(() -> {
                    if (passes.isEmpty()) {
                        Toast.makeText(this, "Không có lịch sử mã QR", Toast.LENGTH_SHORT).show();
                    }
                    adapter.updateData(passes);
                });
            } catch (Exception e) {
                Log.e("GuestQRHistory", "Error loading history: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(this, "Lỗi tải lịch sử", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
