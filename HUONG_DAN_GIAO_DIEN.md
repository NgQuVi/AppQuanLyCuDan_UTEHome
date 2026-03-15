# Hướng Dẫn Xây Dựng Giao Diện Chi Tiết Căn Hộ

## 🎨 Giới Thiệu

File này hướng dẫn cách **xây dựng giao diện giống 100%** với bản thiết kế chuẩn của ứng dụng Quản Lý Cư Dân UTE Home.

---

## 📐 Layout Structure - activity_apartment_info.xml

### Phần 1: Header (Phần Tiêu Đề)
```xml
<androidx.constraintlayout.widget.ConstraintLayout
    android:id="@+id/headerLayout"
    android:layout_height="wrap_content"
    android:background="@color/home_background"
    android:paddingTop="16dp"
    android:paddingBottom="16dp">
    
    <!-- Nút Back -->
    <ImageView
        android:id="@+id/ivBack"
        android:layout_width="24dp"
        android:layout_height="24dp"
        android:layout_marginStart="20dp"
        android:src="@drawable/ic_arrow_back_black" />
    
    <!-- Tiêu Đề -->
    <TextView
        android:id="@+id/tvHeaderTitle"
        android:text="Chi Tiết Căn Hộ"
        android:textSize="18sp"
        android:textStyle="bold"
        android:textColor="@color/home_text_primary" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Lưu ý**:
- Header cố định ở trên
- Background là màu nền nhỏ (light gray)
- Tiêu đề căn giữa
- Nút back trái, close right

---

### Phần 2: Nội Dung (ScrollView)
```xml
<androidx.core.widget.NestedScrollView
    android:layout_height="0dp"
    android:fillViewport="true"
    android:paddingBottom="32dp">
    
    <LinearLayout
        android:orientation="vertical">
        
        <!-- Card 1: Main Apartment Info -->
        <!-- Card 2: Chi Tiết Căn Hộ -->
        <!-- Card 3: Danh Sách Thành Viên -->
        
    </LinearLayout>
</androidx.core.widget.NestedScrollView>
```

---

## 🎯 Chi Tiết Từng Phần

### Card 1: Main Apartment Info (Thông Tin Chính)

```xml
<androidx.constraintlayout.widget.ConstraintLayout
    android:layout_marginHorizontal="20dp"
    android:layout_marginTop="16dp"
    android:background="@drawable/bg_card_white"
    android:padding="16dp">
    
    <!-- Tag "Đang sử dụng" (màu cam nhạt) -->
    <TextView
        android:id="@+id/tvTagLiving"
        android:background="@drawable/bg_tag_orange_light"
        android:text="Đang sử dụng"
        android:textSize="12sp"
        android:paddingHorizontal="10dp"
        android:paddingVertical="4dp"
        android:textColor="@color/home_tag_event_text" />
    
    <!-- Mã căn hộ (P.1205) - 24sp, bold -->
    <TextView
        android:id="@+id/tvMainApartmentCode"
        android:layout_marginTop="8dp"
        android:text="P.1205"
        android:textSize="24sp"
        android:textStyle="bold"
        android:textColor="@color/home_text_primary" />
    
    <!-- Tòa nhà (Tòa S1) - 14sp, secondary color -->
    <TextView
        android:id="@+id/tvMainBuilding"
        android:layout_marginTop="4dp"
        android:text="Tòa S1"
        android:textSize="14sp"
        android:textColor="@color/home_text_secondary" />
    
    <!-- Ảnh căn hộ (140dp height) -->
    <ImageView
        android:id="@+id/ivApartmentPhoto"
        android:layout_width="match_parent"
        android:layout_height="140dp"
        android:layout_marginTop="16dp"
        android:scaleType="centerCrop"
        android:src="@drawable/photo_login_buildings"
        android:clipToOutline="true" />
        
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Đặc điểm**:
- White card với padding 16dp
- Margin ngang 20dp, margin trên 16dp
- Tag màu cam
- Mã căn hộ in đậm, 24sp
- Ảnh cao 140dp, corners rounded

---

### Card 2: Chi Tiết Căn Hộ (Apartment Details)

Cấu trúc:
```
┌─────────────────────────────────────┐
│ CHI TIẾT CĂN HỘ                    │
├─────────────────────────────────────┤
│ Mã căn hộ      │  P.1205           │
├─────────────────────────────────────┤
│ Tòa nhà        │  Tòa S1           │
├─────────────────────────────────────┤
│ Tầng           │  12               │
├─────────────────────────────────────┤
│ Diện tích      │  105.5 m²         │
├─────────────────────────────────────┤
│ Chủ hộ         │  Nguyễn Anh Tuấn  │
├─────────────────────────────────────┤
│ Tình trạng     │  Đang sử dụng     │
└─────────────────────────────────────┘
```

```xml
<TextView
    android:layout_marginStart="20dp"
    android:layout_marginTop="24dp"
    android:text="CHI TIẾT CĂN HỘ"
    android:textSize="14sp"
    android:textStyle="bold"
    android:textColor="@color/home_text_secondary" />

<LinearLayout
    android:id="@+id/detailsLayout"
    android:layout_marginHorizontal="20dp"
    android:layout_marginTop="12dp"
    android:background="@drawable/bg_card_white"
    android:orientation="vertical"
    android:padding="16dp">
    
    <!-- Row 1: Mã căn hộ -->
    <LinearLayout
        android:orientation="horizontal"
        android:paddingVertical="12dp">
        
        <TextView
            android:layout_weight="1"
            android:text="Mã căn hộ"
            android:textSize="14sp"
            android:textColor="@color/home_text_secondary" />
        
        <TextView
            android:text="P.1205"
            android:textSize="14sp"
            android:textStyle="bold"
            android:textColor="@color/home_text_primary" />
    </LinearLayout>
    
    <View
        android:layout_height="1dp"
        android:background="@color/home_border_color" />
    
    <!-- Row 2, 3, 4, 5, 6 tương tự... -->
    
</LinearLayout>
```

**Đặc điểm**:
- LinearLayout horizontal, 2 columns (weight 1 + wrap_content)
- Divider 1dp giữa các rows
- Padding vertical 12dp
- Text trái: secondary color, 14sp
- Text phải: primary color, bold, 14sp

---

### Card 3: Danh Sách Thành Viên (Family Members)

Cấu trúc:
```
┌─────────────────────────────────────┐
│ THÀNH VIÊN GIA ĐÌNH           + Thêm │
├─────────────────────────────────────┤
│ [N] Nguyễn Anh Tuấn          ▶      │
│     Chủ hộ                         │
├─────────────────────────────────────┤
│ [T] Trần Thị Hương           ▶      │
│     Vợ                             │
├─────────────────────────────────────┤
│ [N] Nguyễn Anh Đức           ▶      │
│     Con                            │
└─────────────────────────────────────┘
```

```xml
<!-- Section Header -->
<RelativeLayout
    android:layout_marginHorizontal="20dp"
    android:layout_marginTop="24dp">
    
    <TextView
        android:text="THÀNH VIÊN GIA ĐÌNH"
        android:textSize="14sp"
        android:textStyle="bold"
        android:textColor="@color/home_text_secondary"
        android:layout_alignParentStart="true" />
    
    <TextView
        android:id="@+id/btnAddMember"
        android:text="+ Thêm"
        android:textSize="12sp"
        android:textColor="@color/home_primary_orange"
        android:layout_alignParentEnd="true" />
</RelativeLayout>

<!-- RecyclerView for Members -->
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/recyclerViewMembers"
    android:layout_marginHorizontal="20dp"
    android:layout_marginTop="12dp"
    android:nestedScrollingEnabled="false"
    android:orientation="vertical" />
```

---

## 📋 Layout Item Thành Viên - item_apartment_member.xml

```xml
<androidx.constraintlayout.widget.ConstraintLayout
    android:background="@drawable/bg_card_white"
    android:paddingHorizontal="16dp"
    android:paddingVertical="12dp"
    android:layout_marginBottom="8dp">
    
    <!-- Avatar (48x48dp) -->
    <TextView
        android:id="@+id/tvMemberAvatar"
        android:layout_width="48dp"
        android:layout_height="48dp"
        android:background="@drawable/bg_avatar_orange_light"
        android:text="N"
        android:textColor="@color/home_primary_orange"
        android:textSize="20sp"
        android:textStyle="bold"
        android:gravity="center" />
    
    <!-- Thông tin thành viên -->
    <LinearLayout
        android:orientation="vertical"
        android:layout_marginStart="16dp"
        app:layout_constraintStart_toEndOf="@id/tvMemberAvatar"
        app:layout_constraintEnd_toStartOf="@id/ivArrow">
        
        <!-- Tên thành viên -->
        <TextView
            android:id="@+id/tvMemberName"
            android:text="Nguyễn Anh Tuấn"
            android:textSize="16sp"
            android:textStyle="bold"
            android:textColor="@color/home_text_primary" />
        
        <!-- Vai trò -->
        <TextView
            android:id="@+id/tvMemberRole"
            android:text="Chủ hộ"
            android:textSize="12sp"
            android:textColor="@color/home_text_secondary"
            android:layout_marginTop="2dp" />
    </LinearLayout>
    
    <!-- Arrow Icon -->
    <ImageView
        android:id="@+id/ivArrow"
        android:layout_width="16dp"
        android:layout_height="16dp"
        android:src="@drawable/ic_arrow_right_gray"
        app:layout_constraintEnd_toEndOf="parent" />
        
</androidx.constraintlayout.widget.ConstraintLayout>
```

---

## 🎨 Colors - colors.xml

```xml
<resources>
    <!-- Primary Colors -->
    <color name="home_primary_orange">#FF8C42</color>
    <color name="home_background">#F5F5F5</color>
    
    <!-- Text Colors -->
    <color name="home_text_primary">#333333</color>
    <color name="home_text_secondary">#888888</color>
    
    <!-- Other Colors -->
    <color name="home_border_color">#EEEEEE</color>
    <color name="home_tag_event_text">#FF8C42</color>
    <color name="home_primary_orange_light">#FFE8D6</color>
</resources>
```

---

## 🎨 Drawables - drawable/*.xml

### bg_card_white.xml
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@android:color/white" />
    <corners android:radius="8dp" />
    <stroke android:color="@color/home_border_color" android:width="1dp" />
</shape>
```

### bg_tag_orange_light.xml
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@color/home_primary_orange_light" />
    <corners android:radius="4dp" />
</shape>
```

### bg_avatar_orange_light.xml
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@color/home_primary_orange_light" />
    <corners android:radius="24dp" />
</shape>
```

---

## 📏 Dimensions - dimens.xml

```xml
<resources>
    <!-- Margins -->
    <dimen name="margin_small">8dp</dimen>
    <dimen name="margin_medium">12dp</dimen>
    <dimen name="margin_large">16dp</dimen>
    <dimen name="margin_xlarge">20dp</dimen>
    
    <!-- Padding -->
    <dimen name="padding_small">8dp</dimen>
    <dimen name="padding_medium">12dp</dimen>
    <dimen name="padding_large">16dp</dimen>
    
    <!-- Sizes -->
    <dimen name="avatar_size">48dp</dimen>
    <dimen name="icon_size">24dp</dimen>
    <dimen name="card_height">140dp</dimen>
    
    <!-- Text Sizes -->
    <dimen name="text_small">12sp</dimen>
    <dimen name="text_medium">14sp</dimen>
    <dimen name="text_large">16sp</dimen>
    <dimen name="text_xlarge">18sp</dimen>
    <dimen name="text_xxlarge">24sp</dimen>
</resources>
```

---

## 🔄 Adapter Implementation - ApartmentMemberAdapter.java

```java
public class ApartmentMemberAdapter extends RecyclerView.Adapter<ViewHolder> {
    private List<ApartmentMemberDetail> members;
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_apartment_member, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ApartmentMemberDetail detail = members.get(position);
        
        holder.tvMemberName.setText(detail.resident.fullName);
        holder.tvMemberRole.setText(detail.apartmentMember.role);
        
        // Avatar từ chữ cái đầu
        String firstLetter = detail.resident.fullName.substring(0, 1);
        holder.tvMemberAvatar.setText(firstLetter);
    }
}
```

---

## ✅ Checklist Giao Diện

- ✅ Header cố định với nút back
- ✅ Card thông tin chính (mã căn hộ 24sp)
- ✅ Card chi tiết (6 rows với divider)
- ✅ Section thành viên
- ✅ RecyclerView dynamic
- ✅ Item layout với avatar + tên + vai trò
- ✅ Arrow icon phải
- ✅ Colors, dimensions chuẩn
- ✅ Spacing/margins chuẩn

---

## 🎯 Alignment & Spacing

```
20dp ←─────────────────────────────────────────→ 20dp
     ┌─────────────────────────────────────────┐
     │  [Back]        Chi Tiết Căn Hộ           │
     ├─────────────────────────────────────────┤
     │                                         │
     │  Card 1: Main Info                      │
     │  - Tag (12sp)                           │
     │  - Mã căn (24sp bold)                   │
     │  - Tòa nhà (14sp)                       │
     │  - Ảnh (140dp)                          │
     │                                         │
     │  Section: CHI TIẾT CĂN HỘ              │
     │  Card 2: Details (6 rows)               │
     │                                         │
     │  Section: THÀNH VIÊN              + Thêm│
     │  Card 3: RecyclerView                   │
     │  - Item 1 (avatar + text + arrow)      │
     │  - Item 2                               │
     │  - Item 3                               │
     │                                         │
     └─────────────────────────────────────────┘
```

---

## 🚀 Tips Thiết Kế

1. **Consistent Padding**: Tất cả card có padding 16dp, margin 20dp
2. **Hierarchy**: Text sizes 12, 14, 16, 24sp theo mức độ quan trọng
3. **Colors**: Max 3 text colors (primary, secondary, orange)
4. **Icons**: Consistent size 24dp, 16dp
5. **Spacing**: Vertical spacing 8-12dp giữa elements
6. **Radius**: 8dp cho card, 4dp cho tag, 24dp cho avatar

---

## 📱 Responsive Design

```java
// RecyclerView không scroll trong NestedScrollView
android:nestedScrollingEnabled="false"

// ConstraintLayout tự điều chỉnh
android:layout_width="match_parent"
android:layout_height="wrap_content"

// ScrollView full height
android:fillViewport="true"
```

---

**Bây giờ bạn đã biết cách xây dựng giao diện giống 100%! 🎉**
