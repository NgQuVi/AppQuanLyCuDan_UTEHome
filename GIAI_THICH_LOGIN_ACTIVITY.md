# Giải Thích LoginActivity

## 📝 Mục Đích Của LoginActivity

**LoginActivity.java** là activity xử lý màn hình đăng nhập của ứng dụng. Nó cho phép người dùng:
1. Nhập số điện thoại
2. Nhập mật khẩu
3. Xem/ẩn mật khẩu
4. Đăng nhập vào ứng dụng
5. Chuyển hướng đến RegisterActivity hoặc ForgotPasswordActivity

---

## 🔧 Các Chức Năng Chính

### 1. **Tạo Giao Diện Đăng Ký Có Màu Sắc**
```java
SpannableString spannable = new SpannableString(prefix + link);
spannable.setSpan(new ForegroundColorSpan(orange), linkStart, linkEnd, ...);
```
- Tạo chuỗi "Bạn chưa có tài khoản cư dân? Đăng ký" với từ "Đăng ký" có màu cam

### 2. **Chuyển Hướng Đến RegisterActivity**
```java
tvSignUp.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
```
- Khi người dùng click vào "Đăng ký", chuyển sang RegisterActivity

### 3. **Chuyển Hướng Đến ForgotPasswordActivity**
```java
findViewById(R.id.tvForgotPassword).setOnClickListener(...);
```
- Khi người dùng click vào "Quên mật khẩu?", chuyển sang ForgotPasswordActivity

### 4. **Hiển/Ẩn Mật Khẩu (Eye Icon)**
```java
if (isPasswordVisible) {
    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    ivTogglePassword.setImageResource(R.drawable.ic_eye_outline);
} else {
    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    ivTogglePassword.setImageResource(R.drawable.ic_eye_off_outline);
}
```
- Cho phép người dùng hiển thị/ẩn mật khẩu bằng cách click icon mắt
- Khi hiển thị: icon mắt mở, mật khẩu hiển thị rõ
- Khi ẩn: icon mắt đóng, mật khẩu được che đậu bằng dấu *

### 5. **Xác Thực Đầu Vào**
```java
if (phone.isEmpty()) {
    edtPhone.setError("Vui lòng nhập số điện thoại");
    return;
}
if (password.isEmpty()) {
    edtPassword.setError("Vui lòng nhập mật khẩu");
    return;
}
```
- Kiểm tra số điện thoại không trống
- Kiểm tra mật khẩu không trống
- Hiển thị lỗi nếu không hợp lệ

### 6. **Xử Lý Đăng Nhập**
```java
boolean isSuccess = AuthService.getInstance().login(phone, password);
if (isSuccess) {
    startActivity(new Intent(this, MainActivity.class));
    finish();
} else {
    Toast.makeText(this, "Thông tin đăng nhập không hợp lệ", Toast.LENGTH_SHORT).show();
}
```
- Gọi `AuthService.login()` để xác thực thông tin đăng nhập
- Nếu thành công: chuyển sang MainActivity
- Nếu thất bại: hiển thị thông báo lỗi

---

## 🎨 Các Thành Phần UI

| Thành Phần | ID | Loại | Mục Đích |
|-----------|-----|------|---------|
| Phone Input | `edtPhone` | EditText | Nhập số điện thoại |
| Password Input | `edtPassword` | EditText | Nhập mật khẩu |
| Password Toggle | `ivTogglePassword` | ImageView | Hiển/ẩn mật khẩu |
| Forgot Password Link | `tvForgotPassword` | TextView | Chuyển đến quên mật khẩu |
| Sign Up Link | `tvSignUp` | TextView | Chuyển đến đăng ký |
| Login Button | `btnLogin` | Button | Thực hiện đăng nhập |
| Root Layout | `loginRoot` | ConstraintLayout | Layout chính |

---

## 🔐 Luồng Đăng Nhập

```
┌─────────────────────────────────┐
│ Người dùng mở LoginActivity     │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│ Nhập số điện thoại              │
│ Nhập mật khẩu                   │
│ Click "Đăng nhập"               │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│ Kiểm tra số điện thoại trống?   │
└────┬──────────────────┬─────────┘
     │ Có              │ Không
     ▼                 ▼
 Hiển thị        ┌──────────────────────┐
 lỗi             │ Kiểm tra mật khẩu    │
 return          │ trống?               │
                 └────┬──────────┬──────┘
                      │ Có       │ Không
                      ▼         ▼
                  Hiển thị  ┌──────────────────┐
                  lỗi       │ Gọi AuthService  │
                  return    │ .login()         │
                            └────┬─────────┬──┘
                                 │ Thành   │ Thất
                                 │ công    │ bại
                                 ▼         ▼
                           Chuyển sang  Hiển thị
                           MainActivity lỗi
                           finish()
```

---

## 🔗 Kết Nối Với Các Activity Khác

- **RegisterActivity**: Cho phép đăng ký tài khoản mới
- **ForgotPasswordActivity**: Cho phép khôi phục mật khẩu
- **MainActivity**: Màn hình chính sau khi đăng nhập thành công

---

## 📱 Các Tính Năng Bổ Sung

### Edge-to-Edge Display
```java
ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginRoot), ...);
```
- Ứng dụng mở rộng toàn bộ màn hình (kể cả status bar, navigation bar)
- Tính toán padding để tránh các system UI

### Transformation Method
- `HideReturnsTransformationMethod`: Hiển thị mật khẩu
- `PasswordTransformationMethod`: Ẩn mật khẩu (dấu *)

---

## 💾 Dữ Liệu Lưu Trữ

Thông thường, khi đăng nhập thành công, ứng dụng sẽ:
1. Lưu token đăng nhập vào SharedPreferences
2. Lưu thông tin người dùng vào Database
3. Thiết lập session cho các request tiếp theo

---

## 🚀 Cách Sử Dụng

### Khởi động LoginActivity từ Activity khác:
```java
Intent intent = new Intent(this, LoginActivity.class);
startActivity(intent);
```

### Kiểm tra đăng nhập thành công:
```java
// Kiểm tra SharedPreferences xem có token không
SharedPreferences pref = getSharedPreferences("user_pref", MODE_PRIVATE);
String token = pref.getString("auth_token", null);
if (token == null) {
    // Chưa đăng nhập, mở LoginActivity
}
```

---

## ⚠️ Lưu Ý Bảo Mật

1. **Không lưu mật khẩu rõ**: Chỉ lưu token đăng nhập
2. **Kiểm tra input**: Xác thực số điện thoại, mật khẩu trên server
3. **HTTPS**: Sử dụng SSL/TLS để mã hóa dữ liệu truyền đi
4. **Token hết hạn**: Thiết lập thời gian hết hạn cho token
5. **Logout**: Xóa token khi người dùng logout

---

## 🔍 Cách Debug

1. Thêm breakpoint trong LoginActivity
2. Nhập thông tin và click "Đăng nhập"
3. Kiểm tra biến `phone`, `password`, `isSuccess`
4. Kiểm tra AuthService.login() trả về giá trị gì
5. Kiểm tra MainActivity có được mở không

---

## 📚 Tài Liệu Tham Khảo

- **Android Authentication**: https://developer.android.com/training/basics/data-storage/shared-preferences
- **EditText**: https://developer.android.com/reference/android/widget/EditText
- **Intent**: https://developer.android.com/reference/android/content/Intent
- **SpannableString**: https://developer.android.com/reference/android/text/SpannableString
