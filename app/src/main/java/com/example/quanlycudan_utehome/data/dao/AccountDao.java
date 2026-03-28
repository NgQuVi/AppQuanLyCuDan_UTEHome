package com.example.quanlycudan_utehome.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlycudan_utehome.data.entity.Account;

@Dao
public interface AccountDao {

    @Insert
    long insert(Account account);

    @Update
    void update(Account account);

    @Query("SELECT * FROM accounts WHERE phone = :phone LIMIT 1")
    Account getAccountByPhone(String phone);

    @Query("SELECT accounts.* FROM accounts INNER JOIN residents ON accounts.id = residents.accountId WHERE residents.id = :residentId LIMIT 1")
    Account getAccountByResidentId(int residentId);

    @Query("UPDATE accounts SET password = :newPassword, mustChangePassword = 0, isActive = 1 WHERE phone = :phone")
    void updatePasswordAndClearFirstLogin(String phone, String newPassword);

    @Query("UPDATE accounts SET isActive = :isActive WHERE id = :accountId")
    void updateAccountActive(int accountId, boolean isActive);

    @Query("DELETE FROM accounts WHERE id = :accountId")
    void deleteById(int accountId);

    @Query("SELECT COUNT(*) FROM accounts WHERE phone = :phone")
    int checkPhoneExists(String phone);
}
