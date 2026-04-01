package com.example.quanlycudan_utehome.admin;

import android.os.Bundle;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComposeNotificationActivity extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String selectedType = "MAINTENANCE";
    
    private TextView chipMaintenance, chipEvent, chipUrgent, chipUtility;
    private EditText etTitle, etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_compose_notification);

        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);

        chipMaintenance = findViewById(R.id.chipMaintenance);
        chipEvent = findViewById(R.id.chipEvent);
        chipUrgent = findViewById(R.id.chipUrgent);
        chipUtility = findViewById(R.id.chipUtility);

        chipMaintenance.setOnClickListener(v -> selectChip("MAINTENANCE", chipMaintenance));
        chipEvent.setOnClickListener(v -> selectChip("MEETING", chipEvent));
        chipUrgent.setOnClickListener(v -> selectChip("IMPORTANT", chipUrgent));
        chipUtility.setOnClickListener(v -> selectChip("UTILITY", chipUtility));

        // Start with default selection
        selectChip("MAINTENANCE", chipMaintenance);

        findViewById(R.id.btnPublish).setOnClickListener(v -> publishNotification());
    }

    private void selectChip(String type, TextView selectedView) {
        selectedType = type;
        
        resetChip(chipMaintenance);
        resetChip(chipEvent);
        resetChip(chipUrgent);
        resetChip(chipUtility);
        
        selectedView.setBackgroundResource(R.drawable.bg_pill_orange_transparent);
        selectedView.setTextColor(android.graphics.Color.parseColor("#FF7A50"));
    }

    private void resetChip(TextView chip) {
        chip.setBackgroundResource(R.drawable.bg_compose_chip);
        chip.setTextColor(getResources().getColor(R.color.color_compose_chip_text, null));
    }

    private void publishNotification() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ tiêu đề và nội dung", Toast.LENGTH_SHORT).show();
            return;
        }
        
        executorService.execute(() -> {
            AppNotification notif = new AppNotification();
            notif.title = title;
            notif.shortDescription = content.length() > 50 ? content.substring(0, 50) + "..." : content;
            notif.fullContent = content;
            notif.type = selectedType;
            notif.affectedScope = "Toàn bộ cư dân";
            notif.isRead = false;
            notif.timestamp = System.currentTimeMillis();
            
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date now = new Date();
            
            notif.dateStr = sdfDate.format(now);
            notif.timeStr = sdfTime.format(now);
            
            AppDatabase.getInstance(this).appNotificationDao().insertNotification(notif);
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Thông báo đã được phát hành!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
