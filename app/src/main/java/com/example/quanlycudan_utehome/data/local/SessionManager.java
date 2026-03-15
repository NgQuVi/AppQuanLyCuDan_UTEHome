package com.example.quanlycudan_utehome.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "utehome_session";
    private static final String KEY_RESIDENT_ID = "resident_id";

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

    public boolean isLoggedIn() {
        return getResidentId() != -1;
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}
