package com.example.quanlycudan_utehome.data.entity;

import java.util.List;

public class ApartmentWithMembers {
    public Apartment apartment;
    public List<ApartmentMemberDetail> members;

    public static class ApartmentMemberDetail {
        public ApartmentMember apartmentMember;
        public Resident resident;

        public ApartmentMemberDetail(ApartmentMember apartmentMember, Resident resident) {
            this.apartmentMember = apartmentMember;
            this.resident = resident;
        }
    }
}
