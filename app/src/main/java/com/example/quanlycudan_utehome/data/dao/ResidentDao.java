package com.example.quanlycudan_utehome.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.List;

@Dao
public interface ResidentDao {

    @Insert
    void insert(Resident resident);

    @Update
    void update(Resident resident);

    @Query("SELECT * FROM residents")
    List<Resident> getAllResidents();

    @Query("SELECT * FROM residents WHERE id = :id LIMIT 1")
    Resident getResidentById(int id);
    
    @Query("SELECT * FROM residents WHERE phone = :phone LIMIT 1")
    Resident getResidentByPhone(String phone);

    @Query("UPDATE residents SET password = :newPassword WHERE phone = :phone")
    void updatePassword(String phone, String newPassword);

    @Query("SELECT COUNT(*) FROM residents WHERE phone = :phone")
    int checkPhoneExists(String phone);
}
