package com.example.quanlycudan_utehome.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "invoices")
public class Invoice implements java.io.Serializable{
    @PrimaryKey
    @NonNull
    public String id = "";

    public String apartmentId;
    public String billingMonth; // VD: "10/2023"
    public long totalAmount;
    public String dueDate; // Hạn chót
    public String status; // "UNPAID" (Chưa trả), "PAID" (Đã trả)

    public Invoice(@NonNull String id, String apartmentId, String billingMonth, long totalAmount, String dueDate, String status) {
        this.id = id;
        this.apartmentId = apartmentId;
        this.billingMonth = billingMonth;
        this.totalAmount = totalAmount;
        this.dueDate = dueDate;
        this.status = status;
    }

}
