package com.example.quanlycudan_utehome.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.List;

@Dao
public interface ResidentDao {

    @Insert
    long insert(Resident resident);

    @Query("SELECT * FROM residents")
    List<Resident> getAllResidents();

    @Query("SELECT * FROM residents WHERE id = :id")
    Resident getResidentById(int id);
}
