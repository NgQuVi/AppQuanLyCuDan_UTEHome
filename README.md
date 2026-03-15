# README - Ứng Dụng Quản Lý Cư Dân UTE Home

## 📱 Tổng Quan Ứng Dụng

Ứng dụng **AppQuanLyCuDan_UTEHome** là một ứng dụng Android giúp quản lý thông tin căn hộ, cư dân và danh sách thành viên trong các chung cư.

### ✨ Tính Năng Chính
- ✅ Xem thông tin chi tiết căn hộ
- ✅ Xem danh sách thành viên gia đình
- ✅ Lưu trữ dữ liệu bằng SQLite (Room)
- ✅ Đăng nhập/Đăng ký tài khoản
- ✅ UI đẹp, dễ sử dụng

---

## 📚 Tài Liệu Hướng Dẫn

Dự án này bao gồm 4 file tài liệu chi tiết:

### 1. **LUONG_HOAT_DONG.md**
📖 Giải thích chi tiết về:
- Cấu trúc Entity, DAO, Repository
- Cách thức hoạt động của database
- Luồng dữ liệu từ UI đến database
- Lợi ích của kiến trúc

👉 **Dành cho**: Bạn muốn hiểu toàn bộ hệ thống

### 2. **GIAI_THICH_LOGIN_ACTIVITY.md**
🔐 Hướng dẫn chi tiết về:
- LoginActivity làm gì
- Các chức năng chính (đăng nhập, hiển/ẩn password)
- Luồng xác thực
- Cách debug

👉 **Dành cho**: Bạn muốn hiểu về xác thực người dùng

### 3. **DU_LIEU_5_DONG.md**
📊 Tóm tắt về:
- 5 cư dân được thêm vào database
- 5 căn hộ được thêm vào database
- 5 liên kết thành viên
- Cách dữ liệu được hiển thị trên UI
- Cách kiểm tra dữ liệu

👉 **Dành cho**: Bạn muốn biết dữ liệu mẫu là gì

### 4. **CAU_TRUC_PACKAGE_LAYER.md**
🏗️ Mô tả chi tiết về:
- Cấu trúc package và thư mục
- Kiến trúc lớp (Entity, DAO, DB, Repository)
- Luồng dữ liệu
- Mối quan hệ giữa các lớp
- Best practices

👉 **Dành cho**: Bạn muốn hiểu cấu trúc dự án

---

## 🛠️ Công Nghệ Sử Dụng

| Công Nghệ | Phiên Bản | Mục Đích |
|-----------|----------|---------|
| **Android SDK** | API 24+ | Phát triển ứng dụng |
| **Java** | 11 | Ngôn ngữ lập trình |
| **Room** | 2.6.1 | ORM cho SQLite |
| **RecyclerView** | 1.3.2 | Hiển thị danh sách |
| **AndroidX** | Latest | Support library |

---

## 📂 Cấu Trúc Thư Mục Quan Trọng

```
app/src/main/java/com/example/quanlycudan_utehome/
├── data/
│   ├── database/          # Quản lý cơ sở dữ liệu
│   │   ├── AppDatabase.java
│   │   └── DatabaseInitializer.java
│   ├── entity/            # Models
│   │   ├── Apartment.java
│   │   ├── Resident.java
│   │   └── ApartmentMember.java
│   ├── dao/               # Truy vấn dữ liệu
│   │   ├── ApartmentDao.java
│   │   ├── ResidentDao.java
│   │   └── ApartmentMemberDao.java
│   └── repository/        # Trung gian
│       └── ApartmentRepository.java
├── feature/
│   ├── apartment/         # Chi tiết căn hộ
│   │   ├── ApartmentInfoActivity.java
│   │   └── ApartmentMemberAdapter.java
│   ├── auth/              # Xác thực
│   │   └── login/
│   │       └── LoginActivity.java
│   └── member/            # Chi tiết thành viên
│       └── MemberDetailActivity.java
└── MainActivity.java      # Activity chính
```

---

## 🚀 Cách Chạy Ứng Dụng

### 1. Clone/Tải Dự Án
```bash
git clone <repository-url>
cd AppQuanLyCuDan_UTEHome
```

### 2. Mở Trong Android Studio
- File → Open → Chọn thư mục dự án

### 3. Build Dự Án
```bash
./gradlew build
```

### 4. Chạy Ứng Dụng
- Click "Run" hoặc nhấn `Shift + F10`
- Chọn emulator hoặc device

---

## 💾 Dữ Liệu Mẫu

Ứng dụng được khởi tạo với **5 dòng dữ liệu** cho mỗi bảng:

### Cư Dân (Residents)
| ID | Tên | Vai Trò |
|----|-----|---------|
| 1 | Nguyễn Anh Tuấn | Chủ hộ |
| 2 | Trần Thị Hương | Vợ |
| 3 | Nguyễn Anh Đức | Con |
| 4 | Phạm Thị Tâm | Chủ hộ |
| 5 | Lê Văn Hùng | Gia đình |

### Căn Hộ (Apartments)
| ID | Mã | Tòa | Tầng | Diện Tích |
|----|-----|------|------|----------|
| 1 | P.1205 | S1 | 12 | 105.5 m² |
| 2 | P.1206 | S1 | 12 | 87.3 m² |
| 3 | P.1207 | S1 | 12 | 92.0 m² |
| 4 | P.0805 | S2 | 8 | 115.0 m² |
| 5 | P.0806 | S2 | 8 | 95.5 m² |

> 💡 Dữ liệu được khởi tạo tự động lần đầu tiên ứng dụng chạy

---

## 🎯 Các Màn Hình Chính

### 1. **LoginActivity** (Đăng Nhập)
- Nhập số điện thoại
- Nhập mật khẩu
- Hiển/ẩn mật khẩu
- Link "Quên mật khẩu?"
- Link "Đăng ký"

### 2. **ApartmentInfoActivity** (Chi Tiết Căn Hộ)
- Thông tin căn hộ (Mã, Tòa, Tầng, Diện tích)
- Danh sách thành viên gia đình
- RecyclerView hiển thị động
- Nút "Thêm thành viên"

### 3. **MemberDetailActivity** (Chi Tiết Thành Viên)
- Thông tin cá nhân
- Vai trò trong gia đình
- Thông tin liên hệ

---

## 🔄 Luồng Ứng Dụng

```
Khởi động
    ↓
MainActivity (setContentView = activity_apartment_info.xml)
    ↓
ApartmentInfoActivity.onCreate()
    ↓
DatabaseInitializer.initializeSampleData()
    ↓
Thêm 5 residents, 5 apartments, 5 apartment_members
    ↓
Chờ 1 giây
    ↓
loadApartmentData()
    ↓
Hiển thị thông tin căn hộ + danh sách thành viên
```

---

## 📊 Database Schema

### Bảng: residents
```sql
CREATE TABLE residents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fullName TEXT NOT NULL,
    avatarUrl TEXT
);
```

### Bảng: apartments
```sql
CREATE TABLE apartments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    apartmentCode TEXT NOT NULL,
    buildingCode TEXT NOT NULL,
    floor INTEGER,
    area REAL,
    status TEXT
);
```

### Bảng: apartment_members
```sql
CREATE TABLE apartment_members (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    apartmentId INTEGER NOT NULL,
    residentId INTEGER NOT NULL,
    role TEXT,
    residentType TEXT,
    FOREIGN KEY(apartmentId) REFERENCES apartments(id),
    FOREIGN KEY(residentId) REFERENCES residents(id)
);
```

---

## 🐛 Debug & Troubleshooting

### Vấn Đề: Dữ liệu không hiển thị
**Giải pháp**:
1. Kiểm tra database: Android Studio → Database Inspector
2. Xóa app cache: Settings → Apps → Clear Data
3. Reinstall app

### Vấn Đề: Build thất bại
**Giải pháp**:
1. Clean project: Build → Clean Project
2. Rebuild: Build → Rebuild Project
3. Sync gradle: File → Sync Now

### Vấn Đề: RecyclerView không hiển thị
**Giải pháp**:
1. Kiểm tra adapter có null không
2. Kiểm tra layout manager
3. Kiểm tra dữ liệu list có trống không

---

## 📋 Checklist Yêu Cầu

- ✅ Kiến trúc MVVM/MVP sạch
- ✅ Database Room được setup đúng
- ✅ 3 Entity (Apartment, Resident, ApartmentMember)
- ✅ 3 DAO (ApartmentDao, ResidentDao, ApartmentMemberDao)
- ✅ Repository pattern
- ✅ 5 dòng dữ liệu cho mỗi bảng
- ✅ RecyclerView hiển thị động
- ✅ UI đẹp, thân thiện
- ✅ Tài liệu chi tiết

---

## 🎓 Tài Liệu Tham Khảo

- 📖 [Android Room Database](https://developer.android.com/training/data-storage/room)
- 📖 [RecyclerView Guide](https://developer.android.com/guide/topics/ui/layout/recyclerview)
- 📖 [Android Architecture Components](https://developer.android.com/topic/architecture)
- 📖 [SharedPreferences](https://developer.android.com/training/basics/data-storage/shared-preferences)

---

## 👨‍💻 Tác Giả

**Dự Án**: Ứng Dụng Quản Lý Cư Dân UTE Home  
**Trường**: HCMUTE  
**Kỳ Học**: HK2 2025-2026  
**Môn Học**: LTDD (Lập Trình Ứng Dụng Di Động)  

---

## 📝 Ghi Chú Quan Trọng

1. **Dữ liệu khởi tạo**: DatabaseInitializer chỉ chạy 1 lần, khi database trống
2. **Background threads**: Tất cả database queries chạy trong background thread
3. **Edge-to-edge**: Ứng dụng sử dụng Edge-to-Edge display
4. **No hardcoding**: Tất cả text được lưu trong strings.xml
5. **Responsive**: Layout đáp ứng với nhiều kích thước màn hình

---

## 🚀 Bước Tiếp Theo

1. **Thêm chức năng** (tạo, sửa, xóa)
2. **Thêm tìm kiếm** (search apartments)
3. **Thêm bộ lọc** (filter by floor, building)
4. **Thêm export** (CSV, PDF)
5. **Thêm sync server** (dữ liệu từ backend)
6. **Thêm notification** (sự kiện mới)
7. **Thêm authentication** (JWT tokens)

---

## 📞 Hỗ Trợ

Nếu có câu hỏi, vui lòng:
1. Kiểm tra các file tài liệu `.md`
2. Xem logcat trong Android Studio
3. Đọc code comments
4. Tham khảo official documentation

---

**Happy Coding! 🎉**

Để hiểu thêm chi tiết, vui lòng xem:
- 📖 `LUONG_HOAT_DONG.md` - Luồng hoạt động
- 🔐 `GIAI_THICH_LOGIN_ACTIVITY.md` - Login Activity
- 📊 `DU_LIEU_5_DONG.md` - Dữ liệu mẫu
- 🏗️ `CAU_TRUC_PACKAGE_LAYER.md` - Kiến trúc
