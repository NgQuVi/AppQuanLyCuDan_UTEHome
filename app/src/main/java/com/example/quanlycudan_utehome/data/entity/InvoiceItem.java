package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "invoice_items")
public class InvoiceItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String invoiceId;
    public String serviceType;
    public String description;
    public float quantity;
    public long unitPrice;
    public long amount;
    public int oldIndex;
    public int newIndex;

    public InvoiceItem() {}
}
