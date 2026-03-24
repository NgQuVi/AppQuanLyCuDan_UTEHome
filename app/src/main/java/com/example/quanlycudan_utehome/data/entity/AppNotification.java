package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notifications")
public class AppNotification implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    // e.g. "Bảo trì thang máy tòa S1"
    public String title;
    
    // e.g. "Kế hoạch bảo trì định kỳ thang máy..."
    public String shortDescription;
    
    // Full multiline body text
    public String fullContent;
    
    // "MAINTENANCE", "IMPORTANT", "MEETING", "UTILITY"
    public String type;
    
    // Unix timestamp for sorting
    public long timestamp;
    

    // e.g. "15/10/2023"
    public String dateStr;
    
    // e.g. "10:30 AM"
    public String timeStr;
    
    // The hero image for detail page
    public int imageResId;
    
    // True if user has read it
    public boolean isRead;
    
    // Alert info, e.g. "Toàn bộ cư dân đang sinh sống tại tòa S1"
    public String affectedScope;
    
    // Steps for the timeline encoded as JSON. 
    // Format: [{"title": "Đợt 1: Thang máy 1, 2 & 3", "subtitle": "Thời gian: 08:00 - 12:00..."}]
    public String eventStepsJson;

    public AppNotification() {
    }
}
