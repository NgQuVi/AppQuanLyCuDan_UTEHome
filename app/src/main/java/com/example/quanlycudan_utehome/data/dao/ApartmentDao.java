package com.example.quanlycudan_utehome.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.ApartmentWithOwner;

import java.util.List;

@Dao
public interface ApartmentDao {

    @Insert
    void insert(Apartment apartment);

    @Insert
    long insertApartment(Apartment apartment);

    @androidx.room.Update
    void update(Apartment apartment);

    @Query("SELECT * FROM apartments WHERE id = :id")
    Apartment getApartmentById(int id);

    @Query("SELECT * FROM apartments WHERE accountId = :accountId LIMIT 1")
    Apartment getApartmentByAccountId(int accountId);

    @Query("SELECT * FROM apartments")
    java.util.List<Apartment> getAllApartments();

    @Query("SELECT * FROM apartments")
    java.util.List<Apartment> getAllApartmentsSync();


    @Query("SELECT id FROM apartments WHERE accountId = :accountId")
    Integer getApartmentIdByAccountId(int accountId);

    @Query("SELECT COUNT(*) FROM apartments")
    int getApartmentCount();

    @Query("SELECT COUNT(*) FROM apartments WHERE status = :status")
    int getApartmentCountByStatus(String status);

    @androidx.room.Transaction
    @Query("SELECT * FROM apartments")
    List<ApartmentWithOwner> getApartmentsWithOwners();

    @Query("SELECT * FROM apartments WHERE status = 'Trống' AND accountId = 0")
    List<Apartment> getAvailableApartments();
}
