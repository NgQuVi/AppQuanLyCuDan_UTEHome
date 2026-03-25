package com.example.quanlycudan_utehome.admin;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.InvoiceItemRow;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class InvoiceManagementAdapter extends RecyclerView.Adapter<InvoiceManagementAdapter.InvoiceViewHolder> {

    private List<InvoiceItemRow> invoices = new ArrayList<>();
    private final DecimalFormat currencyFormat = new DecimalFormat("#,###");

    public void setInvoices(List<InvoiceItemRow> invoices) {
        this.invoices = invoices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        InvoiceItemRow item = invoices.get(position);

        holder.tvApartment.setText("P." + item.apartmentCode);
        holder.tvInvoiceId.setText("#" + item.invoiceId);
        holder.tvDueDate.setText("Hạn thanh toán: " + item.dueDate);
        holder.tvAmount.setText(currencyFormat.format(item.totalAmount) + "đ");

        if ("PAID".equalsIgnoreCase(item.status)) {
            holder.tvStatus.setText("ĐÃ THANH TOÁN");
            holder.tvStatus.setTextColor(Color.parseColor("#1F7343")); // Green
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_paid); // fallback if missing? we use existing resource or same logic
        } else {
            holder.tvStatus.setText("CHƯA THANH TOÁN");
            holder.tvStatus.setTextColor(Color.parseColor("#C05030")); // Orange
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_unpaid);
        }

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), InvoiceDetailActivity.class);
            intent.putExtra("invoice_id", item.invoiceId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvApartment, tvInvoiceId, tvDueDate, tvStatus, tvAmount;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvApartment = itemView.findViewById(R.id.tvApartment);
            tvInvoiceId = itemView.findViewById(R.id.tvInvoiceId);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
