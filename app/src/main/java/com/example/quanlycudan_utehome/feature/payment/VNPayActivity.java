package com.example.quanlycudan_utehome.feature.payment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.repository.PaymentRepository;
import com.example.quanlycudan_utehome.util.HmacUtil;
import com.example.quanlycudan_utehome.util.VNPayConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VNPayActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private String invoiceId;
    private long totalAmount;
    private boolean hasElec, hasWater, hasPark, hasInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay);

        // 1. Nhận dữ liệu
        invoiceId = getIntent().getStringExtra("INVOICE_ID");
        totalAmount = getIntent().getLongExtra("TOTAL_SUM", 0);
        hasElec = getIntent().getBooleanExtra("HAS_ELEC", true);
        hasWater = getIntent().getBooleanExtra("HAS_WATER", true);
        hasPark = getIntent().getBooleanExtra("HAS_PARK", true);
        hasInternet = getIntent().getBooleanExtra("HAS_INTERNET", true);

        // 2. Ánh xạ View
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // 3. Cấu hình WebView
        setupWebView();

        // 4. Tạo URL và Load
        String paymentUrl = generateVNPayUrl();
        webView.loadUrl(paymentUrl);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                android.util.Log.d("VNPAY", "Loading URL: " + url);

                if (url.contains(VNPayConfig.VNP_RETURN_URL)) {
                    handlePaymentResult(url);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains(VNPayConfig.VNP_RETURN_URL)) {
                    handlePaymentResult(url);
                    return true;
                }
                return false;
            }
        });
    }

    private void handlePaymentResult(String url) {
        if (url.contains("vnp_ResponseCode=00")) {
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();

            // Cập nhật Database
            PaymentRepository repository = new PaymentRepository(getApplication());
            repository.processMockPayment(invoiceId, totalAmount, "VNPay",
                    hasElec, hasWater, hasPark, hasInternet);

            setResult(RESULT_OK);
            finish();
        } else if (url.contains("vnp_ResponseCode=")) {
            // Các mã lỗi khác
            Toast.makeText(this, "Giao dịch không thành công.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private String generateVNPayUrl() {
        // Rút ngắn TxnRef (Tối đa 24 ký tự)
        String txnRef = (invoiceId != null ? invoiceId : "") + System.currentTimeMillis();
        if (txnRef.length() > 24) {
            txnRef = txnRef.substring(txnRef.length() - 24);
        }

        Map<String, String> vnp_Params = new java.util.HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.VNP_VERSION);
        vnp_Params.put("vnp_Command", VNPayConfig.VNP_COMMAND);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.VNP_TMN_CODE);
        vnp_Params.put("vnp_Amount", String.valueOf(totalAmount * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan hoa don " + (invoiceId != null ? invoiceId : ""));
        vnp_Params.put("vnp_OrderType", "billpayment");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.VNP_RETURN_URL);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_CreateDate",
                new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()));

        // 1. Sắp xếp tham số bằng TreeMap
        java.util.TreeMap<String, String> sortedParams = new java.util.TreeMap<>(vnp_Params);

        // 2. Xây dựng Hash Data và Query String
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        try {
            boolean first = true;
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();

                if (val != null && !val.isEmpty()) {
                    if (!first) {
                        hashData.append('&');
                        query.append('&');
                    }
                    hashData.append(key).append('=').append(val);
                    query.append(URLEncoder.encode(key, "UTF-8")).append('=')
                            .append(URLEncoder.encode(val, "UTF-8").replace("+", "%20"));
                    first = false;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Lấy Hash Secret từ Config bạn vừa nhập
        String secretKey = VNPayConfig.VNP_HASH_SECRET;

        String vnp_SecureHash = HmacUtil.hmacSHA512(secretKey, hashData.toString());
        String finalUrl = VNPayConfig.VNP_PAY_URL + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;

        android.util.Log.d("VNPAY", "FINAL_URL: " + finalUrl);
        return finalUrl;
    }
}
