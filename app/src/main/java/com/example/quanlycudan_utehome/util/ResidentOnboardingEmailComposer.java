package com.example.quanlycudan_utehome.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

public final class ResidentOnboardingEmailComposer {

    private ResidentOnboardingEmailComposer() {
    }

    public static boolean canHandleEmail(AppCompatActivity activity) {
        Intent intent = buildIntent("", "", "");
        PackageManager packageManager = activity.getPackageManager();
        return intent.resolveActivity(packageManager) != null;
    }

    public static void openDraft(AppCompatActivity activity, String email, String fullName,
                                 String phone, String temporaryPassword) {
        String subject = "Thong tin dang nhap cu dan UTEHome";
        String body = "Xin chao " + fullName + ",\n\n"
                + "Ban quan ly da tao tai khoan cu dan cho ban tren UTEHome.\n\n"
                + "So dien thoai dang nhap: " + phone + "\n"
                + "Mat khau tam: " + temporaryPassword + "\n\n"
                + "Luu y: Ban se duoc yeu cau doi mat khau ngay trong lan dang nhap dau tien.\n\n"
                + "Tran trong.";

        Intent intent = buildIntent(email, subject, body);
        activity.startActivity(Intent.createChooser(intent, "Gui email thong tin dang nhap"));
    }

    private static Intent buildIntent(String email, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.fromParts("mailto", email, null));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        return intent;
    }
}
