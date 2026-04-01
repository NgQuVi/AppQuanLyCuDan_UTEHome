package com.example.quanlycudan_utehome.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.ResidentWithApartment;

import java.util.ArrayList;
import java.util.List;

public class ResidentAdapter extends RecyclerView.Adapter<ResidentAdapter.ResidentViewHolder> {

    private List<ResidentWithApartment> residents = new ArrayList<>();

    public void setResidents(List<ResidentWithApartment> residents) {
        this.residents = residents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resident, parent, false);
        return new ResidentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResidentViewHolder holder, int position) {
        ResidentWithApartment item = residents.get(position);
        holder.tvName.setText(item.resident.fullName);
        
        if (item.apartment != null) {
            String roomInfo = "P." + item.apartment.apartmentCode + " - Tòa " + item.apartment.buildingCode;
            holder.tvRoomInfo.setText(roomInfo);
        } else {
            holder.tvRoomInfo.setText("Chưa có căn hộ");
        }

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), ResidentDetailActivity.class);
            intent.putExtra("resident_id", item.resident.id);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return residents.size();
    }

    static class ResidentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvRoomInfo;

        public ResidentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRoomInfo = itemView.findViewById(R.id.tvRoomInfo);
        }
    }
}
