package com.example.quanlycudan_utehome.feature.auth.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Account;
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
            // Check for admin login directly
            if ("123456".equals(phone) && "123".equals(password)) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onResult(-999));
                return;
            }

            Account account = db.accountDao().getAccountByPhone(phone);
            int loggedInId = -1;
            if (account != null && account.password != null) {
                // In a real app we would use hashed password verification (e.g. BCrypt)
                if (account.password.equals(password)) {
                    Resident resident = db.residentDao().getResidentByAccountId(account.id);
                    if (resident != null) {
                        loggedInId = resident.id;
                    } else {
                        // Fallback to 1 if no resident linked for demo or error state
                        loggedInId = 1;
                    }
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
            int count = db.accountDao().checkPhoneExists(phone);
            boolean exists = count > 0;
            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(exists));
        });
    }

    /**
     * Updates the password for a given phone number in DB.
     */
    public void updatePassword(String phone, String newPassword, AuthCallback<Boolean> callback) {
        executorService.execute(() -> {
            int count = db.accountDao().checkPhoneExists(phone);
            boolean success = false;
            if (count > 0) {
                db.accountDao().updatePassword(phone, newPassword);
                success = true;
            }
            boolean finalSuccess = success;
            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(finalSuccess));
        });
    }
}
