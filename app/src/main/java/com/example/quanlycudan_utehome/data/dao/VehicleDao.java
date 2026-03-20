package com.example.quanlycudan_utehome.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quanlycudan_utehome.data.entity.Vehicle;
import com.example.quanlycudan_utehome.feature.vehicle.VehicleWithOwner;

import java.util.List;

@Dao
public interface VehicleDao {

    // Insert
    @Insert
    long insertVehicle(Vehicle vehicle);

    // Update
    @Update
    void updateVehicle(Vehicle vehicle);

    // Delete
    @Delete
    void deleteVehicle(Vehicle vehicle);

    // Get all vehicles
    @Query("SELECT * FROM vehicles")
    LiveData<List<Vehicle>> getAllVehicles();

    // Get vehicle by id
    @Query("SELECT * FROM vehicles WHERE id = :id")
    LiveData<Vehicle> getVehicleById(int id);

    // Get vehicles by apartment
    @Query("SELECT * FROM vehicles WHERE apartmentId = :apartmentId")
    LiveData<List<Vehicle>> getVehiclesByApartment(int apartmentId);

    // Get vehicles by resident
    @Query("SELECT * FROM vehicles WHERE residentId = :residentId")
    LiveData<List<Vehicle>> getVehiclesByResident(int residentId);

    // Search by license plate
    @Query("SELECT * FROM vehicles WHERE licensePlate LIKE '%' || :plate || '%'")
    LiveData<List<Vehicle>> searchByLicensePlate(String plate);


    @Query("SELECT * FROM vehicles WHERE residentId IN (:residentIds)")
    LiveData<List<Vehicle>> getVehiclesByResidentIds(List<Integer> residentIds);


    @Query("SELECT v.*, r.fullName AS ownerName " +
            "FROM vehicles v " +
            "INNER JOIN residents r ON v.residentId = r.id")
    LiveData<List<VehicleWithOwner>> getVehiclesWithOwner();

    @Query("SELECT v.*, r.fullName AS ownerName " +
            "FROM vehicles v " +
            "INNER JOIN residents r ON v.residentId = r.id " +
            "WHERE v.residentId IN (" +
            "   SELECT am.residentId FROM apartment_members am " +
            "   WHERE am.apartmentId IN (" +
            "       SELECT a.id FROM apartments a WHERE a.accountId = :accountId" +
            "   )" +
            ")")
    LiveData<List<VehicleWithOwner>> getVehiclesWithOwnerByAccountId(int accountId);
    // Delete by id
    @Query("DELETE FROM vehicles WHERE id = :id")
    void deleteById(int id);
}
