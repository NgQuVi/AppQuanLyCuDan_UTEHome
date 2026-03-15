package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "residents")
public class Resident {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String fullName;
    public String avatarUrl;
}