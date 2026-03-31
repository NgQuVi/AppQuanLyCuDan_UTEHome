package com.example.quanlycudan_utehome.data.entity;

import androidx.room.ColumnInfo;

public class FacilityBookingRow {
    @ColumnInfo(name = "bookingId")
    public int bookingId;

    @ColumnInfo(name = "facilityId")
    public int facilityId;

    @ColumnInfo(name = "facilityName")
    public String facilityName;

    @ColumnInfo(name = "residentId")
    public int residentId;

    @ColumnInfo(name = "residentName")
    public String residentName;

    @ColumnInfo(name = "bookingDate")
    public String bookingDate;

    @ColumnInfo(name = "DayBooking")
    public String DayBooking;

    @ColumnInfo(name = "startTime")
    public String startTime;

    @ColumnInfo(name = "endTime")
    public String endTime;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "cancelReason")
    public String cancelReason;
}
