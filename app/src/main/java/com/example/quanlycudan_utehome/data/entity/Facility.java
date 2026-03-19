package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "facilities")
public class Facility implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String location;
    public int imageResId;
    public int capacity;
    public String openTime;
    public String CloseTime;
    public boolean isOpen;
    public String description;

    public Facility() {}

    public Facility(int id, String name, String location, int imageResId, int capacity, String openTime, String CloseTime, boolean isOpen, String description) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.imageResId = imageResId;
        this.capacity = capacity;
        this.openTime = openTime;
        this.CloseTime = CloseTime;
        this.isOpen = isOpen;
        this.description = description;
    }
}
