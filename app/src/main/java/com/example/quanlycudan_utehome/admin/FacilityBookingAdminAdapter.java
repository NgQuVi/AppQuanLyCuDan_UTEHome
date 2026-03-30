package com.example.quanlycudan_utehome.admin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.FacilityBookingRow;

import java.util.ArrayList;
import java.util.List;

public class FacilityBookingAdminAdapter extends RecyclerView.Adapter<FacilityBookingAdminAdapter.ViewHolder> {

    public interface OnActionListener {
        void onApprove(FacilityBookingRow booking);
        void onReject(FacilityBookingRow booking);
    }

    private List<FacilityBookingRow> bookingList = new ArrayList<>();
    private OnActionListener listener;

    public void setOnActionListener(OnActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<FacilityBookingRow> list) {
        this.bookingList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_facility_booking_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FacilityBookingRow booking = bookingList.get(position);
        Context ctx = holder.itemView.getContext();

        holder.tvFacilityName.setText(booking.facilityName != null ? booking.facilityName : "Tiện ích");
        holder.tvResidentName.setText(booking.residentName != null ? booking.residentName : "Cư dân #" + booking.residentId);

        // Date and time
        String dateDisplay = booking.DayBooking != null ? booking.DayBooking : (booking.bookingDate != null ? booking.bookingDate : "");
        holder.tvDate.setText(dateDisplay);

        String timeDisplay = (booking.startTime != null ? booking.startTime : "") + " - " + (booking.endTime != null ? booking.endTime : "");
        holder.tvTime.setText(timeDisplay);

        // Status badge
        String status = booking.status != null ? booking.status.toUpperCase() : "PENDING";
        switch (status) {
            case "APPROVED":
                holder.tvStatus.setText("ĐÃ DUYỆT");
                holder.tvStatus.setTextColor(Color.parseColor("#28A745"));
                holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                holder.layoutActions.setVisibility(View.GONE);
                break;
            case "REJECTED":
            case "CANCELLED":
                holder.tvStatus.setText("ĐÃ HUỶ");
                holder.tvStatus.setTextColor(Color.parseColor("#C62828"));
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
                holder.layoutActions.setVisibility(View.GONE);
                break;
            default: // PENDING
                holder.tvStatus.setText("CHỜ DUYỆT");
                holder.tvStatus.setTextColor(Color.parseColor("#E67E22"));
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFF3E0"));
                holder.layoutActions.setVisibility(View.VISIBLE);
                break;
        }

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) listener.onApprove(booking);
        });
        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(booking);
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFacilityName, tvResidentName, tvDate, tvTime, tvStatus;
        Button btnApprove, btnReject;
        LinearLayout layoutActions;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFacilityName = itemView.findViewById(R.id.tvFacilityName);
            tvResidentName = itemView.findViewById(R.id.tvResidentName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            layoutActions = itemView.findViewById(R.id.layoutActions);
        }
    }
}
