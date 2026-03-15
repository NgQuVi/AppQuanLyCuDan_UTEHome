package com.example.quanlycudan_utehome.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.quanlycudan_utehome.data.dao.PaymentDao;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Invoice;
import com.example.quanlycudan_utehome.data.entity.InvoiceItem;
import com.example.quanlycudan_utehome.data.entity.TransactionHistory;

import java.util.List;

public class PaymentRepository {
    private PaymentDao paymentDao;

    public PaymentRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        paymentDao = db.paymentDao();
    }

    // 1. Lấy danh sách Hóa đơn chưa thanh toán
    public LiveData<List<Invoice>> getUnpaidInvoices(String aptId) {
        return paymentDao.getUnpaidInvoices(aptId);
    }

    // 2. Lấy danh sách lịch sử quá khứ
    public LiveData<List<TransactionHistory>> getPaymentHistory() {
        return paymentDao.getAllPaymentHistory();
    }

    // 3. Lấy 4 dòng điện/nước chi tiết của 1 mã hóa đơn
    public LiveData<List<InvoiceItem>> getInvoiceItemsDetails(String invoiceId) {
        return paymentDao.getInvoiceItemsDetails(invoiceId);
    }

    // 4. Khi nhấn Xác nhận ở bước thanh toán MoMo:
    public void confirmSuccessfulPayment(TransactionHistory transaction) {
        // Room bắt buộc các thao tác Lưu/Cập nhật (Insert/Update) phải dùng Luồng phụ
        // (AsyncTask/Thread/Executors)
        new Thread(() -> {
            // Bước 1: Insert vô bảng Lịch sử (Transactions)
            paymentDao.insertTransaction(transaction);

            // Bước 2: Cập nhật biến UNPAID thành PAID bên bảng Hóa đơn (Invoices)
            paymentDao.markInvoiceAsPaid(transaction.invoiceId);
        }).start();
    }
}
