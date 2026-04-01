package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "accounts",
    indices = {@Index(value = "phone", unique = true)}
)
public class Account {
    public Account() {}

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String phone;
    public String password;
    public String role;
    public boolean isActive = true;
    public boolean mustChangePassword = false;

    @Ignore
    public Account(String phone, String password, String role) {
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.isActive = true;
        this.mustChangePassword = false;
    }
}
