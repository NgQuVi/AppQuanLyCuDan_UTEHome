package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "guest_passes")
public class GuestPass {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int apartmentId;
    public String code;
    public String createdDate;
    public String fromDateTime;
    public String toDateTime;
    public String status;
}
