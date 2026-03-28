package com.example.quanlycudan_utehome.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlycudan_utehome.data.entity.FacilityBooking;

import java.util.List;

@Dao
public interface FacilityBookingDao {

    @Insert
    long insertBooking(FacilityBooking booking);

    @Update
    void updateBooking(FacilityBooking booking);

    @Query("SELECT * FROM facility_bookings WHERE facilityId = :facilityId AND bookingDate = :date")
    List<FacilityBooking> getBookingsByFacilityAndDate(int facilityId, String date);

    @Query("SELECT * FROM facility_bookings WHERE residentId = :residentId ORDER BY id DESC")
    List<FacilityBooking> getBookingsByResident(int residentId);

    @Query("UPDATE facility_bookings SET status = :status, cancelReason = :reason WHERE id = :bookingId")
    void updateBookingStatus(int bookingId, String status, String reason);

    @Query("DELETE FROM facility_bookings WHERE residentId = :residentId")
    void deleteByResidentId(int residentId);
}
