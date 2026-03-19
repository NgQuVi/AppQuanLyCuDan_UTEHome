package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "resident_cards")
public class ResidentCard {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int residentId;
    public int apartmentId;
    public String vehicleId;
    public String status;
    public String issueDate;
}
