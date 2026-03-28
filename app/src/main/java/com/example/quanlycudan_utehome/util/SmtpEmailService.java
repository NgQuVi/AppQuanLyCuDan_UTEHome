package com.example.quanlycudan_utehome.util;

import com.example.quanlycudan_utehome.BuildConfig;

import java.util.Locale;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public final class SmtpEmailService {

    private SmtpEmailService() {
    }

    public static boolean isConfigured() {
        return !isBlank(BuildConfig.SMTP_HOST)
                && BuildConfig.SMTP_PORT > 0
                && !isBlank(BuildConfig.SMTP_USERNAME)
                && !isBlank(BuildConfig.SMTP_PASSWORD);
    }

    public static String getConfigurationHint() {
        if (!isConfigured()) {
            return "Chua du thong tin SMTP";
        }

        if (isGmailHost() && BuildConfig.SMTP_PASSWORD.trim().length() < 16) {
            return "Gmail SMTP can App Password 16 ky tu, khong dung mat khau Gmail thuong";
        }

        return "";
    }

    public static void sendResidentOnboardingEmail(String toEmail, String fullName,
                                                   String phone, String temporaryPassword) throws Exception {
        if (!isConfigured()) {
            throw new IllegalStateException("SMTP chua duoc cau hinh");
        }
        if (isGmailHost() && BuildConfig.SMTP_PASSWORD.trim().length() < 16) {
            throw new IllegalStateException("Gmail yeu cau App Password 16 ky tu");
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", BuildConfig.SMTP_HOST);
        properties.put("mail.smtp.port", String.valueOf(BuildConfig.SMTP_PORT));
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "10000");
        properties.put("mail.smtp.writetimeout", "10000");

        String security = BuildConfig.SMTP_SECURITY.toLowerCase(Locale.ROOT).trim();
        if ("ssl".equals(security)) {
            properties.put("mail.smtp.ssl.enable", "true");
        } else {
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.starttls.required", "true");
        }

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        BuildConfig.SMTP_USERNAME,
                        BuildConfig.SMTP_PASSWORD
                );
            }
        });

        MimeMessage message = new MimeMessage(session);
        String fromAddress = isBlank(BuildConfig.SMTP_FROM_ADDRESS)
                ? BuildConfig.SMTP_USERNAME
                : BuildConfig.SMTP_FROM_ADDRESS;
        String fromName = isBlank(BuildConfig.SMTP_FROM_NAME)
                ? "UTEHome"
                : BuildConfig.SMTP_FROM_NAME;

        message.setFrom(new InternetAddress(fromAddress, fromName));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        message.setSubject("Thong tin dang nhap cu dan UTEHome", "UTF-8");
        message.setText(buildResidentOnboardingBody(fullName, phone, temporaryPassword), "UTF-8");

        Transport.send(message);
    }

    private static String buildResidentOnboardingBody(String fullName, String phone, String temporaryPassword) {
        return "Xin chao " + fullName + ",\n\n"
                + "Ban quan ly da tao tai khoan cu dan cho ban tren UTEHome.\n\n"
                + "So dien thoai dang nhap: " + phone + "\n"
                + "Mat khau tam: " + temporaryPassword + "\n\n"
                + "Ban se duoc yeu cau doi mat khau ngay trong lan dang nhap dau tien.\n\n"
                + "Tran trong.";
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static boolean isGmailHost() {
        return "smtp.gmail.com".equalsIgnoreCase(BuildConfig.SMTP_HOST.trim());
    }
}
