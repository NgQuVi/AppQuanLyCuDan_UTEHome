package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
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
import com.example.quanlycudan_utehome.data.entity.AppNotification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationManagementAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<AppNotification> allNotifications = new ArrayList<>();
    
    private TextView chipAll, chipMaintenance, chipImportant, chipMeeting, chipUtility;
    private String currentFilter = "All";
    private TextView tvTotalCount, tvSentCount; // Sent count represents "Read" count in this context for Admin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_management);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootNotifMgmt), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvSentCount = findViewById(R.id.tvSentCount);

        chipAll = findViewById(R.id.chipAll);
        chipMaintenance = findViewById(R.id.chipMaintenance);
        chipImportant = findViewById(R.id.chipImportant);
        chipMeeting = findViewById(R.id.chipMeeting);
        chipUtility = findViewById(R.id.chipUtility);

        chipAll.setOnClickListener(v -> setFilter("All"));
        chipMaintenance.setOnClickListener(v -> setFilter("MAINTENANCE"));
        chipImportant.setOnClickListener(v -> setFilter("IMPORTANT"));
        chipMeeting.setOnClickListener(v -> setFilter("MEETING"));
        chipUtility.setOnClickListener(v -> setFilter("UTILITY"));

        recyclerView = findViewById(R.id.rvNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationManagementAdapter();
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabCreateNotif).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, ComposeNotificationActivity.class));
        });

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadNotifications();
    }

    private void loadNotifications() {
        executorService.execute(() -> {
            allNotifications = AppDatabase.getInstance(this).appNotificationDao().getAllNotifications();
            int readCount = 0;
            for (AppNotification notif : allNotifications) {
                if (notif.isRead) readCount++;
            }
            final int finalReadCount = readCount;
            
            runOnUiThread(() -> {
                tvTotalCount.setText(String.valueOf(allNotifications.size()));
                tvSentCount.setText(String.valueOf(finalReadCount));
                applyFilter(((EditText) findViewById(R.id.etSearch)).getText().toString());
            });
        });
    }

    private void setFilter(String filterText) {
        currentFilter = filterText;
        
        // Reset all
        resetChip(chipAll);
        resetChip(chipMaintenance);
        resetChip(chipImportant);
        resetChip(chipMeeting);
        resetChip(chipUtility);

        // Highlight selected
        if ("All".equals(filterText)) selectChip(chipAll);
        else if ("MAINTENANCE".equals(filterText)) selectChip(chipMaintenance);
        else if ("IMPORTANT".equals(filterText)) selectChip(chipImportant);
        else if ("MEETING".equals(filterText)) selectChip(chipMeeting);
        else if ("UTILITY".equals(filterText)) selectChip(chipUtility);

        applyFilter(((EditText) findViewById(R.id.etSearch)).getText().toString());
    }
    
    private void resetChip(TextView chip) {
        chip.setBackgroundResource(R.drawable.bg_compose_chip);
        chip.setTextColor(getResources().getColor(R.color.color_compose_chip_text, null));
    }
    
    private void selectChip(TextView chip) {
        // use an active style, e.g. solid orange color
        chip.setBackgroundResource(R.drawable.bg_pill_orange_transparent);
        chip.setTextColor(android.graphics.Color.parseColor("#FF7A50"));
    }

    private void applyFilter(String query) {
        String lowerQuery = query.toLowerCase();
        List<AppNotification> filteredList = new ArrayList<>();
        
        for (AppNotification item : allNotifications) {
            boolean matchesFilter = "All".equals(currentFilter) || currentFilter.equalsIgnoreCase(item.type);
            boolean matchesSearch = item.title != null && item.title.toLowerCase().contains(lowerQuery);
            
            if (matchesFilter && matchesSearch) {
                filteredList.add(item);
            }
        }
        adapter.setNotifications(filteredList);
    }
}
