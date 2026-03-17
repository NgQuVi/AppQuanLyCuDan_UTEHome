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

    /** Lấy ID căn hộ của 1 cư dân dựa vào residentId (trả về String vì Invoice.apartmentId là String) */
    @Query("SELECT CAST(apartmentId AS TEXT) FROM apartment_members WHERE residentId = :residentId LIMIT 1")
    String getApartmentIdByResidentId(int residentId);

    @Update
    void update(ApartmentMember member);
}