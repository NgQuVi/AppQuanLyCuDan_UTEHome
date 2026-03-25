package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "facility_bookings")
public class FacilityBooking {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int facilityId;
    public int residentId;
    public String bookingDate;
    public String DayBooking;
    public String startTime;
    public String endTime;
    public String status;
    public String cancelReason;
}
