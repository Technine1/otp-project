package otp.dao;

import otp.model.OtpConfig;

import java.util.Optional;

public interface OtpConfigDao {
    Optional<OtpConfig> getConfig();
    void updateConfig(OtpConfig config);
}