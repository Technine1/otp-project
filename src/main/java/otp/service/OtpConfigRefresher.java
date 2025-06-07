package otp.service;

import otp.dao.OtpConfigDao;
import otp.dao.OtpConfigDaoImpl;
import otp.model.OtpConfig;
import otp.util.PropertiesLoader;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtpConfigRefresher {
    private final OtpConfigDao configDao = new OtpConfigDaoImpl();
    private static final Logger log = LoggerFactory.getLogger(OtpConfigRefresher.class);

    public void refreshFromFile() {
        log.debug("Running refresh properties from file");
        Properties props = PropertiesLoader.load("otp.properties");

        int length = Integer.parseInt(props.getProperty("otp.code_length", "6"));
        int ttl = Integer.parseInt(props.getProperty("otp.ttl_seconds", "120"));

        OtpConfig config = new OtpConfig();
        config.setCodeLength(length);
        config.setTtlSeconds(ttl);

        configDao.updateConfig(config); // просто затираем запись с id=TRUE
        log.debug("Properties updated");
    }
}
