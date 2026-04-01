package com.example.quanlycudan_utehome.feature.invoice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.quanlycudan_utehome.R;
import com.example.quanlycudan_utehome.data.repository.PaymentRepository;
import com.example.quanlycudan_utehome.util.VNPayUtil;

public class VNPayWebActivity extends AppCompatActivity {

    private WebView webView;
    private PaymentRepository paymentRepository;
    
    private long totalSum;
    private String invoiceId;
    private boolean hasElec;
    private boolean hasWater;
    private boolean hasPark;
    private boolean hasInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay_web);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thanh toán VNPAY");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        paymentRepository = new PaymentRepository(getApplication());

        totalSum = getIntent().getLongExtra("TOTAL_SUM", 0L);
        invoiceId = getIntent().getStringExtra("INVOICE_ID");
        if (invoiceId == null) invoiceId = "TEST_INVOICE";
        
        hasElec = getIntent().getBooleanExtra("HAS_ELEC", true);
        hasWater = getIntent().getBooleanExtra("HAS_WATER", true);
        hasPark = getIntent().getBooleanExtra("HAS_PARK", true);
        hasInternet = getIntent().getBooleanExtra("HAS_INTERNET", true);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        String paymentUrl = VNPayUtil.getPaymentURL(totalSum, invoiceId);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith(VNPayUtil.vnp_ReturnUrl)) {
                    handleReturnUrl(request.getUrl());
                    return true; // We handled the return
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(VNPayUtil.vnp_ReturnUrl)) {
                    handleReturnUrl(Uri.parse(url));
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        webView.loadUrl(paymentUrl);
    }

    private void handleReturnUrl(Uri uri) {
        String responseCode = uri.getQueryParameter("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            // Payment success
            Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();

            if (invoiceId != null && !invoiceId.equals("TEST_INVOICE")) {
                paymentRepository.processMockPayment(invoiceId, totalSum, "VNPAY", hasElec, hasWater, hasPark, hasInternet);
            }

            Intent intent = new Intent(this, PaymentSuccessActivity.class);
            intent.putExtra("TOTAL_SUM", totalSum);
            intent.putExtra("METHOD", "VNPAY");
            
            // clear top activities so it acts like a fresh result screen
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            // Payment failed or cancelled
            Toast.makeText(this, "Thanh toán thất bại hoặc đã hủy", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
