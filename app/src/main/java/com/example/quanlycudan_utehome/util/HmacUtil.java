package com.example.quanlycudan_utehome.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Hỗ trợ tạo chữ ký số (Secure Hash) cho VNPay bằng HMAC-SHA512
 */
public class HmacUtil {

    private static final String HMAC_SHA512 = "HmacSHA512";

    /**
     * Tạo Secure Hash từ dữ liệu và secret key
     *
     * @param key   Chuỗi bí mật (vnp_HashSecret)
     * @param data  Chuỗi dữ liệu (đã sắp xếp tham số)
     * @return      Chuỗi hex (in hoa) của chữ ký số
     */
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                return "";
            }

            final Mac hmacSha512 = Mac.getInstance(HMAC_SHA512);
            final byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, HMAC_SHA512);
            hmacSha512.init(secretKey);

            final byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmacSha512.doFinal(dataBytes);

            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString().toUpperCase();

        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            return "";
        }
    }
}
