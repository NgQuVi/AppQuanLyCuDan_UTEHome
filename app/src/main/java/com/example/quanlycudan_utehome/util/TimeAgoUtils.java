package com.example.quanlycudan_utehome.util;

public class TimeAgoUtils {

    /**
     * Tính chuỗi thời gian tương đối từ Unix timestamp (milliseconds).
     * Ví dụ: "Vừa xong", "5 phút trước", "2 giờ trước", "Hôm qua", "3 ngày trước"
     */
    public static String getTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 0) return "Vừa xong";

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours   = minutes / 60;
        long days    = hours / 24;

        if (seconds < 60) {
            return "Vừa xong";
        } else if (minutes < 60) {
            return minutes + " phút trước";
        } else if (hours < 24) {
            return hours + " giờ trước";
        } else if (days == 1) {
            return "Hôm qua";
        } else if (days < 30) {
            return days + " ngày trước";
        } else if (days < 365) {
            long months = days / 30;
            return months + " tháng trước";
        } else {
            long years = days / 365;
            return years + " năm trước";
        }
    }
}
