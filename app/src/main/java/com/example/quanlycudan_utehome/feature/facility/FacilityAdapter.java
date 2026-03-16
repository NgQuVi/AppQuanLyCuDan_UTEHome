package com.example.quanlycudan_utehome.feature.facility;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.Facility;

import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder> {

    private List<Facility> facilityList;
    private OnFacilityClickListener listener;

    public interface OnFacilityClickListener {
        void onFacilityClick(Facility facility);
    }

    public FacilityAdapter(List<Facility> facilityList, OnFacilityClickListener listener) {
        this.facilityList = facilityList;
        this.listener = listener;
    }

    public void updateList(List<Facility> newList) {
        this.facilityList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_facility, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilityList.get(position);
        holder.tvName.setText(facility.name);
        holder.tvLocation.setText(facility.location);
        
        // Use generic image loader or direct resource if standard android
        if (facility.imageResId != 0) {
            holder.ivImage.setImageResource(facility.imageResId);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFacilityClick(facility);
            }
        });
    }

    @Override
    public int getItemCount() {
        return facilityList == null ? 0 : facilityList.size();
    }

    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName;
        TextView tvLocation;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivFacilityImage);
            tvName = itemView.findViewById(R.id.tvFacilityName);
            tvLocation = itemView.findViewById(R.id.tvFacilityLocation);
        }
    }
}
