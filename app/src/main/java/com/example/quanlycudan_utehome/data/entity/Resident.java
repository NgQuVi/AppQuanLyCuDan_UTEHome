package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "residents")
public class Resident {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String fullName;
    public String avatarUrl;
    public long dateOfBirth;
    public String gender;
    public String indentificationType;
    public String indentificationNumber;
    public String phoneNumber;


}