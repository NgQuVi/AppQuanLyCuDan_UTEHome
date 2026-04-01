# Giải Thích Luồng Hoạt Động Ứng Dụng Quản Lý Cư Dân UTE Home

## 📋 Mục đích của các file chính

### 1. **Entity (Model - Lớp đối tượng)**
- **Apartment.java**: Đại diện cho một căn hộ với các thuộc tính:
  - `id`: ID căn hộ (tự động tăng)
  - `apartmentCode`: Mã căn hộ (VD: P.1205)
  - `buildingCode`: Mã tòa nhà (VD: S1, S2)
  - `floor`: Tầng (VD: 12)
  - `area`: Diện tích (VD: 105.5m²)
  - `status`: Tình trạng (VD: Đang sử dụng)

- **Resident.java**: Đại diện cho một cư dân/người dân với các thuộc tính:
  - `id`: ID cư dân (tự động tăng)
  - `fullName`: Họ và tên (VD: Nguyễn Anh Tuấn)
  - `avatarUrl`: Đường dẫn ảnh đại diện

- **ApartmentMember.java**: Liên kết giữa căn hộ và cư dân:
  - `id`: ID thành viên (tự động tăng)
  - `apartmentId`: ID căn hộ (khóa ngoại)
  - `residentId`: ID cư dân (khóa ngoại)
  - `role`: Vai trò (VD: Chủ hộ, Vợ, Con, Giúp việc)
  - `residentType`: Loại cư dân (VD: Chính, Khách)

- **ApartmentWithMembers.java**: Lớp kết hợp để truyền dữ liệu:
  - Chứa đối tượng `Apartment` + danh sách `ApartmentMemberDetail`
  - Dùng để truyền dữ liệu từ database lên giao diện

### 2. **DAO (Data Access Object - Lớp truy cập dữ liệu)**
- **ApartmentDao.java**: Định nghĩa các phương thức truy vấn bảng `apartments`:
  - `insert(Apartment)`: Thêm căn hộ mới
  - `getApartmentById(int id)`: Lấy căn hộ theo ID
  - `getAllApartments()`: Lấy tất cả căn hộ

- **ResidentDao.java**: Định nghĩa các phương thức truy vấn bảng `residents`:
  - `insert(Resident)`: Thêm cư dân mới
  - `getAllResidents()`: Lấy tất cả cư dân
  - `getResidentById(int id)`: Lấy cư dân theo ID

- **ApartmentMemberDao.java**: Định nghĩa các phương thức truy vấn bảng `apartment_members`:
  - `insert(ApartmentMember)`: Thêm thành viên mới
  - `getMembers(int apartmentId)`: Lấy tất cả thành viên của căn hộ
  - `getAllMembers()`: Lấy tất cả thành viên

### 3. **Database (Cơ sở dữ liệu)**
- **AppDatabase.java**: Lớp chính quản lý database Room:
  - Kế thừa từ `RoomDatabase`
  - Định nghĩa các entity: `Apartment`, `Resident`, `ApartmentMember`
  - Cung cấp access các DAO: `apartmentDao()`, `residentDao()`, `apartmentMemberDao()`
  - Sử dụng singleton pattern để tạo instance duy nhất

- **DatabaseInitializer.java**: Khởi tạo dữ liệu mẫu ban đầu:
  - Chèn 5 cư dân vào bảng `residents`
  - Chèn 5 căn hộ vào bảng `apartments`
  - Chèn 5 mối liên kết vào bảng `apartment_members`

### 4. **Repository (Lớp trung gian)**
- **ApartmentRepository.java**: Lớp trung gian giữa UI và database:
  - `insertApartment(Apartment)`: Thêm căn hộ (chạy trong thread riêng)
  - `getApartment(int id)`: Lấy căn hộ theo ID
  - `getApartmentWithMembers(int id)`: Lấy căn hộ với danh sách thành viên đầy đủ

- **ResidentRepository.java**: Lớp trung gian cho cư dân:
  - `insertResident(Resident)`: Thêm cư dân

### 5. **Activity (Giao diện)**
- **MainActivity.java**: Activity chính, hiển thị layout `activity_apartment_info.xml`

- **ApartmentInfoActivity.java**: Activity hiển thị thông tin chi tiết căn hộ và danh sách thành viên:
  - Khởi tạo `DatabaseInitializer` để thêm dữ liệu mẫu
  - Load dữ liệu căn hộ từ database
  - Hiển thị thông tin căn hộ trên giao diện
  - Hiển thị danh sách thành viên bằng RecyclerView

### 6. **Adapter (Bộ chuyển đổi)**
- **ApartmentMemberAdapter.java**: Adapter để hiển thị danh sách thành viên:
  - Kế thừa từ `RecyclerView.Adapter`
  - Định nghĩa `ViewHolder` để giữ các view của mỗi item
  - Phương thức `onBindViewHolder` để bind dữ liệu vào các view

---

## 🔄 Luồng Hoạt Động Chi Tiết

### Khi ứng dụng khởi động:

1. **MainActivity được khởi chạy**
   - Load layout `activity_apartment_info.xml`

2. **ApartmentInfoActivity.onCreate() được gọi**
   - Khởi tạo `ApartmentRepository`
   - Gọi `DatabaseInitializer.initializeSampleData(this)` để khởi tạo dữ liệu
   - DatabaseInitializer kiểm tra nếu database trống, sẽ thêm:
     - 5 cư dân (Residents)
     - 5 căn hộ (Apartments)
     - 5 liên kết (ApartmentMembers)

3. **Chờ 1 giây để dữ liệu được thêm vào database**
   - Gọi `loadApartmentData()`

4. **loadApartmentData() truy vấn database**
   - Gọi `apartmentRepository.getApartmentWithMembers(1)`
   - Repository lấy dữ liệu từ các DAO:
     - Lấy `Apartment` với ID = 1
     - Lấy danh sách `ApartmentMember` của căn hộ này
     - Cho mỗi `ApartmentMember`, lấy đối tượng `Resident` tương ứng
   - Kết hợp và trả về `ApartmentWithMembers`

5. **displayApartmentInfo() cập nhật UI**
   - Cập nhật mã căn hộ, tòa nhà lên giao diện

6. **displayMembers() hiển thị danh sách**
   - Tạo `ApartmentMemberAdapter` với danh sách thành viên
   - RecyclerView render từng item thành viên bằng layout `item_apartment_member.xml`

---

## 📊 Cấu Trúc Database

### Bảng: apartments
| id | apartmentCode | buildingCode | floor | area  | status        |
|----|---------------|--------------|-------|-------|---------------|
| 1  | P.1205        | S1           | 12    | 105.5 | Đang sử dụng  |
| 2  | P.1206        | S1           | 12    | 87.3  | Đang sử dụng  |
| ... | ... | ... | ... | ... | ... |

### Bảng: residents
| id | fullName       | avatarUrl |
|----|----------------|-----------|
| 1  | Nguyễn Anh Tuấn| URL       |
| 2  | Trần Thị Hương | URL       |
| ... | ... | ... |

### Bảng: apartment_members
| id | apartmentId | residentId | role   | residentType |
|----|-------------|-----------|--------|--------------|
| 1  | 1           | 1         | Chủ hộ | Chính        |
| 2  | 1           | 2         | Vợ    | Chính        |
| 3  | 1           | 3         | Con   | Chính        |
| ... | ... | ... | ... | ... |

---

## 🎯 Các Bước Thực Hiện Của Dự Án

1. ✅ Tạo 3 Entity classes (Apartment, Resident, ApartmentMember)
2. ✅ Tạo 3 DAO interfaces (ApartmentDao, ResidentDao, ApartmentMemberDao)
3. ✅ Tạo AppDatabase class để quản lý Room database
4. ✅ Tạo DatabaseInitializer để khởi tạo 5 dòng dữ liệu mẫu
5. ✅ Tạo Repository classes để trung gian giữa UI và database
6. ✅ Tạo ApartmentMemberAdapter để hiển thị danh sách RecyclerView
7. ✅ Cập nhật layout XML:
   - Thêm ID `detailsLayout` cho phần chi tiết căn hộ
   - Thay thế hardcoded member list bằng RecyclerView
8. ✅ Cập nhật ApartmentInfoActivity để load dữ liệu từ database
9. ✅ Thêm RecyclerView dependency vào build.gradle.kts

---

## 💡 Lợi Ích Của Kiến Trúc Này

- **Tách biệt trách nhiệm**: Entity, DAO, Repository, Activity độc lập
- **Dễ bảo trì**: Thay đổi database chỉ cần sửa DAO
- **Dễ test**: Có thể mock Repository để test Activity
- **Tái sử dụng**: Repository có thể được sử dụng bởi nhiều Activity
- **Hiệu suất**: Sử dụng thread riêng để load dữ liệu không block UI

---

## 🚀 Tiếp Theo

Bạn có thể:
- Thêm chức năng thêm/sửa/xóa cư dân
- Thêm chức năng tìm kiếm căn hộ
- Thêm chức năng export dữ liệu
- Thêm authentication/login
- Thêm notification khi có sự kiện mới
