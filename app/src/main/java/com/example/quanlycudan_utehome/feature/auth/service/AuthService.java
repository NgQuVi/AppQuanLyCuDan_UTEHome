package com.example.quanlycudan_utehome.feature.auth.service;

import com.example.quanlycudan_utehome.feature.auth.model.User;

import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private static AuthService instance;

    // Simulate a database of users
    private final Map<String, User> mockDatabase;

    private AuthService() {
        mockDatabase = new HashMap<>();
        // Create 1 default test account: Phone: 0901234567, Pass: 12345678
        mockDatabase.put("0901234567", new User("0901234567", "12345678"));
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Attempts to log in with a phone number and password.
     * @return true if successful, false if not found or wrong password.
     */
    public boolean login(String phone, String password) {
        User user = mockDatabase.get(phone);
        if (user != null) {
            // In a real app we would use hashed password verification (e.g. BCrypt)
            return user.getPassword().equals(password);
        }
        return false;
    }

    /**
     * Checks if a phone number exists in our mock database.
     * Useful for the "Forgot Password" flow.
     */
    public boolean checkPhoneExists(String phone) {
        return mockDatabase.containsKey(phone);
    }

    /**
     * Updates the password for a given phone number.
     * @return true if successful, false if the phone doesn't exist.
     */
    public boolean updatePassword(String phone, String newPassword) {
        User user = mockDatabase.get(phone);
        if (user != null) {
            user.setPassword(newPassword);
            // In a real app we would also update this in DB
            return true;
        }
        return false;
    }
}
