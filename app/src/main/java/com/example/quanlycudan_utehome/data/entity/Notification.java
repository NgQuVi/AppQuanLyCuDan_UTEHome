package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class Notification {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int accountId;
    public String title;
    public String content;
    public String targetType;
    public String targetId;
    public String createdAt;
}
