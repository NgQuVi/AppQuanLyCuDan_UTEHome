package com.example.quanlycudan_utehome.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Transaction;

import com.example.quanlycudan_utehome.data.entity.Resident;
import com.example.quanlycudan_utehome.data.entity.ResidentWithApartment;

import java.util.List;

@Dao
public interface ResidentDao {

    @Insert
    long insert(Resident resident);

    @Update
    void update(Resident resident);

    @Query("SELECT * FROM residents")
    List<Resident> getAllResidents();

    @Transaction
    @Query("SELECT * FROM residents")
    List<ResidentWithApartment> getResidentsWithApartments();

    @Query("SELECT * FROM residents WHERE id = :id LIMIT 1")
    Resident getResidentById(int id);

    @Query("SELECT * FROM residents WHERE accountId = :accountId LIMIT 1")
    Resident getResidentByAccountId(int accountId);

    @Query("DELETE FROM residents WHERE id = :residentId")
    void deleteById(int residentId);

    @Query("SELECT COUNT(*) FROM residents")
    int getResidentCount();
}
