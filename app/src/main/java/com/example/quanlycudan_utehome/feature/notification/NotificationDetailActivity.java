package com.example.quanlycudan_utehome.feature.notification;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.AppNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnUnderstood).setOnClickListener(v -> finish());

        int notificationId = getIntent().getIntExtra("notification_id", -1);
        if (notificationId != -1) {
            loadNotification(notificationId);
        }
    }

    private void loadNotification(int id) {
        new Thread(() -> {
            AppNotification notif = AppDatabase.getInstance(this).appNotificationDao().getNotificationById(id);
            if (notif != null) {
                runOnUiThread(() -> bindData(notif));
            }
        }).start();
    }

    private void bindData(AppNotification notif) {
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvTime = findViewById(R.id.tvTime);
        TextView tvTagCover = findViewById(R.id.tvTagCover);
        TextView tvAffectedScope = findViewById(R.id.tvAffectedScope);
        TextView tvContent = findViewById(R.id.tvContent);
        LinearLayout llSteps = findViewById(R.id.llSteps);
        ImageView ivCover = findViewById(R.id.ivCover);

        tvTitle.setText(notif.title);
        tvDate.setText(notif.dateStr);
        tvTime.setText(notif.timeStr);
        tvTagCover.setText("THÔNG BÁO");
        tvAffectedScope.setText(notif.affectedScope != null ? notif.affectedScope : "Toàn tòa nhà");
        tvContent.setText(notif.fullContent);

        // Customize Tag and Cover based on type
        if ("MAINTENANCE".equals(notif.type)) {
            tvTagCover.setText("BẢO TRÌ");
            tvTagCover.setBackgroundResource(R.drawable.bg_tag_orange);
        } else if ("IMPORTANT".equals(notif.type)) {
            tvTagCover.setText("QUAN TRỌNG");
            tvTagCover.setBackgroundResource(R.drawable.bg_tag_red);
        } else if ("MEETING".equals(notif.type)) {
            tvTagCover.setText("HỌP CƯ DÂN");
            tvTagCover.setBackgroundResource(R.drawable.bg_tag_blue);
        } else if ("UTILITY".equals(notif.type)) {
            tvTagCover.setText("TIỆN ÍCH");
            tvTagCover.setBackgroundResource(R.drawable.bg_tag_green);
        }

        // Render steps safely
        if (notif.eventStepsJson != null && !notif.eventStepsJson.isEmpty()) {
            try {
                JSONArray stepsArray = new JSONArray(notif.eventStepsJson);
                for (int i = 0; i < stepsArray.length(); i++) {
                    JSONObject stepObj = stepsArray.getJSONObject(i);
                    String title = stepObj.getString("title"); // e.g., "Đợt 1: Thang máy 1, 2 & 3"
                    String time = stepObj.getString("time");   // e.g., "Thời gian: 08:00 - 12:00..."

                    addStepView(llSteps, title, time, i + 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addStepView(LinearLayout container, String title, String time, int index) {
        android.view.View stepView = getLayoutInflater().inflate(R.layout.item_notification_step, container, false);
        
        TextView tvIndex = stepView.findViewById(R.id.tvIndex);
        TextView tvStepTitle = stepView.findViewById(R.id.tvStepTitle);
        TextView tvStepTime = stepView.findViewById(R.id.tvStepTime);

        tvIndex.setText(String.valueOf(index));
        tvStepTitle.setText(title);
        tvStepTime.setText(time);

        container.addView(stepView);
    }
}
