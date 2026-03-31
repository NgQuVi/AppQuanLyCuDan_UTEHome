package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "moving_requests")
public class MovingRequest {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int accountId;
    public String movingDate;
    public String startTime;
    public String endTime;
    public String itemsDescription;
    public boolean isDepositPaid;
    public long depositAmount;
    public String status;
    public String createdAt;
}
