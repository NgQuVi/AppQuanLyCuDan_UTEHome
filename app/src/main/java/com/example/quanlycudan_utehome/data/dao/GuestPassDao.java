package com.example.quanlycudan_utehome.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlycudan_utehome.data.entity.GuestPass;

import java.util.List;

@Dao
public interface GuestPassDao {

    @Insert
    long insertGuestPass(GuestPass guestPass);

    @Update
    void updateGuestPass(GuestPass guestPass);

    /** LiveData — dùng để observe trên UI thread */
    @Query("SELECT * FROM guest_passes WHERE code = :code LIMIT 1")
    GuestPass findByCode(String code);

    @Query("SELECT * FROM guest_passes WHERE apartmentId = :apartmentId ORDER BY createdAt DESC")
    List<GuestPass> getGuestPassesByApartmentIdSync(int apartmentId);

    @Query("SELECT * FROM guest_passes WHERE id = :id LIMIT 1")
    GuestPass findByIdSync(int id);
}
