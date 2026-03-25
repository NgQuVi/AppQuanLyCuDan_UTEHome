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

        loadInvoices();
    }

    private void loadInvoices() {
        executorService.execute(() -> {
            allInvoices = AppDatabase.getInstance(this).paymentDao().getInvoiceItemRows();
            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.tvListTitle)).setText("DANH SÁCH HÓA ĐƠN (" + allInvoices.size() + ")");
                applyFilter();
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
            if ("All".equals(currentFilter) || currentFilter.equalsIgnoreCase(item.status)) {
                filteredList.add(item);
            }
        }
        adapter.setInvoices(filteredList);
    }
}
