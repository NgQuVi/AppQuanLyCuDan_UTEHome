package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "residents")
public class Resident {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public int accountId;
    public String fullName;
    public String phone;
    public String email;
    public String dob;
    public String gender;
    public String idNum;
    public String avatarUrl;
}