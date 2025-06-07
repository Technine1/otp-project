package otp.dao;

import otp.model.OtpCode;
import otp.util.DbUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OtpCodeDaoImpl implements OtpCodeDao {
    private final Connection connection = DbUtil.getConnection();

    @Override
    public void create(OtpCode code) {
        String sql = "INSERT INTO otp_codes (user_id, operation_id, code, created_at, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, code.getUserId());
            ps.setString(2, code.getOperationId());
            ps.setString(3, code.getCode());
            ps.setTimestamp(4, Timestamp.valueOf(code.getCreatedAt()));
            ps.setString(5, code.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create OTP code", e);
        }
    }

    @Override
    public Optional<OtpCode> findActiveByUserAndOperation(int userId, String operationId) {
        String sql = "SELECT * FROM otp_codes WHERE user_id = ? AND operation_id = ? AND status = 'ACTIVE'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, operationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find OTP code", e);
        }
        return Optional.empty();
    }

    @Override
    public void updateStatus(int codeId, String status) {
        String sql = "UPDATE otp_codes SET status = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, codeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update OTP code status", e);
        }
    }

    @Override
    public void markExpiredOlderThanSeconds(int ttlSeconds) {
        String sql = "UPDATE otp_codes SET status = 'EXPIRED' " +
                "WHERE status = 'ACTIVE' AND created_at < now() - INTERVAL '? seconds'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ttlSeconds);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to mark expired codes", e);
        }
    }

    @Override
    public List<OtpCode> findByUser(int userId) {
        List<OtpCode> codes = new ArrayList<>();
        String sql = "SELECT * FROM otp_codes WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                codes.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get OTP codes", e);
        }
        return codes;
    }

    @Override
    public void deleteByUser(int userId) {
        String sql = "DELETE FROM otp_codes WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete OTP codes", e);
        }
    }

    private OtpCode mapRow(ResultSet rs) throws SQLException {
        OtpCode code = new OtpCode();
        code.setId(rs.getInt("id"));
        code.setUserId(rs.getInt("user_id"));
        code.setOperationId(rs.getString("operation_id"));
        code.setCode(rs.getString("code"));
        code.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        code.setStatus(rs.getString("status"));
        return code;
    }
}