package com.example.quanlycudan_utehome.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.quanlycudan_utehome.data.dao.ApartmentDao;
import com.example.quanlycudan_utehome.data.dao.ApartmentMemberDao;
import com.example.quanlycudan_utehome.data.dao.ResidentDao;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.Resident;

@Database(
        entities = {Apartment.class, Resident.class, ApartmentMember.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ApartmentDao apartmentDao();
    public abstract ResidentDao residentDao();
    public abstract ApartmentMemberDao apartmentMemberDao();
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context){

        if(instance == null){
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "utehome_db"
                    ).fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
}