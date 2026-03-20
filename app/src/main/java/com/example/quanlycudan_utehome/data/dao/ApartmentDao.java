package com.example.quanlycudan_utehome.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.quanlycudan_utehome.data.entity.Apartment;

@Dao
public interface ApartmentDao {

    @Insert
    void insert(Apartment apartment);

    @Query("SELECT * FROM apartments WHERE id = :id")
    Apartment getApartmentById(int id);

    @Query("SELECT * FROM apartments")
    java.util.List<Apartment> getAllApartments();

    @Query("SELECT id FROM apartments WHERE accountId = :accountId")
    Integer getApartmentIdByAccountId(int accountId);


}
