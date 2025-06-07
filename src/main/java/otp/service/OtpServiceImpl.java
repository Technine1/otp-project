package otp.service;

import otp.dao.OtpCodeDao;
import otp.dao.OtpCodeDaoImpl;
import otp.dao.OtpConfigDao;
import otp.dao.OtpConfigDaoImpl;
import otp.model.OtpCode;
import otp.model.OtpConfig;
import otp.util.*;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtpServiceImpl implements OtpService {
    private final OtpCodeDao otpCodeDao = new OtpCodeDaoImpl();
    private final OtpConfigDao otpConfigDao = new OtpConfigDaoImpl();
    private static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);

    // Заглушки
    private final EmailSender emailSender = new EmailSender();
    private final SmsSender smsSender = new SmsSender();
    private final TelegramSender telegramSender = new TelegramSender();

    @Override
    public void generateAndSendCode(int userId, String operationId, String channel) {
        log.debug("Generating OTP for user {}, operation {}", userId, operationId);

        OtpConfig config = otpConfigDao.getConfig()
                .orElseThrow(() -> new RuntimeException("OTP configuration not found"));

        String code = OtpCodeGenerator.generate(config.getCodeLength());

        OtpCode otp = new OtpCode();
        otp.setUserId(userId);
        otp.setOperationId(operationId);
        otp.setCode(code);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setStatus("ACTIVE");
        otpCodeDao.create(otp);

        boolean sent = switch (channel.toLowerCase()) {
            case "email" -> emailSender.send("demo@example.com", code);
            case "sms"   -> smsSender.send("79000000000", code);
            case "telegram" -> telegramSender.send(code);
            case "file"  -> {
                FileCodeWriter.save(code, "otp_code.txt");
                yield true;
            }
            default -> throw new IllegalArgumentException("Unknown delivery channel: " + channel);
        };

        if (sent) {
            log.info("OTP sent via {}", channel);
        } else {
            log.warn("OTP failed to send via {}", channel);
        }
    }

    @Override
    public boolean validateCode(int userId, String operationId, String inputCode) {
        log.debug("Validating code for user {}, operation {}", userId, operationId);

        var opt = otpCodeDao.findActiveByUserAndOperation(userId, operationId);
        if (opt.isEmpty()) {
            log.warn("No active OTP found for user {}, operation {}", userId, operationId);
            return false;
        }

        OtpCode otp = opt.get();
        OtpConfig config = otpConfigDao.getConfig().orElseThrow();

        if (otp.getCreatedAt().plusSeconds(config.getTtlSeconds()).isBefore(LocalDateTime.now())) {
            otpCodeDao.updateStatus(otp.getId(), "EXPIRED");
            log.info("OTP expired for user {}, operation {}", userId, operationId);
            return false;
        }

        boolean valid = otp.getCode().equals(inputCode);
        log.info("OTP validation result for user {}, operation {}: {}", userId, operationId, valid);

        if (valid) {
            otpCodeDao.updateStatus(otp.getId(), "USED");
        }

        return valid;
    }
}
