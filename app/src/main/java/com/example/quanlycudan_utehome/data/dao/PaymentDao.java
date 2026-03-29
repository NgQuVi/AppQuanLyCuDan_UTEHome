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

    @Query("SELECT * FROM invoices WHERE apartmentId = :aptId AND status IN ('UNPAID', 'PARTIALLY_PAID')")
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

    @Query("SELECT SUM(paidAmount) FROM transactions WHERE status = 'SUCCESS'")
    long getTotalPaidAmount();

    @Query("SELECT i.id AS invoiceId, a.apartmentCode, i.billingMonth, i.dueDate, i.totalAmount, i.status " +
           "FROM invoices i INNER JOIN apartments a ON CAST(i.apartmentId AS INTEGER) = a.id")
    List<com.example.quanlycudan_utehome.data.entity.InvoiceItemRow> getInvoiceItemRows();

    @Query("UPDATE invoice_items SET status = 'PAID' WHERE invoiceId = :invoiceId AND serviceType = :serviceType")
    void markInvoiceItemAsPaid(String invoiceId, String serviceType);

    @Query("SELECT COUNT(*) FROM invoice_items WHERE invoiceId = :invoiceId AND status = 'UNPAID'")
    int countUnpaidItems(String invoiceId);

    @Query("SELECT COUNT(*) FROM invoice_items WHERE invoiceId = :invoiceId")
    int countTotalItems(String invoiceId);

    @Query("UPDATE invoices SET status = 'PARTIALLY_PAID' WHERE id = :invoiceId")
    void markInvoiceAsPartial(String invoiceId);

    @Query("DELETE FROM invoice_items WHERE invoiceId = :invId")
    void deleteInvoiceItems(String invId);

    @androidx.room.Update
    void updateInvoice(Invoice invoice);

    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    Invoice getInvoiceByIdSync(String invoiceId);

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invId")
    List<InvoiceItem> getInvoiceItemsDetailsSync(String invId);
}
