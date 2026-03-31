package com.example.quanlycudan_utehome.util;

public class VNPayConfig {
    // ═══════════════════════════════════════════════════════════════
    // 1. THÔNG SỐ SANDBOX (MÔI TRƯỜNG THỬ NGHIỆM)
    // ═══════════════════════════════════════════════════════════════

    // Website VNPay Sandbox: https://sandbox.vnpayment.vn/test-site/

    // Mã Website (Terminal ID) - Dùng mã Sandbox mặc định (ổn định nhất)
    public static final String VNP_TMN_CODE = "0VLKCKRZ";

    // Chuỗi bí mật HashSecret (Dùng để tạo SecureHash)
    // Thử chuỗi bí mật khác thường dùng cho 2QX19921
    public static final String VNP_HASH_SECRET = "294L2K6GO88P8A2CCHGU6R40LQYDWFDP";

    // URL thanh toán của VNPay Sandbox
    public static final String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    // Return URL (Nơi VNPay sẽ gọi lại sau khi thanh toán xong)
    // Mobile App sẽ bắt URL này qua WebView Redirect
    public static final String VNP_RETURN_URL = "https://utehome.com/vnpay_return";

    // Phiên bản API VNPay
    public static final String VNP_VERSION = "2.1.0";

    // Loại lệnh (mặc định là 'pay')
    public static final String VNP_COMMAND = "pay";
}
