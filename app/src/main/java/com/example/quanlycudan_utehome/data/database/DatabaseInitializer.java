package com.example.quanlycudan_utehome.data.database;

import android.content.Context;

import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.Resident;
import com.example.quanlycudan_utehome.data.entity.Account;
import com.example.quanlycudan_utehome.data.entity.GuestPass;
import com.example.quanlycudan_utehome.data.entity.Invoice;
import com.example.quanlycudan_utehome.data.entity.InvoiceItem;
import com.example.quanlycudan_utehome.data.entity.TransactionHistory;
import com.example.quanlycudan_utehome.data.entity.AppNotification;
import com.example.quanlycudan_utehome.data.entity.Vehicle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseInitializer {

    private static final SimpleDateFormat DATE_ONLY = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public static void initializeSampleData(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);

        insertSampleResidents(db);
        insertSampleAccounts(db);
        insertSampleApartments(db);
        insertSampleApartmentMembers(db);
        insertSampleVehicles(db);
        insertSampleInvoices(db);
        insertSampleGuestPasses(db);
        insertSampleNotifications(db);
    }


    // ══════════════════════════════════════════════════════════════════════════
    // RESIDENTS
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleResidents(AppDatabase db) {
        new Thread(() -> {
            if (db.residentDao().getAllResidents().isEmpty()) {
                Resident r1 = new Resident();
                r1.gender       = "Nam";
                r1.idNum        = "012345678910";
                r1.avatarUrl    = "https://via.placeholder.com/150?text=An";

                Resident r2 = new Resident();
                r2.fullName  = "Trần Thị Hương";
                r2.avatarUrl = "https://via.placeholder.com/150?text=Huong";

                Resident r3 = new Resident();
                r3.fullName  = "Nguyễn Anh Đức";
                r3.avatarUrl = "https://via.placeholder.com/150?text=Duc";

                Resident r4 = new Resident();
                r4.fullName  = "Phạm Thị Tâm";
                r4.avatarUrl = "https://via.placeholder.com/150?text=Tam";

                Resident r5 = new Resident();
                r5.fullName  = "Lê Văn Hùng";
                r5.avatarUrl = "https://via.placeholder.com/150?text=Hung";

                db.residentDao().insert(r1);
                db.residentDao().insert(r2);
                db.residentDao().insert(r3);
                db.residentDao().insert(r4);
                db.residentDao().insert(r5);
            }
        }).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACCOUNTS
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleAccounts(AppDatabase db) {
        new Thread(() -> {
            if (db.accountDao().checkPhoneExists("0901234567") == 0) {
                // residentId = 1 tương ứng resident1 (Nguyễn Văn An)
                Account account1 = new Account("0901234567", "12345678", "Chủ hộ");
                db.accountDao().insert(account1);
            }
        }).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // APARTMENTS
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleApartments(AppDatabase db) {
        new Thread(() -> {
            if (db.apartmentDao().getAllApartments().isEmpty()) {
                Apartment a1 = new Apartment();
                a1.apartmentCode = "P.1205"; a1.buildingCode = "S1";
                a1.floor = 12; a1.area = 105.5f; a1.status = "Đang sử dụng";

                Apartment a2 = new Apartment();
                a2.apartmentCode = "P.1206"; a2.buildingCode = "S1";
                a2.floor = 12; a2.area = 87.3f;  a2.status = "Đang sử dụng";

                Apartment a3 = new Apartment();
                a3.apartmentCode = "P.1207"; a3.buildingCode = "S1";
                a3.floor = 12; a3.area = 92.0f;  a3.status = "Đang sử dụng";

                Apartment a4 = new Apartment();
                a4.apartmentCode = "P.0805"; a4.buildingCode = "S2";
                a4.floor = 8;  a4.area = 115.0f; a4.status = "Đang sử dụng";

                Apartment a5 = new Apartment();
                a5.apartmentCode = "P.0806"; a5.buildingCode = "S2";
                a5.floor = 8;  a5.area = 95.5f;  a5.status = "Đang sử dụng";

                db.apartmentDao().insert(a1);
                db.apartmentDao().insert(a2);
                db.apartmentDao().insert(a3);
                db.apartmentDao().insert(a4);
                db.apartmentDao().insert(a5);
            }
        }).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // APARTMENT MEMBERS
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleApartmentMembers(AppDatabase db) {
        new Thread(() -> {
            if (db.apartmentMemberDao().getAllMembers().isEmpty()) {
                // Căn hộ 1 (id DB = 1, mã P.1205) – 3 thành viên
                ApartmentMember m1 = new ApartmentMember();
                m1.apartmentId = 1; m1.residentId = 1;
                m1.role = "Chủ hộ"; m1.residentType = "Chính";

                ApartmentMember m2 = new ApartmentMember();
                m2.apartmentId = 1; m2.residentId = 2;
                m2.role = "Vợ"; m2.residentType = "Chính";

                ApartmentMember m3 = new ApartmentMember();
                m3.apartmentId = 1; m3.residentId = 3;
                m3.role = "Con"; m3.residentType = "Chính";

                // Căn hộ 2 (id DB = 2, mã P.1206) – 2 thành viên
                ApartmentMember m4 = new ApartmentMember();
                m4.apartmentId = 2; m4.residentId = 4;
                m4.role = "Chủ hộ"; m4.residentType = "Chính";

                ApartmentMember m5 = new ApartmentMember();
                m5.apartmentId = 2; m5.residentId = 5;
                m5.role = "Gia đình"; m5.residentType = "Chính";

                db.apartmentMemberDao().insert(m1);
                db.apartmentMemberDao().insert(m2);
                db.apartmentMemberDao().insert(m3);
                db.apartmentMemberDao().insert(m4);
                db.apartmentMemberDao().insert(m5);
            }
        }).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // VEHICLES
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleVehicles(AppDatabase db) {
        new Thread(() -> {
            // Seed once for apartment 1 to avoid duplicate records on next launches.
            if (!db.vehicleDao().getVehiclesByResidentIdsSync(java.util.Arrays.asList(1, 2, 3, 4, 5)).isEmpty()) {
                return;
            }

            Vehicle v1 = new Vehicle();
            v1.apartmentId = 1;
            v1.residentId = 1;
            v1.vehicleType = "Ô tô";
            v1.brand = "Toyota Vios";
            v1.color = "Trắng";
            v1.licensePlate = "51H-123.45";
            v1.status = "Đã duyệt";

            Vehicle v2 = new Vehicle();
            v2.apartmentId = 1;
            v2.residentId = 2;
            v2.vehicleType = "Xe máy";
            v2.brand = "Honda Vision";
            v2.color = "Đỏ";
            v2.licensePlate = "59X2-456.78";
            v2.status = "Chưa duyệt";

            Vehicle v3 = new Vehicle();
            v3.apartmentId = 1;
            v3.residentId = 3;
            v3.vehicleType = "Xe máy";
            v3.brand = "Yamaha Exciter";
            v3.color = "Đen";
            v3.licensePlate = "59X3-789.01";
            v3.status = "Đã duyệt";

            Vehicle v4 = new Vehicle();
            v4.apartmentId = 2;
            v4.residentId = 4;
            v4.vehicleType = "Ô tô";
            v4.brand = "Mazda CX-5";
            v4.color = "Xanh";
            v4.licensePlate = "50H-246.80";
            v4.status = "Chưa duyệt";

            Vehicle v5 = new Vehicle();
            v5.apartmentId = 2;
            v5.residentId = 5;
            v5.vehicleType = "Xe máy";
            v5.brand = "Honda SH";
            v5.color = "Đen";
            v5.licensePlate = "59S1-111.22";
            v5.status = "Đã duyệt";

            Vehicle v6 = new Vehicle();
            v6.apartmentId = 1;
            v6.residentId = 1;
            v6.vehicleType = "Xe máy";
            v6.brand = "Suzuki Raider";
            v6.color = "Xám";
            v6.licensePlate = "59K1-333.44";
            v6.status = "Chưa duyệt";

            Vehicle v7 = new Vehicle();
            v7.apartmentId = 1;
            v7.residentId = 2;
            v7.vehicleType = "Ô tô";
            v7.brand = "Kia Seltos";
            v7.color = "Cam";
            v7.licensePlate = "51K-555.66";
            v7.status = "Đã duyệt";

            Vehicle v8 = new Vehicle();
            v8.apartmentId = 2;
            v8.residentId = 4;
            v8.vehicleType = "Xe máy";
            v8.brand = "Piaggio Liberty";
            v8.color = "Trắng";
            v8.licensePlate = "59P2-777.88";
            v8.status = "Chưa duyệt";

            Vehicle v9 = new Vehicle();
            v9.apartmentId = 2;
            v9.residentId = 5;
            v9.vehicleType = "Ô tô";
            v9.brand = "Hyundai Accent";
            v9.color = "Bạc";
            v9.licensePlate = "50A-999.10";
            v9.status = "Đã duyệt";

            Vehicle v10 = new Vehicle();
            v10.apartmentId = 1;
            v10.residentId = 3;
            v10.vehicleType = "Xe máy";
            v10.brand = "Honda Air Blade";
            v10.color = "Xanh đen";
            v10.licensePlate = "59F1-222.33";
            v10.status = "Chưa duyệt";

            db.vehicleDao().insertVehicle(v1);
            db.vehicleDao().insertVehicle(v2);
            db.vehicleDao().insertVehicle(v3);
            db.vehicleDao().insertVehicle(v4);
            db.vehicleDao().insertVehicle(v5);
            db.vehicleDao().insertVehicle(v6);
            db.vehicleDao().insertVehicle(v7);
            db.vehicleDao().insertVehicle(v8);
            db.vehicleDao().insertVehicle(v9);
            db.vehicleDao().insertVehicle(v10);
        }).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INVOICES + INVOICE_ITEMS + TRANSACTIONS
    //
    // Lưu ý: Invoice.apartmentId kiểu String, giá trị là "1" (= id của apt P.1205)
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleInvoices(AppDatabase db) {
        new Thread(() -> {
            if (!db.paymentDao().getAllInvoices().isEmpty()) return; // Đã có dữ liệu thì bỏ qua

            // ──────────────────────────────────────────────────────────────────
            // HÓA ĐƠN THÁNG 03/2026 – CHƯA THANH TOÁN (UNPAID)
            // Căn hộ P.1205 (apartmentId = "1")
            // Tổng = 472.500 + 270.000 + 1.500.000 + 400.000 = 2.642.500đ
            // ──────────────────────────────────────────────────────────────────
            Invoice inv_unpaid = new Invoice(
                    "INV-032026-1205",  // ID hóa đơn
                    "1",                // apartmentId (String)
                    "03/2026",          // billingMonth
                    2642500,            // totalAmount
                    "15/04/2026",       // dueDate
                    "UNPAID"            // status
            );
            db.paymentDao().insertInvoice(inv_unpaid);

            List<InvoiceItem> unpaidItems = new ArrayList<>();

            // ── ĐIỆN ────────────────────────────────────────────────────────
            //   Số cũ: 1245 kWh | Số mới: 1380 kWh | Tiêu thụ: 135 kWh
            //   Đơn giá: 3.500đ/kWh → Thành tiền: 472.500đ
            InvoiceItem electric = new InvoiceItem();
            electric.invoiceId  = inv_unpaid.id;
            electric.serviceType = "ELECTRIC";
            electric.oldIndex   = 1245;
            electric.newIndex   = 1380;
            electric.unitPrice  = 3500;           // đồng / kWh
            electric.amount     = 472500;         // = consumption × unitPrice
            unpaidItems.add(electric);

            // ── NƯỚC ────────────────────────────────────────────────────────
            //   Số cũ: 120 m³ | Số mới: 135 m³ | Tiêu thụ: 15 m³
            //   Đơn giá: 18.000đ/m³ → Thành tiền: 270.000đ
            InvoiceItem water = new InvoiceItem();
            water.invoiceId   = inv_unpaid.id;
            water.serviceType = "WATER";
            water.oldIndex    = 120;
            water.newIndex    = 135;
            water.unitPrice   = 18000;            // đồng / m³
            water.amount      = 270000;           // = consumption × unitPrice
            unpaidItems.add(water);

            // ── GỬI XE (PARKING) ────────────────────────────────────────────
            //   1 Ô tô:  800.000đ/tháng  →  800.000đ
            //   1 Xe máy: 200.000đ/tháng → 200.000đ (nếu có thêm xe máy)
            //   Ví dụ: 1 ô tô + 2 xe máy = 800.000 + 2×200.000 = 1.200.000đ
            //   Nhưng căn hộ này có: 1 ô tô + 3 xe máy = 800.000 + 600.000 = 1.400.000đ... Tuỳ chỉnh
            //   Ở đây dùng: 1 ô tô (800.000) + 1 xe máy (200.000)... 
            //   nhưng invoice XML UI có 2 dòng riêng → ta insert 1 PARKING item với cả 2 loại
            InvoiceItem parking = new InvoiceItem();
            parking.invoiceId    = inv_unpaid.id;
            parking.serviceType  = "PARKING";
            parking.quantity     = 1;
            parking.unitPrice    = 1500000;
            parking.amount       = 1500000;
            parking.description  = "01 Ô tô, 02 Xe máy";
            unpaidItems.add(parking);

            // ── INTERNET ─────────────────────────────────────────────────────
            //   3 mức gói:
            //     Gói Cơ bản     – 200.000đ/tháng  (tốc độ 50 Mbps)
            //     Gói Tiêu chuẩn – 400.000đ/tháng  (tốc độ 100 Mbps)  ← căn hộ này đang dùng
            //     Gói Cao cấp    – 600.000đ/tháng  (tốc độ 300 Mbps)
            InvoiceItem internet = new InvoiceItem();
            internet.invoiceId   = inv_unpaid.id;
            internet.serviceType = "INTERNET";
            internet.description = "Gói Tiêu chuẩn – 100 Mbps"; // Tên gói đang dùng
            internet.amount      = 400000;        // 400.000đ / tháng
            unpaidItems.add(internet);

            db.paymentDao().insertInvoiceItems(unpaidItems);

            // ──────────────────────────────────────────────────────────────────
            // HÓA ĐƠN THÁNG 09/2023 – ĐÃ THANH TOÁN (PAID) → Dùng cho Lịch sử
            // Tổng = 1.500.000đ
            // ──────────────────────────────────────────────────────────────────
            Invoice inv_paid = new Invoice(
                    "INV-092023-1205",
                    "1",
                    "09/2023",
                    1500000,
                    "15/10/2023",
                    "PAID"
            );
            db.paymentDao().insertInvoice(inv_paid);

            // Chi tiết hóa đơn đã trả (chỉ có điện + nước, không có xe + internet tháng đó)
            List<InvoiceItem> paidItems = new ArrayList<>();

            InvoiceItem paidElec = new InvoiceItem();
            paidElec.invoiceId   = inv_paid.id;
            paidElec.serviceType = "ELECTRIC";
            paidElec.oldIndex    = 1110;
            paidElec.newIndex    = 1245;
            paidElec.unitPrice   = 3500;
            paidElec.amount      = 472500;
            paidItems.add(paidElec);

            InvoiceItem paidWater = new InvoiceItem();
            paidWater.invoiceId   = inv_paid.id;
            paidWater.serviceType = "WATER";
            paidWater.oldIndex    = 105;
            paidWater.newIndex    = 120;
            paidWater.unitPrice   = 18000;
            paidWater.amount      = 270000;
            paidItems.add(paidWater);

            InvoiceItem paidParking = new InvoiceItem();
            paidParking.invoiceId     = inv_paid.id;
            paidParking.serviceType   = "PARKING";
            paidParking.quantity      = 1;
            paidParking.unitPrice  = 800000;
            paidParking.amount        = 800000; // Chỉ 1 ô tô
            paidParking.description   = "01 Ô tô";
            paidItems.add(paidParking);

            // Không có Internet tháng đó (vi dụ người dùng chưa đăng ký)

            db.paymentDao().insertInvoiceItems(paidItems);

            // Lịch sử giao dịch thanh toán hóa đơn tháng 09/2023
            TransactionHistory history = new TransactionHistory(
                    "#PMH092310",
                    inv_paid.id,
                    "Ví MoMo",
                    "10/09/2023 - 10:15",
                    1500000,
                    "SUCCESS"
            );
            db.paymentDao().insertTransaction(history);

        }).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // NOTIFICATIONS
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleNotifications(AppDatabase db) {
        new Thread(() -> {
            if (db.appNotificationDao().getAllNotifications().isEmpty()) {
                AppNotification n1 = new AppNotification();
                n1.type = "MAINTENANCE";
                n1.title = "Thông báo bảo trì hệ thống thang máy tòa S1";
                n1.shortDescription = "Kế hoạch bảo trì định kỳ thang máy từ 09:00 - 11:00 ngày hôm nay tại...";
                n1.dateStr = "15/10/2023";
                n1.timeStr = "10:30 AM";
                n1.isRead = false;
                n1.timestamp = System.currentTimeMillis() - 10 * 60 * 1000;
                n1.affectedScope = "Toàn bộ cư dân đang sinh sống tại tòa S1 và khách vãng lai.";
                n1.fullContent = "Kính gửi Quý cư dân tòa S1, Ban Quản lý Tòa nhà xin thông báo về kế hoạch bảo trì định kỳ hệ thống thang máy nhằm đảm bảo an toàn vận hành. Chi tiết lịch trình cụ thể như sau:\n\n* Lưu ý: Trong thời gian bảo trì, các thang máy còn lại vẫn hoạt động bình thường. Tuy nhiên, thời gian chờ đợi có thể lâu hơn so với dự kiến.\n\nBan Quản lý rất mong nhận được sự thông cảm và hợp tác của Quý cư dân để công tác bảo trì diễn ra thuận lợi. Mọi thắc mắc xin vui lòng liên hệ Hotline: 1900 xxxx.";
                n1.eventStepsJson = "[{\"title\":\"Đợt 1: Thang máy 1, 2 & 3\",\"time\":\"Thời gian: 08:00 - 12:00, ngày 16/10/2023\"},{\"title\":\"Đợt 2: Thang máy 4, 5 & 6\",\"time\":\"Thời gian: 13:30 - 17:30, ngày 16/10/2023\"}]";
                // Fake imageResId for demo: R.drawable.img_elevator (we'll implement this soon)

                AppNotification n2 = new AppNotification();
                n2.type = "IMPORTANT";
                n2.title = "Thông báo tạm ngắt điện";
                n2.shortDescription = "Tòa nhà S1 sẽ tạm ngừng cung cấp điện để đấu nối hệ thống kỹ thuật...";
                n2.dateStr = "14/10/2023";
                n2.timeStr = "08:00 AM";
                n2.isRead = false;
                n2.timestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000;

                AppNotification n3 = new AppNotification();
                n3.type = "MEETING";
                n3.title = "Họp cư dân định kỳ Q3";
                n3.shortDescription = "Kính mời quý cư dân tham dự buổi họp tổng kết hoạt động quý 3 tại phòng...";
                n3.dateStr = "13/10/2023";
                n3.timeStr = "14:00 PM";
                n3.isRead = true;
                n3.timestamp = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000;

                AppNotification n4 = new AppNotification();
                n4.type = "UTILITY";
                n4.title = "Hóa đơn tiền nước tháng 10";
                n4.shortDescription = "Hóa đơn tiền nước kỳ tháng 10/2023 đã được cập nhật. Quý cư dân vui lòn...";
                n4.dateStr = "12/10/2023";
                n4.timeStr = "09:00 AM";
                n4.isRead = true;
                n4.timestamp = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000;

                db.appNotificationDao().insertNotification(n1);
                db.appNotificationDao().insertNotification(n2);
                db.appNotificationDao().insertNotification(n3);
                db.appNotificationDao().insertNotification(n4);
            }
        }).start();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GUEST PASSES
    // ══════════════════════════════════════════════════════════════════════════
    private static void insertSampleGuestPasses(AppDatabase db) {
        new Thread(() -> {
            if (db.guestPassDao().getGuestPassesByApartmentIdSync(1).isEmpty()) {
                insertGuest(db, 1, "QR88291", "10/10/2023", "10/10/2023 08:00", "20/10/2023 22:00", "ACTIVE");
                insertGuest(db, 1, "QR88290", "05/10/2023", "05/10/2023 08:00", "15/10/2023 22:00", "ACTIVE");
                insertGuest(db, 1, "QR88285", "20/09/2023", "20/09/2023 08:00", "30/09/2023 22:00", "EXPIRED");
                insertGuest(db, 1, "QR88275", "15/09/2023", "15/09/2023 08:00", "25/09/2023 22:00", "CANCELLED");
                insertGuest(db, 1, "QR88270", "01/09/2023", "01/09/2023 08:00", "10/09/2023 22:00", "EXPIRED");
            }
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

    private static long parseDateToMillis(String value) {
        try {
            if (value == null) return System.currentTimeMillis();
            return DATE_ONLY.parse(value).getTime();
        } catch (ParseException | NullPointerException e) {
            return System.currentTimeMillis();
        }
    }

    private static long parseDateTimeToMillis(String value) {
        try {
            if (value == null) return System.currentTimeMillis();
            return DATE_TIME.parse(value).getTime();
        } catch (ParseException | NullPointerException e) {
            return System.currentTimeMillis();
        }
    }
}
