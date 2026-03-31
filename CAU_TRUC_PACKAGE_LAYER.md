# Cấu Trúc Package, Database, Entity, DAO, Repository

## 📁 Cấu Trúc Thư Mục Dự Án

```
AppQuanLyCuDan_UTEHome/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/example/quanlycudan_utehome/
│   │       │       ├── MainActivity.java
│   │       │       ├── data/
│   │       │       │   ├── database/
│   │       │       │   │   ├── AppDatabase.java          ⭐ Quản lý database
│   │       │       │   │   └── DatabaseInitializer.java  ⭐ Khởi tạo dữ liệu
│   │       │       │   ├── entity/
│   │       │       │   │   ├── Apartment.java            ⭐ Model căn hộ
│   │       │       │   │   ├── ApartmentMember.java      ⭐ Model thành viên
│   │       │       │   │   ├── Resident.java             ⭐ Model cư dân
│   │       │       │   │   └── ApartmentWithMembers.java ⭐ Model kết hợp
│   │       │       │   ├── dao/
│   │       │       │   │   ├── ApartmentDao.java         ⭐ Truy vấn căn hộ
│   │       │       │   │   ├── ApartmentMemberDao.java   ⭐ Truy vấn thành viên
│   │       │       │   │   └── ResidentDao.java          ⭐ Truy vấn cư dân
│   │       │       │   └── repository/
│   │       │       │       ├── ApartmentRepository.java   ⭐ Trung gian căn hộ
│   │       │       │       └── ResidentRepository.java    ⭐ Trung gian cư dân
│   │       │       └── feature/
│   │       │           ├── apartment/
│   │       │           │   ├── ApartmentInfoActivity.java ⭐ UI chi tiết căn hộ
│   │       │           │   └── ApartmentMemberAdapter.java ⭐ Adapter danh sách
│   │       │           ├── auth/
│   │       │           │   ├── login/
│   │       │           │   │   └── LoginActivity.java
│   │       │           │   ├── register/
│   │       │           │   ├── forgotpassword/
│   │       │           │   └── service/
│   │       │           │       └── AuthService.java
│   │       │           └── member/
│   │       └── res/
│   │           ├── layout/
│   │           │   ├── activity_apartment_info.xml       ⭐ Layout chi tiết
│   │           │   ├── item_apartment_member.xml         ⭐ Layout item
│   │           │   └── ... (layout khác)
│   │           ├── drawable/
│   │           │   ├── bg_card_white.xml
│   │           │   ├── bg_avatar_orange_light.xml
│   │           │   └── ... (drawable khác)
│   │           ├── values/
│   │           │   ├── strings.xml
│   │           │   ├── colors.xml
│   │           │   └── dimens.xml
│   │           └── ... (res khác)
│   └── build.gradle.kts
└── gradle/
    ├── libs.versions.toml
    └── wrapper/
```

---

## 🏗️ Kiến Trúc Lớp

### Lớp Model (Entity)
```
┌─────────────────────────────────────────┐
│ Database (SQLite - Room Framework)      │
├─────────────────────────────────────────┤
│                                         │
│  ┌──────────────┐  ┌──────────────────┐ │
│  │ residents    │  │ apartments       │ │
│  ├──────────────┤  ├──────────────────┤ │
│  │ id (PK)      │  │ id (PK)          │ │
│  │ fullName     │  │ apartmentCode    │ │
│  │ avatarUrl    │  │ buildingCode     │ │
│  └──────────────┘  │ floor            │ │
│                    │ area             │ │
│  ┌──────────────┐  │ status           │ │
│  │apartment_    │  └──────────────────┘ │
│  │members (FK)  │                       │
│  ├──────────────┤                       │
│  │ id (PK)      │                       │
│  │ apartmentId  │──────────┐            │
│  │ residentId   │──────┐   │            │
│  │ role         │      │   │            │
│  │ residentType │      │   │            │
│  └──────────────┘      │   │            │
│                        │   │            │
└────────────────────────┼───┼────────────┘
                         │   │
                  ┌──────┘   └──────────┐
                  │                     │
          ┌───────────────┐     ┌───────────────┐
          │ Resident.java │     │Apartment.java │
          └───────┬───────┘     └───────┬───────┘
                  │                     │
                  └────────┬────────────┘
                           │
            ┌──────────────────────────────┐
            │ ApartmentMember.java         │
            │ (Link Apartment + Resident)  │
            └──────────────────────────────┘
                           │
            ┌──────────────────────────────┐
            │ ApartmentWithMembers.java    │
            │ (Transfer Object)            │
            └──────────────────────────────┘
```

---

## 🔌 Các Tầng Kiến Trúc

### Tầng 1: Entity (Model)
**Vị trí**: `data/entity/`

| Tệp | Mục Đích | Phương Thức |
|-----|---------|-----------|
| `Apartment.java` | Đại diện căn hộ | - |
| `Resident.java` | Đại diện cư dân | - |
| `ApartmentMember.java` | Liên kết cư dân với căn hộ | - |
| `ApartmentWithMembers.java` | Transfer object | - |

### Tầng 2: DAO (Data Access)
**Vị trí**: `data/dao/`

| Tệp | Mục Đích | Phương Thức |
|-----|---------|-----------|
| `ApartmentDao.java` | Query căn hộ | `insert()`, `getApartmentById()`, `getAllApartments()` |
| `ResidentDao.java` | Query cư dân | `insert()`, `getAllResidents()`, `getResidentById()` |
| `ApartmentMemberDao.java` | Query thành viên | `insert()`, `getMembers()`, `getAllMembers()` |

### Tầng 3: Database
**Vị trí**: `data/database/`

| Tệp | Mục Đích | Phương Thức |
|-----|---------|-----------|
| `AppDatabase.java` | Quản lý DB chính | `getInstance()`, `apartmentDao()`, `residentDao()`, `apartmentMemberDao()` |
| `DatabaseInitializer.java` | Khởi tạo dữ liệu mẫu | `initializeSampleData()` |

### Tầng 4: Repository
**Vị trí**: `data/repository/`

| Tệp | Mục Đích | Phương Thức |
|-----|---------|-----------|
| `ApartmentRepository.java` | Trung gian căn hộ | `insertApartment()`, `getApartment()`, `getApartmentWithMembers()` |
| `ResidentRepository.java` | Trung gian cư dân | `insertResident()` |

### Tầng 5: UI & Adapter
**Vị trí**: `feature/apartment/`

| Tệp | Mục Đích | Phương Thức |
|-----|---------|-----------|
| `ApartmentInfoActivity.java` | Activity hiển thị | `onCreate()`, `loadApartmentData()`, `displayApartmentInfo()`, `displayMembers()` |
| `ApartmentMemberAdapter.java` | Adapter RecyclerView | `onCreateViewHolder()`, `onBindViewHolder()`, `getItemCount()` |

---

## 🔄 Luồng Dữ Liệu

```
┌──────────────────────────────────────────────────────────────────┐
│ User Interface (Activity + RecyclerView)                         │
│ ApartmentInfoActivity.java                                       │
└────────────┬─────────────────────────────────────────────────────┘
             │ Yêu cầu dữ liệu
             ▼
┌──────────────────────────────────────────────────────────────────┐
│ Repository Layer (Trung gian)                                    │
│ ApartmentRepository.java                                         │
│ ResidentRepository.java                                          │
└────────────┬─────────────────────────────────────────────────────┘
             │ Yêu cầu truy vấn
             ▼
┌──────────────────────────────────────────────────────────────────┐
│ DAO Layer (Truy cập dữ liệu)                                     │
│ ApartmentDao.java                                                │
│ ResidentDao.java                                                 │
│ ApartmentMemberDao.java                                          │
└────────────┬─────────────────────────────────────────────────────┘
             │ Thực hiện truy vấn SQL
             ▼
┌──────────────────────────────────────────────────────────────────┐
│ Database Layer (Cơ sở dữ liệu)                                   │
│ AppDatabase.java (Room Framework)                                │
│ SQLite Database (utehome_db)                                     │
└────────────┬─────────────────────────────────────────────────────┘
             │ Trả về dữ liệu
             ▼
┌──────────────────────────────────────────────────────────────────┐
│ Entity Objects (Models)                                          │
│ Apartment, Resident, ApartmentMember                             │
└────────────┬─────────────────────────────────────────────────────┘
             │ Gửi lên
             ▼
┌──────────────────────────────────────────────────────────────────┐
│ Repository (Xử lý)                                               │
│ getApartmentWithMembers() - kết hợp dữ liệu                      │
└────────────┬─────────────────────────────────────────────────────┘
             │ Trả về ApartmentWithMembers
             ▼
┌──────────────────────────────────────────────────────────────────┐
│ Activity UI (Hiển thị)                                           │
│ displayApartmentInfo()                                           │
│ displayMembers() → ApartmentMemberAdapter                        │
└──────────────────────────────────────────────────────────────────┘
```

---

## 📦 Package Structure

```
com.example.quanlycudan_utehome/
│
├── MainActivity.java
│
├── data/
│   ├── database/
│   │   ├── AppDatabase.java (Manager)
│   │   └── DatabaseInitializer.java (Init)
│   │
│   ├── entity/ (Models)
│   │   ├── Apartment.java
│   │   ├── Resident.java
│   │   ├── ApartmentMember.java
│   │   └── ApartmentWithMembers.java
│   │
│   ├── dao/ (Queries)
│   │   ├── ApartmentDao.java
│   │   ├── ResidentDao.java
│   │   └── ApartmentMemberDao.java
│   │
│   └── repository/ (Intermediary)
│       ├── ApartmentRepository.java
│       └── ResidentRepository.java
│
└── feature/
    ├── apartment/
    │   ├── ApartmentInfoActivity.java (UI)
    │   └── ApartmentMemberAdapter.java (Adapter)
    │
    ├── auth/
    │   ├── login/
    │   │   └── LoginActivity.java
    │   ├── register/
    │   ├── forgotpassword/
    │   └── service/
    │       └── AuthService.java
    │
    └── member/
        └── MemberDetailActivity.java
```

---

## 🗂️ Layout Files

```
res/layout/
├── activity_apartment_info.xml    ⭐ Chi tiết căn hộ + danh sách thành viên
├── item_apartment_member.xml      ⭐ Item thành viên trong RecyclerView
├── activity_login.xml
├── activity_register.xml
├── activity_member_detail.xml
└── ...
```

---

## 🎨 Resource Files

```
res/
├── drawable/
│   ├── bg_card_white.xml
│   ├── bg_avatar_orange_light.xml
│   ├── ic_arrow_back_black.xml
│   ├── ic_arrow_right_gray.xml
│   └── ...
│
├── values/
│   ├── colors.xml (Định nghĩa màu)
│   ├── strings.xml (Định nghĩa chuỗi văn bản)
│   ├── dimens.xml (Định nghĩa kích thước)
│   └── themes.xml (Theme)
│
└── mipmap/
    ├── ic_launcher.png
    └── ic_launcher_round.png
```

---

## 🔗 Mối Quan Hệ Giữa Các Lớp

```
ApartmentInfoActivity (Activity)
    │
    ├─→ ApartmentRepository (Trung gian)
    │       │
    │       ├─→ ApartmentDao → SQLite (apartments)
    │       ├─→ ResidentDao → SQLite (residents)
    │       └─→ ApartmentMemberDao → SQLite (apartment_members)
    │
    ├─→ DatabaseInitializer (Khởi tạo)
    │       │
    │       └─→ Thêm dữ liệu vào các bảng
    │
    └─→ ApartmentMemberAdapter (Adapter)
            │
            └─→ RecyclerView (Hiển thị danh sách)
                    │
                    └─→ item_apartment_member.xml (Layout item)
```

---

## 📋 Tóm Tắt Vai Trò Từng Lớp

| Lớp | Vai Trò | Trách Nhiệm |
|-----|---------|-----------|
| Entity | Model | Đại diện dữ liệu |
| DAO | Truy vấn | SQL queries |
| Database | Quản lý DB | Singleton DB instance |
| Repository | Trung gian | Logic nghiệp vụ |
| Activity | UI | Giao diện người dùng |
| Adapter | Render | Hiển thị danh sách |

---

## 🚀 Cách Mở Rộng

### Thêm Entity Mới
1. Tạo class trong `data/entity/`
2. Thêm @Entity annotation
3. Định nghĩa @PrimaryKey, fields

### Thêm DAO Mới
1. Tạo interface trong `data/dao/`
2. Thêm @Dao annotation
3. Định nghĩa query methods

### Thêm Repository Mới
1. Tạo class trong `data/repository/`
2. Khởi tạo DAO
3. Định nghĩa business methods

### Thêm Activity Mới
1. Tạo class trong `feature/`
2. Kế thừa AppCompatActivity
3. Sử dụng Repository để load dữ liệu

---

## 💡 Best Practices

1. **Tách biệt trách nhiệm**: Mỗi lớp chỉ làm một việc
2. **Dependency Injection**: Truyền dependencies qua constructor
3. **Async operations**: Database queries chạy trong background thread
4. **Error handling**: Kiểm tra null, empty, exception
5. **Code reusability**: Sử dụng Repository cho nhiều Activity
