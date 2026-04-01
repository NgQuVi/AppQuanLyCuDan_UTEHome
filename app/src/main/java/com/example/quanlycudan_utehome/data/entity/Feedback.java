package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "feedbacks")
public class Feedback {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int accountId;
    public String title;
    public String content;
    public String status;
    public String createdAt;
}
