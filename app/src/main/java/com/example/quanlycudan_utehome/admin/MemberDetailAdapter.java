package com.example.quanlycudan_utehome.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.ArrayList;
import java.util.List;

public class MemberDetailAdapter extends RecyclerView.Adapter<MemberDetailAdapter.MemberViewHolder> {

    private List<ResidentWithRole> members = new ArrayList<>();

    public void setMembers(List<ResidentWithRole> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_simple, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        ResidentWithRole item = members.get(position);
        holder.tvName.setText(item.resident.fullName);
        holder.tvRole.setText(item.role);
        
        // Simple divider logic: hide for last item if needed, but here we can just use layout padding/margin
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole;
        ImageView ivAvatar;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMemberName);
            tvRole = itemView.findViewById(R.id.tvMemberRole);
            ivAvatar = itemView.findViewById(R.id.ivMemberAvatar);
        }
    }

    public static class ResidentWithRole {
        public Resident resident;
        public String role;

        public ResidentWithRole(Resident resident, String role) {
            this.resident = resident;
            this.role = role;
        }
    }
}
