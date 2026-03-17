package com.example.quanlycudan_utehome.feature.apartment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.ApartmentWithMembers;

import java.util.List;

public class ApartmentMemberAdapter extends RecyclerView.Adapter<ApartmentMemberAdapter.ViewHolder> {

    public interface OnMemberClickListener {
        void onMemberClick(ApartmentWithMembers.ApartmentMemberDetail detail);
    }

    private final List<ApartmentWithMembers.ApartmentMemberDetail> members;
    private final OnMemberClickListener onMemberClickListener;

    public ApartmentMemberAdapter(List<ApartmentWithMembers.ApartmentMemberDetail> members,
                                  OnMemberClickListener onMemberClickListener) {
        this.members = members;
        this.onMemberClickListener = onMemberClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apartment_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApartmentWithMembers.ApartmentMemberDetail detail = members.get(position);

        holder.tvMemberName.setText(detail.resident.fullName);
        holder.tvMemberRole.setText(detail.apartmentMember.role);

        String firstLetter = detail.resident.fullName.substring(0, 1).toUpperCase();
        holder.tvMemberAvatar.setText(firstLetter);
        holder.itemView.setOnClickListener(v -> {
            if (onMemberClickListener != null) {
                onMemberClickListener.onMemberClick(detail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return members != null ? members.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemberName;
        TextView tvMemberRole;
        TextView tvMemberAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMemberRole = itemView.findViewById(R.id.tvMemberRole);
            tvMemberAvatar = itemView.findViewById(R.id.tvMemberAvatar);
        }
    }
}
