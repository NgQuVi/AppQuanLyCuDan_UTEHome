package com.example.quanlycudan_utehome.feature.notification;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.util.TimeAgoUtils;
import com.example.quanlycudan_utehome.data.entity.AppNotification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<AppNotification> notifications;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AppNotification notification);
    }

    public NotificationAdapter(List<AppNotification> notifications, OnItemClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppNotification notif = notifications.get(position);
        holder.bind(notif, listener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llIconContainer;
        ImageView ivIcon;
        TextView tvTag;
        View dotUnread;
        TextView tvTime;
        TextView tvTitle;
        TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llIconContainer = itemView.findViewById(R.id.llIconContainer);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTag = itemView.findViewById(R.id.tvTag);
            dotUnread = itemView.findViewById(R.id.dotUnread);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        public void bind(AppNotification notif, OnItemClickListener listener) {
            tvTitle.setText(notif.title);
            tvDescription.setText(notif.shortDescription);
            tvTime.setText(TimeAgoUtils.getTimeAgo(notif.timestamp));

            dotUnread.setVisibility(notif.isRead ? View.GONE : View.VISIBLE);

            // Styling based on type
            int iconRes = R.drawable.ic_document;
            String iconColorStr = "#4CAF50"; // DEFAULT GREEN
            String bgColorStr = "#E8F5E9";
            String tagText = "TIỆN ÍCH";
            
            if ("MAINTENANCE".equals(notif.type)) {
                iconRes = R.drawable.ic_nav_person; // mock icon
                iconColorStr = "#F57C00";
                bgColorStr = "#FFF3E0";
                tagText = "BẢO TRÌ";
            } else if ("IMPORTANT".equals(notif.type)) {
                iconRes = R.drawable.ic_flash_on; // lightning mock icon
                iconColorStr = "#D32F2F";
                bgColorStr = "#FFEBEE";
                tagText = "QUAN TRỌNG";
            } else if ("MEETING".equals(notif.type)) {
                iconRes = R.drawable.ic_group; // group icon
                iconColorStr = "#1976D2";
                bgColorStr = "#E3F2FD";
                tagText = "HỌP CƯ DÂN";
            } else if ("UTILITY".equals(notif.type)) {
                iconRes = R.drawable.ic_document; 
                iconColorStr = "#388E3C";
                bgColorStr = "#E8F5E9";
                tagText = "TIỆN ÍCH";
            }

            tvTag.setText(tagText);
            tvTag.setTextColor(Color.parseColor(iconColorStr));
            
            ivIcon.setImageResource(iconRes);
            ivIcon.setColorFilter(Color.parseColor(iconColorStr));
            
            // Set container rounded background dynamically
            GradientDrawable bgContainer = new GradientDrawable();
            bgContainer.setShape(GradientDrawable.RECTANGLE);
            bgContainer.setCornerRadius(16f); // 8dp depending on density
            bgContainer.setColor(Color.parseColor(bgColorStr));
            llIconContainer.setBackground(bgContainer);
            
            // Set tag rounded background
            GradientDrawable bgTag = new GradientDrawable();
            bgTag.setShape(GradientDrawable.RECTANGLE);
            bgTag.setCornerRadius(8f);
            bgTag.setColor(Color.parseColor(bgColorStr));
            tvTag.setBackground(bgTag);

            itemView.setOnClickListener(v -> listener.onItemClick(notif));
        }
    }
}
