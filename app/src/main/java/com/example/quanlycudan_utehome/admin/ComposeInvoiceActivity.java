package com.example.quanlycudan_utehome.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.Invoice;
import com.example.quanlycudan_utehome.data.entity.InvoiceItem;
import com.example.quanlycudan_utehome.data.entity.AppNotification;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComposeInvoiceActivity extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private AppDatabase db;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    // UI Elements
    private TextView tvApartmentName, tvBillingMonth, tvGrandTotal, tvDueDateInput;
    private EditText etMgmtArea, etMgmtPrice;
    private TextView tvMgmtTotal;
    private EditText etCarCount, etCarPrice, etMotorCount, etMotorPrice;
    private TextView tvCarTotal, tvMotorTotal, tvAptTotal;
    private EditText etElecOld, etElecNew, etElecPrice;
    private EditText etWaterOld, etWaterNew, etWaterPrice;

    private List<Apartment> apartmentList = new ArrayList<>();
    private Apartment selectedApartment;
    private String selectedDueDate = "";
    
    private String editInvoiceId = null;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_compose_invoice);

        db = AppDatabase.getInstance(this);

        editInvoiceId = getIntent().getStringExtra("edit_invoice_id");
        if (editInvoiceId != null && !editInvoiceId.isEmpty()) {
            isEditMode = true;
        }

        initViews();
        
        // Initialize Due Date display
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 15); // Default due date = 15 days from now
        selectedDueDate = String.format("%02d/%02d/%d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
        tvDueDateInput.setText(selectedDueDate);

        setupListeners();
        loadApartments();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contentScroll), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        updateTotals();
    }

    private void initViews() {
        tvApartmentName = findViewById(R.id.tvApartmentName);
        tvBillingMonth = findViewById(R.id.tvBillingMonth);
        
        Calendar cal = Calendar.getInstance();
        tvBillingMonth.setText(String.format("%02d/%d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));
        
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        tvDueDateInput = findViewById(R.id.tvDueDateInput);

        etMgmtArea = findViewById(R.id.etMgmtArea);
        etMgmtPrice = findViewById(R.id.etMgmtPrice);
        tvMgmtTotal = findViewById(R.id.tvMgmtTotal);

        etCarCount = findViewById(R.id.etCarCount);
        etCarPrice = findViewById(R.id.etCarPrice);
        etMotorCount = findViewById(R.id.etMotorCount);
        etMotorPrice = findViewById(R.id.etMotorPrice);
        tvCarTotal = findViewById(R.id.tvCarTotal);
        tvMotorTotal = findViewById(R.id.tvMotorTotal);
        tvAptTotal = findViewById(R.id.tvAptTotal);

        etElecOld = findViewById(R.id.etElecOld);
        etElecNew = findViewById(R.id.etElecNew);
        etElecPrice = findViewById(R.id.etElecPrice);

        etWaterOld = findViewById(R.id.etWaterOld);
        etWaterNew = findViewById(R.id.etWaterNew);
        etWaterPrice = findViewById(R.id.etWaterPrice);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveInvoice).setOnClickListener(v -> saveInvoice());
        findViewById(R.id.layoutApartmentPicker).setOnClickListener(v -> showApartmentPickerDialog());
        findViewById(R.id.layoutMonthPicker).setOnClickListener(v -> showMonthPickerDialog());
        
        View btnPickDate = findViewById(R.id.btnPickDate);
        if (btnPickDate != null) {
            btnPickDate.setOnClickListener(v -> showDatePicker());
        }

        if (isEditMode) {
            TextView tvTitle = findViewById(R.id.tvHeaderTitle);
            if (tvTitle != null) tvTitle.setText("Chỉnh sửa hóa đơn");
            TextView btnSave = findViewById(R.id.btnSaveInvoice);
            if (btnSave != null) btnSave.setText("Cập nhật thay đổi");
        }
    }

    private void setupListeners() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateTotals();
            }
        };

        etMgmtArea.addTextChangedListener(watcher);
        etMgmtPrice.addTextChangedListener(watcher);
        etCarCount.addTextChangedListener(watcher);
        etCarPrice.addTextChangedListener(watcher);
        etMotorCount.addTextChangedListener(watcher);
        etMotorPrice.addTextChangedListener(watcher);
        etElecOld.addTextChangedListener(watcher);
        etElecNew.addTextChangedListener(watcher);
        etElecPrice.addTextChangedListener(watcher);
        etWaterOld.addTextChangedListener(watcher);
        etWaterNew.addTextChangedListener(watcher);
        etWaterPrice.addTextChangedListener(watcher);
    }

    private void loadApartments() {
        executorService.execute(() -> {
            apartmentList = db.apartmentDao().getAllApartmentsSync();
            
            if (isEditMode) {
                loadInvoiceDataForEdit();
            }
        });
    }

    private void loadInvoiceDataForEdit() {
        Invoice inv = db.paymentDao().getInvoiceByIdSync(editInvoiceId);
        List<InvoiceItem> items = db.paymentDao().getInvoiceItemsDetailsSync(editInvoiceId);
        if (inv == null) return;

        runOnUiThread(() -> {
            tvBillingMonth.setText(inv.billingMonth);
            selectedDueDate = inv.dueDate;
            tvDueDateInput.setText(selectedDueDate);

            for (Apartment apt : apartmentList) {
                if (String.valueOf(apt.id).equals(inv.apartmentId)) {
                    selectedApartment = apt;
                    tvApartmentName.setText(apt.buildingCode + " - " + apt.apartmentCode);
                    break;
                }
            }

            if (items != null) {
                for (InvoiceItem item : items) {
                    try {
                        if ("MANAGEMENT".equals(item.serviceType)) {
                            etMgmtArea.setText(String.valueOf((long)item.quantity));
                            etMgmtPrice.setText(String.valueOf(item.unitPrice));
                        } else if ("ELECTRIC".equals(item.serviceType)) {
                            etElecOld.setText(String.valueOf(item.oldIndex));
                            etElecNew.setText(String.valueOf(item.newIndex));
                            etElecPrice.setText(String.valueOf(item.unitPrice));
                        } else if ("WATER".equals(item.serviceType)) {
                            etWaterOld.setText(String.valueOf(item.oldIndex));
                            etWaterNew.setText(String.valueOf(item.newIndex));
                            etWaterPrice.setText(String.valueOf(item.unitPrice));
                        } else if ("PARKING".equals(item.serviceType)) {
                            String desc = item.description != null ? item.description : "";
                            long cCount = 0, mCount = 0;
                            if (desc.contains("Ô tô (")) {
                                int s1 = desc.indexOf("Ô tô (") + 6;
                                int e1 = desc.indexOf(")", s1);
                                if (e1 > s1) cCount = Long.parseLong(desc.substring(s1, e1));
                            }
                            if (desc.contains("Xe máy (")) {
                                int s2 = desc.indexOf("Xe máy (") + 8;
                                int e2 = desc.indexOf(")", s2);
                                if (e2 > s2) mCount = Long.parseLong(desc.substring(s2, e2));
                            }
                            etCarCount.setText(String.valueOf(cCount));
                            etMotorCount.setText(String.valueOf(mCount));
                        }
                    } catch (Exception ignored) {}
                }
            }
            updateTotals();
        });
    }

    private void showApartmentPickerDialog() {
        if (apartmentList.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu căn hộ", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] apartmentNames = new String[apartmentList.size()];
        for (int i = 0; i < apartmentList.size(); i++) {
            Apartment apt = apartmentList.get(i);
            apartmentNames[i] = apt.buildingCode + " - " + apt.apartmentCode;
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn căn hộ")
                .setItems(apartmentNames, (dialog, which) -> {
                    selectedApartment = apartmentList.get(which);
                    tvApartmentName.setText(apartmentNames[which]);
                    // Auto-fill area if needed
                    etMgmtArea.setText(String.valueOf((int)selectedApartment.area));
                    updateTotals();
                })
                .show();
    }

    private void showMonthPickerDialog() {
        List<String> monthList = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        
        // Generate months for previous year and current year
        for (int year = currentYear - 1; year <= currentYear + 1; year++) {
            for (int month = 1; month <= 12; month++) {
                monthList.add(String.format("%02d/%d", month, year));
            }
        }
        
        String[] months = monthList.toArray(new String[0]);
        
        new AlertDialog.Builder(this)
                .setTitle("Chọn kỳ thanh toán")
                .setItems(months, (dialog, which) -> {
                    tvBillingMonth.setText(months[which]);
                })
                .show();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDueDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
            tvDueDateInput.setText(selectedDueDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateTotals() {
        long mgmtTotal = getLong(etMgmtArea) * getLong(etMgmtPrice);
        tvMgmtTotal.setText(currencyFormat.format(mgmtTotal));

        long carTotal = getLong(etCarCount) * getLong(etCarPrice);
        tvCarTotal.setText("= " + currencyFormat.format(carTotal));

        long motorTotal = getLong(etMotorCount) * getLong(etMotorPrice);
        tvMotorTotal.setText("= " + currencyFormat.format(motorTotal));

        long aptTotal = mgmtTotal + carTotal + motorTotal;
        tvAptTotal.setText(currencyFormat.format(aptTotal));

        long elecUsage = Math.max(0, getLong(etElecNew) - getLong(etElecOld));
        long elecTotal = elecUsage * getLong(etElecPrice);

        long waterUsage = Math.max(0, getLong(etWaterNew) - getLong(etWaterOld));
        long waterTotal = waterUsage * getLong(etWaterPrice);

        long grandTotal = aptTotal + elecTotal + waterTotal;
        tvGrandTotal.setText(currencyFormat.format(grandTotal).replace("₫", "").trim());
    }

    private long getLong(EditText et) {
        String s = et.getText().toString().trim();
        if (s.isEmpty()) return 0;
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private void saveInvoice() {
        if (selectedApartment == null) {
            Toast.makeText(this, "Vui lòng chọn căn hộ", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            String invId = isEditMode ? editInvoiceId : "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String month = tvBillingMonth.getText().toString();
            
            // Calculate grand total one last time
            long mgmtTotalValue = getLong(etMgmtArea) * getLong(etMgmtPrice);
            long carTotalValue = getLong(etCarCount) * getLong(etCarPrice);
            long motorTotalValue = getLong(etMotorCount) * getLong(etMotorPrice);
            long elecUsage = Math.max(0, getLong(etElecNew) - getLong(etElecOld));
            long elecTotalValue = elecUsage * getLong(etElecPrice);
            long waterUsage = Math.max(0, getWaterUsageCount());
            long waterTotalValue = waterUsage * getLong(etWaterPrice);
            
            long grandTotal = mgmtTotalValue + carTotalValue + motorTotalValue + elecTotalValue + waterTotalValue;

            if (isEditMode) {
                Invoice invoice = db.paymentDao().getInvoiceByIdSync(invId);
                if (invoice != null) {
                    invoice.apartmentId = String.valueOf(selectedApartment.id);
                    invoice.billingMonth = month;
                    invoice.totalAmount = grandTotal;
                    invoice.dueDate = selectedDueDate;
                    db.paymentDao().updateInvoice(invoice);
                }
                db.paymentDao().deleteInvoiceItems(invId);
            } else {
                Invoice invoice = new Invoice(invId, String.valueOf(selectedApartment.id), month, grandTotal, selectedDueDate, "UNPAID");
                db.paymentDao().insertInvoice(invoice);
            }

            // Insert Items
            List<InvoiceItem> items = new ArrayList<>();
            
            // Mgmt
            InvoiceItem itemMgmt = new InvoiceItem();
            itemMgmt.invoiceId = invId;
            itemMgmt.serviceType = "MANAGEMENT";
            itemMgmt.description = "Phí quản lý căn hộ";
            itemMgmt.quantity = getLong(etMgmtArea);
            itemMgmt.unitPrice = getLong(etMgmtPrice);
            itemMgmt.amount = mgmtTotalValue;
            items.add(itemMgmt);

            // Parking (Gộp Car và Motor)
            long carQty = getLong(etCarCount);
            long motorQty = getLong(etMotorCount);
            if (carQty > 0 || motorQty > 0) {
                InvoiceItem itemPark = new InvoiceItem();
                itemPark.invoiceId = invId;
                itemPark.serviceType = "PARKING";
                
                if (carQty > 0 && motorQty > 0) {
                    itemPark.description = "Ô tô (" + carQty + ") & Xe máy (" + motorQty + ")";
                } else if (carQty > 0) {
                    itemPark.description = "Ô tô (" + carQty + ")";
                } else {
                    itemPark.description = "Xe máy (" + motorQty + ")";
                }
                
                itemPark.quantity = carQty + motorQty;
                // unitPrice doesn't make sense to combine, so just put 0 or math
                itemPark.unitPrice = 0;
                itemPark.amount = carTotalValue + motorTotalValue;
                items.add(itemPark);
            }

            // Elec
            InvoiceItem itemElec = new InvoiceItem();
            itemElec.invoiceId = invId;
            itemElec.serviceType = "ELECTRIC";
            itemElec.description = "Tiền điện";
            itemElec.oldIndex = (int)getLong(etElecOld);
            itemElec.newIndex = (int)getLong(etElecNew);
            itemElec.quantity = itemElec.newIndex - itemElec.oldIndex;
            itemElec.unitPrice = getLong(etElecPrice);
            itemElec.amount = elecTotalValue;
            items.add(itemElec);

            // Water
            InvoiceItem itemWater = new InvoiceItem();
            itemWater.invoiceId = invId;
            itemWater.serviceType = "WATER";
            itemWater.description = "Tiền nước";
            itemWater.oldIndex = (int)getLong(etWaterOld);
            itemWater.newIndex = (int)getLong(etWaterNew);
            itemWater.quantity = itemWater.newIndex - itemWater.oldIndex;
            itemWater.unitPrice = getLong(etWaterPrice);
            itemWater.amount = waterTotalValue;
            items.add(itemWater);

            db.paymentDao().insertInvoiceItems(items);

            if (!isEditMode) {
                // Gửi thông báo
                AppNotification notif = new AppNotification();
                notif.title = "Hóa đơn mới " + month;
                notif.shortDescription = "Căn hộ " + selectedApartment.apartmentCode + " có hóa đơn mới cần thanh toán.";
                notif.fullContent = "Kỳ thanh toán: " + month + "\nTổng tiền: " + currencyFormat.format(grandTotal) + "\nHạn thanh toán: " + selectedDueDate;
                notif.type = "UTILITY";
                notif.timestamp = System.currentTimeMillis();
                
                java.util.Date now = new java.util.Date();
                notif.dateStr = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(now);
                notif.timeStr = new java.text.SimpleDateFormat("HH:mm", Locale.getDefault()).format(now);
                notif.imageResId = R.drawable.ic_receipt_24;
                notif.affectedScope = "P." + selectedApartment.apartmentCode;

                db.appNotificationDao().insertNotification(notif);
            }

            runOnUiThread(() -> {
                String msg = isEditMode ? "Đã cập nhật hóa đơn thành công!" : "Đã tạo hóa đơn và gửi thông báo thành công!";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
    
    // Helper method to extract water usage to avoid variable shadowing scope limits if any
    private long getWaterUsageCount() {
        return getLong(etWaterNew) - getLong(etWaterOld);
    }
}

