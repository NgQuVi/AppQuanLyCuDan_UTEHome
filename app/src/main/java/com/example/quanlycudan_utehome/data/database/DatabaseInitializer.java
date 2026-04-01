package com.example.quanlycudan_utehome.data.database;

import android.content.Context;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.Resident;
import com.example.quanlycudan_utehome.data.entity.Account;
import com.example.quanlycudan_utehome.data.entity.AppNotification;
import com.example.quanlycudan_utehome.data.entity.Facility;
import com.example.quanlycudan_utehome.data.entity.FacilityBooking;
import com.example.quanlycudan_utehome.data.entity.GuestPass;
import com.example.quanlycudan_utehome.data.entity.Invoice;
import com.example.quanlycudan_utehome.data.entity.InvoiceItem;
import com.example.quanlycudan_utehome.data.entity.TransactionHistory;
import com.example.quanlycudan_utehome.data.entity.Vehicle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DatabaseInitializer {

    private static final SimpleDateFormat DATE_ONLY = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public static void initializeSampleData(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);

        insertCoreSampleData(db);
        // Chờ một chút để core data insert xong (do chạy thread riêng)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        insertSampleVehicles(db);
        insertSampleInvoices(db);
        insertSampleGuestPasses(db);
        insertSampleNotifications(db);
        insertSampleFacilities(db);
        insertSampleFacilityBookings(db);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CORE: Residents + Accounts + Apartments + Members
    // Chung cư UTEHome – Quận 3, TP.HCM
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertCoreSampleData(AppDatabase db) {
        new Thread(() -> {
            if (!db.residentDao().getAllResidents().isEmpty()
                    || !db.apartmentDao().getAllApartmentsSync().isEmpty()
                    || !db.apartmentMemberDao().getAllMembers().isEmpty()) {
                return;
            }

            db.runInTransaction(() -> {
                // ─── Cư dân ─────────────────────────────────────────────
                // Căn hộ P.1205 (id=1)
                Resident r1 = new Resident();
                r1.fullName = "Nguyễn Văn Minh";
                r1.phone = "0901234567";
                r1.email = "minh.nguyen@gmail.com";
                r1.dob = "15/03/1980";
                r1.gender = "Nam";
                r1.idNum = "079180012345";
                r1.avatarUrl = "";

                Resident r2 = new Resident();
                r2.fullName = "Trần Thị Lan";
                r2.phone = "0901234568";
                r2.email = "lan.tran@gmail.com";
                r2.dob = "22/07/1983";
                r2.gender = "Nữ";
                r2.idNum = "079183045678";
                r2.avatarUrl = "";

                Resident r3 = new Resident();
                r3.fullName = "Nguyễn Minh Khoa";
                r3.phone = "0901234569";
                r3.email = "khoa.nguyen@gmail.com";
                r3.dob = "10/09/2008";
                r3.gender = "Nam";
                r3.idNum = "";
                r3.avatarUrl = "";

                // Căn hộ P.0802 (id=2)
                Resident r4 = new Resident();
                r4.fullName = "Phạm Thị Hoa";
                r4.phone = "0912345678";
                r4.email = "hoa.pham@gmail.com";
                r4.dob = "05/11/1975";
                r4.gender = "Nữ";
                r4.idNum = "079175067890";
                r4.avatarUrl = "";

                Resident r5 = new Resident();
                r5.fullName = "Lê Quang Hùng";
                r5.phone = "0912345679";
                r5.email = "hung.le@gmail.com";
                r5.dob = "18/06/1973";
                r5.gender = "Nam";
                r5.idNum = "079173023456";
                r5.avatarUrl = "";

                // Căn hộ P.0305 (id=3)
                Resident r6 = new Resident();
                r6.fullName = "Vũ Đức Thành";
                r6.phone = "0987654321";
                r6.email = "thanh.vu@gmail.com";
                r6.dob = "30/01/1988";
                r6.gender = "Nam";
                r6.idNum = "079188034567";
                r6.avatarUrl = "";

                Resident r7 = new Resident();
                r7.fullName = "Ngô Thị Bích Ngọc";
                r7.phone = "0987654322";
                r7.email = "ngoc.ngo@gmail.com";
                r7.dob = "14/04/1990";
                r7.gender = "Nữ";
                r7.idNum = "079190056789";
                r7.avatarUrl = "";

                // Căn hộ P.0506 (id=4)
                Resident r8 = new Resident();
                r8.fullName = "Đinh Công Toàn";
                r8.phone = "0933445566";
                r8.email = "toan.dinh@gmail.com";
                r8.dob = "25/12/1985";
                r8.gender = "Nam";
                r8.idNum = "079185078901";
                r8.avatarUrl = "";

                // Căn hộ P.0712 (id=5)
                Resident r9 = new Resident();
                r9.fullName = "Lý Hải Yến";
                r9.phone = "0909001122";
                r9.email = "yen.ly@gmail.com";
                r9.dob = "12/05/1992";
                r9.gender = "Nữ";
                r9.idNum = "079192011223";
                r9.avatarUrl = "";

                // Căn hộ P.1105 (id=6)
                Resident r10 = new Resident();
                r10.fullName = "Hoàng Tuấn Anh";
                r10.phone = "0988776655";
                r10.email = "anh.hoang@gmail.com";
                r10.dob = "03/08/1982";
                r10.gender = "Nam";
                r10.idNum = "079182044556";
                r10.avatarUrl = "";

                Resident r11 = new Resident();
                r11.fullName = "Trương Mỹ Linh";
                r11.phone = "0988776656";
                r11.email = "linh.truong@gmail.com";
                r11.dob = "19/11/1985";
                r11.gender = "Nữ";
                r11.idNum = "079185077889";
                r11.avatarUrl = "";

                // Căn hộ P.0201 (id=7)
                Resident r12 = new Resident();
                r12.fullName = "Phan Đình Phùng";
                r12.phone = "0911223344";
                r12.email = "phung.phan@gmail.com";
                r12.dob = "28/02/1970";
                r12.gender = "Nam";
                r12.idNum = "079170099001";
                r12.avatarUrl = "";

                // Căn hộ P.0404 (id=8)
                Resident r13 = new Resident();
                r13.fullName = "Bùi Thị Mai";
                r13.phone = "0966554433";
                r13.email = "mai.bui@gmail.com";
                r13.dob = "09/09/1995";
                r13.gender = "Nữ";
                r13.idNum = "079195022334";
                r13.avatarUrl = "";

                // Thực hiện insert cư dân
                List<Resident> residents = Arrays.asList(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
                for (Resident r : residents) {
                    r.id = (int) db.residentDao().insert(r);
                }

                // ─── Tài khoản đăng nhập ────────────────────────────────
                List<Account> accounts = new ArrayList<>();
                accounts.add(new Account("0901234567", "Minh@123", "Resident")); // 1: P.1205
                accounts.add(new Account("0912345678", "Hoa@2024", "Resident")); // 2: P.0802
                accounts.add(new Account("0987654321", "Thanh@2024", "Resident")); // 3: P.0305
                accounts.add(new Account("0933445566", "Toan@2025", "Resident")); // 4: P.0506
                accounts.add(new Account("0909001122", "Yen@2026", "Resident")); // 5: P.0712
                accounts.add(new Account("0988776655", "Anh@1234", "Resident")); // 6: P.1105
                accounts.add(new Account("0911223344", "Phung@1970", "Resident")); // 7: P.0201
                accounts.add(new Account("0966554433", "Mai@1995", "Resident")); // 8: P.0404

                for (int i = 0; i < accounts.size(); i++) {
                    accounts.get(i).id = (int) db.accountDao().insert(accounts.get(i));
                }

                // Map account id cho chủ hộ
                r1.accountId = accounts.get(0).id;
                db.residentDao().update(r1);
                r4.accountId = accounts.get(1).id;
                db.residentDao().update(r4);
                r6.accountId = accounts.get(2).id;
                db.residentDao().update(r6);
                r8.accountId = accounts.get(3).id;
                db.residentDao().update(r8);
                r9.accountId = accounts.get(4).id;
                db.residentDao().update(r9);
                r10.accountId = accounts.get(5).id;
                db.residentDao().update(r10);
                r12.accountId = accounts.get(6).id;
                db.residentDao().update(r12);
                r13.accountId = accounts.get(7).id;
                db.residentDao().update(r13);

                // ─── Căn hộ ─────────────────────────────────────────────
                List<Apartment> apts = new ArrayList<>();
                apts.add(createApt(accounts.get(0).id, "P.1205", "S1", 12, 105.5f, "Dang su dung")); // id=1
                apts.add(createApt(accounts.get(1).id, "P.0802", "S1", 8, 87.3f, "Dang su dung")); // id=2
                apts.add(createApt(accounts.get(2).id, "P.0305", "S2", 3, 92.0f, "Dang su dung")); // id=3
                apts.add(createApt(accounts.get(3).id, "P.0506", "S2", 5, 75.0f, "Dang su dung")); // id=4
                apts.add(createApt(accounts.get(4).id, "P.0712", "S1", 7, 70.0f, "Dang su dung")); // id=5
                apts.add(createApt(accounts.get(5).id, "P.1105", "S2", 11, 110.5f, "Dang su dung")); // id=6
                apts.add(createApt(accounts.get(6).id, "P.0201", "S1", 2, 65.0f, "Dang su dung")); // id=7
                apts.add(createApt(accounts.get(7).id, "P.0404", "S2", 4, 80.0f, "Dang su dung")); // id=8
                apts.add(createApt(0, "P.1410", "S1", 14, 120.0f, "Trong")); // id=9
                apts.add(createApt(0, "P.0901", "S2", 9, 65.5f, "Trong")); // id=10
                apts.add(createApt(0, "P.1502", "S1", 15, 95.0f, "Trong")); // id=11
                apts.add(createApt(0, "P.0608", "S2", 6, 88.0f, "Trong")); // id=12

                for (Apartment apt : apts) {
                    apt.id = (int) db.apartmentDao().insertApartment(apt);
                }

                // ─── Thành viên hộ gia đình ──────────────────────────────
                List<ApartmentMember> members = new ArrayList<>();
                members.add(createMember(apts.get(0).id, r1.id, "Chủ hộ", "Chính"));
                members.add(createMember(apts.get(0).id, r2.id, "Vợ", "Gia đình"));
                members.add(createMember(apts.get(0).id, r3.id, "Con", "Gia đình"));

                members.add(createMember(apts.get(1).id, r4.id, "Chủ hộ", "Chính"));
                members.add(createMember(apts.get(1).id, r5.id, "Chồng", "Gia đình"));

                members.add(createMember(apts.get(2).id, r6.id, "Chủ hộ", "Chính"));
                members.add(createMember(apts.get(2).id, r7.id, "Vợ", "Gia đình"));

                members.add(createMember(apts.get(3).id, r8.id, "Chủ hộ", "Chính"));

                members.add(createMember(apts.get(4).id, r9.id, "Chủ hộ", "Chính"));

                members.add(createMember(apts.get(5).id, r10.id, "Chủ hộ", "Chính"));
                members.add(createMember(apts.get(5).id, r11.id, "Vợ", "Gia đình"));

                members.add(createMember(apts.get(6).id, r12.id, "Chủ hộ", "Chính"));

                members.add(createMember(apts.get(7).id, r13.id, "Chủ hộ", "Chính"));

                for (ApartmentMember m : members) {
                    db.apartmentMemberDao().insert(m);
                }
            });
        }).start();
    }

    private static Apartment createApt(int accId, String aptCode, String bCode, int floor, float area, String status) {
        Apartment a = new Apartment();
        a.accountId = accId;
        a.apartmentCode = aptCode;
        a.buildingCode = bCode;
        a.floor = floor;
        a.area = area;
        a.status = status;
        return a;
    }

    private static ApartmentMember createMember(int aptId, int resId, String role, String type) {
        ApartmentMember m = new ApartmentMember();
        m.apartmentId = aptId;
        m.residentId = resId;
        m.role = role;
        m.residentType = type;
        return m;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // VEHICLES
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleVehicles(AppDatabase db) {
        new Thread(() -> {
            if (!db.vehicleDao().getVehiclesByResidentIdsSync(Arrays.asList(1, 2, 3)).isEmpty())
                return;

            List<Vehicle> vh = new ArrayList<>();
            vh.add(createVehicle(1, 1, "Ô tô", "Toyota Camry", "Đen bóng", "51H-168.88", "Đã duyệt"));
            vh.add(createVehicle(1, 2, "Xe máy", "Honda SH 150i", "Trắng ngọc trai", "59X1-345.67", "Đã duyệt"));
            vh.add(createVehicle(1, 3, "Xe máy", "Yamaha NVX 155", "Xanh navy", "59P3-901.23", "Chưa duyệt"));

            vh.add(createVehicle(2, 4, "Xe máy", "Honda Vision 110", "Đỏ đô", "59M2-456.78", "Đã duyệt"));
            vh.add(createVehicle(2, 5, "Ô tô", "Kia K5", "Trắng", "50A-567.89", "Đã duyệt"));
            vh.add(createVehicle(2, 5, "Xe máy", "Yamaha Sirius", "Đen", "59K1-112.22", "Đã duyệt"));

            vh.add(createVehicle(3, 6, "Ô tô", "Mazda CX-8", "Xám titan", "51K-789.01", "Đã duyệt"));
            vh.add(createVehicle(3, 7, "Xe máy", "Piaggio Liberty S", "Trắng sữa", "59E1-234.56", "Đã duyệt"));

            vh.add(createVehicle(4, 8, "Xe máy", "Honda Air Blade 125", "Đen mờ", "59K5-678.90", "Đã duyệt"));
            vh.add(createVehicle(4, 8, "Ô tô", "Hyundai Tucson", "Bạc", "50H-321.09", "Chưa duyệt"));

            vh.add(createVehicle(5, 9, "Xe máy", "Vespa Sprint", "Vàng", "59V2-555.55", "Đã duyệt"));

            vh.add(createVehicle(6, 10, "Ô tô", "Ford Everest", "Đen", "51F-888.99", "Đã duyệt"));
            vh.add(createVehicle(6, 11, "Xe máy", "Honda Lead", "Cam", "59L1-334.44", "Đã duyệt"));

            vh.add(createVehicle(7, 12, "Xe máy", "Honda Wave Alpha", "Xanh dương", "59W1-121.21", "Đã duyệt"));

            vh.add(createVehicle(8, 13, "Xe đạp điện", "VinFast Ludo", "Đỏ", "59MĐ-123.45", "Đã duyệt"));

            for (Vehicle v : vh)
                db.vehicleDao().insertVehicle(v);
        }).start();
    }

    private static Vehicle createVehicle(int aptId, int resId, String type, String brand, String color, String plate,
            String status) {
        Vehicle v = new Vehicle();
        v.apartmentId = aptId;
        v.residentId = resId;
        v.vehicleType = type;
        v.brand = brand;
        v.color = color;
        v.licensePlate = plate;
        v.status = status;
        return v;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INVOICES + INVOICE_ITEMS + TRANSACTIONS
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleInvoices(AppDatabase db) {
        new Thread(() -> {
            if (!db.paymentDao().getAllInvoices().isEmpty())
                return;

            // ── CĂN HỘ 1 (P.1205) ────────────────────────────────────────────────
            insertInvoiceFull(db, "INV-032026-1205", "1", "03/2026", 2370700, "15/04/2026", "UNPAID",
                    1430, 1580, 142, 157, 1, 1000000, "01 Ô tô, 02 Xe máy", "Gói Tốc độ cao – 150 Mbps", 450000, null,
                    null);
            insertInvoiceFull(db, "INV-022026-1205", "1", "02/2026", 2195000, "15/03/2026", "PAID",
                    1290, 1430, 128, 142, 1, 1000000, "01 Ô tô, 02 Xe máy", "Gói Tốc độ cao – 150 Mbps", 450000,
                    "#TXN0226-88A1", "10/03/2026 - 09:42");
            insertInvoiceFull(db, "INV-012026-1205", "1", "01/2026", 2087500, "15/02/2026", "PAID",
                    1165, 1290, 115, 128, 1, 1000000, "01 Ô tô, 02 Xe máy", "Gói Tốc độ cao – 150 Mbps", 450000,
                    "#TXN0126-77B2", "12/02/2026 - 14:20");
            insertInvoiceFull(db, "INV-122025-1205", "1", "12/2025", 2320000, "15/01/2026", "PAID",
                    1010, 1165, 100, 115, 1, 1000000, "01 Ô tô, 02 Xe máy", "Gói Tốc độ cao – 150 Mbps", 450000,
                    "#TXN1225-66C3", "08/01/2026 - 10:05");
            insertInvoiceFull(db, "INV-112025-1205", "1", "11/2025", 2150000, "15/12/2025", "PAID",
                    880, 1010, 90, 100, 1, 1000000, "01 Ô tô, 02 Xe máy", "Gói Tốc độ cao – 150 Mbps", 450000,
                    "#TXN1125-99D4", "10/12/2025 - 08:30");

            // ── CĂN HỘ 2 (P.0802) ────────────────────────────────────────────────
            insertInvoiceFull(db, "INV-032026-0802", "2", "03/2026", 1850000, "15/04/2026", "UNPAID",
                    500, 620, 40, 50, 1, 850000, "01 Ô tô, 02 Xe máy", "Gói Cơ bản – 80 Mbps", 250000, null, null);
            insertInvoiceFull(db, "INV-022026-0802", "2", "02/2026", 1790000, "15/03/2026", "PAID",
                    390, 500, 32, 40, 1, 850000, "01 Ô tô, 02 Xe máy", "Gói Cơ bản – 80 Mbps", 250000, "#TXN0226-11A2",
                    "12/03/2026 - 19:15");

            // ── CĂN HỘ 3 (P.0305) ────────────────────────────────────────────────
            insertInvoiceFull(db, "INV-032026-0305", "3", "03/2026", 2100000, "15/04/2026", "PAID",
                    700, 850, 60, 75, 1, 850000, "01 Ô tô, 01 Xe máy", "Gói Tiêu chuẩn – 100 Mbps", 350000,
                    "#TXN0326-55C5", "05/04/2026 - 11:20");

            // ── CĂN HỘ 4 (P.0506) ────────────────────────────────────────────────
            insertInvoiceFull(db, "INV-032026-0506", "4", "03/2026", 650000, "15/04/2026", "UNPAID",
                    200, 250, 15, 20, 1, 150000, "01 Xe máy", "Gói Sinh viên – 50 Mbps", 150000, null, null);

            // ── CĂN HỘ 5 (P.0712) ────────────────────────────────────────────────
            insertInvoiceFull(db, "INV-032026-0712", "5", "03/2026", 980000, "15/04/2026", "UNPAID",
                    300, 410, 25, 33, 1, 150000, "01 Xe máy", "Gói Cơ bản – 80 Mbps", 250000, null, null);
            insertInvoiceFull(db, "INV-022026-0712", "5", "02/2026", 920000, "15/03/2026", "PAID",
                    210, 300, 18, 25, 1, 150000, "01 Xe máy", "Gói Cơ bản – 80 Mbps", 250000, "#TXN0226-44B4",
                    "14/03/2026 - 20:00");

            // ── CĂN HỘ 6 (P.1105) ────────────────────────────────────────────────
            insertInvoiceFull(db, "INV-032026-1105", "6", "03/2026", 2550000, "15/04/2026", "UNPAID",
                    800, 1000, 80, 100, 1, 850000, "01 Ô tô, 01 Xe máy", "Gói Doanh nghiệp – 300 Mbps", 600000, null,
                    null);

        }).start();
    }

    private static void insertInvoiceFull(AppDatabase db, String invId, String aptId, String month, long total,
            String due, String status,
            int elecOld, int elecNew, int waterOld, int waterNew,
            int parkQty, int parkAmount, String parkDesc,
            String netDesc, int netAmount, String txnId, String txnTime) {

        Invoice inv = new Invoice(invId, aptId, month, total, due, status);
        db.paymentDao().insertInvoice(inv);

        List<InvoiceItem> items = new ArrayList<>();

        InvoiceItem elec = new InvoiceItem();
        elec.invoiceId = invId;
        elec.serviceType = "ELECTRIC";
        elec.oldIndex = elecOld;
        elec.newIndex = elecNew;
        elec.unitPrice = 3858;
        elec.amount = (elecNew - elecOld) * elec.unitPrice;
        items.add(elec);

        InvoiceItem water = new InvoiceItem();
        water.invoiceId = invId;
        water.serviceType = "WATER";
        water.oldIndex = waterOld;
        water.newIndex = waterNew;
        water.unitPrice = 22800;
        water.amount = (waterNew - waterOld) * water.unitPrice;
        items.add(water);

        InvoiceItem parking = new InvoiceItem();
        parking.invoiceId = invId;
        parking.serviceType = "PARKING";
        parking.quantity = parkQty;
        parking.unitPrice = parkAmount;
        parking.amount = parkAmount;
        parking.description = parkDesc;
        items.add(parking);

        InvoiceItem internet = new InvoiceItem();
        internet.invoiceId = invId;
        internet.serviceType = "INTERNET";
        internet.description = netDesc;
        internet.amount = netAmount;
        items.add(internet);

        db.paymentDao().insertInvoiceItems(items);

        if ("PAID".equals(status) && txnId != null) {
            TransactionHistory tx = new TransactionHistory(txnId, invId, "VNPAY", txnTime, total, "SUCCESS");
            db.paymentDao().insertTransaction(tx);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GUEST PASSES
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleGuestPasses(AppDatabase db) {
        new Thread(() -> {
            if (!db.guestPassDao().getGuestPassesByApartmentIdSync(1).isEmpty())
                return;

            // Căn hộ 1 (P.1205)
            insertGuest(db, 1, "QR26040101", "01/04/2026", "01/04/2026 08:00", "03/04/2026 22:00", "ACTIVE");
            insertGuest(db, 1, "QR26032801", "28/03/2026", "28/03/2026 09:00", "05/04/2026 20:00", "ACTIVE");
            insertGuest(db, 1, "QR26031501", "15/03/2026", "15/03/2026 08:00", "20/03/2026 22:00", "EXPIRED");
            insertGuest(db, 1, "QR26030501", "05/03/2026", "05/03/2026 07:00", "08/03/2026 18:00", "EXPIRED");
            insertGuest(db, 1, "QR26021001", "10/02/2026", "10/02/2026 10:00", "12/02/2026 20:00", "CANCELLED");

            // Căn hộ 2 (P.0802)
            insertGuest(db, 2, "QR26040202", "02/04/2026", "02/04/2026 14:00", "02/04/2026 22:00", "ACTIVE");
            insertGuest(db, 2, "QR26032002", "20/03/2026", "20/03/2026 08:00", "21/03/2026 12:00", "EXPIRED");

            // Căn hộ 3 (P.0305)
            insertGuest(db, 3, "QR26033003", "30/03/2026", "30/03/2026 09:00", "01/04/2026 18:00", "ACTIVE");
            insertGuest(db, 3, "QR26031003", "10/03/2026", "10/03/2026 10:00", "10/03/2026 16:00", "EXPIRED");

            // Căn hộ 6 (P.1105)
            insertGuest(db, 6, "QR26040506", "05/04/2026", "05/04/2026 07:00", "10/04/2026 22:00", "ACTIVE");

        }).start();
    }

    private static void insertGuest(AppDatabase db, int aptId, String code,
            String created, String from, String to, String status) {
        GuestPass gp = new GuestPass();
        gp.apartmentId = aptId;
        gp.code = code;
        gp.createdAt = parseDateToMillis(created);
        gp.validFrom = parseDateTimeToMillis(from);
        gp.validTo = parseDateTimeToMillis(to);
        gp.status = status;
        db.guestPassDao().insertGuestPass(gp);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // NOTIFICATIONS
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleNotifications(AppDatabase db) {
        new Thread(() -> {
            if (!db.appNotificationDao().getAllNotifications().isEmpty())
                return;

            AppNotification n1 = new AppNotification();
            n1.type = "MAINTENANCE";
            n1.title = "Bảo trì định kỳ hệ thống thang máy tòa S1 & S2";
            n1.shortDescription = "Ban Quản lý thông báo lịch bảo trì thang máy ngày 05/04/2026. Trong thời gian bảo trì...";
            n1.dateStr = "01/04/2026";
            n1.timeStr = "08:00 SA";
            n1.isRead = false;
            n1.timestamp = System.currentTimeMillis() - 30 * 60 * 1000L;
            n1.affectedScope = "Toàn bộ cư dân tòa S1 và S2.";
            n1.fullContent = "Kính gửi Quý cư dân,\n\nBan Quản lý Chung cư UTEHome trân trọng thông báo kế hoạch bảo trì thang máy...\nThời gian: 05/04/2026.";
            n1.eventStepsJson = "[{\"title\":\"Đợt 1: Thang S1\",\"time\":\"07:00 – 12:00, 05/04/2026\"},{\"title\":\"Đợt 2: Thang S2\",\"time\":\"13:00 – 17:00, 05/04/2026\"}]";

            AppNotification n2 = new AppNotification();
            n2.type = "UTILITY";
            n2.title = "Hóa đơn dịch vụ tháng 03/2026 đã sẵn sàng";
            n2.shortDescription = "Hóa đơn dịch vụ kỳ tháng 03/2026 đã được phát hành. Hạn thanh toán: 15/04/2026.";
            n2.dateStr = "01/04/2026";
            n2.timeStr = "07:00 SA";
            n2.isRead = false;
            n2.timestamp = System.currentTimeMillis() - 2 * 60 * 60 * 1000L;

            AppNotification n3 = new AppNotification();
            n3.type = "MEETING";
            n3.title = "Họp cư dân tổng kết Q1/2026 – 06/04/2026";
            n3.shortDescription = "Kính mời Quý cư dân tham dự Hội nghị cư dân tổng kết Q1/2026 tại Trung tâm Sinh hoạt...";
            n3.dateStr = "31/03/2026";
            n3.timeStr = "16:00 CH";
            n3.isRead = false;
            n3.timestamp = System.currentTimeMillis() - 18 * 60 * 60 * 1000L;

            AppNotification n4 = new AppNotification();
            n4.type = "IMPORTANT";
            n4.title = "Thông báo tạm ngừng cấp nước – Sáng 02/04/2026";
            n4.shortDescription = "Công ty Cấp nước TP.HCM thực hiện bảo dưỡng đường ống chính. Tòa nhà sẽ tạm ngừng cấp nước...";
            n4.dateStr = "30/03/2026";
            n4.timeStr = "14:00 CH";
            n4.isRead = true;
            n4.timestamp = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L;

            AppNotification n5 = new AppNotification();
            n5.type = "UTILITY";
            n5.title = "Ưu đãi đặc biệt – Nâng cấp Internet miễn phí";
            n5.shortDescription = "Từ ngày 01/04/2026, cư dân nâng cấp gói Internet sẽ được miễn phí tháng đầu...";
            n5.dateStr = "28/03/2026";
            n5.timeStr = "09:00 SA";
            n5.isRead = true;
            n5.timestamp = System.currentTimeMillis() - 4 * 24 * 60 * 60 * 1000L;

            AppNotification n6 = new AppNotification();
            n6.type = "MAINTENANCE";
            n6.title = "Phun thuốc diệt muỗi toàn khu";
            n6.shortDescription = "Nhằm đảm bảo vệ sinh môi trường, BQL sẽ tổ chức phun thuốc diệt muỗi vào cuối tuần này...";
            n6.dateStr = "25/03/2026";
            n6.timeStr = "10:00 SA";
            n6.isRead = true;
            n6.timestamp = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L;

            AppNotification n7 = new AppNotification();
            n7.type = "IMPORTANT";
            n7.title = "Kiểm tra hệ thống báo cháy định kỳ";
            n7.shortDescription = "BQL sẽ tiến hành kiểm tra và thử chuông báo cháy vào sáng Thứ Bảy tuần tới.";
            n7.dateStr = "20/03/2026";
            n7.timeStr = "15:30 CH";
            n7.isRead = true;
            n7.timestamp = System.currentTimeMillis() - 12 * 24 * 60 * 60 * 1000L;

            AppNotification n8 = new AppNotification();
            n8.type = "MEETING";
            n8.title = "Tập huấn PCCC cho cư dân";
            n8.shortDescription = "Kính mời toàn thể cư dân tham gia buổi tập huấn PCCC tại sân sinh hoạt chung Tòa S1.";
            n8.dateStr = "15/03/2026";
            n8.timeStr = "08:00 SA";
            n8.isRead = true;
            n8.timestamp = System.currentTimeMillis() - 17 * 24 * 60 * 60 * 1000L;

            db.appNotificationDao().insertNotification(n1);
            db.appNotificationDao().insertNotification(n2);
            db.appNotificationDao().insertNotification(n3);
            db.appNotificationDao().insertNotification(n4);
            db.appNotificationDao().insertNotification(n5);
            db.appNotificationDao().insertNotification(n6);
            db.appNotificationDao().insertNotification(n7);
            db.appNotificationDao().insertNotification(n8);
        }).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // FACILITIES
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleFacilities(AppDatabase db) {
        new Thread(() -> {
            if (!db.facilityDao().getAllFacilities().isEmpty())
                return;

            Facility f1 = new Facility();
            f1.name = "Sân cầu lông";
            f1.location = "Tầng 3, Khu thể thao, Tòa S1";
            f1.imageResId = R.drawable.img_badminton_court;
            f1.capacity = 4;
            f1.openTime = "06:00";
            f1.CloseTime = "22:00";
            f1.isOpen = true;
            f1.description = "Sân cầu lông tiêu chuẩn với 2 làn thi đấu, sàn gỗ chống trơn...";

            Facility f2 = new Facility();
            f2.name = "Sân bóng đá mini";
            f2.location = "Tầng 4, Khu thể thao, Tòa S1";
            f2.imageResId = R.drawable.img_football_field;
            f2.capacity = 10;
            f2.openTime = "06:00";
            f2.CloseTime = "21:00";
            f2.isOpen = true;
            f2.description = "Sân bóng đá mini 5 vs 5 với mặt cỏ nhân tạo thế hệ mới...";

            Facility f3 = new Facility();
            f3.name = "Sân bóng chuyền";
            f3.location = "Ngoài trời, Khu vui chơi, Tòa S2";
            f3.imageResId = R.drawable.img_volleyball_court;
            f3.capacity = 12;
            f3.openTime = "06:00";
            f3.CloseTime = "21:00";
            f3.isOpen = true;
            f3.description = "Sân bóng chuyền ngoài trời tiêu chuẩn, nền bê tông phủ sơn...";

            db.facilityDao().insertFacility(f1);
            db.facilityDao().insertFacility(f2);
            db.facilityDao().insertFacility(f3);
        }).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // FACILITY BOOKINGS
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleFacilityBookings(AppDatabase db) {
        new Thread(() -> {
            if (!db.facilityBookingDao().getAdminFacilityBookings().isEmpty())
                return;

            // ── Sân cầu lông (facilityId=1) ─────────────────────────────
            insertBooking(db, 1, 1, "01/04/2026", "01/04/2026", "08:00", "10:00", "APPROVED", null); // Ông Minh
            insertBooking(db, 1, 2, "28/03/2026", "03/04/2026", "18:00", "20:00", "PENDING", null); // Bà Lan
            insertBooking(db, 1, 6, "20/03/2026", "22/03/2026", "14:00", "16:00", "CANCELLED", "Có việc đột xuất"); // Ông
                                                                                                                    // Thành
            insertBooking(db, 1, 9, "02/04/2026", "05/04/2026", "09:00", "11:00", "PENDING", null); // Lý Hải Yến
            insertBooking(db, 1, 12, "25/03/2026", "27/03/2026", "16:00", "18:00", "COMPLETED", null); // Phan Đình
                                                                                                       // Phùng

            // ── Sân bóng đá (facilityId=2) ───────────────────────────────
            insertBooking(db, 2, 5, "01/04/2026", "02/04/2026", "16:00", "18:00", "APPROVED", null); // Ông Hùng
            insertBooking(db, 2, 8, "01/04/2026", "05/04/2026", "06:00", "08:00", "PENDING", null); // Ông Toàn
            insertBooking(db, 2, 10, "29/03/2026", "03/04/2026", "18:00", "20:00", "APPROVED", null); // Hoàng Tuấn Anh
            insertBooking(db, 2, 1, "15/03/2026", "18/03/2026", "17:00", "19:00", "COMPLETED", null); // Ông Minh
            insertBooking(db, 2, 4, "20/03/2026", "22/03/2026", "19:00", "21:00", "CANCELLED", "Trời mưa"); // Bà Hoa

            // ── Sân bóng chuyền (facilityId=3) ───────────────────────────
            insertBooking(db, 3, 4, "30/03/2026", "01/04/2026", "17:00", "19:00", "APPROVED", null); // Bà Hoa
            insertBooking(db, 3, 1, "22/03/2026", "25/03/2026", "09:00", "11:00", "COMPLETED", null); // Ông Minh
            insertBooking(db, 3, 7, "02/04/2026", "06/04/2026", "16:00", "18:00", "PENDING", null); // Ngô Thị Bích Ngọc
            insertBooking(db, 3, 11, "28/03/2026", "02/04/2026", "07:00", "09:00", "APPROVED", null); // Trương Mỹ Linh
            insertBooking(db, 3, 13, "15/03/2026", "16/03/2026", "18:00", "20:00", "COMPLETED", null); // Bùi Thị Mai

        }).start();
    }

    private static void insertBooking(AppDatabase db, int facId, int resId, String bDate, String dDate, String start,
            String end, String status, String reason) {
        FacilityBooking b = new FacilityBooking();
        b.facilityId = facId;
        b.residentId = resId;
        b.bookingDate = bDate;
        b.DayBooking = dDate;
        b.startTime = start;
        b.endTime = end;
        b.status = status;
        b.cancelReason = reason;
        db.facilityBookingDao().insertBooking(b);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // UTILITIES
    // ══════════════════════════════════════════════════════════════════════════
    private static long parseDateToMillis(String value) {
        try {
            if (value == null)
                return System.currentTimeMillis();
            return DATE_ONLY.parse(value).getTime();
        } catch (ParseException | NullPointerException e) {
            return System.currentTimeMillis();
        }
    }

    private static long parseDateTimeToMillis(String value) {
        try {
            if (value == null)
                return System.currentTimeMillis();
            return DATE_TIME.parse(value).getTime();
        } catch (ParseException | NullPointerException e) {
            return System.currentTimeMillis();
    }
}

        }