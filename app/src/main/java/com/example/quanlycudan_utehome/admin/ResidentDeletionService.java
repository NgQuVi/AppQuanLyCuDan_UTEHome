package com.example.quanlycudan_utehome.admin;

import android.content.Context;

import com.example.quanlycudan_utehome.data.dao.ApartmentMemberDao;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Account;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ResidentDeletionService {

    private final AppDatabase db;

    public ResidentDeletionService(Context context) {
        db = AppDatabase.getInstance(context.getApplicationContext());
    }

    public void deleteResident(int residentId) {
        Resident resident = db.residentDao().getResidentById(residentId);
        if (resident == null) {
            throw new IllegalStateException("Khong tim thay cu dan");
        }

        Account account = db.accountDao().getAccountByResidentId(residentId);
        Apartment ownerApartment = account == null ? null : db.apartmentDao().getApartmentByAccountId(account.id);

        Set<Integer> apartmentIds = new LinkedHashSet<>();
        List<Integer> memberApartmentIds = db.apartmentMemberDao().getApartmentIdsByResidentId(residentId);
        if (memberApartmentIds != null) {
            apartmentIds.addAll(memberApartmentIds);
        }
        if (ownerApartment != null) {
            apartmentIds.add(ownerApartment.id);
        }

        validateDeleteRule(residentId, account, ownerApartment, apartmentIds);

        db.runInTransaction(() -> {
            db.vehicleDao().deleteByResidentId(residentId);
            db.facilityBookingDao().deleteByResidentId(residentId);
            db.apartmentMemberDao().deleteByResidentId(residentId);
            db.residentDao().deleteById(residentId);

            for (int apartmentId : apartmentIds) {
                Apartment apartment = db.apartmentDao().getApartmentById(apartmentId);
                if (apartment == null) {
                    continue;
                }

                List<ApartmentMember> remainingMembers = db.apartmentMemberDao().getMembers(apartmentId);
                if (account != null && apartment.accountId == account.id) {
                    apartment.accountId = 0;
                }
                if (remainingMembers == null || remainingMembers.isEmpty()) {
                    apartment.status = "Trống";
                }
                db.apartmentDao().update(apartment);
            }

            if (account != null) {
                db.accountDao().deleteById(account.id);
            }
        });
    }

    private void validateDeleteRule(int residentId, Account account, Apartment ownerApartment, Set<Integer> apartmentIds) {
        if (account == null || ownerApartment == null) {
            return;
        }

        ApartmentMemberDao apartmentMemberDao = db.apartmentMemberDao();
        for (int apartmentId : apartmentIds) {
            if (apartmentId != ownerApartment.id) {
                continue;
            }

            List<ApartmentMember> members = apartmentMemberDao.getMembers(apartmentId);
            int otherMembers = 0;
            if (members != null) {
                for (ApartmentMember member : members) {
                    if (member.residentId != residentId) {
                        otherMembers++;
                    }
                }
            }

            if (otherMembers > 0) {
                throw new IllegalStateException(
                        "Can ho nay con thanh vien khac. Hay chuyen chu ho hoac xoa cac thanh vien khac truoc."
                );
            }
        }
    }
}
