package com.example.quanlycudan_utehome.data.repository;

import android.content.Context;

import com.example.quanlycudan_utehome.data.dao.ApartmentDao;
import com.example.quanlycudan_utehome.data.dao.ApartmentMemberDao;
import com.example.quanlycudan_utehome.data.dao.ResidentDao;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.ApartmentWithMembers;
import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.ArrayList;
import java.util.List;

public class ApartmentRepository {

    private ApartmentDao apartmentDao;
    private ApartmentMemberDao apartmentMemberDao;
    private ResidentDao residentDao;

    public ApartmentRepository(Context context){

        AppDatabase db = AppDatabase.getInstance(context);
        apartmentDao = db.apartmentDao();
        apartmentMemberDao = db.apartmentMemberDao();
        residentDao = db.residentDao();
    }

    public void insertApartment(Apartment apartment){

        new Thread(() -> apartmentDao.insert(apartment)).start();
    }

    public Apartment getApartment(int id){

        return apartmentDao.getApartmentById(id);
    }

    public ApartmentWithMembers getApartmentWithMembers(int id) {
        Apartment apartment = apartmentDao.getApartmentById(id);
        List<ApartmentMember> members = apartmentMemberDao.getMembers(id);

        List<ApartmentWithMembers.ApartmentMemberDetail> memberDetails = new ArrayList<>();
        for (ApartmentMember member : members) {
            Resident resident = residentDao.getResidentById(member.residentId);
            memberDetails.add(new ApartmentWithMembers.ApartmentMemberDetail(member, resident));
        }

        ApartmentWithMembers result = new ApartmentWithMembers();
        result.apartment = apartment;
        result.members = memberDetails;
        return result;
    }

    public void insertApartmentMember(ApartmentMember member) {
        new Thread(() -> apartmentMemberDao.insert(member)).start();
    }

    public void insertApartmentMemberSync(ApartmentMember member) {
        apartmentMemberDao.insert(member);
    }
}
