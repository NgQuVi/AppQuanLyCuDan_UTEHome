package com.example.quanlycudan_utehome.feature.profile;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quanlycudan_utehome.R;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Header Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Save Button
        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            Toast.makeText(EditProfileActivity.this, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
