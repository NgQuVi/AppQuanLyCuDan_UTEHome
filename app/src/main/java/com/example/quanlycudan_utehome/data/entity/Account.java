package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "accounts",
    foreignKeys = @ForeignKey(
        entity = Resident.class,
        parentColumns = "id",
        childColumns = "residentId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index(value = "phone", unique = true), @Index("residentId")}
)
public class Account {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int residentId;
    public String phone;
    public String password;
    public boolean isActive = true;

    public Account(int residentId, String phone, String password) {
        this.residentId = residentId;
        this.phone = phone;
        this.password = password;
        this.isActive = true;
    }
}
