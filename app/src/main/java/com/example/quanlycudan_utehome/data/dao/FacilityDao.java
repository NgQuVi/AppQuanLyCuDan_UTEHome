package com.example.quanlycudan_utehome.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlycudan_utehome.data.entity.Facility;

import java.util.List;

@Dao
public interface FacilityDao {

    @Insert
    long insertFacility(Facility facility);

    @Update
    void updateFacility(Facility facility);

    @Delete
    void deleteFacility(Facility facility);

    @Query("SELECT * FROM facilities ORDER BY id ASC")
    List<Facility> getAllFacilities();

    @Query("SELECT * FROM facilities WHERE id = :id")
    Facility getFacilityById(int id);
}
