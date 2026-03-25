package com.example.quanlycudan_utehome.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.quanlycudan_utehome.data.entity.Invoice;
import com.example.quanlycudan_utehome.data.entity.InvoiceItem;
import com.example.quanlycudan_utehome.data.entity.TransactionHistory;

import java.util.List;

@Dao
public interface PaymentDao {

    @Insert
    void insertInvoice(Invoice invoice);

    @Insert
    void insertInvoiceItems(List<InvoiceItem> items);

    @Insert
    void insertTransaction(TransactionHistory transaction);

    @Query("SELECT * FROM invoices WHERE apartmentId = :aptId AND status = 'UNPAID'")
    LiveData<List<Invoice>> getUnpaidInvoices(String aptId);

    @Query("SELECT * FROM transactions WHERE status = 'SUCCESS' ORDER BY transactionTime DESC")
    LiveData<List<TransactionHistory>> getAllPaymentHistory();

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invId")
    LiveData<List<InvoiceItem>> getInvoiceItemsDetails(String invId);

    @Query("UPDATE invoices SET status = 'PAID' WHERE id = :invoiceId")
    void markInvoiceAsPaid(String invoiceId);

    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    LiveData<Invoice> getInvoiceById(String invoiceId);

    @Query("SELECT * FROM invoices")
    List<Invoice> getAllInvoices();

    @Query("SELECT * FROM transactions WHERE transactionCode = :code")
    LiveData<TransactionHistory> getTransactionByCode(String code);


    @Query("UPDATE invoice_items SET status = 'PAID' WHERE invoiceId = :invId AND serviceType = :type")
    void markInvoiceItemAsPaid(String invId, String type);


    @Query("SELECT COUNT(*) FROM invoice_items WHERE invoiceId = :invId AND status = 'UNPAID'")
    int countUnpaidItems(String invId);

}
