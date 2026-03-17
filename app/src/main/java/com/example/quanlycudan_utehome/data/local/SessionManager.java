package com.example.quanlycudan_utehome.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "utehome_session";
    private static final String KEY_RESIDENT_ID  = "resident_id";
    private static final String KEY_APARTMENT_ID = "apartment_id"; // ← THÊM MỚI

    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveResidentId(int id) {
        prefs.edit().putInt(KEY_RESIDENT_ID, id).apply();
    }

    public int getResidentId() {
        return prefs.getInt(KEY_RESIDENT_ID, -1);
    }

    // ── Lưu và lấy ID căn hộ (gọi sau khi đăng nhập thành công) ──
    public void saveApartmentId(String aptId) {
        prefs.edit().putString(KEY_APARTMENT_ID, aptId).apply();
    }

    public String getApartmentId() {
        return prefs.getString(KEY_APARTMENT_ID, ""); // "" nếu chưa lưu
    }

    public boolean isLoggedIn() {
        return getResidentId() != -1;
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}
