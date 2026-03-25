package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ResidentWithApartment {
    @Embedded
    public Resident resident;

    @Relation(
            parentColumn = "accountId",
            entityColumn = "accountId"
    )
    public Apartment apartment;
}
