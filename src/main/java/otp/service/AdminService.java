package otp.service;

import otp.model.OtpConfig;
import otp.model.User;

import java.util.List;

public interface AdminService {
    void updateOtpConfig(OtpConfig config);
    OtpConfig getOtpConfig();
    List<User> getAllNonAdminUsers();
    void deleteUserWithCodes(int userId);
}
