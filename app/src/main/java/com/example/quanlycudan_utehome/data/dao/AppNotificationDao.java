package com.example.quanlycudan_utehome.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.quanlycudan_utehome.data.entity.AppNotification;

import java.util.List;

@Dao
public interface AppNotificationDao {

    @Insert
    void insertNotification(AppNotification notification);

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    List<AppNotification> getAllNotifications();

    @Query("SELECT * FROM notifications WHERE id = :id LIMIT 1")
    AppNotification getNotificationById(int id);

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    void markAsRead(int id);

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    int getUnreadCount();
}
