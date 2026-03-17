package com.example.quanlycudan_utehome.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlycudan_utehome.data.entity.ApartmentMember;

import java.util.List;

@Dao
public interface ApartmentMemberDao {

    @Insert
    void insert(ApartmentMember member);

    @Query("SELECT * FROM apartment_members WHERE apartmentId = :apartmentId")
    List<ApartmentMember> getMembers(int apartmentId);

    @Query("SELECT * FROM apartment_members")
    List<ApartmentMember> getAllMembers();

    @Query("SELECT apartmentId FROM apartment_members WHERE residentId = :id LIMIT 1")
    Integer getApartmentIdByResidentId(int id);


    @Update
    void update(ApartmentMember member);
}