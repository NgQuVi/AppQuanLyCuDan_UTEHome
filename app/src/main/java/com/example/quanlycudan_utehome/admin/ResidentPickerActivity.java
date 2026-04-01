package com.example.quanlycudan_utehome.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResidentPickerActivity extends AppCompatActivity {

    private RecyclerView rvResidents;
    private EditText etSearch;
    private PickerAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<Resident> allResidents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resident_picker);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etSearch = findViewById(R.id.etSearch);
        rvResidents = findViewById(R.id.rvResidents);
        rvResidents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PickerAdapter();
        rvResidents.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadResidents();
    }

    private void loadResidents() {
        executorService.execute(() -> {
            allResidents = AppDatabase.getInstance(this).residentDao().getAllResidents();
            runOnUiThread(() -> adapter.setResidents(allResidents));
        });
    }

    private void filter(String query) {
        List<Resident> filteredList = new ArrayList<>();
        for (Resident r : allResidents) {
            if (r.fullName.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(r);
            }
        }
        adapter.setResidents(filteredList);
    }

    class PickerAdapter extends RecyclerView.Adapter<PickerAdapter.ViewHolder> {
        private List<Resident> list = new ArrayList<>();

        public void setResidents(List<Resident> residents) {
            this.list = residents;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resident, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Resident r = list.get(position);
            holder.tvName.setText(r.fullName);
            holder.tvInfo.setText("SĐT: " + r.phone);

            holder.itemView.setOnClickListener(v -> {
                Intent res = new Intent();
                res.putExtra("resident_id", r.id);
                res.putExtra("resident_name", r.fullName);
                res.putExtra("account_id", r.accountId);
                setResult(RESULT_OK, res);
                finish();
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvInfo;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvName);
                tvInfo = itemView.findViewById(R.id.tvRoomInfo);
            }
        }
    }
}
