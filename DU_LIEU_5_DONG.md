# Tóm Tắt 5 Dòng Dữ Liệu Đã Chèn Vào Database

## 📊 Dữ Liệu Residents (Cư Dân)

5 cư dân được thêm vào bảng `residents`:

| ID | Họ và Tên | Vai Trò |
|----|-----------|---------|
| 1 | Nguyễn Anh Tuấn | Chủ hộ (chính) |
| 2 | Trần Thị Hương | Vợ/Gia đình |
| 3 | Nguyễn Anh Đức | Con (gia đình) |
| 4 | Phạm Thị Tâm | Chủ hộ (chính) |
| 5 | Lê Văn Hùng | Gia đình |

**Mã SQL:**
```sql
INSERT INTO residents (fullName, avatarUrl) VALUES 
('Nguyễn Anh Tuấn', 'https://via.placeholder.com/150?text=Tuan'),
('Trần Thị Hương', 'https://via.placeholder.com/150?text=Huong'),
('Nguyễn Anh Đức', 'https://via.placeholder.com/150?text=Duc'),
('Phạm Thị Tâm', 'https://via.placeholder.com/150?text=Tam'),
('Lê Văn Hùng', 'https://via.placeholder.com/150?text=Hung');
```

---

## 🏠 Dữ Liệu Apartments (Căn Hộ)

5 căn hộ được thêm vào bảng `apartments`:

| ID | Mã Căn | Tòa | Tầng | Diện Tích | Tình Trạng |
|----|--------|-----|------|----------|-----------|
| 1 | P.1205 | S1 | 12 | 105.5 m² | Đang sử dụng |
| 2 | P.1206 | S1 | 12 | 87.3 m² | Đang sử dụng |
| 3 | P.1207 | S1 | 12 | 92.0 m² | Đang sử dụng |
| 4 | P.0805 | S2 | 8 | 115.0 m² | Đang sử dụng |
| 5 | P.0806 | S2 | 8 | 95.5 m² | Đang sử dụng |

**Mã SQL:**
```sql
INSERT INTO apartments (apartmentCode, buildingCode, floor, area, status) VALUES 
('P.1205', 'S1', 12, 105.5, 'Đang sử dụng'),
('P.1206', 'S1', 12, 87.3, 'Đang sử dụng'),
('P.1207', 'S1', 12, 92.0, 'Đang sử dụng'),
('P.0805', 'S2', 8, 115.0, 'Đang sử dụng'),
('P.0806', 'S2', 8, 95.5, 'Đang sử dụng');
```

---

## 👥 Dữ Liệu ApartmentMembers (Thành Viên Căn Hộ)

5 liên kết được thêm vào bảng `apartment_members`:

| ID | Căn Hộ ID | Cư Dân ID | Họ và Tên | Vai Trò | Loại |
|----|-----------|----------|----------|--------|------|
| 1 | 1 | 1 | Nguyễn Anh Tuấn | Chủ hộ | Chính |
| 2 | 1 | 2 | Trần Thị Hương | Vợ | Chính |
| 3 | 1 | 3 | Nguyễn Anh Đức | Con | Chính |
| 4 | 2 | 4 | Phạm Thị Tâm | Chủ hộ | Chính |
| 5 | 2 | 5 | Lê Văn Hùng | Gia đình | Chính |

**Mã SQL:**
```sql
INSERT INTO apartment_members (apartmentId, residentId, role, residentType) VALUES 
(1, 1, 'Chủ hộ', 'Chính'),
(1, 2, 'Vợ', 'Chính'),
(1, 3, 'Con', 'Chính'),
(2, 4, 'Chủ hộ', 'Chính'),
(2, 5, 'Gia đình', 'Chính');
```

---

## 🎯 Cách Dữ Liệu Được Hiển Thị Trên Giao Diện

Khi mở ApartmentInfoActivity, ứng dụng sẽ:

### 1. **Hiển Thị Thông Tin Căn Hộ (ID = 1)**
```
├─ Mã căn hộ: P.1205
├─ Tòa nhà: S1
├─ Tầng: 12
├─ Diện tích: 105.5 m²
└─ Tình trạng: Đang sử dụng
```

### 2. **Hiển Thị Danh Sách Thành Viên (3 người)**
```
├─ Nguyễn Anh Tuấn (Chủ hộ)
├─ Trần Thị Hương (Vợ)
└─ Nguyễn Anh Đức (Con)
```

---

## 📱 Cấu Trúc Hiển Thị

```
┌─────────────────────────────────────┐
│    CHI TIẾT CĂN HỘ                  │
├─────────────────────────────────────┤
│ Mã căn hộ:        P.1205            │
│ Tòa nhà:          Tòa S1            │
│ Tầng:             12                │
│ Diện tích:        105.5 m²          │
│ Tình trạng:       Đang sử dụng      │
├─────────────────────────────────────┤
│                                     │
│  THÀNH VIÊN GIA ĐÌNH         + Thêm │
├─────────────────────────────────────┤
│ [N] Nguyễn Anh Tuấn                │
│     Chủ hộ                          │
├─────────────────────────────────────┤
│ [T] Trần Thị Hương                 │
│     Vợ                              │
├─────────────────────────────────────┤
│ [N] Nguyễn Anh Đức                 │
│     Con                             │
└─────────────────────────────────────┘
```

---

## 🔄 Luồng Tạo Dữ Liệu

### Bước 1: Ứng dụng khởi động
```
MainActivity → setContentView(activity_apartment_info.xml)
    ↓
ApartmentInfoActivity.onCreate()
    ↓
DatabaseInitializer.initializeSampleData(context)
```

### Bước 2: Kiểm tra database trống
```java
if (db.residentDao().getAllResidents().isEmpty()) {
    // Thêm 5 residents
    // Thêm 5 apartments
    // Thêm 5 apartment_members
}
```

### Bước 3: Chờ 1 giây để dữ liệu được commit
```java
Thread.sleep(1000);
loadApartmentData();
```

### Bước 4: Load dữ liệu từ database
```java
ApartmentWithMembers data = apartmentRepository.getApartmentWithMembers(1);
// data chứa:
// - Apartment (P.1205)
// - List<ApartmentMemberDetail> (3 thành viên)
```

### Bước 5: Hiển thị trên giao diện
```
displayApartmentInfo(data.apartment);
displayMembers(data.members);
```

---

## 💾 Tệp Khởi Tạo Dữ Liệu

File: `DatabaseInitializer.java`

```java
// Ví dụ: Thêm Resident
Resident resident1 = new Resident();
resident1.fullName = "Nguyễn Anh Tuấn";
resident1.avatarUrl = "https://via.placeholder.com/150?text=Tuan";
db.residentDao().insert(resident1);

// Ví dụ: Thêm Apartment
Apartment apt1 = new Apartment();
apt1.apartmentCode = "P.1205";
apt1.buildingCode = "S1";
apt1.floor = 12;
apt1.area = 105.5f;
apt1.status = "Đang sử dụng";
db.apartmentDao().insert(apt1);

// Ví dụ: Thêm ApartmentMember
ApartmentMember member1 = new ApartmentMember();
member1.apartmentId = 1;
member1.residentId = 1;
member1.role = "Chủ hộ";
member1.residentType = "Chính";
db.apartmentMemberDao().insert(member1);
```

---

## ✅ Kiểm Tra Dữ Liệu Được Thêm

Bạn có thể kiểm tra dữ liệu bằng **Android Studio**:

1. Mở **Database Inspector**
2. Chọn ứng dụng `quanlycudan_utehome`
3. Mở database `utehome_db`
4. Xem các bảng:
   - `residents`: 5 dòng
   - `apartments`: 5 dòng
   - `apartment_members`: 5 dòng

Hoặc sử dụng **adb shell**:
```bash
adb shell sqlite3 /data/data/com.example.quanlycudan_utehome/databases/utehome_db
sqlite> SELECT COUNT(*) FROM residents;     -- Output: 5
sqlite> SELECT COUNT(*) FROM apartments;    -- Output: 5
sqlite> SELECT COUNT(*) FROM apartment_members; -- Output: 5
```

---

## 🎨 Cách Mở Rộng Dữ Liệu

Để thêm thêm căn hộ và thành viên, chỉnh sửa `DatabaseInitializer.java`:

```java
// Thêm căn hộ thứ 6
Apartment apt6 = new Apartment();
apt6.apartmentCode = "P.1208";
apt6.buildingCode = "S1";
apt6.floor = 12;
apt6.area = 98.0f;
apt6.status = "Đang sử dụng";
db.apartmentDao().insert(apt6);

// Thêm thành viên cho căn hộ 6
ApartmentMember member6 = new ApartmentMember();
member6.apartmentId = 6;
member6.residentId = 1; // Reuse existing resident
member6.role = "Chủ hộ";
member6.residentType = "Chính";
db.apartmentMemberDao().insert(member6);
```

---

## 📌 Ghi Chú Quan Trọng

1. **DatabaseInitializer chỉ chạy 1 lần**: Sau lần đầu tiên, dữ liệu được giữ trong database
2. **Không có lặp lại dữ liệu**: Kiểm tra `isEmpty()` trước khi thêm
3. **Thread riêng**: Tất cả thao tác database chạy trong background thread
4. **Hiệu suất**: Dữ liệu tĩnh được khởi tạo, có thể thay đổi thành dữ liệu động từ server

