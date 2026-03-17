package com.example.quanlycudan_utehome.data.entity;

import java.io.Serializable;

public class Facility implements Serializable {
    public int id;
    public String name;
    public String location;
    public String category; // e.g., "Thể thao"
    public int imageResId; // To load local drawable for now
    
    // Detailed fields
    public int capacity; // e.g., 20
    public String openTime; // e.g., "06:00 - 22:00"
    public boolean isOpen;
    public String description;

    public Facility(int id, String name, String location, String category, int imageResId, int capacity, String openTime, boolean isOpen, String description) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.category = category;
        this.imageResId = imageResId;
        this.capacity = capacity;
        this.openTime = openTime;
        this.isOpen = isOpen;
        this.description = description;
    }
}
