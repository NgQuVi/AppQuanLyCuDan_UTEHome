package com.example.quanlycudan_utehome.util;

import java.security.SecureRandom;

public final class PasswordGenerator {

    private static final String UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijkmnopqrstuvwxyz";
    private static final String DIGITS = "23456789";
    private static final String ALL = UPPER + LOWER + DIGITS;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordGenerator() {
    }

    public static String generateTemporaryPassword(int length) {
        if (length < 8) {
            length = 8;
        }

        StringBuilder password = new StringBuilder(length);
        password.append(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
        password.append(LOWER.charAt(RANDOM.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));

        while (password.length() < length) {
            password.append(ALL.charAt(RANDOM.nextInt(ALL.length())));
        }

        for (int i = password.length() - 1; i > 0; i--) {
            int swapIndex = RANDOM.nextInt(i + 1);
            char current = password.charAt(i);
            password.setCharAt(i, password.charAt(swapIndex));
            password.setCharAt(swapIndex, current);
        }

        return password.toString();
    }
}
