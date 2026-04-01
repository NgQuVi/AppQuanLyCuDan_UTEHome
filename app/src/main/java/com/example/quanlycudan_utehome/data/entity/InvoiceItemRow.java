package com.example.quanlycudan_utehome.data.entity;

public class InvoiceItemRow {
    public String invoiceId;
    public String apartmentCode;
    public String billingMonth;
    public String dueDate;
    public long totalAmount;
    public String status;
    public long unpaidAmount; // Mới thêm: Số tiền còn nợ
}
