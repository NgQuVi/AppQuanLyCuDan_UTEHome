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
                .inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        VehicleWithOwner v = vehicleList.get(position);

        // tránh null crash
        holder.tvLicensePlate.setText(v.licensePlate != null ? v.licensePlate : "");
        holder.tvStatus.setText(v.status != null ? v.status : "");
        holder.tvBrandColor.setText(v.brand != null ? v.brand : "");
        holder.tvBrandColor2.setText("• Màu " + (v.color != null ? v.color : ""));

        // hiển thị tên chủ sở hữu (nếu layout có TextView tương ứng)
        if (holder.tvOwnerName != null) {
            holder.tvOwnerName.setText(
                    v.ownerName != null ? v.ownerName : "Không rõ"
            );
        }

        // click mở thẻ ra vào
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) listener.onItemClick(v);
        });

        // icon theo loại xe
        if (v.vehicleType != null) {
            switch (v.vehicleType) {
                case "Ô tô":
                    holder.ivVehicleType.setImageResource(R.drawable.ic_car_orange);
                    break;
                case "Xe máy":
                    holder.ivVehicleType.setImageResource(R.drawable.ic_car_orange); // nên dùng icon khác
                    break;
                default:
                    holder.ivVehicleType.setImageResource(R.drawable.ic_car_orange);
                    break;
            }
        } else {
            holder.ivVehicleType.setImageResource(R.drawable.ic_car_orange);
        }
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {

        TextView tvLicensePlate, tvStatus, tvBrandColor, tvBrandColor2;
        // TextView hiển thị tên chủ sở hữu (nếu có trong layout)
        TextView tvOwnerName;
        ImageView ivVehicleType;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);

            tvLicensePlate = itemView.findViewById(R.id.tvLicensePlate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBrandColor = itemView.findViewById(R.id.tvBrandColor);
            tvBrandColor2 = itemView.findViewById(R.id.tvBrandColor2);
            ivVehicleType = itemView.findViewById(R.id.ivVehicleType);
            // nếu layout có TextView tên chủ xe thì gán id ở đây, ví dụ R.id.tvOwnerName
            try {
                tvOwnerName = itemView.findViewById(R.id.tvOwnerName);
            } catch (Exception e) {
                tvOwnerName = null; // tránh crash nếu chưa thêm vào layout
            }
        }
    }
}