package com.example.quanlycudan_utehome.feature.auth.service;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OtpService {
    private static final String TAG = "OTP_TEST";
    private static OtpService instance;

    // Map phone number to OTP information
    private final Map<String, OtpInfo> otpStorage;

    // Minimum validity period in milliseconds (e.g., 60 seconds)
    private static final long OTP_VALIDITY_MS = 60 * 1000;

    private OtpService() {
        otpStorage = new HashMap<>();
    }

    public static synchronized OtpService getInstance() {
        if (instance == null) {
            instance = new OtpService();
        }
        return instance;
    }

    /**
     * Generates a 6-digit OTP for the given phone number, prints it to logcat,
     * and stores it with an expiration timestamp.
     */
    public void generateOtp(String phone) {
        // Generate a random 6-digit number
        int randomPin = 100000 + new Random().nextInt(900000);
        String otp = String.valueOf(randomPin);

        // Calculate expiration timestamp
        long expirationTime = System.currentTimeMillis() + OTP_VALIDITY_MS;

        // Store OTP info
        otpStorage.put(phone, new OtpInfo(otp, expirationTime));

        // IMPORTANT: Log the OTP so we can test it
        Log.d(TAG, "===============================================");
        Log.d(TAG, "OTP for phone " + phone + " is: " + otp);
        Log.d(TAG, "===============================================");
    }

    /**
     * Validates the provided OTP for the given phone number.
     * Returns true if correct and not expired, false otherwise.
     */
    public boolean verifyOtp(String phone, String inputOtp) {
        OtpInfo storedInfo = otpStorage.get(phone);
        if (storedInfo == null) {
            return false;
        }

        // Check if expired
        if (System.currentTimeMillis() > storedInfo.expirationTime) {
            Log.d(TAG, "OTP expired for phone: " + phone);
            // Optionally, remove it from mapping
            otpStorage.remove(phone);
            return false;
        }

        // Check if it matches
        boolean isCorrect = storedInfo.otpCode.equals(inputOtp);
        if (isCorrect) {
            // Remove the OTP after successful verification to prevent reuse
            otpStorage.remove(phone);
        }
        return isCorrect;
    }

    // A simple container class for OTP data
    private static class OtpInfo {
        String otpCode;
        long expirationTime;

        OtpInfo(String otpCode, long expirationTime) {
            this.otpCode = otpCode;
            this.expirationTime = expirationTime;
        }
    }
}
