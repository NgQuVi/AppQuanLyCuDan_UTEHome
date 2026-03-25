package com.example.quanlycudan_utehome.data.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ApartmentWithOwner {
    @Embedded
    public Apartment apartment;

    @Relation(
            parentColumn = "accountId",
            entityColumn = "accountId"
    )
    public List<Resident> owners;

    // Convenience getter for the first owner's name
    public String getOwnerName() {
        if (owners != null && !owners.isEmpty()) {
            return owners.get(0).fullName;
        }
        return null;
    }
}
