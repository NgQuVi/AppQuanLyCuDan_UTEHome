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

    // 4. Lấy thông tin hóa đơn theo ID
    public LiveData<Invoice> getInvoiceById(String invoiceId) {
        return paymentDao.getInvoiceById(invoiceId);
    }

    // 5. Khi nhấn Xác nhận ở bước thanh toán MoMo:
    public void confirmSuccessfulPayment(TransactionHistory transaction) {
        // Room bắt buộc các thao tác Lưu/Cập nhật (Insert/Update) phải dùng Luồng phụ
        new Thread(() -> {
            // Bước 1: Insert vô bảng Lịch sử (Transactions)
            paymentDao.insertTransaction(transaction);

            // Bước 2: Cập nhật biến UNPAID thành PAID bên bảng Hóa đơn (Invoices)
            paymentDao.markInvoiceAsPaid(transaction.invoiceId);
        }).start();
    }

    public void processMockPayment(String invoiceId, long totalAmount, String method,
                                   boolean elec, boolean water, boolean park, boolean internet) {
        new Thread(() -> {
            // Cập nhật trạng thái TỪNG ITEM MỘT nếu người dùng có tích chọn
            if (elec) paymentDao.markInvoiceItemAsPaid(invoiceId, "ELECTRIC");
            if (water) paymentDao.markInvoiceItemAsPaid(invoiceId, "WATER");
            if (park) paymentDao.markInvoiceItemAsPaid(invoiceId, "PARKING");
            if (internet) paymentDao.markInvoiceItemAsPaid(invoiceId, "INTERNET");

            // Kiểm tra xem đã thanh toán hết các mục trong Menu chưa?
            int unpaidCount = paymentDao.countUnpaidItems(invoiceId);
            if (unpaidCount == 0) {
                // Nếu không còn mục nào UNPAID, lúc này mới cập nhật hóa đơn tổng thành PAID
                paymentDao.markInvoiceAsPaid(invoiceId);
            }

            // Ghi nhận Lịch sử giao dịch (Giữ nguyên như code cũ)
            String txCode = "MOCK" + System.currentTimeMillis();
            String currentTime = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm",
                    java.util.Locale.getDefault()).format(new java.util.Date());
            TransactionHistory history = new TransactionHistory(txCode, invoiceId, method, currentTime, totalAmount, "SUCCESS");
            paymentDao.insertTransaction(history);
        }).start();
    }


}
