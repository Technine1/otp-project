package otp.dao;

import otp.model.OtpCode;

import java.util.List;
import java.util.Optional;

public interface OtpCodeDao {
    void create(OtpCode code);
    Optional<OtpCode> findActiveByUserAndOperation(int userId, String operationId);
    void updateStatus(int codeId, String status);
    void markExpiredOlderThanSeconds(int ttlSeconds);
    List<OtpCode> findByUser(int userId);
    void deleteByUser(int userId);
}