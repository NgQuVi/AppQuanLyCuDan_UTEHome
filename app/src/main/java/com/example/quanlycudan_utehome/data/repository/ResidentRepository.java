package com.example.quanlycudan_utehome.data.repository;

import android.content.Context;

import com.example.quanlycudan_utehome.data.dao.ResidentDao;
import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.Collections;
import java.util.List;

public class ResidentRepository {
    private ResidentDao residentDao;
    public ResidentRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        residentDao = db.residentDao();
    }
    public void insertResident(Resident resident) {
        new Thread(() -> residentDao.insert(resident)).start();
    }

    public long insertResidentSync(Resident resident) {
        return residentDao.insert(resident);
    }
}
