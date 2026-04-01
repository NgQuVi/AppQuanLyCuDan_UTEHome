package com.example.quanlycudan_utehome.data.dao;

import androidx.lifecycle.LiveData;
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

    @Query("SELECT apartmentId FROM apartment_members WHERE residentId = :id")
    List<Integer> getApartmentIdsByResidentId(int id);

    @Query("DELETE FROM apartment_members WHERE residentId = :residentId")
    void deleteByResidentId(int residentId);

    @Query("DELETE FROM apartment_members WHERE apartmentId = :apartmentId")
    void deleteByApartmentId(int apartmentId);

    @androidx.room.Delete
    void delete(ApartmentMember member);

    @Query("SELECT residentId FROM apartment_members WHERE apartmentId = :apartmentId")
    LiveData<List<Integer>> getResidentIdsByApartmentId(int apartmentId);


    @Query("SELECT am.residentId " +
            "FROM apartment_members am " +
            "WHERE am.apartmentId IN (" +
            " SELECT a.id FROM apartments a WHERE a.accountId = :accountId" +
            ")")
    List<Integer> getResidentIdsByAccountId(int accountId);
    @Update
    void update(ApartmentMember member);
}
