package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "apartments")
public class Apartment {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String apartmentCode;
    public String buildingCode;
    public int floor;
    public float area;
    public String status;
}
