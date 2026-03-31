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

public class OccupancyAdapter extends RecyclerView.Adapter<OccupancyAdapter.OccupancyViewHolder> {

    private List<ApartmentWithOwner> apartments = new ArrayList<>();

    public void setApartments(List<ApartmentWithOwner> apartments) {
        this.apartments = apartments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OccupancyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_occupancy_apartment, parent, false);
        return new OccupancyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OccupancyViewHolder holder, int position) {
        ApartmentWithOwner item = apartments.get(position);
        
        holder.tvBuilding.setText("TÒA " + item.apartment.buildingCode);
        holder.tvApartmentNumber.setText("P." + item.apartment.apartmentCode);
        holder.tvArea.setText("Diện tích: " + item.apartment.area + " m²");
        
        String ownerName = item.getOwnerName();
        if (ownerName != null) {
            holder.tvOwner.setText("Chủ hộ: " + ownerName);
        } else {
            holder.tvOwner.setText("Chưa có chủ hộ");
        }

        holder.tvStatus.setText(item.apartment.status);
        if ("Trống".equalsIgnoreCase(item.apartment.status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#E06A33")); 
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_empty);
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#1F7343")); 
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_occupied);
        }

        holder.btnDetails.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), ApartmentDetailActivity.class);
            intent.putExtra("apartment_id", item.apartment.id);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return apartments.size();
    }

    static class OccupancyViewHolder extends RecyclerView.ViewHolder {
        TextView tvBuilding, tvApartmentNumber, tvStatus, tvOwner, tvArea, btnDetails;

        public OccupancyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBuilding = itemView.findViewById(R.id.tvBuilding);
            tvApartmentNumber = itemView.findViewById(R.id.tvApartmentNumber);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvOwner = itemView.findViewById(R.id.tvOwner);
            tvArea = itemView.findViewById(R.id.tvArea);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }
    }
}
