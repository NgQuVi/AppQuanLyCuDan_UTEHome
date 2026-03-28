package com.example.quanlycudan_utehome.admin;

import android.content.Context;

import com.example.quanlycudan_utehome.data.database.AppDatabase;
import com.example.quanlycudan_utehome.data.entity.Account;
import com.example.quanlycudan_utehome.data.entity.Apartment;
import com.example.quanlycudan_utehome.data.entity.ApartmentMember;
import com.example.quanlycudan_utehome.data.entity.Resident;
import com.example.quanlycudan_utehome.util.PasswordGenerator;

public class ResidentOnboardingService {

    public static class OnboardingResult {
        public final Resident resident;
        public final Apartment apartment;
        public final String temporaryPassword;

        public OnboardingResult(Resident resident, Apartment apartment, String temporaryPassword) {
            this.resident = resident;
            this.apartment = apartment;
            this.temporaryPassword = temporaryPassword;
        }
    }

    private final AppDatabase db;

    public ResidentOnboardingService(Context context) {
        db = AppDatabase.getInstance(context.getApplicationContext());
    }

    public OnboardingResult createResident(String fullName, String phone, String email,
                                           String idCard, Apartment selectedApartment) {
        if (isBlank(fullName) || isBlank(phone) || isBlank(email)) {
            throw new IllegalArgumentException("Thiếu thông tin bắt buộc");
        }
        if (selectedApartment == null) {
            throw new IllegalArgumentException("Vui lòng chọn căn hộ");
        }
        if (db.accountDao().checkPhoneExists(phone) > 0) {
            throw new IllegalStateException("Số điện thoại đã tồn tại");
        }
        if (!db.apartmentMemberDao().getMembers(selectedApartment.id).isEmpty()) {
            throw new IllegalStateException("Căn hộ đã có cư dân chính");
        }

        String temporaryPassword = PasswordGenerator.generateTemporaryPassword(10);
        Resident resident = new Resident();

        db.runInTransaction(() -> {
            Account account = new Account();
            account.phone = phone;
            account.password = temporaryPassword;
            account.role = "Resident";
            account.isActive = false;
            account.mustChangePassword = true;

            long accountId = db.accountDao().insert(account);

            resident.accountId = (int) accountId;
            resident.fullName = fullName;
            resident.phone = phone;
            resident.email = email;
            resident.idNum = isBlank(idCard) ? null : idCard;
            long residentId = db.residentDao().insert(resident);
            resident.id = (int) residentId;

            ApartmentMember apartmentMember = new ApartmentMember();
            apartmentMember.apartmentId = selectedApartment.id;
            apartmentMember.residentId = resident.id;
            apartmentMember.role = "Chủ hộ";
            apartmentMember.residentType = "Chính";
            db.apartmentMemberDao().insert(apartmentMember);

            selectedApartment.accountId = (int) accountId;
            selectedApartment.status = "Đang sử dụng";
            db.apartmentDao().update(selectedApartment);
        });

        return new OnboardingResult(resident, selectedApartment, temporaryPassword);
    }

    public void activateAccount(int accountId) {
        db.accountDao().updateAccountActive(accountId, true);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
