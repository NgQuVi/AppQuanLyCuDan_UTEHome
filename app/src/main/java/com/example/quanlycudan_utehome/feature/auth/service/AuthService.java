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

    public static class LoginResult {
        public enum Status {
            ADMIN_SUCCESS,
            RESIDENT_SUCCESS,
            MUST_CHANGE_PASSWORD,
            INACTIVE_ACCOUNT,
            INVALID_CREDENTIALS
        }

        public final Status status;
        public final int residentId;
        public final String apartmentId;
        public final String phone;

        private LoginResult(Status status, int residentId, String apartmentId, String phone) {
            this.status = status;
            this.residentId = residentId;
            this.apartmentId = apartmentId == null ? "" : apartmentId;
            this.phone = phone == null ? "" : phone;
        }

        public static LoginResult adminSuccess() {
            return new LoginResult(Status.ADMIN_SUCCESS, -1, "", "");
        }

        public static LoginResult residentSuccess(int residentId, String apartmentId, String phone) {
            return new LoginResult(Status.RESIDENT_SUCCESS, residentId, apartmentId, phone);
        }

        public static LoginResult mustChangePassword(int residentId, String apartmentId, String phone) {
            return new LoginResult(Status.MUST_CHANGE_PASSWORD, residentId, apartmentId, phone);
        }

        public static LoginResult inactiveAccount(String phone) {
            return new LoginResult(Status.INACTIVE_ACCOUNT, -1, "", phone);
        }

        public static LoginResult invalidCredentials() {
            return new LoginResult(Status.INVALID_CREDENTIALS, -1, "", "");
        }
    }

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

    public void login(String phone, String password, AuthCallback<LoginResult> callback) {
        executorService.execute(() -> {
            if ("123456".equals(phone) && "123".equals(password)) {
                postResult(callback, LoginResult.adminSuccess());
                return;
            }

            Account account = db.accountDao().getAccountByPhone(phone);
            LoginResult result = LoginResult.invalidCredentials();

            if (account != null && account.password != null && account.password.equals(password)) {
                if (!account.isActive) {
                    result = LoginResult.inactiveAccount(phone);
                } else {
                    Resident resident = db.residentDao().getResidentByAccountId(account.id);
                    if (resident != null) {
                        Integer apartmentId = db.apartmentMemberDao().getApartmentIdByResidentId(resident.id);
                        if (apartmentId == null) {
                            apartmentId = db.apartmentDao().getApartmentIdByAccountId(account.id);
                        }
                        String apartmentIdValue = apartmentId == null ? "" : String.valueOf(apartmentId);
                        if (account.mustChangePassword) {
                            result = LoginResult.mustChangePassword(resident.id, apartmentIdValue, phone);
                        } else {
                            result = LoginResult.residentSuccess(resident.id, apartmentIdValue, phone);
                        }
                    }
                }
            }

            postResult(callback, result);
        });
    }

    public void checkPhoneExists(String phone, AuthCallback<Boolean> callback) {
        executorService.execute(() -> {
            int count = db.accountDao().checkPhoneExists(phone);
            postResult(callback, count > 0);
        });
    }

    public void updatePassword(String phone, String newPassword, AuthCallback<Boolean> callback) {
        executorService.execute(() -> {
            int count = db.accountDao().checkPhoneExists(phone);
            boolean success = false;
            if (count > 0) {
                db.accountDao().updatePasswordAndClearFirstLogin(phone, newPassword);
                success = true;
            }
            postResult(callback, success);
        });
    }

    private <T> void postResult(AuthCallback<T> callback, T result) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onResult(result));
    }
}
