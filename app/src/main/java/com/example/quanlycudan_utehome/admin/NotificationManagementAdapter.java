package com.example.quanlycudan_utehome.admin;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.AppNotification;

import java.util.ArrayList;
import java.util.List;

public class NotificationManagementAdapter extends RecyclerView.Adapter<NotificationManagementAdapter.NotifViewHolder> {

    private List<AppNotification> notifications = new ArrayList<>();

    public void setNotifications(List<AppNotification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_admin, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        AppNotification item = notifications.get(position);

        holder.tvTitle.setText(item.title);
        holder.tvDesc.setText(item.shortDescription);
        holder.tvTime.setText(item.dateStr + "  " + item.timeStr);
        
        holder.vUnreadDot.setVisibility(item.isRead ? View.INVISIBLE : View.VISIBLE);

        // Styling based on type
        String typeStr = "KHÁC";
        int textColor = Color.parseColor("#8E8E8E");
        int bgTint = Color.parseColor("#E0E0E0");

        if (item.type != null) {
            switch (item.type) {
                case "MAINTENANCE":
                    typeStr = "BẢO TRÌ";
                    textColor = Color.parseColor("#2196F3");
                    bgTint = Color.parseColor("#E3F2FD");
                    holder.ivTypeIcon.setImageResource(R.drawable.ic_autorenew_24);
                    break;
                case "IMPORTANT":
                    typeStr = "QUAN TRỌNG";
                    textColor = Color.parseColor("#E53935");
                    bgTint = Color.parseColor("#FFEBEE");
                    holder.ivTypeIcon.setImageResource(R.drawable.ic_info_outline_24);
                    break;
                case "MEETING":
                    typeStr = "HỌP CƯ DÂN";
                    textColor = Color.parseColor("#43A047");
                    bgTint = Color.parseColor("#E8F5E9");
                    holder.ivTypeIcon.setImageResource(R.drawable.ic_person_24);
                    break;
                case "UTILITY":
                    typeStr = "TIỆN ÍCH";
                    textColor = Color.parseColor("#FB8C00");
                    bgTint = Color.parseColor("#FFF3E0");
                    holder.ivTypeIcon.setImageResource(R.drawable.ic_calendar_check_24);
                    break;
            }
        }

        holder.tvTag.setText(typeStr);
        holder.tvTag.setTextColor(textColor);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bgTint);
        gd.setCornerRadius(12f);
        holder.tvTag.setBackground(gd);

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), NotificationDetailAdminActivity.class);
            intent.putExtra("notif_id", item.id);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvTime, tvTitle, tvDesc;
        View vUnreadDot;
        ImageView ivTypeIcon;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tvTag);
            vUnreadDot = itemView.findViewById(R.id.vUnreadDot);
            ivTypeIcon = itemView.findViewById(R.id.ivTypeIcon);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
        }
    }
}
