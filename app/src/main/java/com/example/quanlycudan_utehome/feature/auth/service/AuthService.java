package com.example.quanlycudan_utehome.feature.auth.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Resident;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthService {

    private static AuthService instance;
    private final AppDatabase db;
    private final ExecutorService executorService;

    public interface AuthCallback<T> {
        void onResult(T result);
    }

    private AuthService(Context context) {
        db = AppDatabase.getInstance(context);
        executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized AuthService getInstance(Context context) {
        if (instance == null) {
            instance = new AuthService(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Attempts to log in with a phone number and password.
     * Returns the resident's ID upon success, or -1 if failed.
     */
    public void login(String phone, String password, AuthCallback<Integer> callback) {
        executorService.execute(() -> {
            Resident resident = db.residentDao().getResidentByPhone(phone);
            int loggedInId = -1;
            if (resident != null && resident.password != null) {
                // In a real app we would use hashed password verification (e.g. BCrypt)
                if (resident.password.equals(password)) {
                    loggedInId = resident.id;
                }
            }
            int finalId = loggedInId;
            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(finalId));
        });
    }

    /**
     * Checks if a phone number exists in our Room database.
     * Useful for the "Forgot Password" flow.
     */
    public void checkPhoneExists(String phone, AuthCallback<Boolean> callback) {
        executorService.execute(() -> {
            int count = db.residentDao().checkPhoneExists(phone);
            boolean exists = count > 0;
            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(exists));
        });
    }

    /**
     * Updates the password for a given phone number in DB.
     */
    public void updatePassword(String phone, String newPassword, AuthCallback<Boolean> callback) {
        executorService.execute(() -> {
            int count = db.residentDao().checkPhoneExists(phone);
            boolean success = false;
            if (count > 0) {
                db.residentDao().updatePassword(phone, newPassword);
                success = true;
            }
            boolean finalSuccess = success;
            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(finalSuccess));
        });
    }
}
