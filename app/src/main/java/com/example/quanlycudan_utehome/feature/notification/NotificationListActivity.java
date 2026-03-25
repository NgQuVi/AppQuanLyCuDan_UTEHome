package com.example.quanlycudan_utehome.feature.notification;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.AppNotification;

import java.util.ArrayList;
import java.util.List;

public class NotificationListActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<AppNotification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList, this::openDetail);
        rvNotifications.setAdapter(adapter);

        loadNotifications();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications(); // Reload to refresh unread status if back from detail
    }

    private void loadNotifications() {
        new Thread(() -> {
            List<AppNotification> list = AppDatabase.getInstance(this).appNotificationDao().getAllNotifications();
            runOnUiThread(() -> {
                notificationList.clear();
                notificationList.addAll(list);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void openDetail(AppNotification notification) {
        // Mark as read immediately in UI and DB
        notification.isRead = true;
        adapter.notifyDataSetChanged();

        new Thread(() -> {
            AppDatabase.getInstance(this).appNotificationDao().markAsRead(notification.id);
        }).start();

        // Start NotificationDetailActivity
        android.content.Intent intent = new android.content.Intent(this, NotificationDetailActivity.class);
        intent.putExtra("notification_id", notification.id);
        startActivity(intent);
    }
}
