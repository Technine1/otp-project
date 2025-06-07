package otp.service;

public interface OtpService {
    void generateAndSendCode(int userId, String operationId, String channel);
    boolean validateCode(int userId, String operationId, String code);
}
