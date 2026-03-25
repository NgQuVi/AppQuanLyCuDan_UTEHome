package com.example.quanlycudan_utehome.admin;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.AppNotification;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationDetailAdminActivity extends AppCompatActivity {

    private int notifId;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_detail_admin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootNotifDetail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        notifId = getIntent().getIntExtra("notif_id", -1);
        if (notifId == -1) {
            Toast.makeText(this, "Không tìm thấy thông báo", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        findViewById(R.id.btnEditNotif).setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng chỉnh sửa chưa khả dụng", Toast.LENGTH_SHORT).show();
        });

        loadNotification();
    }

    private void loadNotification() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            AppNotification notif = db.appNotificationDao().getNotificationById(notifId);
            
            // Mark as read when opening
            if (notif != null && !notif.isRead) {
                db.appNotificationDao().markAsRead(notifId);
            }
            
            runOnUiThread(() -> bindData(notif));
        });
    }

    private void bindData(AppNotification notif) {
        if (notif == null) return;

        TextView tvDetailTag = findViewById(R.id.tvDetailTag);
        TextView tvDetailTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDetailDate = findViewById(R.id.tvDetailDate);
        TextView tvDetailTime = findViewById(R.id.tvDetailTime);
        TextView tvImpact = findViewById(R.id.tvImpact);
        TextView tvDetailBody = findViewById(R.id.tvDetailBody);

        tvDetailTitle.setText(notif.title);
        tvDetailDate.setText(notif.dateStr);
        tvDetailTime.setText(notif.timeStr);
        tvImpact.setText(notif.affectedScope != null ? notif.affectedScope : "Toàn bộ cư dân");
        tvDetailBody.setText(notif.fullContent);

        String typeStr = "KHÁC";
        int textColor = Color.parseColor("#8E8E8E");
        int bgTint = Color.parseColor("#E0E0E0");

        if (notif.type != null) {
            switch (notif.type) {
                case "MAINTENANCE":
                    typeStr = "BẢO TRÌ";
                    textColor = Color.parseColor("#2196F3");
                    bgTint = Color.parseColor("#E3F2FD");
                    break;
                case "IMPORTANT":
                    typeStr = "QUAN TRỌNG";
                    textColor = Color.parseColor("#E53935");
                    bgTint = Color.parseColor("#FFEBEE");
                    break;
                case "MEETING":
                    typeStr = "HỌP CƯ DÂN";
                    textColor = Color.parseColor("#43A047");
                    bgTint = Color.parseColor("#E8F5E9");
                    break;
                case "UTILITY":
                    typeStr = "TIỆN ÍCH";
                    textColor = Color.parseColor("#FB8C00");
                    bgTint = Color.parseColor("#FFF3E0");
                    break;
            }
        }

        tvDetailTag.setText(typeStr);
        tvDetailTag.setTextColor(textColor);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bgTint);
        gd.setCornerRadius(12f);
        tvDetailTag.setBackground(gd);
    }
}
