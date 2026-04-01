package com.example.quanlycudan_utehome.feature.guest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.GuestPass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GuestQrHistoryAdapter extends RecyclerView.Adapter<GuestQrHistoryAdapter.ViewHolder> {

    private List<GuestPass> passes;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private Context context;

    public GuestQrHistoryAdapter(List<GuestPass> passes) {
        this.passes = passes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_guest_qr_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GuestPass pass = passes.get(position);

        // Hiển thị mã QR (16 ký tự đầu)
        String shortCode = pass.code != null && pass.code.length() > 16
                ? pass.code.substring(0, 16).toUpperCase(Locale.getDefault())
                : pass.code;
        holder.tvCode.setText("Mã: " + shortCode);

        // Hiển thị status
        String status = pass.status != null ? pass.status : "UNKNOWN";
        int statusColor = getStatusColor(status);
        holder.tvStatus.setText(status);
        holder.tvStatus.setTextColor(statusColor);

        // Hiển thị thời gian tạo
        String createdDate = dateFormat.format(new Date(pass.createdAt));
        holder.tvCreatedAt.setText("Tạo: " + createdDate);

        // Hiển thị thời gian hiệu lực
        String fromDate = dateFormat.format(new Date(pass.validFrom));
        String toDate = dateFormat.format(new Date(pass.validTo));
        holder.tvValidTime.setText("Từ: " + fromDate + "\nĐến: " + toDate);

        // Click listener to open detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GuestQrDetailActivity.class);
            intent.putExtra("qr_id", pass.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return passes.size();
    }

    public void updateData(List<GuestPass> newPasses) {
        this.passes = newPasses;
        notifyDataSetChanged();
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "ACTIVE":
                return 0xFF00AA00; // Xanh lá
            case "PENDING":
                return 0xFFFF9800; // Cam
            case "EXPIRED":
                return 0xFFAA0000; // Đỏ
            case "CANCELLED":
                return 0xFF888888; // Xám
            default:
                return 0xFF000000; // Đen
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode;
        TextView tvStatus;
        TextView tvCreatedAt;
        TextView tvValidTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvValidTime = itemView.findViewById(R.id.tvValidTime);
        }
    }
}


