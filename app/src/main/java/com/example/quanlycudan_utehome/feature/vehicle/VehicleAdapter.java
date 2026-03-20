package com.example.quanlycudan_utehome.feature.vehicle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicleList = new ArrayList<>();

    public void setData(List<Vehicle> list) {
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
                .inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle v = vehicleList.get(position);

        // tránh null crash
        holder.tvLicensePlate.setText(v.licensePlate != null ? v.licensePlate : "");
        holder.tvStatus.setText(v.status != null ? v.status : "");
        holder.tvBrandColor.setText(v.brand != null ? v.brand : "");
        holder.tvBrandColor2.setText("• Màu " + (v.color != null ? v.color : ""));

        // icon theo loại xe
        if (v.vehicleType != null) {
            switch (v.vehicleType) {
                case "Ô tô":
                    holder.ivVehicleType.setImageResource(R.drawable.ic_car_orange);
                    break;
                case "Xe máy":
                    holder.ivVehicleType.setImageResource(R.drawable.ic_car_orange);
                    break;
                default:
                    holder.ivVehicleType.setImageResource(R.drawable.ic_car_orange);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {

        TextView tvLicensePlate, tvStatus, tvBrandColor, tvBrandColor2;
        ImageView ivVehicleType;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);

            tvLicensePlate = itemView.findViewById(R.id.tvLicensePlate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBrandColor = itemView.findViewById(R.id.tvBrandColor);
            tvBrandColor2 = itemView.findViewById(R.id.tvBrandColor2);
            ivVehicleType = itemView.findViewById(R.id.ivVehicleType);
        }
    }
}