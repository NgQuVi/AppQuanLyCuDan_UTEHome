package com.example.quanlycudan_utehome.data.database;

import android.content.Context;

import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.Resident;

public class DatabaseInitializer {

    public static void initializeSampleData(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);

        // Chèn dữ liệu vào bảng Resident (5 cư dân)
        insertSampleResidents(db);

        // Chèn dữ liệu vào bảng Apartment (5 căn hộ)
        insertSampleApartments(db);

        // Chèn dữ liệu vào bảng ApartmentMember (liên kết cư dân với căn hộ)
        insertSampleApartmentMembers(db);
    }

    private static void insertSampleResidents(AppDatabase db) {
        new Thread(() -> {
            // Kiểm tra xem đã có dữ liệu chưa
            if (db.residentDao().getAllResidents().isEmpty()) {
                Resident resident1 = new Resident();
                resident1.fullName = "Nguyễn Anh Tuấn";
                resident1.avatarUrl = "https://via.placeholder.com/150?text=Tuan";


                Resident resident2 = new Resident();
                resident2.fullName = "Trần Thị Hương";
                resident2.avatarUrl = "https://via.placeholder.com/150?text=Huong";

                Resident resident3 = new Resident();
                resident3.fullName = "Nguyễn Anh Đức";
                resident3.avatarUrl = "https://via.placeholder.com/150?text=Duc";

                Resident resident4 = new Resident();
                resident4.fullName = "Phạm Thị Tâm";
                resident4.avatarUrl = "https://via.placeholder.com/150?text=Tam";

                Resident resident5 = new Resident();
                resident5.fullName = "Lê Văn Hùng";
                resident5.avatarUrl = "https://via.placeholder.com/150?text=Hung";

                db.residentDao().insert(resident1);
                db.residentDao().insert(resident2);
                db.residentDao().insert(resident3);
                db.residentDao().insert(resident4);
                db.residentDao().insert(resident5);
            }
        }).start();
    }

    private static void insertSampleApartments(AppDatabase db) {
        new Thread(() -> {
            // Kiểm tra xem đã có dữ liệu chưa
            if (db.apartmentDao().getAllApartments().isEmpty()) {
                Apartment apt1 = new Apartment();
                apt1.apartmentCode = "P.1205";
                apt1.buildingCode = "S1";
                apt1.floor = 12;
                apt1.area = 105.5f;
                apt1.status = "Đang sử dụng";

                Apartment apt2 = new Apartment();
                apt2.apartmentCode = "P.1206";
                apt2.buildingCode = "S1";
                apt2.floor = 12;
                apt2.area = 87.3f;
                apt2.status = "Đang sử dụng";

                Apartment apt3 = new Apartment();
                apt3.apartmentCode = "P.1207";
                apt3.buildingCode = "S1";
                apt3.floor = 12;
                apt3.area = 92.0f;
                apt3.status = "Đang sử dụng";

                Apartment apt4 = new Apartment();
                apt4.apartmentCode = "P.0805";
                apt4.buildingCode = "S2";
                apt4.floor = 8;
                apt4.area = 115.0f;
                apt4.status = "Đang sử dụng";

                Apartment apt5 = new Apartment();
                apt5.apartmentCode = "P.0806";
                apt5.buildingCode = "S2";
                apt5.floor = 8;
                apt5.area = 95.5f;
                apt5.status = "Đang sử dụng";

                db.apartmentDao().insert(apt1);
                db.apartmentDao().insert(apt2);
                db.apartmentDao().insert(apt3);
                db.apartmentDao().insert(apt4);
                db.apartmentDao().insert(apt5);
            }
        }).start();
    }

    private static void insertSampleApartmentMembers(AppDatabase db) {
        new Thread(() -> {
            // Kiểm tra xem đã có dữ liệu chưa
            if (db.apartmentMemberDao().getAllMembers().isEmpty()) {
                // Căn hộ 1 (P.1205) - có 3 thành viên
                ApartmentMember member1 = new ApartmentMember();
                member1.apartmentId = 1;
                member1.residentId = 1;
                member1.role = "Chủ hộ";
                member1.residentType = "Chính";

                ApartmentMember member2 = new ApartmentMember();
                member2.apartmentId = 1;
                member2.residentId = 2;
                member2.role = "Vợ";
                member2.residentType = "Chính";

                ApartmentMember member3 = new ApartmentMember();
                member3.apartmentId = 1;
                member3.residentId = 3;
                member3.role = "Con";
                member3.residentType = "Chính";

                // Căn hộ 2 (P.1206) - có 2 thành viên
                ApartmentMember member4 = new ApartmentMember();
                member4.apartmentId = 2;
                member4.residentId = 4;
                member4.role = "Chủ hộ";
                member4.residentType = "Chính";

                ApartmentMember member5 = new ApartmentMember();
                member5.apartmentId = 2;
                member5.residentId = 5;
                member5.role = "Gia đình";
                member5.residentType = "Chính";

                db.apartmentMemberDao().insert(member1);
                db.apartmentMemberDao().insert(member2);
                db.apartmentMemberDao().insert(member3);
                db.apartmentMemberDao().insert(member4);
                db.apartmentMemberDao().insert(member5);
            }
        }).start();
    }
}
