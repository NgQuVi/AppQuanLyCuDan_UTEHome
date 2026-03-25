package com.example.quanlycudan_utehome.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "transactions")
public class TransactionHistory implements java.io.Serializable{
    @PrimaryKey
    @NonNull
    public String transactionCode = ""; // VD: "#PMH102315"

    public String invoiceId;
    public String paymentMethod; // "Ví MoMo", "Chuyển khoản"
    public String transactionTime; // "15/10/2023 - 09:45"
    public long paidAmount;
    public String status; // "SUCCESS", "FAILED"
    public TransactionHistory(@NonNull String transactionCode, String invoiceId, String paymentMethod, String transactionTime, long paidAmount, String status) {
        this.transactionCode = transactionCode;
        this.invoiceId = invoiceId;
        this.paymentMethod = paymentMethod;
        this.transactionTime = transactionTime;
        this.paidAmount = paidAmount;
        this.status = status;
    }
}
