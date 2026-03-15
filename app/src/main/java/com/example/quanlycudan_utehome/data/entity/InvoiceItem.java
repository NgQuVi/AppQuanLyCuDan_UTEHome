package com.example.quanlycudan_utehome.data.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "invoice_items")
public class InvoiceItem {
    @PrimaryKey(autoGenerate = true)
    public int id; // ID tự tăng

    public String invoiceId; // Khóa chỉ về Invoice cha
    public String serviceType; // "ELECTRIC", "WATER", "PARKING", "INTERNET"
    public long amount; // Số tiền riêng của loại phí này

    // Các chỉ số phụ (Cho Điện/nước) - Có thể null/0
    public int oldIndex;
    public int newIndex;
    public int consumption;
    public int unitPrice;

    // Dành cho Internet/Xe (Lưu text "Gói Combo 2", "01 Ô tô, 02 Xe máy")
    public String description;

    // Constructor rỗng (Room bắt buộc phải có)
    public InvoiceItem() {}


}
