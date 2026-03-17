package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Bảng invoice_items – lưu chi tiết từng khoản phí của 1 hóa đơn.
 *
 * serviceType có 4 giá trị:
 *   "ELECTRIC"  → dùng oldIndex, newIndex, consumption, unitPrice, amount
 *   "WATER"     → dùng oldIndex, newIndex, consumption, unitPrice, amount
 *   "PARKING"   → dùng carCount, carUnitPrice, motoCount, motoUnitPrice, amount
 *   "INTERNET"  → dùng description (tên gói), amount
 */
@Entity(tableName = "invoice_items")
public class InvoiceItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    /** Khóa ngoại trỏ về bảng invoices */
    public String invoiceId;

    /** Loại phí: "ELECTRIC" | "WATER" | "PARKING" | "INTERNET" */
    public String serviceType;

    /** Tổng tiền của khoản phí này (đơn vị: đồng) */
    public long amount;

    // ─── Dành cho ĐIỆN và NƯỚC ────────────────────────────────────────────────

    /** Chỉ số cũ (kWh hoặc m³) */
    public int oldIndex;

    /** Chỉ số mới (kWh hoặc m³) */
    public int newIndex;

    /** Lượng tiêu thụ = newIndex - oldIndex */
    public int consumption;

    /** Đơn giá (đồng / kWh hoặc đồng / m³) */
    public int unitPrice;

    // ─── Dành cho GỬI XEM (PARKING) ──────────────────────────────────────────

    /** Số lượng ô tô */
    public int carCount;

    /** Đơn giá ô tô (đồng / xe / tháng) */
    public long carUnitPrice;

    /** Số lượng xe máy */
    public int motoCount;

    /** Đơn giá xe máy (đồng / xe / tháng) */
    public long motoUnitPrice;

    // ─── Dành cho INTERNET ────────────────────────────────────────────────────

    /**
     * Tên gói internet, ví dụ:
     *   "Gói Cơ bản – 200.000đ/tháng"
     *   "Gói Tiêu chuẩn – 400.000đ/tháng"
     *   "Gói Cao cấp – 600.000đ/tháng"
     * Cũng dùng cho PARKING để lưu mô tả bổ sung nếu cần
     */
    public String description;

    /** Constructor rỗng – Room bắt buộc phải có */
    public InvoiceItem() {}
}
