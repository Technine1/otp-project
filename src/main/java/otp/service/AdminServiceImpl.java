package otp.service;

import otp.dao.OtpCodeDao;
import otp.dao.OtpCodeDaoImpl;
import otp.dao.OtpConfigDao;
import otp.dao.OtpConfigDaoImpl;
import otp.dao.UserDao;
import otp.dao.UserDaoImpl;
import otp.model.OtpConfig;
import otp.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class AdminServiceImpl implements AdminService {
    private final UserDao userDao = new UserDaoImpl();
    private final OtpCodeDao otpCodeDao = new OtpCodeDaoImpl();
    private final OtpConfigDao otpConfigDao = new OtpConfigDaoImpl();

    @Override
    public void updateOtpConfig(OtpConfig config) {
        otpConfigDao.updateConfig(config);
    }

    @Override
    public OtpConfig getOtpConfig() {
        return otpConfigDao.getConfig()
                .orElseThrow(() -> new RuntimeException("OTP config not found"));
    }

    @Override
    public List<User> getAllNonAdminUsers() {
        return userDao.findAll().stream()
                .filter(u -> !"ADMIN".equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserWithCodes(int userId) {
        otpCodeDao.deleteByUser(userId);
        userDao.deleteById(userId);
    }
}
