package com.example.quanlycudan_utehome.feature.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.Invoice;
import com.example.quanlycudan_utehome.data.entity.InvoiceItem;
import com.example.quanlycudan_utehome.data.local.SessionManager;
import com.example.quanlycudan_utehome.data.repository.PaymentRepository;
import com.example.quanlycudan_utehome.feature.payment.PaymentConfirmationActivity;
import com.example.quanlycudan_utehome.feature.payment.PaymentHistoryActivity;

import java.text.DecimalFormat;
import java.util.List;
import android.app.AlertDialog;

public class InvoiceActivity extends AppCompatActivity {

    // ═══════════════════════════════════════════════════════════════
    // 1. KHAI BÁO BIẾN
    // ═══════════════════════════════════════════════════════════════

    // CheckBoxes – người dùng chọn/bỏ chọn loại phí
    private CheckBox cbElec, cbWater, cbPark, cbInternet;

    // TextView tổng tiền dưới cùng + tháng hiển thị
    private TextView tvSumValue, tvMonth;

    // TextViews thẻ ĐIỆN
    private TextView tvElecOld, tvElecNew, tvElecConsumed, tvElecPrice, tvElecTotal;

    // TextViews thẻ NƯỚC
    private TextView tvWaterOld, tvWaterNew, tvWaterConsumed, tvWaterPrice, tvWaterTotal;

    // TextViews thẻ GỬI XE
    private TextView tvParkCar, tvParkCarPrice, tvParkMoto, tvParkMotoPrice, tvParkTotal;

    // TextViews thẻ INTERNET
    private TextView tvIntPkg, tvIntSpeed, tvIntTotal;

    private List<Invoice> unpaidInvoiceList;
    private int selectedInvoiceIndex = 0;

    // Giá trị thực lấy từ DB (mặc định 0, sẽ được cập nhật sau khi observe)
    private long priceElec = 0, priceWater = 0, pricePark = 0, priceInternet = 0;

    // ID hóa đơn hiện tại – dùng khi bấm nút Thanh Toán
    private String currentInvoiceId = "";

    // Repository kết nối tới Room Database
    private PaymentRepository paymentRepository;

    // Bộ định dạng tiền: 472500 → "472,500" rồi ta đổi dấu phẩy → chấm
    private final DecimalFormat df = new DecimalFormat("#,###");

    // ═══════════════════════════════════════════════════════════════
    // 2. onCreate
    // ═══════════════════════════════════════════════════════════════
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // ── Bước 1: Ánh xạ View ─────────────────────────────────
        bindViews();

        // ── Bước 2: Nút Back & Lịch sử ──────────────────────────
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnHistory).setOnClickListener(v ->
                startActivity(new Intent(this, PaymentHistoryActivity.class)));

        // ── Bước 3: CheckBox listener – tính lại tổng khi tick/bỏ tick ──
        cbElec.setOnCheckedChangeListener((b, c) -> calculateTotal());
        cbWater.setOnCheckedChangeListener((b, c) -> calculateTotal());
        cbPark.setOnCheckedChangeListener((b, c) -> calculateTotal());
        cbInternet.setOnCheckedChangeListener((b, c) -> calculateTotal());

        findViewById(R.id.layoutMonthSelector).setOnClickListener(v -> showInvoicePicker());

        // ── Bước 4: Khởi tạo Repository ─────────────────────────
        paymentRepository = new PaymentRepository(getApplication());

        // ── Bước 5: Lấy aptId từ SessionManager rồi load hóa đơn ─
        // SessionManager lưu aptId dạng String (VD: "1") khi đăng nhập thành công
        String aptId = SessionManager.getInstance(this).getApartmentId();
        if (aptId != null && !aptId.isEmpty()) {
            loadInvoices(aptId);
        } else {
            // Chưa có aptId → dùng giá trị mặc định "1" để demo
            // (Cần lưu aptId vào SessionManager ở màn đăng nhập)
            showMissingApartmentState();
        }

        // ── Bước 6: Nút Thanh Toán ──────────────────────────────
        findViewById(R.id.btnPay).setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentConfirmationActivity.class);
            intent.putExtra("INVOICE_ID", currentInvoiceId);
            intent.putExtra("TOTAL_SUM", calculateCurrentTotal());
            intent.putExtra("HAS_ELEC", cbElec.isChecked());
            intent.putExtra("HAS_WATER", cbWater.isChecked());
            intent.putExtra("HAS_PARK", cbPark.isChecked());
            intent.putExtra("HAS_INTERNET", cbInternet.isChecked());
            startActivity(intent);
        });
    }

    // ═══════════════════════════════════════════════════════════════
    // 3. ÁNH XẠ VIEW (findViewById)
    // ═══════════════════════════════════════════════════════════════
    private void bindViews() {
        cbElec     = findViewById(R.id.cbElec);
        cbWater    = findViewById(R.id.cbWater);
        cbPark     = findViewById(R.id.cbPark);
        cbInternet = findViewById(R.id.cbInternet);
        tvSumValue = findViewById(R.id.tvSumValue);
        tvMonth    = findViewById(R.id.tvMonth);

        // Thẻ Điện
        tvElecOld      = findViewById(R.id.tvElecOld);
        tvElecNew      = findViewById(R.id.tvElecNew);
        tvElecConsumed = findViewById(R.id.tvElecConsumed);
        tvElecPrice    = findViewById(R.id.tvElecPrice);
        tvElecTotal    = findViewById(R.id.tvElecTotal);

        // Thẻ Nước
        tvWaterOld      = findViewById(R.id.tvWaterOld);
        tvWaterNew      = findViewById(R.id.tvWaterNew);
        tvWaterConsumed = findViewById(R.id.tvWaterConsumed);
        tvWaterPrice    = findViewById(R.id.tvWaterPrice);
        tvWaterTotal    = findViewById(R.id.tvWaterTotal);

        // Thẻ Gửi xe
        tvParkCar      = findViewById(R.id.tvParkCar);
        tvParkCarPrice = findViewById(R.id.tvParkCarPrice);
        tvParkMoto     = findViewById(R.id.tvParkMoto);
        tvParkMotoPrice= findViewById(R.id.tvParkMotoPrice);
        tvParkTotal    = findViewById(R.id.tvParkTotal);

        // Thẻ Internet
        tvIntPkg   = findViewById(R.id.tvIntPkg);
        tvIntSpeed = findViewById(R.id.tvIntSpeed);
        tvIntTotal = findViewById(R.id.tvIntTotal);
    }

    // ═══════════════════════════════════════════════════════════════
    // 4. LOAD HÓA ĐƠN CHƯA THANH TOÁN
    // ═══════════════════════════════════════════════════════════════
    private void loadInvoices(String aptId) {
        // SQL tương đương: SELECT * FROM invoices WHERE apartmentId = aptId AND status = 'UNPAID'
        // observe() tự chạy khi DB có dữ liệu, không cần gọi thủ công
        paymentRepository.getUnpaidInvoices(aptId).observe(this, invoiceList -> {

            if (invoiceList == null || invoiceList.isEmpty()) {
                unpaidInvoiceList = null;
                // Không có hóa đơn chưa trả
                tvSumValue.setText("0đ");
                tvMonth.setText("Không có hóa đơn");
                findViewById(R.id.layoutElec).setVisibility(android.view.View.GONE);
                findViewById(R.id.layoutWater).setVisibility(android.view.View.GONE);
                findViewById(R.id.layoutPark).setVisibility(android.view.View.GONE);
                findViewById(R.id.layoutInternet).setVisibility(android.view.View.GONE);
                findViewById(R.id.btnPay).setEnabled(false);
                return;
            }

            unpaidInvoiceList = invoiceList;
            findViewById(R.id.btnPay).setEnabled(true);

            // Kiểm tra list nhỏ hơn index đã chọn
            if (selectedInvoiceIndex >= unpaidInvoiceList.size()) {
                selectedInvoiceIndex = 0;
            }

            Invoice invoice = unpaidInvoiceList.get(selectedInvoiceIndex);
            currentInvoiceId = invoice.id;

            if (unpaidInvoiceList.size() > 1) {
                tvMonth.setText("Tháng " + invoice.billingMonth + " (Chọn)");
            } else {
                tvMonth.setText("Tháng " + invoice.billingMonth);
            }

            loadInvoiceItems(invoice.id);
        });
    }

    private void showInvoicePicker() {
        if (unpaidInvoiceList == null || unpaidInvoiceList.size() <= 1) return;

        String[] options = new String[unpaidInvoiceList.size()];
        for (int i = 0; i < unpaidInvoiceList.size(); i++) {
            Invoice inv = unpaidInvoiceList.get(i);
            String status = "PARTIALLY_PAID".equals(inv.status) ? " (Đóng 1 phần)" : " (Chưa đóng)";
            options[i] = "Tháng " + inv.billingMonth + status;
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn hóa đơn cần thanh toán")
                .setSingleChoiceItems(options, selectedInvoiceIndex, (dialog, which) -> {
                    selectedInvoiceIndex = which;
                    dialog.dismiss();
                    
                    Invoice invoice = unpaidInvoiceList.get(selectedInvoiceIndex);
                    currentInvoiceId = invoice.id;
                    tvMonth.setText("Tháng " + invoice.billingMonth + " (Chọn)");
                    loadInvoiceItems(invoice.id);
                })
                .show();
    }

    // ═══════════════════════════════════════════════════════════════
    // 5. LOAD CHI TIẾT TỪNG KHOẢN PHÍ
    // ═══════════════════════════════════════════════════════════════
    private void showMissingApartmentState() {
        tvSumValue.setText("0d");
        tvMonth.setText("Chua co can ho");
        findViewById(R.id.layoutElec).setVisibility(android.view.View.GONE);
        findViewById(R.id.layoutWater).setVisibility(android.view.View.GONE);
        findViewById(R.id.layoutPark).setVisibility(android.view.View.GONE);
        findViewById(R.id.layoutInternet).setVisibility(android.view.View.GONE);
        findViewById(R.id.btnPay).setEnabled(false);
    }

    private void loadInvoiceItems(String invoiceId) {
        // SQL: SELECT * FROM invoice_items WHERE invoiceId = invoiceId
        paymentRepository.getInvoiceItemsDetails(invoiceId).observe(this, items -> {
            if (items == null) return;

            for (InvoiceItem item : items) {
                boolean isPaid = "PAID".equals(item.status); // Kiểm tra xem đã thanh toán chưa
                String type = item.serviceType != null ? item.serviceType.toUpperCase() : "";

                switch (type) {
                    case "ELECTRIC":
                        if (isPaid) {
                            findViewById(R.id.layoutElec).setVisibility(android.view.View.GONE);
                            cbElec.setChecked(false);
                            priceElec = 0;
                        } else {
                            findViewById(R.id.layoutElec).setVisibility(android.view.View.VISIBLE);
                            priceElec = item.amount;
                        }
                        tvElecOld.setText("Số cũ: " + fmt(item.oldIndex) + " kWh");
                        tvElecNew.setText("Số mới: " + fmt(item.newIndex) + " kWh");
                        tvElecConsumed.setText("Tiêu thụ: " + fmt(item.newIndex - item.oldIndex) + " kWh");
                        tvElecPrice.setText("Đơn giá: " + fmt(item.unitPrice) + "đ/kWh");
                        tvElecTotal.setText(fmt(item.amount) + "đ");
                        break;

                    case "WATER":
                        if (isPaid) {
                            findViewById(R.id.layoutWater).setVisibility(android.view.View.GONE);
                            cbWater.setChecked(false);
                            priceWater = 0;
                        } else {
                            findViewById(R.id.layoutWater).setVisibility(android.view.View.VISIBLE);
                            priceWater = item.amount;
                        }
                        tvWaterOld.setText("Số cũ: " + fmt(item.oldIndex) + " m³");
                        tvWaterNew.setText("Số mới: " + fmt(item.newIndex) + " m³");
                        tvWaterConsumed.setText("Tiêu thụ: " + fmt(item.newIndex - item.oldIndex) + " m³");
                        tvWaterPrice.setText("Đơn giá: " + fmt(item.unitPrice) + "đ/m³");
                        tvWaterTotal.setText(fmt(item.amount) + "đ");
                        break;

                    case "PARKING":
                        if (isPaid) {
                            findViewById(R.id.layoutPark).setVisibility(android.view.View.GONE);
                            cbPark.setChecked(false);
                            pricePark = 0;
                        } else {
                            findViewById(R.id.layoutPark).setVisibility(android.view.View.VISIBLE);
                            pricePark = item.amount;
                        }
                        tvParkCar.setText(item.description != null ? item.description : "Khu vực để xe");
                        tvParkCarPrice.setText(fmt(item.amount) + "đ/tháng");
                        tvParkTotal.setText(fmt(item.amount) + "đ");
                        break;

                    case "INTERNET":
                    case "MANAGEMENT":
                        if (isPaid) {
                            findViewById(R.id.layoutInternet).setVisibility(android.view.View.GONE);
                            cbInternet.setChecked(false);
                            priceInternet = 0;
                        } else {
                            findViewById(R.id.layoutInternet).setVisibility(android.view.View.VISIBLE);
                            priceInternet = item.amount;
                        }
                        
                        TextView tvIntTitle = findViewById(R.id.tvIntTitle);
                        if ("MANAGEMENT".equals(item.serviceType)) {
                            tvIntTitle.setText("Phí quản lý căn hộ");
                        } else {
                            tvIntTitle.setText("Thanh toán Internet");
                        }
                        
                        tvIntPkg.setText(item.description);
                        tvIntSpeed.setText(fmt(item.amount) + "đ/tháng");
                        tvIntTotal.setText(fmt(item.amount) + "đ");
                        break;
                }
            }


            // Tính và hiển thị tổng sau khi có đủ dữ liệu thật
            calculateTotal();
        });
    }

    // ═══════════════════════════════════════════════════════════════
    // 6. TÍNH TỔNG TIỀN
    // ═══════════════════════════════════════════════════════════════
    private long calculateCurrentTotal() {
        long total = 0;
        if (cbElec.isChecked())     total += priceElec;
        if (cbWater.isChecked())    total += priceWater;
        if (cbPark.isChecked())     total += pricePark;
        if (cbInternet.isChecked()) total += priceInternet;
        return total;
    }

    private void calculateTotal() {
        // fmt(2642500) → "2,642,500" → sau replace → "2.642.500"
        tvSumValue.setText(fmt(calculateCurrentTotal()) + "đ");
    }

    // ═══════════════════════════════════════════════════════════════
    // 7. HELPER: Định dạng số kiểu Việt Nam (dấu chấm phân cách)
    // ═══════════════════════════════════════════════════════════════
    private String fmt(long number) {
        // DecimalFormat dùng dấu phẩy mặc định → đổi thành dấu chấm
        return df.format(number).replace(',', '.');
    }
}
