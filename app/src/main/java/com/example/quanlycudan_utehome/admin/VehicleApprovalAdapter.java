package com.example.quanlycudan_utehome.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.feature.vehicle.VehicleWithOwner;

import java.util.ArrayList;
import java.util.List;

public class VehicleApprovalAdapter extends RecyclerView.Adapter<VehicleApprovalAdapter.VehicleViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(VehicleWithOwner vehicle);
    }

    private List<VehicleWithOwner> vehicleList = new ArrayList<>();
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<VehicleWithOwner> list) {
        if (list != null) {
            this.vehicleList = list;
        } else {
            this.vehicleList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle_admin, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        VehicleWithOwner vehicle = vehicleList.get(position);

        // License plate
        holder.tvLicensePlate.setText(vehicle.licensePlate != null ? vehicle.licensePlate : "");

        // Vehicle description (brand and color)
        String desc = (vehicle.brand != null ? vehicle.brand : "") + " - " + (vehicle.color != null ? vehicle.color : "");
        holder.tvVehicleDesc.setText(desc);

        // Owner info - format: Name • P.ApartmentCode
        String apartmentCode = (vehicle.apartmentCode != null && !vehicle.apartmentCode.isEmpty())
                ? vehicle.apartmentCode
                : "P." + vehicle.apartmentId;
        String ownerInfo = (vehicle.ownerName != null ? vehicle.ownerName : "Không xác định") + " • " + apartmentCode;
        holder.tvOwnerInfo.setText(ownerInfo);

        // Status
        String status = vehicle.status != null ? vehicle.status : "PENDING";
        switch (status.toUpperCase()) {
            case "APPROVED":
                holder.tvStatus.setText("ĐÃ DUYỆT");
                holder.tvStatus.setTextColor(0xFF28A745); // Green
                holder.tvStatus.setBackgroundColor(0xFFE8F5E9);
                break;
            case "REJECTED":
                holder.tvStatus.setText("ĐÃ TỪ CHỐI");
                holder.tvStatus.setTextColor(0xFFC62828); // Red
                holder.tvStatus.setBackgroundColor(0xFFFFEBEE);
                break;
            default:
                holder.tvStatus.setText("CHỜ DUYỆT");
                holder.tvStatus.setTextColor(0xFFFF9800); // Orange
                holder.tvStatus.setBackgroundColor(0xFFFFF3E0);
        }

        // Vehicle type icon
        if (vehicle.vehicleType != null) {
            switch (vehicle.vehicleType.toLowerCase()) {
                case "ô tô":
                case "oto":
                    holder.ivVehicleIcon.setImageResource(R.drawable.ic_directions_car_24);
                    break;
                case "xe máy":
                case "xemay":
                    holder.ivVehicleIcon.setImageResource(R.drawable.ic_vehicle);
                    break;
                default:
                    holder.ivVehicleIcon.setImageResource(R.drawable.ic_directions_car_24);
            }
        }

        // Item click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(vehicle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView tvLicensePlate;
        TextView tvVehicleDesc;
        TextView tvOwnerInfo;
        TextView tvStatus;
        ImageView ivVehicleIcon;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLicensePlate = itemView.findViewById(R.id.tvLicensePlate);
            tvVehicleDesc = itemView.findViewById(R.id.tvVehicleDesc);
            tvOwnerInfo = itemView.findViewById(R.id.tvOwnerInfo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivVehicleIcon = itemView.findViewById(R.id.ivVehicleIcon);
        }
    }
}
