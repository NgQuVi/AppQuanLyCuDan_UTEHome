package com.example.quanlycudan_utehome.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlycudan_utehome.data.entity.Account;

@Dao
public interface AccountDao {

    @Insert
    void insert(Account account);

    @Update
    void update(Account account);

    @Query("SELECT * FROM accounts WHERE phone = :phone LIMIT 1")
    Account getAccountByPhone(String phone);

    @Query("SELECT * FROM accounts WHERE residentId = :residentId LIMIT 1")
    Account getAccountByResidentId(int residentId);

    @Query("UPDATE accounts SET password = :newPassword WHERE phone = :phone")
    void updatePassword(String phone, String newPassword);

    @Query("SELECT COUNT(*) FROM accounts WHERE phone = :phone")
    int checkPhoneExists(String phone);
}
