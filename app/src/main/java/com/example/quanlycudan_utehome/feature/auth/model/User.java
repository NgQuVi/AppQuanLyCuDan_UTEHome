package com.example.quanlycudan_utehome.feature.auth.model;

public class User {
    private String phone;
    private String password; // In a real app, this should be a hashed password

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
