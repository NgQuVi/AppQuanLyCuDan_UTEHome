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
    private OnEditMemberListener editListener;
    private OnDeleteMemberListener deleteListener;

    public interface OnEditMemberListener {
        void onEditMember(ResidentWithRole member, int position);
    }

    public interface OnDeleteMemberListener {
        void onDeleteMember(ResidentWithRole member, int position);
    }

    public MemberDetailAdapter(OnEditMemberListener editListener, OnDeleteMemberListener deleteListener) {
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

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
        
        // Edit button
        holder.btnEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEditMember(item, position);
            }
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteMember(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole;
        ImageView ivAvatar, btnEdit, btnDelete;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMemberName);
            tvRole = itemView.findViewById(R.id.tvMemberRole);
            ivAvatar = itemView.findViewById(R.id.ivMemberAvatar);
            btnEdit = itemView.findViewById(R.id.btnEditMember);
            btnDelete = itemView.findViewById(R.id.btnDeleteMember);
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
