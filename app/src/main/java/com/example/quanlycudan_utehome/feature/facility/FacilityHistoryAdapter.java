package com.example.quanlycudan_utehome.feature.facility;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.FacilityBooking;

import java.util.List;

public class FacilityHistoryAdapter extends RecyclerView.Adapter<FacilityHistoryAdapter.HistoryViewHolder> {

    private final List<FacilityBooking> bookingList;

    public FacilityHistoryAdapter(List<FacilityBooking> bookingList) {
        this.bookingList = bookingList;
    }

    public void updateData(List<FacilityBooking> newData) {
        bookingList.clear();
        bookingList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_facility_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        FacilityBooking booking = bookingList.get(position);

        // Simple mock for facility name/icon since we only have facilityId in booking.
        // In a real app we would join tables.
        String facilityName = "Sân bóng đá";
        int iconRes = R.drawable.img_football_field;
        switch (booking.facilityId) {
            case 1:
                facilityName = "Sân bóng đá";
                iconRes = R.drawable.img_football_field;
                break;
            case 2:
                facilityName = "Sân bóng chuyền";
                iconRes = R.drawable.img_volleyball_court;
                break;
            case 3:
                facilityName = "Sân cầu lông";
                iconRes = R.drawable.img_badminton_court;
                break;
        }

        holder.tvFacilityName.setText(facilityName);
        holder.ivIcon.setImageResource(iconRes);
        holder.tvDate.setText(booking.DayBooking + ", " + booking.bookingDate);
        holder.tvTime.setText(booking.startTime + " - " + booking.endTime);

        if ("CANCELLED".equals(booking.status)) {
            holder.tvStatus.setText("ĐÃ HUỶ");
            holder.tvStatus.setTextColor(Color.parseColor("#F44336"));
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFCDD2")));
            holder.btnCancel.setVisibility(View.GONE);
            
            holder.tvCancelReason.setVisibility(View.VISIBLE);
            holder.tvCancelReason.setText("Lý do: " + (booking.cancelReason != null ? booking.cancelReason : ""));
        } else {
            holder.tvStatus.setText("ĐÃ ĐẶT");
            holder.tvStatus.setTextColor(Color.parseColor("#388E3C"));
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#C8E6C9")));
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.tvCancelReason.setVisibility(View.GONE);

            holder.btnCancel.setOnClickListener(v -> showCancelDialog(holder.itemView, booking, position));
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    private void showCancelDialog(View view, FacilityBooking booking, int position) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_cancel_booking);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        EditText etReason = dialog.findViewById(R.id.etReason);
        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        
        dialog.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            String reason = etReason.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(view.getContext(), "Vui lòng nhập lý do huỷ sân!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Perform cancel
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(view.getContext());
                db.facilityBookingDao().updateBookingStatus(booking.id, "CANCELLED", reason);
                
                // Update local list
                booking.status = "CANCELLED";
                booking.cancelReason = reason;
                
                view.getContext().getMainExecutor().execute(() -> {
                    notifyItemChanged(position);
                    Toast.makeText(view.getContext(), "Đã huỷ đặt sân thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }).start();
        });

        dialog.show();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvFacilityName, tvDate, tvTime, tvStatus, btnCancel, tvCancelReason;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvFacilityName = itemView.findViewById(R.id.tvFacilityName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            tvCancelReason = itemView.findViewById(R.id.tvCancelReason);
        }
    }
}
