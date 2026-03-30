package com.example.quanlycudan_utehome.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.InvoiceItemRow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InvoiceManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InvoiceManagementAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<InvoiceItemRow> allInvoices = new ArrayList<>();
    
    private TextView chipAll, chipUnpaid, chipPaid;
    private String currentFilter = "All";
    
    private String currentMonthFilter = "Tất cả các tháng";
    private String currentApartmentFilter = "Tất cả căn hộ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invoice_management);

        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        chipAll = findViewById(R.id.chipAll);
        chipUnpaid = findViewById(R.id.chipUnpaid);
        chipPaid = findViewById(R.id.chipPaid);

        chipAll.setOnClickListener(v -> setFilter("All"));
        chipUnpaid.setOnClickListener(v -> setFilter("UNPAID"));
        chipPaid.setOnClickListener(v -> setFilter("PAID"));

        recyclerView = findViewById(R.id.rvInvoices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InvoiceManagementAdapter();
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabAdd).setOnClickListener(v -> {
            startActivity(new Intent(this, ComposeInvoiceActivity.class));
        });

        // Setup Dropdown UI text
        TextView tvFilterMonth = findViewById(R.id.tvFilterMonth);
        TextView tvFilterApartment = findViewById(R.id.tvFilterApartment);
        if (tvFilterMonth != null) tvFilterMonth.setText(currentMonthFilter);
        if (tvFilterApartment != null) tvFilterApartment.setText(currentApartmentFilter);

        findViewById(R.id.btnFilterMonth).setOnClickListener(v -> showMonthPicker());
        findViewById(R.id.btnFilterApartment).setOnClickListener(v -> showApartmentPicker());

        loadInvoices();
    }

    private void loadInvoices() {
        executorService.execute(() -> {
            allInvoices = AppDatabase.getInstance(this).paymentDao().getInvoiceItemRows();
            runOnUiThread(() -> {
                // Mặc định "Tất cả các tháng" -> Force UNPAID
                if ("Tất cả các tháng".equals(currentMonthFilter)) {
                    chipAll.setEnabled(false);
                    chipPaid.setEnabled(false);
                    chipAll.setAlpha(0.5f);
                    chipPaid.setAlpha(0.5f);
                    setFilter("UNPAID");
                } else {
                    applyFilter();
                }
            });
        });
    }

    private void setFilter(String filterText) {
        currentFilter = filterText;
        
        chipAll.setBackgroundResource(R.drawable.bg_compose_chip);
        chipAll.setTextColor(Color.parseColor("#718096"));
        
        chipUnpaid.setBackgroundResource(R.drawable.bg_compose_chip);
        chipUnpaid.setTextColor(Color.parseColor("#718096"));
        
        chipPaid.setBackgroundResource(R.drawable.bg_compose_chip);
        chipPaid.setTextColor(Color.parseColor("#718096"));

        if ("All".equals(filterText)) {
            chipAll.setBackgroundResource(R.drawable.bg_pill_orange_transparent); // using arbitrary active look
            chipAll.setTextColor(Color.parseColor("#FF7A50"));
        } else if ("UNPAID".equals(filterText)) {
            chipUnpaid.setBackgroundResource(R.drawable.bg_pill_orange_transparent);
            chipUnpaid.setTextColor(Color.parseColor("#FF7A50"));
        } else {
            chipPaid.setBackgroundResource(R.drawable.bg_pill_orange_transparent);
            chipPaid.setTextColor(Color.parseColor("#FF7A50"));
        }

        applyFilter();
    }

    private void applyFilter() {
        List<InvoiceItemRow> filteredList = new ArrayList<>();
        for (InvoiceItemRow item : allInvoices) {
            boolean matchesStatus = false;
            if ("All".equals(currentFilter)) {
                matchesStatus = true;
            } else if ("UNPAID".equals(currentFilter)) {
                matchesStatus = "UNPAID".equalsIgnoreCase(item.status) || "PARTIALLY_PAID".equalsIgnoreCase(item.status);
            } else {
                matchesStatus = currentFilter.equalsIgnoreCase(item.status);
            }
            
            boolean matchesMonth = "Tất cả các tháng".equals(currentMonthFilter) || 
                                   (item.billingMonth != null && item.billingMonth.equals(currentMonthFilter));
            
            boolean matchesApartment = "Tất cả căn hộ".equals(currentApartmentFilter) || 
                                       (item.apartmentCode != null && item.apartmentCode.equals(currentApartmentFilter));
            
            if (matchesStatus && matchesMonth && matchesApartment) {
                filteredList.add(item);
            }
        }
        adapter.setInvoices(filteredList);
        TextView tvListTitle = findViewById(R.id.tvListTitle);
        if (tvListTitle != null) {
            tvListTitle.setText("DANH SÁCH HÓA ĐƠN (" + filteredList.size() + ")");
        }
    }

    private void showMonthPicker() {
        // Lấy danh sách tháng duy nhất từ allInvoices
        java.util.Set<String> set = new java.util.HashSet<>();
        for (InvoiceItemRow r : allInvoices) {
            if (r.billingMonth != null && !r.billingMonth.isEmpty()) {
                set.add(r.billingMonth);
            }
        }
        List<String> months = new ArrayList<>(set);
        java.util.Collections.sort(months, java.util.Collections.reverseOrder()); // Mới nhất lên đầu
        months.add(0, "Tất cả các tháng"); // Luôn để "All" ở đầu
        
        String[] arr = months.toArray(new String[0]);
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Chọn kỳ thanh toán")
            .setItems(arr, (dialog, which) -> {
                currentMonthFilter = arr[which];
                TextView tvFilterMonth = findViewById(R.id.tvFilterMonth);
                if (tvFilterMonth != null) tvFilterMonth.setText(currentMonthFilter);
                
                if ("Tất cả các tháng".equals(currentMonthFilter)) {
                    chipAll.setEnabled(false);
                    chipPaid.setEnabled(false);
                    chipAll.setAlpha(0.5f);
                    chipPaid.setAlpha(0.5f);
                    setFilter("UNPAID");
                } else {
                    chipAll.setEnabled(true);
                    chipPaid.setEnabled(true);
                    chipAll.setAlpha(1.0f);
                    chipPaid.setAlpha(1.0f);
                    setFilter("All");
                }
            })
            .show();
    }

    private void showApartmentPicker() {
        // Lấy danh sách mã căn hộ duy nhất từ allInvoices
        java.util.Set<String> set = new java.util.HashSet<>();
        for (InvoiceItemRow r : allInvoices) {
            if (r.apartmentCode != null && !r.apartmentCode.isEmpty()) {
                set.add(r.apartmentCode);
            }
        }
        List<String> apts = new ArrayList<>(set);
        java.util.Collections.sort(apts);
        apts.add(0, "Tất cả căn hộ"); // Thêm All
        
        String[] arr = apts.toArray(new String[0]);
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Chọn căn hộ")
            .setItems(arr, (dialog, which) -> {
                currentApartmentFilter = arr[which];
                TextView tvFilterApartment = findViewById(R.id.tvFilterApartment);
                if (tvFilterApartment != null) tvFilterApartment.setText(currentApartmentFilter);
                applyFilter();
            })
            .show();
    }
}
