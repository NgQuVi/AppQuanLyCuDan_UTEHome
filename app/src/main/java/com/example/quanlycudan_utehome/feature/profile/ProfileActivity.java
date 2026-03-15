package com.example.quanlycudan_utehome.feature.profile;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quanlycudan_utehome.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Header Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Edit Profile Button
        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }
}
