package com.example.quanlycudan_utehome.admin;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.ApartmentWithOwner;

import java.util.ArrayList;
import java.util.List;

public class ApartmentAdminAdapter extends RecyclerView.Adapter<ApartmentAdminAdapter.ApartmentViewHolder> {

    private List<ApartmentWithOwner> apartments = new ArrayList<>();
    private OnDeleteListener deleteListener;

    public interface OnDeleteListener {
        void onDelete(ApartmentWithOwner apartment);
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void setApartments(List<ApartmentWithOwner> apartments) {
        this.apartments = apartments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ApartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apartment, parent, false);
        return new ApartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApartmentViewHolder holder, int position) {
        ApartmentWithOwner item = apartments.get(position);
        
        holder.tvBuilding.setText("TÒA " + item.apartment.buildingCode);
        holder.tvApartmentNumber.setText("P." + item.apartment.apartmentCode);
        holder.tvArea.setText("Diện tích: " + item.apartment.area + " m²");
        
        String ownerName = item.getOwnerName();
        if ("Trống".equalsIgnoreCase(item.apartment.status) || ownerName == null) {
            holder.tvOwner.setText("Chưa có chủ hộ");
        } else {
            holder.tvOwner.setText("Chủ hộ: " + ownerName);
        }

        holder.tvStatus.setText(item.apartment.status);
        if ("Trống".equalsIgnoreCase(item.apartment.status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#E06A33")); // orange-ish
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_empty);
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#1F7343")); // green
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_occupied);
        }

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), ApartmentDetailActivity.class);
            intent.putExtra("apartment_id", item.apartment.id);
            v.getContext().startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return apartments.size();
    }

    static class ApartmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvBuilding, tvApartmentNumber, tvStatus, tvOwner, tvArea;
        android.widget.ImageView btnDelete;

        public ApartmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBuilding = itemView.findViewById(R.id.tvBuilding);
            tvApartmentNumber = itemView.findViewById(R.id.tvApartmentNumber);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvOwner = itemView.findViewById(R.id.tvOwner);
            tvArea = itemView.findViewById(R.id.tvArea);
            btnDelete = itemView.findViewById(R.id.btnDeleteItem);
        }
    }
}
