package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vehicles")
public class Vehicle {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int apartmentId;
    public int residentId;
    public String brand;
    public String color;
    public String vehicleType;
    public String licensePlate;
    public String status;
}
