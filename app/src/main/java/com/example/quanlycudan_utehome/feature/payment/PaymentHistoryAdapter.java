package com.example.quanlycudan_utehome.feature.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.TransactionHistory;
import java.text.DecimalFormat;
import java.util.List;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder> {

    private List<TransactionHistory> transactions;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TransactionHistory transaction);
    }

    public PaymentHistoryAdapter(List<TransactionHistory> transactions, OnItemClickListener listener) {
        this.transactions = transactions;
        this.listener = listener;
    }

    public void setTransactions(List<TransactionHistory> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionHistory tx = transactions.get(position);

        // Hiển thị dữ liệu
        holder.tvTitle.setText("Thanh toán hóa đơn"); // Có thể bổ sung tháng nếu lấy được từ invoiceId
        holder.tvDate.setText(tx.transactionTime);
        holder.tvCode.setText(tx.transactionCode);

        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvAmount.setText(formatter.format(tx.paidAmount) + "đ");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(tx);
        });
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvCode, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvPaymentTitle);
            tvDate = itemView.findViewById(R.id.tvPaymentDate);
            tvCode = itemView.findViewById(R.id.tvPaymentCode);
            tvAmount = itemView.findViewById(R.id.tvPaymentAmount);
        }
    }
}
