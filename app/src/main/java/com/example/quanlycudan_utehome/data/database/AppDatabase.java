package com.example.quanlycudan_utehome.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.quanlycudan_utehome.data.dao.ApartmentDao;
import com.example.quanlycudan_utehome.data.dao.ApartmentMemberDao;
import com.example.quanlycudan_utehome.data.dao.PaymentDao;
import com.example.quanlycudan_utehome.data.dao.ResidentDao;
import com.example.quanlycudan_utehome.data.dao.AccountDao;
import com.example.quanlycudan_utehome.data.dao.VehicleDao;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.Account;
import com.example.quanlycudan_utehome.data.entity.Invoice;
import com.example.quanlycudan_utehome.data.entity.InvoiceItem;
import com.example.quanlycudan_utehome.data.entity.Resident;
import com.example.quanlycudan_utehome.data.entity.TransactionHistory;
import com.example.quanlycudan_utehome.data.entity.Vehicle;

@Database(entities = { Apartment.class,
        Resident.class,
        Account.class,
        ApartmentMember.class,
        Invoice.class,
        InvoiceItem.class,
        TransactionHistory.class,
        Vehicle.class
}, version = 8, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ApartmentDao apartmentDao();
    public abstract ResidentDao residentDao();
    public abstract AccountDao accountDao();
    public abstract ApartmentMemberDao apartmentMemberDao();
    public abstract PaymentDao paymentDao();
    public abstract VehicleDao vehicleDao();
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