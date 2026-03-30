package com.example.quanlycudan_utehome.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.List;

public class ResidentPickerAdapter extends RecyclerView.Adapter<ResidentPickerAdapter.ResidentViewHolder> {

    private List<Resident> residents;
    private final OnResidentSelectedListener listener;

    public interface OnResidentSelectedListener {
        void onResidentSelected(Resident resident);
    }

    public ResidentPickerAdapter(List<Resident> residents, OnResidentSelectedListener listener) {
        this.residents = residents;
        this.listener = listener;
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resident_picker, parent, false);
        return new ResidentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResidentViewHolder holder, int position) {
        Resident resident = residents.get(position);
        holder.tvName.setText(resident.fullName);
        holder.tvPhone.setText(resident.phone != null ? resident.phone : "");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onResidentSelected(resident);
            }
        });
    }

    @Override
    public int getItemCount() {
        return residents.size();
    }

    static class ResidentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPhone;

        ResidentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvResidentName);
            tvPhone = itemView.findViewById(R.id.tvResidentPhone);
        }
    }
}
