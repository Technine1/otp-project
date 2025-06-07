package otp.service.scheduler;

import otp.dao.OtpCodeDao;
import otp.dao.OtpCodeDaoImpl;
import otp.dao.OtpConfigDao;
import otp.dao.OtpConfigDaoImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtpExpirationTask implements Runnable {
    private final OtpCodeDao otpCodeDao = new OtpCodeDaoImpl();
    private final OtpConfigDao otpConfigDao = new OtpConfigDaoImpl();
    private static final Logger log = LoggerFactory.getLogger(OtpExpirationTask.class);

    @Override
    public void run() {
        int ttl = otpConfigDao.getConfig().orElseThrow().getTtlSeconds();
        log.info("Running OTP expiration check for TTL = {}s", ttl);
        otpCodeDao.markExpiredOlderThanSeconds(ttl);
        log.info("Expired OTPs marked");
    }
}
