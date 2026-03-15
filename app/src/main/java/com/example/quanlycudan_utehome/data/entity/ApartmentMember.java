package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "apartment_members")
public class ApartmentMember {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int apartmentId;
    public int residentId;
    public String role;
    public String residentType;
}