package com.example.quanlycudan_utehome.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlycudan_utehome.data.entity.GuestPass;

import java.util.List;

@Dao
public interface GuestPassDao {
    @Insert
    void insertGuestPass(GuestPass guestPass);

    @Update
    void updateGuestPass(GuestPass guestPass);

    @Query("SELECT * FROM guest_passes WHERE apartmentId = :apartmentId")
    List<GuestPass> getGuestPassesByApartmentIdSync(int apartmentId);
}
