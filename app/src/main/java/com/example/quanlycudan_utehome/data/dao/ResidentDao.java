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
    long insert(Resident resident);

    @Update
    void update(Resident resident);

    @Query("SELECT * FROM residents")
    List<Resident> getAllResidents();

    @Query("SELECT * FROM residents WHERE id = :id LIMIT 1")
    Resident getResidentById(int id);
    
    @Query("SELECT * FROM residents WHERE phone = :phone LIMIT 1")
    Resident getResidentByPhone(String phone);
}
